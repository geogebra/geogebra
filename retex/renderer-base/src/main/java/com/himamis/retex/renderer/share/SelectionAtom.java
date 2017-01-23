package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.graphics.Color;

public class SelectionAtom extends ColorAtom {

	@Override
	final public Atom duplicate() {
		return setFields(
				new SelectionAtom(elements, getBackground(), getColor()));
	}

	public SelectionAtom(Atom atom, Color bg, Color c) {
		super(atom, bg, c);
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		Box box = super.createBox(env);
		return new SelectionBox(box);
	}

}
