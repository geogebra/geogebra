package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.graphics.Color;

public class CursorAtom extends ColorAtom {

	private float height;

	public CursorAtom(Atom atom, Color color, float height) {
		super(atom, null, color);
		this.height = height;
	}

	public Box createBox(TeXEnvironment env) {
		Box box = super.createBox(env);
		return new CursorBox(box, height);
	}

}
