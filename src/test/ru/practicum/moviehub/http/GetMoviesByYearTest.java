package ru.practicum.moviehub.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.model.Movie;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetMoviesByYearTest extends MoviesApiTest {

    @Test
    void incorrectYear() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies?year=qwe")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(400, response.statusCode(), "GET /movies должен вернуть 400");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals("Некорректный параметр запроса — 'year'", jsonObject.get("error").getAsString(), "Ошибка проверки параметров");

    }

    @Test
    void returnsData() throws Exception {

        moviesStore.saveMovie(new Movie("First", 2000));
        moviesStore.saveMovie(new Movie("Second", 2001));
        moviesStore.saveMovie(new Movie("Third", 2000));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies?year=2005")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), "GET /movies должен вернуть 200");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "Ожидается JSON-массив");

        List<Movie> movies = gson.fromJson(response.body(), new ListOfMoviesTypeToken().getType());
        assertEquals(0, movies.size(), "Список фильмов пуст");

    }


    @Test
    void returnsNotEmptyArray() throws Exception {

        moviesStore.saveMovie(new Movie("First", 2000));
        moviesStore.saveMovie(new Movie("Second", 2001));
        moviesStore.saveMovie(new Movie("Third", 2000));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies?year=2000")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), "GET /movies должен вернуть 200");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "Ожидается JSON-массив");

        List<Movie> movies = gson.fromJson(response.body(), new ListOfMoviesTypeToken().getType());
        assertEquals(2, movies.size(), "Список фильмов пуст");

    }
}
