/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.dialog;

public class TableSymbolsD {

	/**
	 * @return math frak symbols
	 */
	public static String[] mathfrak() {
		String[] mathfrak = new String[52];
		char letter;
		int i = 0;
		for (letter = 'A'; letter <= 'Z'; letter++) {
			mathfrak[i] = "\\mathfrak{" + letter + "}";
			i++;
		}
		for (letter = 'a'; letter <= 'z'; letter++) {
			mathfrak[i] = "\\mathfrak{" + letter + "}";
			i++;
		}
		return mathfrak;
	}

	/**
	 * @return math calligraphy symbols
	 */
	public static String[] mathCal() {
		String[] mathcal = new String[26];
		char letter;
		int i = 0;
		for (letter = 'A'; letter <= 'Z'; letter++) {
			mathcal[i] = "\\mathcal{" + letter + "}";
			i++;
		}
		return mathcal;
	}

	/**
	 * @return math blackboard bold symbols
	 */
	public static String[] mathbb() {
		String[] mathbb = new String[26];
		char letter;
		int i = 0;
		for (letter = 'A'; letter <= 'Z'; letter++) {
			mathbb[i] = "\\mathbb{" + letter + "}";
			i++;
		}
		return mathbb;
	}

	/**
	 * @return math scr symbols
	 */
	public static String[] mathscr() {
		String[] mathscr = new String[26];
		char letter;
		int i = 0;
		for (letter = 'A'; letter <= 'Z'; letter++) {
			mathscr[i] = "\\mathscr{" + letter + "}";
			i++;
		}
		return mathscr;
	}
}
