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

	router.HandleFunc("/users", srv.GetAllUsers).Methods("GET")

	router.HandleFunc("/auth", au.Auth).Methods("POST")
	router.HandleFunc("/{token}/changeLogin", au.ChangeLogin).Methods("POST")
	router.HandleFunc("/{token}/changePassword", au.ChangePassword).Methods("POST")

	router.HandleFunc("/signup", srv.CreateUser).Methods("POST")
	router.HandleFunc("/{token}/tags", srv.GetAllTags).Methods("GET")

	router.HandleFunc("/{token}/{tag_id}/notes", srv.GetNotes).Methods("GET")
	router.HandleFunc("/{token}/{tag_id}/note", srv.AddNote).Methods("POST")

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
