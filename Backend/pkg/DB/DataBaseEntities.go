package DB

import "time"

type User struct {
	UserId int
}

type Tag struct {
	UserId int
	TagId  string
}

type Note struct {
	Note string
	Time time.Time
}
