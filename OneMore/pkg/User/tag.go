package user

import "strconv"

// struct for tag and working with it

type Tag struct {
	TagID int32
	Notes []NoteData
}

func TagInit(TagID int32) *Tag {
	return &Tag{TagID: TagID}
}

func TagInitNote(TagID int32, note string) *Tag {
	tag := TagInit(TagID)
	tag.AddNoteTag(note)
	return tag
}

func (t *Tag) AddNoteTag(note string) {
	t.Notes = append(t.Notes, NewNote(note))
}

func (t *Tag) ToString() string {
	return strconv.Itoa(int(t.TagID))
}
