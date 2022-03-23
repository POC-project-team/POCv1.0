// Package service /*
package service

import (
	"encoding/json"
	"github.com/gorilla/mux"
	log "github.com/sirupsen/logrus"
	"io"
	"io/ioutil"
	"math/rand"
	"net/http"
	"strconv"
	"user/pkg/APIerror"
	u "user/pkg/user"
)

/*
todo:
	- remove unmarshal to another file
*/

// struct to parse the request from user
type request struct {
	UserID int    `json:"userID"`
	TagID  int    `json:"tagID"`
	Note   string `json:"note"`
}

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
func (s *Service) ContainsTag(userId, tagId int, w http.ResponseWriter) bool {
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

	if err := json.NewEncoder(w).Encode(resp.Users); err != nil {
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

	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

func (s *Service) AddTag(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	userId, _ := strconv.Atoi(vars["user_id"])

	if !(s.ContainsUser(userId, w)) {
		return
	}

	id := s.Store[userId].NewTag()

	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags[id]); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

func (s *Service) GetNotes(w http.ResponseWriter, r *http.Request) {
	// params checking
	vars := mux.Vars(r)
	userId, err := strconv.Atoi(vars["user_id"])
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No userID param",
		})
	}
	tagId, err := strconv.Atoi(vars["tag_id"])
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tagID param",
		})
	}

	if !(s.ContainsUser(userId, w)) || !(s.ContainsTag(userId, tagId, w)) {
		return
	}

	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags[tagId]); err != nil {
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
	content, err := ioutil.ReadAll(r.Body)
	defer func(Body io.ReadCloser) {
		if err := Body.Close(); err != nil {
			APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
				ErrorCode:   http.StatusInternalServerError,
				Description: "Error while closing file after reading note",
			})
		}
	}(r.Body)

	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot read the data from request",
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
	tagId, err := strconv.Atoi(vars["tag_id"])
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tagID param",
		})
	}

	if !(s.ContainsUser(userId, w)) || !(s.ContainsTag(userId, tagId, w)) {
		return
	}

	// json parsing
	var req request
	if err := json.Unmarshal(content, &req); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot parse data from json",
		})
		return
	}

	s.Store[userId].Tags[tagId].AddNoteTag(req.Note)

	log.Info("New note for userID: ", userId, " tagID: ", tagId, " was created")

	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags[tagId]); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}
