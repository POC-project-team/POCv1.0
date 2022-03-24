package user

import (
	"time"
)

type Note struct {
	Note     string    `json:"note"`
	NoteTime time.Time `json:"time"`
}

// NewNote constructor for struct note
func NewNote(note string) Note {
	return Note{note, time.Now()}
}

func (n *Note) ToString() string {
	var result string
	result += "Data: " + n.NoteTime.String() + "\nNote: " + n.Note
	return result
}
