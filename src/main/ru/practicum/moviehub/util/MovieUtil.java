package ru.practicum.moviehub.util;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.config.Config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MovieUtil {

    public static Optional<Integer> getInteger(String str) {
        try {
            return Optional.of(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

    }

    public static Map<String, String> parseQueryParams(HttpExchange exchange) {
        Map<String, String> parameters = new HashMap<>();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();

        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] param = pair.split("=");
                String key = null;
                String value = null;

                if (param.length > 0) {
                    key = URLDecoder.decode(param[0], Config.DEFAULT_CHARSET);
                }
                if (param.length > 1) {
                    value = URLDecoder.decode(param[1], Config.DEFAULT_CHARSET);
                }

                if (key != null) {
                    parameters.putIfAbsent(key, value);
                }
            }
        }
        return parameters;
    }
}

