// Package user /*
package user

import (
	"crypto/sha1"
	"encoding/hex"
	log "github.com/sirupsen/logrus"
	"math/rand"
)

type User struct {
	UserID string         `json:"userID"`
	Tags   map[int32]*Tag `json:"tagID"`
}

func (u *User) ToString() string {
	result := u.UserID
	for _, tag := range u.Tags {
		result += tag.ToString()
	}
	return result
}

func NewHashUser() string {
	h := sha1.New()
	return hex.EncodeToString(h.Sum(nil))
}

func NewUser() *User {
	return &User{NewHashUser(), nil}
}

func (u *User) NewTag() int32 {
	if u.Tags == nil {
		u.Tags = make(map[int32]*Tag)
	}
	var id int32
	for _, ok := u.Tags[id]; ok; {
		id = rand.Int31()
	}
	log.Info("New id: ", id, " was generated")
	u.Tags[id] = TagInit(id)

	return id
}
