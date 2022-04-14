// Package user /* for user data structure */
package user

import (
	"strings"
)

// struct for tag and working with it

type Tag struct {
	//UserID int
	TagID   string `json:"tagID"`
	TagName string `json:"tagName"`
	Notes   []Note `json:"userNotes"`
}

func TagInit(TagID string) *Tag {
	return &Tag{TagID: TagID}
}

func (t *Tag) AddNoteTag(note string) {
	if t.Notes == nil {
		t.Notes = make([]Note, 0)
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
