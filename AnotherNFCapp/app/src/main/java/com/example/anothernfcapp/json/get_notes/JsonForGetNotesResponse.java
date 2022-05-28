package com.example.anothernfcapp.json.get_notes;

public class JsonForGetNotesResponse {
    private String note;
    private String time;

    public JsonForGetNotesResponse(String note, String time){
        this.note = note;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Note: " + note + ", time: " + time + "\n";
    }
}
