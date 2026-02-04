package ru.practicum.moviehub.validator;

import ru.practicum.moviehub.model.Movie;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieValidator {

    public static Optional<List<String>> checkMovie(Movie movie) {

        List<String> errors = new ArrayList<>();

        if (movie.getTitle() == null || movie.getTitle().isEmpty())
            errors.add("название не должно быть пустым");
        else if (movie.getTitle().length() > 100)
            errors.add("название не должно превышать 100 символов");

        if (movie.getYear() == null)
            errors.add("год не должен быть пустым");
        else if ( 1888 > movie.getYear() || movie.getYear() > LocalDate.now().getYear() + 1 )
            errors.add("год должен быть между 1888 и 2026");

        if (errors.isEmpty())
            return Optional.empty();
        else
            return Optional.of(errors);

    }

}
