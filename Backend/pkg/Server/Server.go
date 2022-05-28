// Package server /* for setting up a server */
package server

import (
	log "github.com/sirupsen/logrus"
	"golang.org/x/net/context"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"
)

type myServer struct {
	http.Server
	shutdownReq chan bool
	reqCount    uint32
}

// NewServer constructor for Server
func NewServer() *myServer {
	myRouter := &myServer{
		Server: http.Server{
			Addr:         "0.0.0.0:60494", //it's going to be redone
			ReadTimeout:  15 * time.Second,
			WriteTimeout: 15 * time.Second,
		},
	}

	myRouter.Handler = MyHandler()

	return myRouter
}

// WaitShutdown for correct shutting down the server
func (myRouter *myServer) WaitShutdown() {
	irqSig := make(chan os.Signal, 1)
	signal.Notify(irqSig, syscall.SIGINT, syscall.SIGTERM)

	//Wait interrupt or shutdown request through /shutdown
	select {
	case sig := <-irqSig:
		log.Info("Shutdown request signal: ", sig)
	}

	//Create shutdown context with 10 second timeout
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	//shutdown the server
	if err := myRouter.Shutdown(ctx); err != nil {
		log.Error("Error on shutdown", err)
	}
}
