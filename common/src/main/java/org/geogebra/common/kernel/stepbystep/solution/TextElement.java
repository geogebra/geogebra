package org.geogebra.common.kernel.stepbystep.solution;

public class TextElement {
	public final String latex;
	public final String plain;

	public TextElement(String latex, String plain) {
		this.latex = latex;
		this.plain = plain;
	}

	public TextElement(String plain) {
		this(null, plain);
	}
}
