package com.kittypay;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.util.*;

/** Session payments live in memory (what the overlay shows); every payment is also appended to a history file. */
final class Store {
    private static final List<String[]> rows = new ArrayList<>(); // time, I|O, player, amount
    private static Path file;

    static synchronized void init() {
        try {
            Path dir = FabricLoader.getInstance().getConfigDir().resolve("kittypay");
            Files.createDirectories(dir);
            file = dir.resolve("payments_history.tsv");
        } catch (Exception e) { System.err.println("[kittypay] history disabled: " + e); }
    }

    static synchronized void add(boolean in, String player, long amount) {
        String[] r = { String.valueOf(System.currentTimeMillis()), in ? "I" : "O", player, String.valueOf(amount) };
        rows.add(r);
        System.out.println("[kittypay] " + (in ? "GOT  " : "SENT ") + player + " " + amount);
        try {
            if (file != null) Files.writeString(file, String.join("\t", r) + "\n",
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) { System.err.println("[kittypay] history write failed: " + e); }
    }

    static synchronized void reset() { rows.clear(); }

    static synchronized String json() {
        StringBuilder sb = new StringBuilder("{\"log\":\"Minecraft chat (in-game mod)\",\"log_ok\":true,\"items\":[");
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if (i > 0) sb.append(',');
            sb.append("{\"t\":").append(r[0]).append(",\"d\":\"").append(r[1].equals("I") ? "in" : "out")
              .append("\",\"p\":\"").append(r[2].replaceAll("[^.\\w]", "")).append("\",\"a\":").append(r[3]).append('}');
        }
        return sb.append("]}").toString();
    }
}
