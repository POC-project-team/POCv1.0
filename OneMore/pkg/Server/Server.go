package server

import (
	"encoding/json"
	"github.com/gorilla/mux"
	log "github.com/sirupsen/logrus"
	"golang.org/x/net/context"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"
	s "user/pkg/Service"
)

// todo: auth

type myServer struct {
	http.Server
	shutdownReq chan bool
	reqCount    uint32
}

func NewServer() (*myServer, *s.Service) {
	// create server
	myRouter := &myServer{
		Server: http.Server{
			Addr:         ":60494",
			ReadTimeout:  10 * time.Second,
			WriteTimeout: 10 * time.Second,
		},
	}

	srv := s.NewService()
	if err := json.Unmarshal(s.ReadJSONFile("pkg/Server/Data.json"), &srv.Store); err != nil {
		log.Error("Cannot read data from file")
	}
	router := mux.NewRouter()

	// todo: use methods
	// just for testing the
	userRouter := router.PathPrefix("/getUsers").Subrouter()
	userRouter.HandleFunc("", srv.GetAllUsers).Methods("GET")

	//router.HandleFunc("/getUsers", srv.GetAllUsers).Methods("GET")
	router.HandleFunc("/createUser", srv.CreateUser).Methods("GET")
	// todo: one func -> two things
	router.HandleFunc("/{user_id:[0-9]+}/createTag", srv.AddTag).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/getTags", srv.GetAllTags).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/{tag_id:[0-9]+}/getNotes", srv.GetNotes).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/{tag_id:[0-9]+}/addNote", srv.AddNote).Methods("POST")
	router.Handle("/", router)

	myRouter.Handler = router

	return myRouter, srv
}

func (myRouter *myServer) WaitShutdown() {
	irqSig := make(chan os.Signal, 1)
	signal.Notify(irqSig, syscall.SIGINT, syscall.SIGTERM)

	//Wait interrupt or shutdown request through /shutdown
	select {
	case sig := <-irqSig:
		log.Info("Shutdown request (signal: %v)", sig)
	case sig := <-myRouter.shutdownReq:
		log.Info("Shutdown request (/shutdown %v)", sig)
	}

	log.Info("Stopping http server ...")

	//Create shutdown context with 10 second timeout
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	//shutdown the server
	if err := myRouter.Shutdown(ctx); err != nil {
		log.Error("Error on shutdown", err)
	}
}

func foo() {
	srv := s.NewService()
	router := mux.NewRouter()

	// todo: use methods
	// just for testing the
	userRouter := router.PathPrefix("/getUsers").Subrouter()
	userRouter.HandleFunc("", srv.GetAllUsers).Methods("GET")

	//router.HandleFunc("/getUsers", srv.GetAllUsers).Methods("GET")
	router.HandleFunc("/createUser", srv.CreateUser).Methods("GET")
	// todo: one func -> two things
	router.HandleFunc("/{user_id:[0-9]+}/createTag", srv.AddTag).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/getTags", srv.GetAllTags).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/{tag_id:[0-9]+}/getNotes", srv.GetNotes).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/{tag_id:[0-9]+}/addNote", srv.AddNote).Methods("POST")
	http.Handle("/", router)

	if err := http.ListenAndServe(":60494", nil); err != nil {
		log.Error("The server is not listening")
	}
}
