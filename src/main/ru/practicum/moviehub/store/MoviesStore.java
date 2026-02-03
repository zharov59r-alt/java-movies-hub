package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.*;

public class MoviesStore {

    private Map<Integer, Movie> store = new HashMap<>();
    private Integer sequence = 1;

    public int addMovie(String title, int year) {
        int id = sequence++;
        store.put(id, new Movie(title, year));
        return id;
    }

    public void deleteById(Integer id) {
        store.remove(id);
    }

    public Collection<Movie> findAdd() {
        return store.values();
    }

    public Movie findById(Integer id) {
        return store.get(id);
    }

    public List<Movie> findByYear(Integer year) {

        List<Movie> movies = new ArrayList<>();

        for (Movie movie: store.values())
            if (movie.getYear() == year)
                movies.add(movie);

        return movies;
    }


    public void clearStore() {
        sequence = 1;
        store = new HashMap<>();
    }
}