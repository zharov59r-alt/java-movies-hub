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

public class PostMoviesTest extends MoviesApiTest {

    @Test
    void checkAdd() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\": \"First\", \"year\": 2000}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(201, response.statusCode(), "POST /movies должен вернуть 201");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        Movie movie = gson.fromJson(response.body(), Movie.class);
        assertEquals("First", movie.getTitle(), "Найден не тот фильм");

    }

    @Test
    void incorrectHeaders() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\": \"First\", \"year\": 2000}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(415, response.statusCode(), "POST /movies должен вернуть 415");


    }

    @Test
    void checkAddEmptyJson() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(422, response.statusCode(), "POST /movies должен вернуть 422");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals("Ошибка валидации", jsonObject.get("error").getAsString(), "Ошибка валидации");

        List<String> details = gson.fromJson(jsonObject.get("details").getAsJsonArray(), new ListOfStringTypeToken().getType());
        assertTrue(details.contains("название не должно быть пустым"), "Ошибка проверки названия");
        assertTrue(details.contains("год не должен быть пустым"), "Ошибка проверки года");

    }

    @Test
    void checkAddEmptyTitle() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\": \"\", \"year\": 2000}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(422, response.statusCode(), "POST /movies должен вернуть 422");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals("Ошибка валидации", jsonObject.get("error").getAsString(), "Ошибка валидации");

        List<String> details = gson.fromJson(jsonObject.get("details").getAsJsonArray(), new ListOfStringTypeToken().getType());
        assertTrue(details.contains("название не должно быть пустым"), "Ошибка проверки названия");

    }

    @Test
    void checkAddIncorrectTitleAndYearValues() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\": \"1234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890\", \"year\": 1000}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(422, response.statusCode(), "POST /movies должен вернуть 422");
        checkContentType(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ожидается JSON-объект");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals("Ошибка валидации", jsonObject.get("error").getAsString(), "Ошибка валидации");

        List<String> details = gson.fromJson(jsonObject.get("details").getAsJsonArray(), new ListOfStringTypeToken().getType());
        assertTrue(details.contains("название не должно превышать 100 символов"), "Ошибка проверки названия");
        assertTrue(details.contains("год должен быть между 1888 и 2026"), "Ошибка проверки года");

    }

}
