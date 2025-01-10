package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.PhantomAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandPhantom extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new PhantomAtom(a, true, true, true);
	}
}
