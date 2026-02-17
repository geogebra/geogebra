package com.himamis.retex.renderer.share;

import org.geogebra.common.awt.GColor;


public class SelectionAtom extends ColorAtom {

	public SelectionAtom(Atom atom, GColor bg, GColor c) {
		super(atom, bg, c);
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		Box box = super.createBox(env);
		return new SelectionBox(box);
	}

}
