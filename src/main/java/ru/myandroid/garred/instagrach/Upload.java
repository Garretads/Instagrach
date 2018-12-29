package ru.myandroid.garred.instagrach;

public class Upload {

    public String description;
    public String author;
    public String date;
    public String url;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() {
    }

    public Upload(String description, String author, String date, String url) {
        this.description = description;
        this.author = author;
        this.date = date;
        this.url= url;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
