package com.example.events;

public class EventInfo {

    int imageId;
    String date;
    String name;
    String tags;



    public EventInfo(int imageId, String  date, String name, String tags){

        this.imageId = imageId;
        this.date = date;
        this.name = name;
        this.tags = tags;


    }

    public int getImageId() {
        return imageId;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

}
