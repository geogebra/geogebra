package com.himamis.retex.renderer.share.commands;

import org.geogebra.common.awt.GColor;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.FBoxAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandFColorBox extends Command1A {

	GColor frame;
	GColor bg;

	public CommandFColorBox() {
		//
	}

	public CommandFColorBox(GColor frame2, GColor bg2) {
		this.frame = frame2;
		this.bg = bg2;
	}

	@Override
	public boolean init(TeXParser tp) {
		frame = tp.getArgAsColor();
		bg = tp.getArgAsColor();
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new FBoxAtom(a, bg, frame);
	}

}
