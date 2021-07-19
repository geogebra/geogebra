package org.geogebra.desktop.gui.dialog;

public class TableSymbolsD {
	public final static String[] mathfrak() {
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

	public final static String[] mathcal() {
		String[] mathcal = new String[26];
		char letter;
		int i = 0;
		for (letter = 'A'; letter <= 'Z'; letter++) {
			mathcal[i] = "\\mathcal{" + letter + "}";
			i++;
		}
		return mathcal;
	}

	public final static String[] mathbb() {
		String[] mathbb = new String[26];
		char letter;
		int i = 0;
		for (letter = 'A'; letter <= 'Z'; letter++) {
			mathbb[i] = "\\mathbb{" + letter + "}";
			i++;
		}
		return mathbb;
	}

	public final static String[] mathscr() {
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
