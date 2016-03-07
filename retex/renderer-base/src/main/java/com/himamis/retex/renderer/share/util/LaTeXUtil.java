package com.himamis.retex.renderer.share.util;

public class LaTeXUtil {

    private static final String escapeableSymbols[] = {"%", "$", "#", "&", "{", "}", "_"};
    private static final String replaceableSymbols[][] = {{"~", "^", "\\"}, {"\u223C ",
            "\\^{\\ } ", "\\backslash "}};


    public static boolean isSymbolEscapeable(String symbol) {
        for (int i = 0; i < escapeableSymbols.length; i++) {
            if (escapeableSymbols[i].equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReplaceableSymbol(String symbol) {
        return getReplaceableSymbolIndex(symbol) != -1;
    }

    private static int getReplaceableSymbolIndex(String symbol) {
        for (int i = 0; i < replaceableSymbols[0].length; i++) {
            if (replaceableSymbols[0][i].equals(symbol)) {
                return i;
            }
        }
        return -1;
    }

    public static String replaceSymbol(String symbol) {
        if (isReplaceableSymbol(symbol)) {
            int index = getReplaceableSymbolIndex(symbol);
            return replaceableSymbols[1][index];
        }
        return symbol;
    }
}
