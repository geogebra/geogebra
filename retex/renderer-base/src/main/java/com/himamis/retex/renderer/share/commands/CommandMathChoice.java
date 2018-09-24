package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.MathchoiceAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandMathChoice extends Command4A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b, Atom c, Atom d) {
		return new MathchoiceAtom(a, b, c, d);
	}

	@Override
	public Command duplicate() {
		CommandMathChoice ret = new CommandMathChoice();
		ret.atom1 = atom1;
		ret.atom2 = atom2;
		ret.atom3 = atom3;
		return ret;
	}

}
