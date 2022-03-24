// Package service /*
package service

import (
	"backend/pkg/APIerror"
	domain "backend/pkg/Domain"
	u "backend/pkg/User"
	"encoding/json"
	"github.com/gorilla/mux"
	log "github.com/sirupsen/logrus"
	"math/rand"
	"net/http"
	"strconv"
)

type Service struct {
	Store map[int]*u.User
}

func NewService() *Service {
	return &Service{make(map[int]*u.User)}
}

// ContainsUser Contains check if the map Contains the specific user
func (s *Service) ContainsUser(userId int, w http.ResponseWriter) bool {
	if _, ok := s.Store[userId]; !ok {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No such user",
		})
		return false
	}
	return true
}

// ContainsTag Contains check if the map Contains the specific user
func (s *Service) ContainsTag(userId int, tagId string, w http.ResponseWriter) bool {
	if _, ok := s.Store[userId].Tags[tagId]; !ok {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "User don't have such tag",
		})
		return false
	}
	return true
}

// Id generator
func (s *Service) newId() int {
	var id int
	for s.Store[id+1] != nil {
		id = rand.Int()
	}
	return id + 1
}

// GetAllUsers func to return all of the users in the map
func (s *Service) GetAllUsers(w http.ResponseWriter, _ *http.Request) {
	type response struct {
		Users []string `json:"Users"`
	}
	var resp response
	for id := range s.Store {
		resp.Users = append(resp.Users, strconv.Itoa(id))
	}

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
	id := s.newId()
	s.Store[id] = u.NewUser(id)

	log.Info("New user: ", id)

	if err := json.NewEncoder(w).Encode(s.Store[id]); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

func (s *Service) GetAllTags(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	userId, _ := strconv.Atoi(vars["user_id"])
	if !(s.ContainsUser(userId, w)) {
		return
	}

	type response struct {
		Tags []string `json:"Tags"`
	}
	var resp response

	for tag := range s.Store[userId].Tags {
		resp.Tags = append(resp.Tags, tag)
	}

	if err := json.NewEncoder(w).Encode(resp); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

func (s *Service) GetNotes(w http.ResponseWriter, r *http.Request) {
	var req domain.Request
	if err := req.Bind(w, r); err != nil {
		return
	}
	// params checking
	vars := mux.Vars(r)
	userId, err := strconv.Atoi(vars["user_id"])
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No userID param",
		})
	}

	if !(s.ContainsUser(userId, w)) || !(s.ContainsTag(userId, req.TagID, w)) {
		return
	}

	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags[req.TagID].Notes); err != nil {
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
	var req domain.Request
	// try to parse the answer from user
	if err := req.Bind(w, r); err != nil {
		return
	}
	if len(req.Note) == 0 {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "Field 'note' has no data",
		})
		return
	}

	// params checking
	vars := mux.Vars(r)
	userId, err := strconv.Atoi(vars["user_id"])
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No userID param",
		})
	}

	if !(s.ContainsUser(userId, w)) {
		return
	}

	if s.Store[userId].Tags[req.TagID] == nil {
		s.Store[userId].NewTag(req.TagID)
	}

	s.Store[userId].Tags[req.TagID].AddNoteTag(req.Note)

	log.Info("New note for userID: ", userId, " tagID: ", req.TagID, " was created")

	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags[req.TagID]); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}
