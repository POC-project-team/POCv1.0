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
	u "user/pkg/user"
)

/*
todo:
	- error handler
	- func to check user's and tags
	- remover unmarshal to another file
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

func FuncErrorHandler(err error) {
	if err != nil {
		log.Error("While writing an error occurred")
	}
}

// ContainsUser Contains check if the map Contains the specific user
func (s *Service) ContainsUser(userId int, w http.ResponseWriter) bool {
	if _, ok := s.Store[userId]; !ok {
		_, err := w.Write([]byte("No such user"))
		FuncErrorHandler(err)
		return false
	}
	return true
}

// ContainsTag Contains check if the map Contains the specific user
func (s *Service) ContainsTag(userId, tagId int, w http.ResponseWriter) bool {
	if _, ok := s.Store[userId].Tags[tagId]; !ok {
		_, err := w.Write([]byte("No such tag"))
		FuncErrorHandler(err)
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
func (s *Service) GetAllUsers(w http.ResponseWriter, r *http.Request) {
	type response struct {
		Users []string `json:"Users"`
	}
	var resp response
	for id := range s.Store {
		resp.Users = append(resp.Users, strconv.Itoa(id))
	}

	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(resp.Users); err != nil {
		log.Error("Something went wrong")
	}
}

func (s *Service) CreateUser(w http.ResponseWriter, r *http.Request) {
	if r.Method == "POST" {
		// todo: check for error on POST
		content, err := ioutil.ReadAll(r.Body)
		defer func(Body io.ReadCloser) {
			err := Body.Close()
			if err != nil {
				log.Error("Cannot close the file while creating user")
			}
		}(r.Body)
		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			log.Error("Cannot read the data from request")
			_, err := w.Write([]byte(err.Error()))
			if err != nil {
				return
			}
			return
		}

		tmpUser := u.User{s.newId(), nil}

		if err := json.Unmarshal(content, &tmpUser); err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			_, err := w.Write([]byte(err.Error()))
			if err != nil {
				return
			}
			log.Error("Cannot parse data from json")
			return
		}

		w.WriteHeader(http.StatusCreated)
		if err := json.NewEncoder(w).Encode(tmpUser); err != nil {
			log.Error("Cannot write data to CreateUser")
		}

	} else {
		id := s.newId()
		s.Store[id] = u.NewUser(id)

		log.Info("New user: ", id)
		w.WriteHeader(http.StatusCreated)
		if err := json.NewEncoder(w).Encode(s.Store[id]); err != nil {
			log.Error("Cannot write data to CreateUser")
		}
	}
}

func (s *Service) GetAllTags(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	userId, _ := strconv.Atoi(vars["user_id"])
	if !(s.ContainsUser(userId, w)) {
		return
	}

	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags); err != nil {
		log.Error("Something went wrong")
	}
}

func (s *Service) AddTag(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	userId, _ := strconv.Atoi(vars["user_id"])

	if !(s.ContainsUser(userId, w)) {
		return
	}

	id := s.Store[userId].NewTag()

	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags[id]); err != nil {
		log.Error("Something went wrong")
	}
}

func (s *Service) GetNotes(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	// todo: check for the arguments
	userId, _ := strconv.Atoi(vars["user_id"])

	if !(s.ContainsUser(userId, w)) {
		return
	}

	tagId, _ := strconv.Atoi(vars["tag_id"])

	if !(s.ContainsTag(userId, tagId, w)) {
		return
	}

	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags[tagId]); err != nil {
		log.Error("Something went wrong")
	}
}

// AddNote of specific user
func (s *Service) AddNote(w http.ResponseWriter, r *http.Request) {
	content, err := ioutil.ReadAll(r.Body)
	defer func(Body io.ReadCloser) {
		err := Body.Close()
		if err != nil {
			log.Error("Error while closing file after reading note")
		}
	}(r.Body)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		log.Error("Cannot read the data from request")
		_, err := w.Write([]byte(err.Error()))
		if err != nil {
			return
		}
		return
	}
	var req request
	if err := json.Unmarshal(content, &req); err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		_, err := w.Write([]byte(err.Error()))
		if err != nil {
			return
		}
		log.Error("Cannot parse data from json")
		return
	}

	vars := mux.Vars(r)
	userId, _ := strconv.Atoi(vars["user_id"])
	if !(s.ContainsUser(userId, w)) {
		return
	}

	tagId, _ := strconv.Atoi(vars["tag_id"])
	if !(s.ContainsTag(userId, tagId, w)) {
		return
	}

	s.Store[userId].Tags[tagId].AddNoteTag(req.Note)

	log.Info("New note for userID: ", userId, " tagID: ", tagId, " was created")

	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(s.Store[userId].Tags[tagId]); err != nil {
		log.Error("Something went wrong")
	}
}
