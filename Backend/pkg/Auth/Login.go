package Auth

import (
	"backend/pkg/APIerror"
	"backend/pkg/DB"
	service "backend/pkg/Service"
	"encoding/json"
	"github.com/dgrijalva/jwt-go"
	log "github.com/sirupsen/logrus"
	"net/http"
	"time"
)

var jwtKey = []byte("secret_key__DO_NOT_POST_IT_TO_GITHUB")

type Claims struct {
	UserId int `json:"userID"`
	jwt.StandardClaims
}

type Token struct {
	JWTToken string `json:"token"`
}

// Auth generates the token for the user
func Auth(w http.ResponseWriter, r *http.Request) {
	var response service.Request

	if err := response.Bind(w, r); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: err.Error(),
		})
		return
	}

	UserID, err := DB.NewSQLDataBase().GetUserID(response.Login, response.Password)
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusBadRequest,
			Description: err.Error(),
		})
		return
	}

	claims := Claims{
		UserId: UserID,
		StandardClaims: jwt.StandardClaims{
			ExpiresAt: time.Now().Add(time.Hour).Unix(),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)

	var answer Token
	answer.JWTToken, err = token.SignedString(jwtKey)
	if err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: err.Error(),
		})
		return
	}

	if err = json.NewEncoder(w).Encode(answer); err != nil {
		APIerror.HTTPErrorHandle(w, APIerror.HTTPErrorHandler{
			ErrorCode:   http.StatusInternalServerError,
			Description: "Cannot make token",
		})
	} else {
		log.Info("New token was created for user ", UserID)
		w.WriteHeader(http.StatusCreated)
	}
}
