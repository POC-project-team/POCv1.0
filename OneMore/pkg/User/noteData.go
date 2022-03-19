package user

import (
	"time"
)

type NoteData struct {
	Note     string
	NoteTime time.Time
}

// AddNote constructor for struct note
func NewNote(note string) NoteData {
	return NoteData{note, time.Now()}
}
