package com.himamis.retex.renderer.share.commands;

import org.geogebra.common.awt.GColor;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.FBoxAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandColorBox extends Command1A {

	GColor bg;

	public CommandColorBox() {
		//
	}

	public CommandColorBox(GColor bg2) {
		this.bg = bg2;
	}

	@Override
	public boolean init(TeXParser tp) {
		bg = CommandDefinecolor.getColor(tp);
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new FBoxAtom(a, bg, bg);
	}

}
