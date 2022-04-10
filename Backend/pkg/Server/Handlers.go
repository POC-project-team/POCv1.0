// Package server /* for setting up a server */
package server

import (
	"backend/pkg/APIerror"
	au "backend/pkg/Auth"
	service "backend/pkg/Service"
	"github.com/gorilla/mux"
	"net/http"
)

// MyHandler defines the routes, returns router
func MyHandler() *mux.Router {
	srv := service.NewService()
	router := mux.NewRouter()

	router.HandleFunc("/auth", au.Auth).Methods("POST")

	//router.HandleFunc("/{token}/testString", au.ParseUserIDFromToken)

	router.HandleFunc("/users", srv.GetAllUsers).Methods("GET")
	router.HandleFunc("/signup", srv.CreateUser).Methods("POST")
	router.HandleFunc("/{user_id:[0-9]+}/getTags", srv.GetAllTags).Methods("GET")

	router.HandleFunc("/{user_id:[0-9]+}/getNotes", srv.GetNotes).Methods("POST")
	router.HandleFunc("/{user_id:[0-9]+}/addNote", srv.AddNote).Methods("POST")

	router.HandleFunc("/test", func(writer http.ResponseWriter, request *http.Request) {
		writer.WriteHeader(http.StatusCreated)
		if _, err := writer.Write([]byte("Hello, I'm working\n")); err != nil {
			APIerror.HTTPErrorHandle(writer, APIerror.HTTPErrorHandler{
				ErrorCode:   http.StatusBadRequest,
				Description: "I don't know…",
			})
		}
	}).Methods("GET")
	router.Handle("/", router)
	return router
}
