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

public class DeleteMoviesByIdTest extends MoviesApiTest {

    @Test
    void noDataFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies/2")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), "DELETE /movies должен вернуть 404");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals("Фильм не найден", jsonObject.get("error").getAsString(), "Ошибка поиска фильма по ID");

    }

    @Test
    void incorrectID() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies/asdf")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(400, response.statusCode(), "DELETE /movies должен вернуть 400");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals("Некорректный ID", jsonObject.get("error").getAsString(), "Ошибка проверки корректности ID");

    }

    @Test
    void checkDelete() throws Exception {
        moviesStore.saveMovie(new Movie("First", 2000));
        moviesStore.saveMovie(new Movie("Second", 2001));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + "/movies/2")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(204, response.statusCode(), "DELETE /movies должен вернуть 204");
        checkContentType(response);

    }
}
