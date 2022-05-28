// Package service /* logic for requesting */
package service

import (
	"backend/pkg/APIerror"
	"crypto/sha1"
	"encoding/hex"
	"encoding/json"
	"errors"
	"github.com/dgrijalva/jwt-go"
	"github.com/gorilla/mux"
	"io"
	"io/ioutil"
	"net/http"
)

type Request struct {
	UserID   int    `json:"userID"`
	TagID    string `json:"tagID"`
	TagName  string `json:"tagName"`
	Note     string `json:"note"`
	Login    string `json:"login"`
	Password string `json:"password"`
}

func toHash(passwd string) string {
	h := sha1.New()
	return hex.EncodeToString(h.Sum([]byte(passwd)))
}

// Bind read body of request, return error when exist
func (req *Request) Bind(w http.ResponseWriter, r *http.Request) error {
	content, err := ioutil.ReadAll(r.Body)
	defer func(Body io.ReadCloser) {
		if err = Body.Close(); err != nil {
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
		return errors.New("cannot read the data from request")
	}

	if err = json.Unmarshal(content, &req); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot parse data from JSON",
		})
		return errors.New("cannot parse data from JSON")
	}
	if req.Password != "" {
		req.Password = toHash(req.Password)
	}

	return nil
}

// ParseTagID from the header and put into the struct
func (req *Request) ParseTagID(w http.ResponseWriter, r *http.Request) error {
	tagID := mux.Vars(r)["tag_id"]
	if tagID == "" && req.TagID == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No tagID provided",
		})
		return errors.New("no tagID provided")
	}

	req.TagID = tagID
	return nil
}

// ParseToken needed to parse the userId from request header if it exists
func (req *Request) ParseToken(w http.ResponseWriter, r *http.Request) error {
	data := &Claims{}

	tokenString := mux.Vars(r)["token"]

	if tokenString == "" {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: "No token provided",
		})
		return errors.New("no token provided")
	}

	tkn, err := jwt.ParseWithClaims(tokenString, data,
		func(t *jwt.Token) (interface{}, error) {
			return jwtKey, nil
		},
	)

	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   406,
			Description: "The token has expired",
		})
		return err
	}

	if !tkn.Valid {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusUnauthorized,
			Description: "The token has expired, login again",
		})
		return err
	}

	req.UserID = data.UserId
	return nil
}

var jwtKey = []byte("secret_key__DO_NOT_POST_IT_TO_GITHUB")

type Claims struct {
	UserId int `json:"userID"`
	jwt.StandardClaims
}
