package com.kakao.pay.api;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class TokenGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUM = UPPER + LOWER + DIGITS;

    private final Random random;
    private final char[] symbols;
    private final char[] buf;

    public TokenGenerator(int length) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }

        this.random = new SecureRandom();
        this.symbols = ALPHANUM.toCharArray();
        this.buf = new char[length];
    }

    public String getToken() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }
}