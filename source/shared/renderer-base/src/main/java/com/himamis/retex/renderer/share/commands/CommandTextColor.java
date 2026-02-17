package com.himamis.retex.renderer.share.commands;

import org.geogebra.common.awt.GColor;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandTextColor extends Command1A {

	GColor fg;

	public CommandTextColor() {
		//
	}

	public CommandTextColor(GColor fg) {
		this.fg = fg;
	}

	@Override
	public boolean init(TeXParser tp) {
		fg = CommandDefinecolor.getColor(tp);
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new ColorAtom(a, null, fg);
	}

}
