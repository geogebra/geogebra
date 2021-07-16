package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.graphics.Color;

public class CursorAtom extends Atom {

	private double height;
	// foreground color
	private final Color color;

	public CursorAtom(Color color, double height) {
		super();
		this.color = color;
		this.height = height;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		env.isColored = true;
		TeXEnvironment copy = env.copy();
		if (color != null) {
			copy.setColor(color);
		}
		TeXFont tf = env.getTeXFont();
		int style = env.getStyle();
		Char c = tf.getChar("vert", style);
		Box cb = new CharBox(c);
		return new CursorBox(cb, height, color);
	}

}
