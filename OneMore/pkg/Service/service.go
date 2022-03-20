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

// struct to parse the request from user
type request struct {
	UserID int32  `json:"target_id"`
	TagID  int32  `json:"tag_id"`
	Note   string `json:"note"`
}

type service struct {
	store map[int32]*u.User
}

func NewService() *service {
	return &service{make(map[int32]*u.User)}
}

// Contains check if the map Contains the specific user
func (s *service) Contains(u *u.User) bool {
	for _, i := range s.store {
		if i == u {
			return true
		}
	}
	return false
}

// Id generator
func (s *service) newId() int32 {
	var id int32
	// It's limited to 2^31 + 1
	// Wanted to use hash, but then thought it would be too much
	for s.store[id+1] != nil {
		id = rand.Int31()
	}
	return id + 1
}

// GetAllUsers func to return all of the users in the map
func (s *service) GetAllUsers(w http.ResponseWriter, r *http.Request) {
	// error checking
	if r.Method != "GET" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	for id, user := range s.store {
		_, err := w.Write([]byte("id: " + strconv.Itoa(int(id)) + "\nUser: " + user.ToString() + "\n"))
		if err != nil {
			return
		}
	}
}

func (s *service) CreateUser(w http.ResponseWriter, r *http.Request) {
	// error and requirements checking
	if r.Method != "GET" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	// wanted to make post one
	if r.Method == "POST" {
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

		tmpUser := u.NewUser()

		if err := json.Unmarshal(content, &tmpUser); err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			_, err := w.Write([]byte(err.Error()))
			if err != nil {
				return
			}
			log.Error("Cannot parse data from json")
			return
		}
	}
	tmpUser := u.NewUser()
	id := s.newId()
	s.store[id] = tmpUser

	log.Info("New user: ", id)
	w.WriteHeader(http.StatusCreated)
	if _, err := w.Write([]byte("new User was created\nid:" + strconv.Itoa(int(id)) + "\n")); err != nil {
		log.Error("Cannot write data to CreateUser")
	}
	return
}

func (s *service) GetAllTags(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	vars := mux.Vars(r)
	tmp, _ := strconv.Atoi(vars["user_id"])
	userId := int32(tmp)

	if _, ok := s.store[userId]; !ok {
		if _, err := w.Write([]byte("No such user")); err != nil {
			return
		}
		return
	}

	var result string
	for _, tag := range s.store[userId].Tags {
		result += "tag_id: " + tag.ToString() + "\n"
	}
	w.WriteHeader(http.StatusCreated)
	if _, err := w.Write([]byte("Tags of user_id: " + strconv.Itoa(int(userId)) + " " + result)); err != nil {
		log.Error("Cannot write data to user from Printing tags")
		return
	}
}

func (s *service) AddTag(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	vars := mux.Vars(r)
	tmp, _ := strconv.Atoi(vars["user_id"])
	userId := int32(tmp)

	if _, ok := s.store[userId]; !ok {
		_, err := w.Write([]byte("No such user"))
		if err != nil {
			return
		}
		return
	}

	id := s.store[userId].NewTag()
	w.WriteHeader(http.StatusCreated)
	if _, err := w.Write([]byte("New Tag id:" + strconv.Itoa(int(id)) + " was created\n")); err != nil {
		log.Error("Cannot write the data from adding tag")
		return
	}
}

func (s *service) GetNotes(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	vars := mux.Vars(r)
	tmp, _ := strconv.Atoi(vars["user_id"])
	userId := int32(tmp)

	if _, ok := s.store[userId]; !ok {
		_, err := w.Write([]byte("No such user"))
		if err != nil {
			return
		}
		return
	}

	tmp, _ = strconv.Atoi(vars["tag_id"])
	tagId := int32(tmp)

	if _, ok := s.store[userId].Tags[tagId]; !ok {
		_, err := w.Write([]byte("No such tag"))
		if err != nil {
			return
		}
	}

	_, err := w.Write([]byte(s.store[userId].Tags[tagId].GetNotes() + "\n"))
	if err != nil {
		return
	}

}

// AddNote of specific user
func (s *service) AddNote(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

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
	tmp, _ := strconv.Atoi(vars["user_id"])
	userId := int32(tmp)

	if _, ok := s.store[userId]; !ok {
		_, err := w.Write([]byte("No such user"))
		if err != nil {
			return
		}
		return
	}

	tmp, _ = strconv.Atoi(vars["tag_id"])
	tagId := int32(tmp)

	if _, ok := s.store[userId].Tags[tagId]; !ok {
		_, err := w.Write([]byte("No such tag"))
		if err != nil {
			return
		}
	}

	s.store[userId].Tags[tagId].AddNoteTag(req.Note)

	log.Info("New note was created")

	_, err = w.Write([]byte("Note was successfully created\n"))
	if err != nil {
		return
	}
}
