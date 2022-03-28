package APIerror

import (
	log "github.com/sirupsen/logrus"
	"net/http"
)

type HTTPErrorHandler struct {
	ErrorCode   int
	Description string
}

func HTTPErrorHandle(w http.ResponseWriter, err HTTPErrorHandler) {
	w.WriteHeader(err.ErrorCode)
	// If the Error is on server, then log it
	if err.ErrorCode == http.StatusInternalServerError {
		log.Error(err.Description)
	}
	_, err1 := w.Write([]byte(err.Description))
	if err1 != nil {
		return
	}
	return
}
