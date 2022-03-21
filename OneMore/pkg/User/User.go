// Package user /*
package user

import (
	"crypto/sha1"
	"encoding/hex"
	log "github.com/sirupsen/logrus"
	"math/rand"
	"strconv"
	"time"
)

type User struct {
	UserID int          `json:"userID"`
	Tags   map[int]*Tag `json:"tagID"`
}

func (u *User) ToString() string {
	result := strconv.Itoa(u.UserID)
	for _, tag := range u.Tags {
		result += tag.ToString()
	}
	return result
}

func NewHashUser() string {
	h := sha1.New()
	return hex.EncodeToString(h.Sum([]byte(strconv.Itoa(int(time.Now().UnixNano())))))
}

func NewUser(userID int) *User {
	//return &User{NewHashUser(), nil}
	return &User{userID, nil}
}

func (u *User) NewTag() int {
	if u.Tags == nil {
		u.Tags = make(map[int]*Tag)
	}
	var id int
	for u.Tags[id] != nil {
		id = rand.Int()
	}
	log.Info("New tagID: ", id, " for user_id: ", u.UserID, " was generated")
	u.Tags[id] = TagInit(id)

	return id
}
