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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetMoviesByIdTest extends MoviesApiTest {

    @Test
    void noDataFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies/2")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), "GET /movies должен вернуть 404");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals("Фильм не найден", jsonObject.get("error").getAsString(), "Ошибка поиска фильма по ID");

    }

    @Test
    void incorrectID() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies/qwe")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(400, response.statusCode(), "GET /movies должен вернуть 400");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals("Некорректный ID", jsonObject.get("error").getAsString(), "Ошибка проверки корректности ID");

    }


    @Test
    void returnsData() throws Exception {

        moviesStore.addMovie("First", 2000);
        moviesStore.addMovie("Second", 2001);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies/2")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), "GET /movies должен вернуть 200");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        Movie movie = gson.fromJson(response.body(), Movie.class);
        assertEquals("Second", movie.getTitle(), "Найден не тот фильм");

    }
}
