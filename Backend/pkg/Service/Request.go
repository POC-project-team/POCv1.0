// Package service /* logic for requesting */
package service

import (
	"backend/pkg/APIerror"
	"encoding/json"
	"errors"
	"io"
	"io/ioutil"
	"net/http"
)

type Request struct {
	UserID int    `json:"userID"`
	TagID  string `json:"tagID"`
	Note   string `json:"note"`
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
			ErrorCode:   http.StatusBadRequest,
			Description: "Cannot parse data from json",
		})
		return errors.New("cannot parse data form JSON")
	}

	return nil
}
