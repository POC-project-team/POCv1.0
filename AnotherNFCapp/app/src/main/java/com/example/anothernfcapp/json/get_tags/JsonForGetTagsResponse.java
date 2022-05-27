package com.example.anothernfcapp.json.get_tags;

public class JsonForGetTagsResponse {
    private String tagId;
    private String tagName;

    public JsonForGetTagsResponse(String tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return "Tag:\n" +
                "tagId = " + tagId + '\n' +
                "tagName = " + tagName;
    }
}
