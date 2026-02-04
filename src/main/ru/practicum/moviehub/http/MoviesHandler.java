package ru.practicum.moviehub.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.util.MovieUtil;
import ru.practicum.moviehub.validator.MovieValidator;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MoviesHandler extends BaseHttpHandler {

    private final MoviesStore moviesStore;
    private final Gson gson;

    public MoviesHandler(MoviesStore moviesStore) {
        this.gson = new Gson();
        this.moviesStore = moviesStore;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        Endpoint endpoint = getEndpoint(exchange.getRequestURI(), exchange.getRequestMethod());

        switch (endpoint) {
            case Endpoint.GET_MOVIES: {
                handleGetMoviesAll(exchange);
                break;
            }
            case Endpoint.GET_MOVIES_BY_ID: {
                handleGetMoviesById(exchange);
                break;
            }
            case Endpoint.GET_MOVIES_BY_YEAR: {
                handleGetMoviesByYear(exchange);
                break;
            }
            case Endpoint.DELETE_MOVIE_BY_ID: {
                handleDeleteMovieById(exchange);
                break;
            }
            case Endpoint.POST_MOVIE: {
                handlePostMovie(exchange);
                break;
            }
            default:
                sendNoContent(exchange, 405);
        }
    }

    private void handlePostMovie(HttpExchange exchange) throws IOException {

        List<String> contentTypeValues = exchange.getRequestHeaders().get("Content-Type");
        if (contentTypeValues == null || contentTypeValues.isEmpty() || !contentTypeValues.contains("application/json; charset=UTF-8")) {
            sendNoContent(exchange, 415);
            return;
        }

        Optional<Movie> movieOpt = MovieUtil.parseMovie(exchange.getRequestBody());
        if (movieOpt.isEmpty()) {
            sendJson(exchange, 422, gson.toJson(new ErrorResponse("Некорректный JSON")));
            return;
        }

        Movie movie = movieOpt.get();
        Optional<List<String>> valid = MovieValidator.checkMovie(movie);

        if (valid.isPresent()) {
            sendJson(exchange, 422, gson.toJson(new ErrorResponse("Ошибка валидации", valid.get())));
            return;
        }

        moviesStore.saveMovie(movie);
        sendJson(exchange, 201, gson.toJson(movie));

    }


    private void handleDeleteMovieById(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = MovieUtil.getInteger(exchange.getRequestURI().getPath().split("/")[2]);
        if (idOpt.isEmpty()) {
            sendJson(exchange, 400, gson.toJson(new ErrorResponse("Некорректный ID")));
            return;
        }
        Movie movie = moviesStore.findById(idOpt.get());
        if (movie == null) {
            sendJson(exchange, 404, gson.toJson(new ErrorResponse("Фильм не найден")));
            return;
        }
        moviesStore.deleteById(idOpt.get());
        sendNoContent(exchange, 204);
    }

    private void handleGetMoviesById(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = MovieUtil.getInteger(exchange.getRequestURI().getPath().split("/")[2]);
        if (idOpt.isEmpty()) {
            sendJson(exchange, 400, gson.toJson(new ErrorResponse("Некорректный ID")));
            return;
        }
        Movie movie = moviesStore.findById(idOpt.get());
        if (movie == null) {
            sendJson(exchange, 404, gson.toJson(new ErrorResponse("Фильм не найден")));
            return;
        }
        sendJson(exchange, 200, gson.toJson(movie));
    }

    private void handleGetMoviesByYear(HttpExchange exchange) throws IOException {

        Map<String, String> params = MovieUtil.parseQueryParams(exchange);

        if (!params.containsKey("year")) {
            sendJson(exchange, 400, gson.toJson(new ErrorResponse("Некорректный параметр запроса — 'year'")));
            return;
        }

        Optional<Integer> yearOpt = MovieUtil.getInteger(params.get("year"));
        if (yearOpt.isEmpty()) {
            sendJson(exchange, 400, gson.toJson(new ErrorResponse("Некорректный параметр запроса — 'year'")));
            return;
        }

        sendJson(exchange, 200, gson.toJson(moviesStore.findByYear(yearOpt.get())));
    }

    private void handleGetMoviesAll(HttpExchange exchange) throws IOException {
        sendJson(exchange, 200, gson.toJson(moviesStore.findAll()));
    }

    private Endpoint getEndpoint(URI requestURI, String requestMethod) {
        String[] pathParts = requestURI.getPath().split("/");
        String query = requestURI.getQuery();

        if (requestMethod.equalsIgnoreCase("GET"))
            if (pathParts[1].equals("movies"))
                if (pathParts.length == 2)
                    if (query == null)
                        return Endpoint.GET_MOVIES;
                    else
                        return Endpoint.GET_MOVIES_BY_YEAR;
                else if (pathParts.length == 3)
                    return Endpoint.GET_MOVIES_BY_ID;
        if (requestMethod.equalsIgnoreCase("DELETE") && pathParts[1].equals("movies") && pathParts.length == 3)
            return Endpoint.DELETE_MOVIE_BY_ID;
        if (requestMethod.equalsIgnoreCase("POST") && pathParts[1].equals("movies") && pathParts.length == 2)
            return Endpoint.POST_MOVIE;

        return Endpoint.UNKNOWN;
    }

}
