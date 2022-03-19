package user

import (
	"strconv"
	"strings"
)

// struct for tag and working with it

type Tag struct {
	TagID int32
	Notes []NoteData
}

func TagInit(TagID int32) *Tag {
	return &Tag{TagID: TagID}
}

/*
func TagInitNote(TagID int32, note string) *Tag {
	tag := TagInit(TagID)
	tag.AddNoteTag(note)
	return tag
}
*/

func (t *Tag) AddNoteTag(note string) {
	t.Notes = append(t.Notes, NewNote(note))
}

func (t *Tag) ToString() string {
	return strconv.Itoa(int(t.TagID))
}

func (t Tag) GetNotes() string {
	result := ""
	for _, note := range t.Notes {
		result += note.ToString() + "\n\n"
	}
	result = strings.TrimSuffix(result, "\n\n")
	return result
}
