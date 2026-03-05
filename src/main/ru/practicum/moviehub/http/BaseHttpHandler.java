package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.moviehub.constant.Constant;

import java.io.IOException;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", Constant.CT_JSON);
        byte[] bytes = json.getBytes(Constant.DEFAULT_CHARSET);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    protected void sendNoContent(HttpExchange exchange, int status) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", Constant.CT_JSON);
        exchange.sendResponseHeaders(status, -1);
    }

}