package ru.practicum.moviehub.http;

import com.google.gson.*;
import org.junit.jupiter.api.*;
import ru.practicum.moviehub.model.Movie;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class GetMoviesTest extends MoviesApiTest {

    @Test
    void getMovies_whenEmpty_returnsEmptyArray() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), "GET /movies должен вернуть 200");
        checkContentType(response);

        String body = response.body().trim();
        assertTrue(body.startsWith("[") && body.endsWith("]"),
                "Ожидается JSON-массив");
    }

    @Test
    void getMovies_whenNotEmpty_returnsNotEmptyArray() throws Exception {

        moviesStore.saveMovie(new Movie("First", 2000));
        moviesStore.saveMovie(new Movie("Second", 2001));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), "GET /movies должен вернуть 200");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "Ожидается JSON-массив");

        List<Movie> movies = gson.fromJson(response.body(), new ListOfMoviesTypeToken().getType());
        assertEquals(2, movies.size(), "Список фильмов пуст");

    }

}