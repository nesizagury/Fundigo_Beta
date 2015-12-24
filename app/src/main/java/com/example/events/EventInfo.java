package com.example.events;

public class EventInfo {

    int imageId;
    String date;
    String name;
    String tags;
    String price;
    String info;

    public EventInfo(int imageId, String date, String name, String tags, String price, String info) {
        this.imageId = imageId;
        this.date = date;
        this.name = name;
        this.tags = tags;
        this.price = price;
        this.info = info;
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
