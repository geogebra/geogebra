package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.platform.graphics.Color;

public class CommandColor extends CommandStyle {

	Color fg;

	public CommandColor() {
		//
	}

	public CommandColor(RowAtom size, Color fg2) {
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
