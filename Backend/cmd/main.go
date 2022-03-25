// Package main /* Entry point for the programs */
package main

import (
	s "backend/pkg/Server"
	log "github.com/sirupsen/logrus"
)

func main() {
	server, data := s.NewServer()

	// signal handler for correct shutdown
	done := make(chan bool)
	go func() {
		err := server.ListenAndServe()
		if err != nil {
			log.Info(err.Error())
		}
		done <- true
	}()

	server.WaitShutdown()
	data.Database.SaveData()

	<-done
}
