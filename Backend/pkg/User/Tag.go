package user

import (
	"strings"
)

// struct for tag and working with it

type Tag struct {
	TagID string     `json:"tagID"`
	Notes []NoteData `json:"userNotes"`
}

func TagInit(TagID string) *Tag {
	return &Tag{TagID: TagID}
}

func (t *Tag) AddNoteTag(note string) {
	if t.Notes == nil {
		t.Notes = make([]NoteData, 0)
	}
	t.Notes = append(t.Notes, NewNote(note))
}

func (t Tag) GetNotes() string {
	result := ""
	for _, note := range t.Notes {
		result += note.ToString() + "\n\n"
	}
	result = strings.TrimSuffix(result, "\n\n")
	return result
}
