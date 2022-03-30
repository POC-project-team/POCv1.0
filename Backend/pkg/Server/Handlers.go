// Package server /* for setting up a server */
package server

import (
	"backend/pkg/APIerror"
	"backend/pkg/DB"
	service "backend/pkg/Service"
	"encoding/json"
	"github.com/gorilla/mux"
	"net/http"
	"strconv"
)

// MyHandler defines the routes, returns router
func MyHandler(srv *service.Service) *mux.Router {
	router := mux.NewRouter()

	userRouter := router.PathPrefix("/getUsers").Subrouter()
	userRouter.HandleFunc("", srv.GetAllUsers).Methods("GET")

	//router.HandleFunc("/getUsers", srv.GetAllUsers).Methods("GET")
	router.HandleFunc("/createUser", srv.CreateUser).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/getTags", srv.GetAllTags).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/getNotes", srv.GetNotes).Methods("POST")
	router.HandleFunc("/{user_id:[0-9]+}/addNote", srv.AddNote).Methods("POST")
	router.HandleFunc("/{user_id:[0-9]+}/test", Test)
	router.Handle("/", router)

	return router
}

func Test(w http.ResponseWriter, r *http.Request) {

	data := DB.NewSQLDataBase()

	type response struct {
		Tags []string `json:"Tags"`
	}

	var (
		resp response
		err  error
	)
	vars := mux.Vars(r)
	userId, _ := strconv.Atoi(vars["user_id"])

	if resp.Tags, err = data.GetUserTags(userId); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: err.Error(),
		})
		return
	}

	if err = json.NewEncoder(w).Encode(resp); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}
