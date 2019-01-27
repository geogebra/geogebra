package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.SerifAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandMathSerif extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new SerifAtom(a);
	}

}
