/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.serializer;

public final class TeXEscaper {

	private static final String[] escapableSymbols = { "%", "$", "#", "&", "{",
			"}", "_" };

	private static final String[][] replaceableSymbols = { { "~", "^", "\\" },
			{ "\u223C", "\\^{} ", "\\backslash{}" } };

	private TeXEscaper() {
		// utility class
	}

	/**
	 * Escapes symbols that have special behavior in math mode
	 * @param symbol symbol that might have a special meaning in LaTeX
	 * @return the symbol escaped, so that it renders as expected
	 */
	public static String escapeSymbol(String symbol) {
		for (int i = 0; i < replaceableSymbols[0].length; i++) {
			if (replaceableSymbols[0][i].equals(symbol)) {
				return replaceableSymbols[1][i];
			}
		}

		for (String escapableSymbol : escapableSymbols) {
			if (escapableSymbol.equals(symbol)) {
				return "\\" + symbol;
			}
		}

		return symbol;
	}

	/**
	 * Like {@link #escapeSymbol(String)}, but also escapes text mode symbols.
	 * @param str input string with special LaTeX characters
	 * @return the escaped string
	 */
	public static String escapeStringTextMode(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '-') {
				sb.append("-{}");
			} else {
				sb.append(escapeSymbol(String.valueOf(c)));
			}
		}
		return sb.toString();
	}
}
