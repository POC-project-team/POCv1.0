package main

import (
	"backend/pkg/DB"
	s "backend/pkg/Server"
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
	DB.WriteJSONFile("pkg/DB/Data.json", &data.Database)

	<-done
	log.Info("Server shutdown complete")
}
