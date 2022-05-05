package com.example.wytwornia;

import javafx.beans.property.SimpleStringProperty;

public class Film {
    private String title;
    private String director;
    private String genre;
    private float price;

    public Film(String title, String director, String genre, float price) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.price = price;
    }

    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
}