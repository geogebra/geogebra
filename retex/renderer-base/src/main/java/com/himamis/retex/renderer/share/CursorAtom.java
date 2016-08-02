package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.graphics.Color;

public class CursorAtom extends ColorAtom {

	public CursorAtom(Atom atom, Color bg, Color c) {
		super(atom, bg, c);
	}

	public Box createBox(TeXEnvironment env) {
		Box box = super.createBox(env);
		return new CursorBox(box);
	}

}
