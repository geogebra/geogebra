package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ScaleAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandScaleBox extends Command1A {

	double hscale;
	double vscale;

	public CommandScaleBox(double hscale2, double vscale2) {
		this.hscale = hscale2;
		this.vscale = vscale2;
	}

	public CommandScaleBox() {
		//
	}

	@Override
	public boolean init(TeXParser tp) {
		hscale = tp.getArgAsDecimal();
		vscale = tp.getOptionAsDecimal(hscale);
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new ScaleAtom(a, hscale, vscale);
	}
}
