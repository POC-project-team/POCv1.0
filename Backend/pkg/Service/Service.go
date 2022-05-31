// Package service /* logic for requesting */
package service

import (
	"backend/pkg/APIerror"
	db "backend/pkg/DB"
	u "backend/pkg/User"
	"encoding/json"
	log "github.com/sirupsen/logrus"
	"net/http"
)

type Service struct {
	BaseSQL db.SQL
}

func NewService() *Service {
	return &Service{
		*db.NewSQLDataBase(),
	}
}

// GetAllUsers func to return all users in the map
func (s *Service) GetAllUsers(w http.ResponseWriter, _ *http.Request) {
	type response struct {
		Users []string `json:"Users"`
	}

	var (
		resp response
		err  error
	)
	resp.Users, err = s.BaseSQL.GetAllUsers()
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: err.Error(),
		})
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

// CreateUser handler for creating new user
func (s *Service) CreateUser(w http.ResponseWriter, r *http.Request) {
	var req Request
	if req.Bind(w, r) != nil {
		return
	}

	if req.Login == "" || req.Password == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No login or password provided",
		})
		return
	}

	result, err := s.BaseSQL.CreateUser(req.Login, req.Password)
	if err != nil {
		if err.Error() == "user with such login exists" {
			APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
				ErrorCode:   http.StatusBadRequest,
				Description: err.Error(),
			})
			return
		} else {
			APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
				ErrorCode:   http.StatusInternalServerError,
				Description: err.Error(),
			})
			return
		}
	}
	if err := json.NewEncoder(w).Encode(result); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

// GetAllUsersTags handler for getting all tags of specific user
func (s *Service) GetAllUsersTags(w http.ResponseWriter, r *http.Request) {
	var (
		tags []db.TagNoUserNotes
		req  Request
		err  error
	)
	if req.ParseToken(w, r) != nil {
		return
	}

	if tags, err = s.BaseSQL.GetUserTags(req.UserID); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: err.Error(),
		})
		return
	}

	if err = json.NewEncoder(w).Encode(tags); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

func (s *Service) GetTag(w http.ResponseWriter, r *http.Request) {
	var (
		resp db.TagNoUserNotes
		req  Request
		err  error
	)
	if req.ParseToken(w, r) != nil || req.ParseTagID(w, r) != nil {
		return
	}

	if req.TagID == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tag id provided",
		})
		return
	}

	if resp, err = s.BaseSQL.GetTag(req.UserID, req.TagID); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: err.Error(),
		})
		return
	}

	if resp.TagName == "" && resp.TagID == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No such tag",
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

func (s *Service) UpdateTag(w http.ResponseWriter, r *http.Request) {
	var (
		req  Request
		resp db.TagNoUserNotes
		err  error
	)
	if req.Bind(w, r) != nil || req.ParseToken(w, r) != nil || req.ParseTagID(w, r) != nil {
		return
	}

	if req.TagID == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tag id provided",
		})
		return
	}

	if req.TagName == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tag name provided",
		})
		return
	}

	if resp, err = s.BaseSQL.UpdateTag(req.UserID, req.TagID, req.TagName); err != nil {
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

func (s *Service) CreateTag(w http.ResponseWriter, r *http.Request) {
	type response struct {
		Tag db.TagNoUserNotes `json:"tag"`
	}
	var (
		resp response
		req  Request
		err  error
	)
	if req.Bind(w, r) != nil || req.ParseToken(w, r) != nil || req.ParseTagID(w, r) != nil {
		return
	}

	if req.TagID == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tag id provided",
		})
		return
	}

	if req.TagName == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tag name provided",
		})
		return
	}

	if resp.Tag, err = s.BaseSQL.CreateTag(req.UserID, req.TagID, req.TagName); err != nil {
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

func (s *Service) DeleteTag(w http.ResponseWriter, r *http.Request) {
	var (
		req Request
		err error
	)
	if req.ParseToken(w, r) != nil || req.ParseTagID(w, r) != nil {
		return
	}

	if req.TagID == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tag id provided",
		})
		return
	}

	if err = s.BaseSQL.DeleteTag(req.UserID, req.TagID); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: err.Error(),
		})
		return
	}

	resp := "Tag deleted"

	if err = json.NewEncoder(w).Encode(resp); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

func (s *Service) TransferTag(w http.ResponseWriter, r *http.Request) {
	var (
		req Request
		err error
	)
	if req.Bind(w, r) != nil || req.ParseToken(w, r) != nil || req.ParseTagID(w, r) != nil {
		return
	}

	if req.TagID == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tag id provided",
		})
		return
	}

	if err = s.BaseSQL.TransferTag(req.UserID, req.TagID, req.Login); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: err.Error(),
		})
		return
	}

	resp := "Tag was transferred"

	if err = json.NewEncoder(w).Encode(resp); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot write data to request",
		})
	} else {
		w.WriteHeader(http.StatusCreated)
	}
}

// GetNotes handler for getting notes for specific tag of user
func (s *Service) GetNotes(w http.ResponseWriter, r *http.Request) {
	var req Request
	if req.ParseToken(w, r) != nil || req.ParseTagID(w, r) != nil {
		return
	}

	var notes []u.Note

	notes, err := s.BaseSQL.GetUserNotes(req.UserID, req.TagID)
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

// AddNote handler for creating new note for specific tag of user
func (s *Service) AddNote(w http.ResponseWriter, r *http.Request) {
	var req Request
	// param checking
	if req.Bind(w, r) != nil || req.ParseToken(w, r) != nil || req.ParseTagID(w, r) != nil {
		return
	}

	response, err := s.BaseSQL.AddNote(req.UserID, req.TagID, req.Note)
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
		log.Info("New note for userID: ", req.UserID, " tagID: ", req.TagID, " was created")
		w.WriteHeader(http.StatusCreated)
	}
}
