package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.FBoxAtom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.platform.graphics.Color;

public class CommandFColorBox extends Command1A {

	Color frame;
	Color bg;

	public CommandFColorBox() {
		//
	}

	public CommandFColorBox(Color frame2, Color bg2) {
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
