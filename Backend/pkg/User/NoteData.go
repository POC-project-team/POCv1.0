package user

import (
	"time"
)

type NoteData struct {
	Note     string    `json:"note"`
	NoteTime time.Time `json:"time"`
}

// NewNote constructor for struct note
func NewNote(note string) NoteData {
	return NoteData{note, time.Now()}
}

func (n *NoteData) ToString() string {
	var result string
	result += "Data: " + n.NoteTime.String() + "\nNote: " + n.Note
	return result
}
