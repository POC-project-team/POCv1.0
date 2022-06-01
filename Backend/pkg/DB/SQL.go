package DB

import (
	u "backend/pkg/User"
	"database/sql"
	"errors"
	"strconv"
	"time"

	_ "github.com/mattn/go-sqlite3"
	log "github.com/sirupsen/logrus"
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

func (database *SQL) Close() error {
	err := database.Store.Close()
	if err != nil {
		log.Error(err.Error())
		return errors.New("error closing database")
	}
	return nil
}

func (database *SQL) Open() {
	database.Store = openDataBase()
}

// NewSQLDataBase creates the database and connects to it
func NewSQLDataBase() *SQL {
	var database SQL
	return &database
}

func (database *SQL) containsUser(userId int) bool {
	rows, err := database.Store.Query(`select count(UserId) from users where UserID = ?`, userId)
	if err != nil {
		log.Error(err.Error())
	}
	var contain int
	for rows.Next() {
		_ = rows.Scan(&contain)
	}
	if contain == 0 {
		return false
	}
	return true
}

func (database *SQL) containsTag(userID int, tagID string) bool {
	rows, err := database.Store.Query(`select count(TagID) from Tags where TagID = ? and UserID = ?`,
		tagID, userID)
	if err != nil {
		log.Error(err.Error())
	}
	var contain int
	for rows.Next() {
		_ = rows.Scan(&contain)
	}
	if contain == 0 {
		return false
	}
	return true
}

func (database *SQL) tagStoresNotes(userID int, tagID string) bool {
	rows, err := database.Store.Query(`select count(TagID) from Notes where TagID = ? and UserID = ?`,
		tagID, userID)
	if err != nil {
		log.Error(err.Error())
	}
	var contain int
	for rows.Next() {
		_ = rows.Scan(&contain)
	}
	if contain == 0 {
		return false
	}
	return true
}

func (database *SQL) containsLogin(login string) bool {
	rows, err := database.Store.Query(`select count(Login) from users where Login = ?`, login)
	if err != nil {
		log.Error(err.Error())
	}
	var contain int
	for rows.Next() {
		_ = rows.Scan(&contain)
	}
	if contain == 0 {
		return false
	}
	return true
}

func (database *SQL) containsTagByLogin(login string, tagID string) bool {
	rows, err := database.Store.Query(`select count(TagID) from Tags where TagID = ? and UserID = (select UserID from users where Login = ?)`,
		tagID, login)
	if err != nil {
		log.Error(err.Error())
	}
	var contain int
	for rows.Next() {
		_ = rows.Scan(&contain)
	}
	if contain == 0 {
		return false
	}
	return true
}

// GetUserID return UserID, if user with such login and password exist
func (database *SQL) GetUserID(login, password string) (int, error) {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	// get login
	rows, err := database.Store.Query(`
	select count(UserID) from Users where Login = ?;
	`, login)

	if err != nil {
		return 0, err
	}

	var cnt, UserID int

	for rows.Next() {
		err = rows.Scan(&cnt)
	}

	if cnt == 0 {
		return 0, errors.New("no such user")
	}

	rows, err = database.Store.Query(`
	select count(UserID), UserID from Users where Login = ? and Password = ?;
	`, login, password)

	if err != nil {
		return 0, err
	}

	for rows.Next() {
		err = rows.Scan(&cnt, &UserID)
		// if no userID provided makes error with parsing NULL to int
		if err != nil {
			return 0, errors.New("login or password is incorrect")
		}
	}

	return UserID, nil
}

// GetAllUsers for getting all users from database
// Return []string for answering the request and error status
func (database *SQL) GetAllUsers() ([]string, error) {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	rows, err := database.Store.Query(`select UserID from "Users"`)
	if err != nil {
		return nil, err
	}

	var UserID int
	var items []u.User

	for rows.Next() {
		err = rows.Scan(&UserID)
		if err != nil {
			return nil, err
		}

		items = append(items, u.User{
			UserID: UserID,
		})
	}

	var response []string
	for _, id := range items {
		response = append(response, strconv.Itoa(id.UserID))
	}
	return response, nil
}

// CreateUser creates a user in db
// Returns created user and error status
func (database *SQL) CreateUser(login, password string) (u.User, error) {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	rows, err := database.Store.Query(`select count(UserID) from Users`)
	if err != nil {
		return u.User{}, err
	}
	var userId int

	for rows.Next() {
		err = rows.Scan(&userId)
		if err != nil {
			return u.User{}, err
		}
	}

	var cnt int

	rows, err = database.Store.Query(`select count(Login) from Users where Login = ?`, login)
	if err != nil {
		return u.User{}, err
	}

	for rows.Next() {
		err = rows.Scan(&cnt)
		if err != nil {
			return u.User{}, err
		}
	}

	if cnt != 0 {
		return u.User{}, errors.New("user with such login exists")
	}

	stmt, err := database.Store.Prepare(`insert into Users (UserID, Login, Password) values (?, ?, ?)`)
	if err != nil {
		return u.User{}, err
	}
	_, err = stmt.Exec(userId, login, password)
	if err != nil {
		return u.User{}, err
	}

	log.Info("New user: ", userId)

	return u.User{
		UserID: userId,
	}, nil
}

// ChangeLogin change login of user
func (database *SQL) ChangeLogin(userId int, login string) error {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	stmt, err := database.Store.Prepare(`update Users set Login = ? where UserID = ?`)
	if err != nil {
		return err
	}
	_, err = stmt.Exec(login, userId)
	if err != nil {
		return err
	}
	return nil
}

// ChangePassword change password of user
func (database *SQL) ChangePassword(userId int, password string) error {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	stmt, err := database.Store.Prepare(`update Users set Password = ? where UserID = ?`)
	if err != nil {
		return err
	}
	_, err = stmt.Exec(password, userId)
	if err != nil {
		return err
	}
	return nil
}

type TagNoUserNotes struct {
	TagID   string `json:"tagID"`
	TagName string `json:"tagName"`
}

// GetUserTags get all tags from specific user
// Return []string for answering the request and error status
func (database *SQL) GetUserTags(userId int) ([]TagNoUserNotes, error) {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	if !database.containsUser(userId) {
		return nil, errors.New("no such user")
	}
	rows, err := database.Store.Query(`select distinct TagId, TagName from Tags where UserID = ?`, userId)
	if err != nil {
		log.Error(err.Error())
		return nil, err
	}
	var tagID, tagName string
	var result []TagNoUserNotes

	for rows.Next() {
		err = rows.Scan(&tagID, &tagName)
		if err != nil {
			return nil, err
		}

		result = append(result, TagNoUserNotes{
			TagID:   tagID,
			TagName: tagName,
		})
	}
	return result, nil
}

func (database *SQL) GetTag(userId int, tagId string) (TagNoUserNotes, error) {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	if !database.containsUser(userId) {
		return TagNoUserNotes{}, errors.New("no such user")
	}
	rows, err := database.Store.Query(`select TagID, TagName from Tags where UserID = ? and TagId = ?`, userId, tagId)
	if err != nil {
		return TagNoUserNotes{}, err
	}
	var tag TagNoUserNotes
	for rows.Next() {
		err = rows.Scan(&tag.TagID, &tag.TagName)
		if err != nil {
			return TagNoUserNotes{}, err
		}
	}
	return tag, nil
}

func (database *SQL) UpdateTag(userId int, tagId string, tagName string) (TagNoUserNotes, error) {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	if !database.containsUser(userId) {
		return TagNoUserNotes{}, errors.New("no such user")
	}
	stmt, err := database.Store.Prepare(`update Tags set TagName = ? where UserID = ? and TagId = ?`)
	if err != nil {
		return TagNoUserNotes{}, err
	}
	_, err = stmt.Exec(tagName, userId, tagId)
	if err != nil {
		return TagNoUserNotes{}, err
	}

	rows, err := database.Store.Query(`select TagID, TagName from Tags where UserID = ? and TagId = ?`, userId, tagId)

	var tag TagNoUserNotes
	for rows.Next() {
		err = rows.Scan(&tag.TagID, &tag.TagName)
		if err != nil {
			return TagNoUserNotes{}, err
		}
	}
	return tag, nil
}

func (database *SQL) CreateTag(userId int, tagId, tagName string) (TagNoUserNotes, error) {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	if !database.containsUser(userId) {
		return TagNoUserNotes{}, errors.New("no such user")
	}

	if database.containsTag(userId, tagId) {
		return TagNoUserNotes{}, errors.New("user already has such tag")
	}

	stmt, err := database.Store.Prepare(`insert into Tags (UserID, TagID, TagName) values (?, ?, ?)`)
	if err != nil {
		return TagNoUserNotes{}, err
	}
	_, err = stmt.Exec(userId, tagId, tagName)
	if err != nil {
		return TagNoUserNotes{}, err
	}

	log.Info("New tag: ", tagId)
	return TagNoUserNotes{
		TagID:   tagId,
		TagName: tagName,
	}, nil
}

func (database *SQL) DeleteTag(userId int, tagId string) error {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	if !database.containsUser(userId) {
		return errors.New("no such user")
	}
	if !database.containsTag(userId, tagId) {
		return errors.New("user doesn't have such tag")
	}

	stmt, err := database.Store.Prepare(`delete from Tags where UserID = ? and TagId = ?`)
	if err != nil {
		return err
	}
	_, err = stmt.Exec(userId, tagId)
	if err != nil {
		return err
	}

	stmt, err = database.Store.Prepare(`delete from Notes where UserID = ? and TagId = ?`)
	if err != nil {
		return err
	}
	_, err = stmt.Exec(userId, tagId)
	if err != nil {
		return err
	}
	return nil
}

func (database *SQL) TransferTag(userId int, tagId, login string) error {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	if !database.containsUser(userId) {
		return errors.New("no such user")
	}

	if !database.containsTag(userId, tagId) {
		return errors.New("user doesn't have such tag")
	}

	if !database.containsLogin(login) {
		return errors.New("no such user")
	}

	if database.containsTagByLogin(login, tagId) {
		return errors.New("user already has such tag")
	}

	stmt, err := database.Store.Prepare(`update Tags set UserID = (select UserID from users where Login = ?) where UserID = ? and TagID = ?`)
	if err != nil {
		return err
	}

	if _, err = stmt.Exec(login, userId, tagId); err != nil {
		return err
	}
	// update usedid in notes
	stmt, err = database.Store.Prepare(`update Notes set UserID = (select UserID from users where Login = ?) where UserID = ? and TagID = ?`)
	if err != nil {
		return err
	}
	if _, err = stmt.Exec(login, userId, tagId); err != nil {
		return err
	}

	return nil
}

// GetUserNotes get user notes from tag
// Return []string for answering the request and error status
func (database *SQL) GetUserNotes(userId int, tagId string) ([]u.Note, error) {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	if !database.containsUser(userId) {
		return nil, errors.New("no such user")
	}

	if !database.containsTag(userId, tagId) {
		return nil, errors.New("user doesn't have such tag")
	}

	if !database.tagStoresNotes(userId, tagId) {
		return nil, errors.New("user has no notes in such tag")
	}

	rows, err := database.Store.Query(
		`select note, data from Notes where UserID = ? and notes.TagID = ?`, userId, tagId)
	if err != nil {
		return nil, err
	}
	var note u.Note
	var result []u.Note

	for rows.Next() {
		err = rows.Scan(&note.Note, &note.Time)
		if err != nil {
			return nil, err
		}
		result = append(result, note)
	}
	return result, nil
}

// AddNote creates a new note for tag
// Creates tag if not exist
// Return TagNoUserNotes object and error status
func (database *SQL) AddNote(userId int, tagId, noteInfo string) (u.Tag, error) {
	database.Open()
	defer func(database *SQL) {
		err := database.Close()
		if err != nil {
			log.Error(err.Error())
		}
	}(database)
	if !database.containsUser(userId) {
		return u.Tag{}, errors.New("no such user")
	}

	stmt, err := database.Store.Prepare(`insert into Notes (UserID, TagID, Note, Data)  values (?, ?, ?, ?)`)
	if err != nil {
		return u.Tag{}, err
	}

	if _, err = stmt.Exec(userId, tagId, noteInfo, time.Now()); err != nil {
		return u.Tag{}, err
	}

	rows, err := database.Store.Query(
		`select note, data from Notes where UserID = ? and notes.TagID = ?`, userId, tagId)
	if err != nil {
		return u.Tag{}, err
	}

	var note u.Note
	var result u.Tag

	result.TagID = tagId
	result.TagName, err = database.getTagName(userId, tagId)

	if err != nil {
		return u.Tag{}, errors.New("cannot parse tag name")
	}

	for rows.Next() {
		err = rows.Scan(&note.Note, &note.Time)
		if err != nil {
			return u.Tag{}, err
		}
		result.Notes = append(result.Notes, note)
	}

	return result, nil
}

func (database *SQL) getTagName(userId int, tagId string) (string, error) {
	if !database.containsUser(userId) {
		return "", errors.New("no such user")
	}

	if !database.containsTag(userId, tagId) {
		stmt, err := database.Store.Prepare(`insert into Tags (UserID, TagID, TagName) values (?, ?, ?)`)
		if err != nil {
			return "", err
		}
		if _, err = stmt.Exec(userId, tagId, "Untitled"); err != nil {
			return "", err
		}
		return "Untitled", nil
	}

	rows, err := database.Store.Query(
		`select TagName from Tags where UserID = ? and TagID = ?`, userId, tagId)
	if err != nil {
		return "", err
	}

	var result string

	for rows.Next() {
		err = rows.Scan(&result)
		if err != nil {
			return "", err
		}
	}

	return result, nil
}
