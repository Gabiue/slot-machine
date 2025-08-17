package com.slotmachine;

import java.util.Map;
import java.util.Random;

public class GameEngine {
    private static final String[] SYMBOLS = {"uva", "melancia", "limao", "laranja", "banana", "cereja"};
    private static final int[] WEIGHTS = {2, 3, 6, 6, 4, 5};
    private static final Map<String, Integer> PAYTABLE = Map.of(
            "uva", 25,
            "melancia", 15,
            "cereja", 12,
            "banana", 8,
            "laranja", 6,
            "limao", 5
    );

    private static final int REELS = 3;
    private final Random rng = new Random();
    private int saldo;

    public GameEngine(int saldoInicial) {
        this.saldo = saldoInicial;
    }

    public String[] spin() {
        String[] resultado = new String[REELS];
        for (int i = 0; i < REELS; i++) {
            resultado[i] = pickWeighted();
        }
        return resultado;
    }

    public int processarAposta(String[] resultado, int aposta) {
        if (aposta > saldo) return -1;

        int premio = evaluate(resultado, aposta);
        if (premio > 0) {
            saldo += (premio - aposta);
            return premio;
        } else {
            saldo -= aposta;
            return 0;
        }
    }

    public boolean isWin(String[] resultado) {
        for (int i = 1; i < resultado.length; i++) {
            if (!resultado[i].equals(resultado[0])) {
                return false;
            }
        }
        return true;
    }

    public int getMultiplier(String symbol) {
        return PAYTABLE.getOrDefault(symbol, 0);
    }

    public int getSaldo() {
        return saldo;
    }

    public boolean podeApostar(int aposta) {
        return aposta > 0 && aposta <= saldo;
    }

    public boolean temSaldo() {
        return saldo > 0;
    }

    private int evaluate(String[] resultado, int aposta) {
        if (!isWin(resultado)) return 0;
        int mult = PAYTABLE.getOrDefault(resultado[0], 0);
        return aposta * mult;
    }

    private String pickWeighted() {
        int total = 0;
        for (int w : WEIGHTS) total += w;
        int x = rng.nextInt(total);
        int acc = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            acc += WEIGHTS[i];
            if (x < acc) return SYMBOLS[i];
        }
        return SYMBOLS[SYMBOLS.length - 1];
    }
}