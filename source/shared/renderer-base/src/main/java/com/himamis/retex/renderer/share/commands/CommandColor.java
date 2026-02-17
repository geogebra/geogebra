package com.himamis.retex.renderer.share.commands;

import org.geogebra.common.awt.GColor;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandColor extends CommandStyle {

	GColor fg;

	public CommandColor() {
		//
	}

	public CommandColor(RowAtom size, GColor fg2) {
		this.size = size;
		this.fg = fg2;
	}

	@Override
	public boolean init(TeXParser tp) {
		fg = CommandDefinecolor.getColor(tp);
		return super.init(tp);
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new ColorAtom(a, null, fg);
	}

}
