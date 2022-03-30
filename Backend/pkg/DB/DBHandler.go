// Package DB /* for working with data base */
package DB

import (
	u "backend/pkg/User"
	"encoding/json"
	"errors"
	"fmt"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"math/rand"
	"strconv"
)

// WriteJSONFile for saving data to file
func WriteJSONFile(fileName string, srv *DB) {
	file, err := json.MarshalIndent(srv.Store, "", "	")
	if err != nil {
		log.Error("Cannot read data from server to JSON file: ", fileName)
	}
	err = ioutil.WriteFile(fileName, file, 0644)
	if err != nil {
		log.Error("Cannot write data to JSON file: ", fileName)
	}
}

// ReadJSONFile for dumping the data from file to RAM
func ReadJSONFile(fileName string) []byte {
	data, err := ioutil.ReadFile(fileName)
	if err != nil {
		log.Error("Cannot read data from JSON file: ", fileName)
		return []byte("{}")
	}
	return data
}

// DB structure for storing data
type DB struct {
	Store map[int]*u.User
}

// NewDataBase init new object of database and dumps data from file
func NewDataBase() *DB {
	var srv DB
	if err := json.Unmarshal(ReadJSONFile("pkg/DB/Data.json"), &srv.Store); err != nil {
		log.Error("Cannot read data from file")
	}
	return &srv
	//return &Service{make(map[int]*u.User)}
}

// SaveData to file from RAM
func (dataBase *DB) SaveData() {
	WriteJSONFile("pkg/DB/Data.json", dataBase)
}

// generates newId() for user
func (dataBase *DB) newId() int {
	var id int
	for dataBase.Store[id+1] != nil {
		id = rand.Int()
	}
	return id + 1
}

// check if the map Contains the specific user
func (dataBase *DB) containsUser(userId int) bool {
	if _, ok := dataBase.Store[userId]; !ok {
		return false
	}
	return true
}

// check if the map Contains the specific user
func (dataBase *DB) containsTag(userId int, tagId string) bool {
	if _, ok := dataBase.Store[userId].Tags[tagId]; !ok {
		return false
	}
	return true
}

// GetAllUsers to get all users from database
func (dataBase *DB) GetAllUsers() []string {
	sqlTest := GetAllUsersSQL()
	fmt.Println(sqlTest)

	var response []string
	for id := range dataBase.Store {
		response = append(response, strconv.Itoa(id))
	}
	return response
}

// CreateUser creates new user, via searching for new id
func (dataBase *DB) CreateUser() *u.User {
	id := dataBase.newId()
	dataBase.Store[id] = u.NewUser(id)

	log.Info("New user: ", id)

	return dataBase.Store[id]
}

// GetUserTags returns all user's tags
func (dataBase *DB) GetUserTags(userId int) ([]string, error) {
	if !(dataBase.containsUser(userId)) {
		return nil, errors.New("no user found")
	}

	var response []string

	for tag := range dataBase.Store[userId].Tags {
		response = append(response, tag)
	}

	return response, nil
}

// GetUserNotes returns all users notes, returns error if no user or no tags
func (dataBase *DB) GetUserNotes(userId int, tagId string) ([]u.Note, error) {
	if !(dataBase.containsUser(userId)) {
		return nil, errors.New("no user found")
	}

	if !(dataBase.containsTag(userId, tagId)) {
		return nil, errors.New("user has no notes on this tag")
	}

	return dataBase.Store[userId].Tags[tagId].Notes, nil
}

// AddNote returns error if no user contains, writes new note and return all user notes
func (dataBase *DB) AddNote(userId int, tagId, note string) ([]u.Note, error) {
	if !(dataBase.containsUser(userId)) {
		return nil, errors.New("no such user")
	}

	if len(note) == 0 {
		return nil, errors.New("field 'note' has no data")
	}

	if dataBase.Store[userId].Tags[tagId] == nil {
		dataBase.Store[userId].NewTag(tagId)
	}

	dataBase.Store[userId].Tags[tagId].AddNoteTag(note)

	return dataBase.GetUserNotes(userId, tagId)
}
