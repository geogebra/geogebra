package com.himamis.retex.editor.share.util;

public enum Greek {
	alpha('\u03B1', false),

	beta('\u03B2', false),

	gamma('\u03B3', false),

	delta('\u03B4', false),

	epsilon('\u03B5', false),

	zeta('\u03B6', false),

	eta('\u03B7', false),

	theta('\u03B8', false),

	iota('\u03B9', false),

	kappa('\u03BA', false),

	lambda('\u03BB', false),

	mu('\u03BC', false),

	nu('\u03BD', false),

	xi('\u03BE', false),

	omicron('\u03BF', false),

	pi('\u03C0', false),

	rho('\u03C1', false),

	// don't want \u03C2 \varsigma here

	sigma('\u03C3', false),

	tau('\u03C4', false),

	upsilon('\u03C5', false),

	// \\varphi, curly
	phi('\u03C6', false),
	// \\phi "straight"
	// public static final char phi_symbol = '\u03D5';

	chi('\u03C7', false),

	psi('\u03C8', false),

	omega('\u03C9', false),

	Alpha('\u0391', true),

	Beta('\u0392', true),

	Gamma('\u0393', true),

	Delta('\u0394', true),

	Epsilon('\u0395', true),

	Zeta('\u0396', true),

	Eta('\u0397', true),

	Theta('\u0398', true),

	Iota('\u0399', true),

	Kappa('\u039A', true),

	Lambda('\u039B', true),

	Mu('\u039C', true),

	Nu('\u039D', true),

	Xi('\u039E', true),

	Omicron('\u039F', true),

	Pi('\u03A0', true),

	Rho('\u03A1', true),

	Sigma('\u03A3', true),

	Tau('\u03A4', true),

	Upsilon('\u03A5', true),

	Phi('\u03A6', true),

	Chi('\u03A7', true),

	Psi('\u03A8', true),

	Omega('\u03A9', true);

	public final char unicode;
	public final boolean upperCase;
	private static char[] greekLowerCaseNoPi;
	private static char[] greekUpperCase;

	private Greek(char ch, boolean upper) {
		this.unicode = ch;
		this.upperCase = upper;
	}

	/**
	 * 
	 * @return the LaTeX name WITHOUT the leading \
	 */
	public String getLaTeX() {

		if (this.equals(phi)) {
			return "var" + name();
		}

		return name();
	}

	public String getHTML() {
		return "&" + name() + ";";
	}

	/**
	 * @param ch
	 *            unicode char
	 * @return LaTeX form WITHOUT leading \ eg "alpha" (or null if it's not a
	 *         Greek char)
	 */
	public static String getLaTeX(char ch) {
		for (Greek greek : Greek.values()) {
			if (greek.unicode == ch) {
				return greek.getLaTeX();
			}
		}

		return null;
	}

	/**
	 * @return greek lowercase characters excluding pi
	 */
	public static char[] getGreekLowerCaseNoPi() {

		if (greekLowerCaseNoPi == null) {
			greekLowerCaseNoPi = new char[23];
		}

		int i = 0;
		for (Greek greek : Greek.values()) {
			if (!greek.upperCase && greek.unicode != Unicode.pi) {

				// \u03d5 in place of \u03c6
				greekLowerCaseNoPi[i++] = greek.getUnicodeNonCurly();
			}
		}

		return greekLowerCaseNoPi;

	}

	public char getUnicodeNonCurly() {
		return unicode == Unicode.phi ? Unicode.phi_symbol : unicode;
	}

	/**
	 * @return uppercase greek characters
	 */
	public static char[] getGreekUpperCase() {

		if (greekUpperCase == null) {
			greekUpperCase = new char[24];
		}

		int i = 0;
		for (Greek greek : Greek.values()) {
			if (greek.upperCase) {

				greekUpperCase[i++] = greek.unicode;
			}
		}

		return greekUpperCase;

	}

}
