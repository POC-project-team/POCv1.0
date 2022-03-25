package DB

import (
	u "backend/pkg/User"
	"encoding/json"
	"errors"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"math/rand"
	"strconv"
)

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

func ReadJSONFile(fileName string) []byte {
	data, err := ioutil.ReadFile(fileName)
	if err != nil {
		log.Error("Cannot read data from JSON file: ", fileName)
		return []byte("{}")
	}
	return data
}

type DB struct {
	Store map[int]*u.User
}

func NewDataBase() *DB {
	var srv DB
	if err := json.Unmarshal(ReadJSONFile("pkg/DB/Data.json"), &srv.Store); err != nil {
		log.Error("Cannot read data from file")
	}
	return &srv
	//return &Service{make(map[int]*u.User)}
}

func (dataBase *DB) SaveData() {
	WriteJSONFile("pkg/DB/Data.json", dataBase)
}

// Id generator
func (dataBase *DB) newId() int {
	var id int
	for dataBase.Store[id+1] != nil {
		id = rand.Int()
	}
	return id + 1
}

// ContainsUser Contains check if the map Contains the specific user
func (dataBase *DB) ContainsUser(userId int) bool {
	if _, ok := dataBase.Store[userId]; !ok {
		return false
	}
	return true
}

// ContainsTag Contains check if the map Contains the specific user
func (dataBase *DB) ContainsTag(userId int, tagId string) bool {
	if _, ok := dataBase.Store[userId].Tags[tagId]; !ok {
		return false
	}
	return true
}

func (dataBase *DB) AllUsers() []string {
	var response []string
	for id := range dataBase.Store {
		response = append(response, strconv.Itoa(id))
	}
	return response
}

func (dataBase *DB) CreateUser() *u.User {
	id := dataBase.newId()
	dataBase.Store[id] = u.NewUser(id)

	log.Info("New user: ", id)

	return dataBase.Store[id]
}

func (dataBase *DB) GetUserTags(userId int) ([]string, error) {
	if !(dataBase.ContainsUser(userId)) {
		return nil, errors.New("no user found")
	}

	var response []string

	for tag := range dataBase.Store[userId].Tags {
		response = append(response, tag)
	}

	return response, nil
}

func (dataBase *DB) GetUserNotes(userId int, tagId string) ([]u.Note, error) {
	if !(dataBase.ContainsUser(userId)) {
		return nil, errors.New("no user found")
	}

	if !(dataBase.ContainsTag(userId, tagId)) {
		return nil, errors.New("user has no tags")
	}

	return dataBase.Store[userId].Tags[tagId].Notes, nil
}

func (dataBase *DB) AddNote(userId int, tagId, note string) ([]u.Note, error) {
	if !(dataBase.ContainsUser(userId)) {
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
