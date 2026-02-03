package ru.practicum.moviehub.http;

import com.google.gson.*;
import org.junit.jupiter.api.*;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;


public class MoviesApiTest {
    public static final String BASE = "http://localhost:8080"; // !!! добавьте базовую часть URL
    public static MoviesServer server;
    public static HttpClient client;
    public static MoviesStore moviesStore;
    public static Gson gson;

    @BeforeAll
    public static void beforeAll() {
        gson = new Gson();
        moviesStore = new MoviesStore();
        server = new MoviesServer(moviesStore, 8080);
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        server.start();

    }

    @BeforeEach
    public void beforeEach() {
        moviesStore.clearStore();
    }

    @AfterAll
    public static void afterAll() {
        server.stop();
    }

    public void checkContentType(HttpResponse<String> response) {
        String contentTypeHeaderValue =
                response.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json; charset=UTF-8", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

}