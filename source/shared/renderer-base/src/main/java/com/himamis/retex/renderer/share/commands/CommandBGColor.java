package com.himamis.retex.renderer.share.commands;

import org.geogebra.common.awt.GColor;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandBGColor extends Command1A {

	GColor bg;

	public CommandBGColor() {
		//
	}

	public CommandBGColor(GColor bg2) {
		this.bg = bg2;
	}

	@Override
	public boolean init(TeXParser tp) {
		bg = CommandDefinecolor.getColor(tp);
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new ColorAtom(a, bg, null);
	}

}
