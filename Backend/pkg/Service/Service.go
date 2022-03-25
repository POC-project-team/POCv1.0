// Package service /*
package service

import (
	"backend/pkg/APIerror"
	db "backend/pkg/DB"
	u "backend/pkg/User"
	"encoding/json"
	"github.com/gorilla/mux"
	log "github.com/sirupsen/logrus"
	"net/http"
	"strconv"
)

type Service struct {
	Database db.DB
}

func NewService() *Service {
	return &Service{Database: *db.NewDataBase()}
}

// GetAllUsers func to return all of the users in the map
func (s *Service) GetAllUsers(w http.ResponseWriter, _ *http.Request) {
	type response struct {
		Users []string `json:"Users"`
	}

	var resp response
	resp.Users = s.Database.AllUsers()

	if err := json.NewEncoder(w).Encode(resp); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

func (s *Service) CreateUser(w http.ResponseWriter, _ *http.Request) {
	if err := json.NewEncoder(w).Encode(s.Database.CreateUser()); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

func (s *Service) GetAllTags(w http.ResponseWriter, r *http.Request) {
	type response struct {
		Tags []string `json:"Tags"`
	}

	var (
		resp response
		err  error
	)
	vars := mux.Vars(r)
	userId, _ := strconv.Atoi(vars["user_id"])

	if resp.Tags, err = s.Database.GetUserTags(userId); err != nil {
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

func (s *Service) GetNotes(w http.ResponseWriter, r *http.Request) {
	var req Request
	if err := req.Bind(w, r); err != nil {
		return
	}
	// params checking
	userId, err := strconv.Atoi(mux.Vars(r)["user_id"])
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No userID param",
		})
		return
	}

	var notes []u.Note

	notes, err = s.Database.GetUserNotes(userId, req.TagID)
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: err.Error(),
		})
		return
	}

	if err = json.NewEncoder(w).Encode(notes); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

// AddNote of specific user
func (s *Service) AddNote(w http.ResponseWriter, r *http.Request) {
	// try to parse the answer from user
	var req Request
	if err := req.Bind(w, r); err != nil {
		return
	}

	// params checking
	userId, err := strconv.Atoi(mux.Vars(r)["user_id"])
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No userID param",
		})
	}

	response, err := s.Database.AddNote(userId, req.TagID, req.Note)
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: err.Error(),
		})
		return
	}

	if err = json.NewEncoder(w).Encode(response); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		log.Info("New note for userID: ", userId, " tagID: ", req.TagID, " was created")
		w.WriteHeader(http.StatusCreated)
	}
}
