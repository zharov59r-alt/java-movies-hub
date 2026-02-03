package ru.practicum.moviehub.validator;

import ru.practicum.moviehub.model.Movie;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieValidator {

    public static Optional<List<String>> checkMovie(Movie movie) {

        List<String> errors = new ArrayList<>();

        if (movie.getTitle() == null)
            errors.add("название не должно быть пустым");

        if (movie.getTitle().length() > 100)
            errors.add("название не должно превышать 100 символов");

        if (movie.getYear() > 1888 && LocalDate.now().getYear() + 1 > movie.getYear())
            errors.add("год должен быть между 1888 и 2026");

        if (errors.isEmpty())
            return Optional.empty();
        else
            return Optional.of(errors);

    }

}
