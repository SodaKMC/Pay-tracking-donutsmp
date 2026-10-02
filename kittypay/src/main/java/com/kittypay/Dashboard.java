package com.kittypay;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * Tiny localhost-only web server built into the mod.
 *   /overlay      -> the page OBS shows as a Browser Source
 *   /             -> control page (preview + reset)
 *   /api/state    -> live data the overlay polls
 */
final class Dashboard {
    static void start(int port) {
        try {
            HttpServer s = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
            s.createContext("/api/state", ex -> send(ex, 200, "application/json", Store.json().getBytes(StandardCharsets.UTF_8)));
            s.createContext("/api/reset", ex -> {
                // custom header can't be sent by other websites, which blocks drive-by resets
                boolean ok = "POST".equals(ex.getRequestMethod()) && "1".equals(ex.getRequestHeaders().getFirst("X-Kitty"));
                if (ok) Store.reset();
                send(ex, ok ? 200 : 403, "text/plain", (ok ? "ok" : "no").getBytes());
            });
            s.createContext("/overlay", ex -> page(ex, "overlay.html"));
            s.createContext("/", ex -> {
                if (ex.getRequestURI().getPath().equals("/")) page(ex, "panel.html");
                else send(ex, 404, "text/plain", "not found".getBytes());
            });
            s.setExecutor(Executors.newCachedThreadPool(r -> { Thread t = new Thread(r, "kittypay-http"); t.setDaemon(true); return t; }));
            s.start();
            System.out.println("[kittypay] OBS overlay at http://localhost:" + port + "/overlay");
        } catch (IOException e) { System.err.println("[kittypay] overlay server failed (port busy?): " + e); }
    }

    private static void page(HttpExchange ex, String name) throws IOException {
        try (InputStream in = Dashboard.class.getResourceAsStream("/assets/kittypay/" + name)) {
            send(ex, 200, "text/html; charset=utf-8", in.readAllBytes());
        }
    }

    private static void send(HttpExchange ex, int code, String type, byte[] body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(code, body.length);
        try (OutputStream o = ex.getResponseBody()) { o.write(body); }
    }
}
