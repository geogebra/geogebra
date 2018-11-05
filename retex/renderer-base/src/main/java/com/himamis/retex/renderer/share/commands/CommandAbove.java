package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandAbove extends CommandOver {

	TeXLength len;

	@Override
	public boolean init(TeXParser tp) {
		super.init(tp);
		len = tp.getArgAsLength();
		return false;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		return new FractionAtom(a, b, len);
	}
}
