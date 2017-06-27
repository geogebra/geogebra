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

	sigma('\u03C3', false),

	tau('\u03C4', false),

	upsilon('\u03C5', false),

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

	public char unicode;
	public boolean upperCase;

	private Greek(char ch, boolean upper) {
		this.unicode = ch;
		this.upperCase = upper;
	}

	public String getLaTeX() {
		return "\\" + name();
	}

	public String getHTML() {
		return "&" + name() + ";";
	}

}
