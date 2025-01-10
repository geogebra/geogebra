package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandMathRm extends Command1A {
	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new RomanAtom(a);
	}

}
