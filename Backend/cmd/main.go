package main

import (
	s "backend/pkg/Server"
	service "backend/pkg/Service"
	log "github.com/sirupsen/logrus"
)

func main() {
	server, data := s.NewServer()

	done := make(chan bool)
	go func() {
		err := server.ListenAndServe()
		if err != nil {
			log.Info("Listen and server: ", err)
		}
		done <- true
	}()
	server.WaitShutdown()
	service.WriteJSONFile("pkg/Server/Data.json", *data)

	<-done
	log.Info("Server shutdown complete")
}
