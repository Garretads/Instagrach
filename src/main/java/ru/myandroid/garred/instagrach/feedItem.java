package ru.myandroid.garred.instagrach;

import android.graphics.Bitmap;

public class feedItem {
    private String author;
    private String date;
    private Bitmap image;

    feedItem(String date, Bitmap image, String author) {
        this.author = author;
        this.date = date;
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }
}
