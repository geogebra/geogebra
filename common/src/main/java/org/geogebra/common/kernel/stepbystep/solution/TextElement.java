package org.geogebra.common.kernel.stepbystep.solution;

public class TextElement {
	public final String latex;
	public final String plain;

	public TextElement(String latex, String plainText) {
		this.latex = latex;
		this.plain = plainText;
	}

	public TextElement(String plainText) {
		this(null, plainText);
	}
}
