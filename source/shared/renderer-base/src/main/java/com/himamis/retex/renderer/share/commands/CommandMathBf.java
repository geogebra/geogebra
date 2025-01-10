package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.BoldAtom;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandMathBf extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new BoldAtom(new RomanAtom(a));
	}
}
