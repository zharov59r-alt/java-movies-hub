package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.*;

public class MoviesStore {

    private Map<Integer, Movie> store = new HashMap<>();
    private Integer sequence = 1;

    public void saveMovie(Movie movie) {
        int id = sequence++;
        movie.setId(id);
        store.put(id, movie);
    }

    public void deleteById(Integer id) {
        store.remove(id);
    }

    public Collection<Movie> findAll() {
        return store.values();
    }

    public Movie findById(Integer id) {
        return store.get(id);
    }

    public List<Movie> findByYear(Integer year) {

        List<Movie> movies = new ArrayList<>();

        for (Movie movie: store.values())
            if (Objects.equals(movie.getYear(), year))
                movies.add(movie);

        return movies;
    }


    public void clearStore() {
        sequence = 1;
        store = new HashMap<>();
    }
}