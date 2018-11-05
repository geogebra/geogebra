package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RaiseAtom;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandRaiseBox extends Command1A {

	TeXLength raise;
	TeXLength height;
	TeXLength depth;

	@Override
	public boolean init(TeXParser tp) {
		raise = tp.getArgAsLength();
		height = tp.getOptionAsLength(null);
		depth = tp.getOptionAsLength(null);
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new RaiseAtom(a, raise, height, depth);
	}
}
