package main

import (
	"github.com/gorilla/mux"
	"net/http"
	s "user/pkg/service"
)

// The entry point

func main() {
	srv := s.NewService()
	router := mux.NewRouter()

	router.HandleFunc("/getUsers", srv.GetAllUsers)
	router.HandleFunc("/createUser", srv.CreateUser)
	router.HandleFunc("/{user_id:[0-9]+}/createTag", srv.AddTag)
	router.HandleFunc("/{user_id:[0-9]+}/{tag_id:[0-9]+}/getNotes", srv.GetNotes)
	router.HandleFunc("/{user_id:[0-9]+}/{tag_id:[0-9]+}/addNote", srv.AddNote)
	http.Handle("/", router)

	err := http.ListenAndServe("localhost:8080", nil)
	if err != nil {
		return
	}
}
