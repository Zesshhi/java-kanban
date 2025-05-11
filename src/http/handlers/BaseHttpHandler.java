package http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {


    protected void returnText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        try (exchange; OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(resp);
        }
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        returnText(exchange, text, statusCode);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        returnText(exchange, "Данных по этому запросу не найдено", 404);
    }

    protected void sendHasIntersections(HttpExchange exchange) throws IOException {
        returnText(exchange, "Задача пересекается с существующими", 406);
    }

    protected String getRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
