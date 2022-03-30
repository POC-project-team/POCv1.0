package DB

import (
	u "backend/pkg/User"
	"database/sql"
	"errors"
	_ "github.com/mattn/go-sqlite3"
	log "github.com/sirupsen/logrus"
	"strconv"
	"time"
)

type SQL struct {
	Store *sql.DB
}

func openDataBase() *sql.DB {
	db, err := sql.Open("sqlite3", "pkg/DB/StorageData.db")
	if err != nil {
		log.Error(err.Error())
	}
	return db
}

func NewSQLDataBase() *SQL {
	var database SQL
	database.Store = openDataBase()
	return &database
}

func (database *SQL) containsUser(userId int) bool {
	rows, err := database.Store.Query(`select count(UserId) from users where UserID = ?`, userId)
	if err != nil {
		log.Error(err.Error())
	}
	var contain int
	for rows.Next() {
		rows.Scan(&contain)
	}
	if contain == 0 {
		return false
	}
	return true
}

func (database *SQL) containsTag(tagID string) bool {
	rows, err := database.Store.Query(`select count(TagID) from Tags where TagID = ?`, tagID)
	if err != nil {
		log.Error(err.Error())
	}
	var contain int
	for rows.Next() {
		rows.Scan(&contain)
	}
	if contain == 0 {
		return false
	}
	return true
}

func (database *SQL) GetAllUsers() []string {
	rows, err := database.Store.Query(`select * from "Users"`)
	if err != nil {
		log.Error(err.Error())
		return nil
	}

	var UserID int
	var items []u.User

	for rows.Next() {
		rows.Scan(&UserID)

		items = append(items, u.User{
			UserID: UserID,
		})
	}

	// todo check if there many of this
	var response []string
	for _, id := range items {
		response = append(response, strconv.Itoa(id.UserID))
	}
	return response
}

func (database *SQL) CreateUser() u.User {
	rows, err := database.Store.Query(`select count(UserID) from Users`)
	if err != nil {
		log.Error(err.Error())
		return u.User{}
	}
	var userId int

	for rows.Next() {
		rows.Scan(&userId)
	}
	stmt, err := database.Store.Prepare(`insert into Users (UserID) values (?)`)
	if err != nil {
		log.Error(err.Error())
		return u.User{}
	}
	stmt.Exec(userId)
	log.Info("New user: ", userId)
	return u.User{
		UserID: userId,
	}
}

func (database *SQL) GetUserTags(userId int) ([]string, error) {
	if !database.containsUser(userId) {
		return nil, errors.New("no such user")
	}
	rows, err := database.Store.Query(`select TagId from Tags where UserID = ?`, userId)
	if err != nil {
		log.Error(err.Error())
		return nil, err
	}
	var tagID string
	var result []u.Tag

	for rows.Next() {
		rows.Scan(&tagID)

		result = append(result, u.Tag{
			UserID: userId,
			TagID:  tagID,
		})
	}

	var response []string
	for _, tag := range result {
		response = append(response, tag.TagID)
	}
	return response, nil
}

func (database *SQL) GetUserNotes(userId int, tagId string) ([]u.Note, error) {
	if !database.containsUser(userId) {
		return nil, errors.New("no such user")
	}

	if !database.containsTag(tagId) {
		return nil, errors.New("no such tag")
	}

	rows, err := database.Store.Query(`select Note, Data from Note where TagID = ?`, tagId)
	if err != nil {
		return nil, err
	}
	var note u.Note
	var result []u.Note

	for rows.Next() {
		rows.Scan(&note.Note, &note.Time)
		result = append(result, note)
	}
	return result, nil
}

func (database *SQL) AddNote(userId int, tagId, note string) ([]u.Note, error) {
	if !database.containsUser(userId) {
		return nil, errors.New("no such user")
	}

	if !database.containsTag(tagId) {
		stmt, err := database.Store.Prepare(`insert into Tags (UserID, TagID)  values (?, ?)`)
		if err != nil {
			log.Error(err.Error())
		}

		stmt.Exec(userId, tagId)
	}

	stmt, err := database.Store.Prepare(`insert into Note (TagID, Note, Data)  values (?, ?, ?)`)
	if err != nil {
		log.Error(err.Error())
	}

	stmt.Exec(tagId, note, time.Now())

	return database.GetUserNotes(userId, tagId)
}
