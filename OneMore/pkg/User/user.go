// Package user /*
package user

import (
	"crypto/sha1"
	"encoding/hex"
	"math/rand"
)

type User struct {
	UserID string        `json:"userID"`
	Tags   map[int32]Tag `json:"tagID"`
}

func (u *User) ToString() string {
	result := u.UserID
	for _, tag := range u.Tags {
		result += tag.ToString()
	}
	return result
}

func (u *User) NewTagID() int32 {
	var id int32
	for _, ok := u.Tags[id]; ok == true; id = rand.Int31() {}
	return id
}

func NewHashUser() string {
	h := sha1.New()
	return hex.EncodeToString(h.Sum(nil))
}

func NewUser() *User {
	return &User{NewHashUser(), nil}
}

func (u *User) NewTag() {
	id := u.NewTagID()
	u.Tags[id] = *TagInit(id)
}