package DB

import (
	"backend/pkg/Service"
	"encoding/json"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
)

func WriteJSONFile(fileName string, srv service.Service) {
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
