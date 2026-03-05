package ru.practicum.moviehub.model;

public class Movie {

    private Integer id;
    private String title;
    private Integer year;

    public Movie(String title, Integer year) {
        this.title = title;
        this.year = year;
    }

    public Movie(Integer id, String title, Integer year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "Id=" + id +
                ", title='" + title + '\'' +
                ", year=" + year +
                '}';
    }
}