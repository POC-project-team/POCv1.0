// Package user /* for user data structure */
package user

import (
	"crypto/sha1"
	"encoding/hex"
	log "github.com/sirupsen/logrus"
	"strconv"
	"time"
)

type User struct {
	UserID int
	Tags   map[string]*Tag `json:"tagID"`
}

func (u *User) ToString() string {
	result := strconv.Itoa(u.UserID)
	for _, tag := range u.Tags {
		result += tag.TagID
	}
	return result
}

func _() string {
	h := sha1.New()
	return hex.EncodeToString(h.Sum([]byte(strconv.Itoa(int(time.Now().UnixNano())))))
}

func (u *User) NewTag(id string) {
	if u.Tags == nil {
		u.Tags = make(map[string]*Tag)
	}
	u.Tags[id] = TagInit(id)
	log.Info("New tagID: ", id, " for user_id: ", u.UserID, " was generated")
}
