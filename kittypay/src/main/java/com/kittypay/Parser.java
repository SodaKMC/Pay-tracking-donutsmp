package com.kittypay;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Parser {
    private static final String AMT = "\\$?\\s*(\\d[\\d,]*\\.?\\d*[kKmMbBtT]?)(?![A-Za-z])";
    // Anchored at the start so a player typing "X paid you $1B" in public chat
    // ("Name » text") can't fake an entry.
    private static final Pattern IN  = Pattern.compile("^\\s*([.\\w]{3,17}) (?:has )?paid you " + AMT);
    private static final Pattern OUT = Pattern.compile("^\\s*You (?:have )?(?:paid|sent) ([.\\w]{3,17}) " + AMT);

    static void handle(String raw) {
        if (raw == null) return;
        String s = raw.replaceAll("\u00a7.", "").trim();
        Matcher m = IN.matcher(s);
        if (m.find()) { add(true, m.group(1), m.group(2)); return; }
        m = OUT.matcher(s);
        if (m.find()) add(false, m.group(1), m.group(2));
    }

    private static void add(boolean in, String player, String amt) {
        long a = parseAmount(amt);
        if (a > 0) Store.add(in, player, a);
    }

    static long parseAmount(String s) {
        s = s.toLowerCase().replaceAll("[$,\\s]", "");
        double mult = 1;
        if (!s.isEmpty()) {
            char c = s.charAt(s.length() - 1);
            if (c == 'k') mult = 1e3; else if (c == 'm') mult = 1e6;
            else if (c == 'b') mult = 1e9; else if (c == 't') mult = 1e12;
            if (!Character.isDigit(c)) s = s.substring(0, s.length() - 1);
        }
        try { return Math.round(Double.parseDouble(s) * mult); } catch (Exception e) { return -1; }
    }
}
