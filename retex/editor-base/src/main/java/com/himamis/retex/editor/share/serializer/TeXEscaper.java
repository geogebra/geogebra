package com.himamis.retex.editor.share.serializer;

public final class TeXEscaper {

	private static final String[] escapeableSymbols = { "%", "$", "#", "&", "{",
			"}", "_" };

	private static final String[][] replaceableSymbols = { { "~", "^", "\\" },
			{ "\u223C ", "\\^{\\ } ", "\\backslash{}" } };

	private TeXEscaper() {
		// untility class
	}

	/**
	 * @param symbol symbol that might have a special meaning in LaTeX
	 * @return the symbol escaped, so that it renders as expected
	 */
	public static String escapeSymbol(String symbol) {
		for (int i = 0; i < replaceableSymbols[0].length; i++) {
			if (replaceableSymbols[0][i].equals(symbol)) {
				return replaceableSymbols[1][i];
			}
		}

		for (String escapeableSymbol : escapeableSymbols) {
			if (escapeableSymbol.equals(symbol)) {
				return "\\" + symbol;
			}
		}

		return symbol;
	}

	/**
	 * @param str input string with special LaTeX characters
	 * @return the escaped string
	 */
	public static String escapeString(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			sb.append(escapeSymbol(String.valueOf(str.charAt(i))));
		}
		return sb.toString();
	}
}
