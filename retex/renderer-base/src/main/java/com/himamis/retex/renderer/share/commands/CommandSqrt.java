package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.NthRoot;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandSqrt extends Command1O1A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		return new NthRoot(b, a);
	}

	@Override
	public Command duplicate() {
		CommandSqrt ret = new CommandSqrt();
		ret.hasopt = hasopt;
		ret.option = option;
		return ret;
	}

}
