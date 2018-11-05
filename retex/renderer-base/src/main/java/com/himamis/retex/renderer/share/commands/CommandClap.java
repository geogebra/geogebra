package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.LapedAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandClap extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new LapedAtom(a, 'c');
	}

}
