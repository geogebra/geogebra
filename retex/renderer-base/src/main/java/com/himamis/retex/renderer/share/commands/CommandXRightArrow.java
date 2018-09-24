package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.XArrowAtom;

public class CommandXRightArrow extends Command1O1A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		return new XArrowAtom(b, a, XArrowAtom.Kind.Right);
	}

	@Override
	public Command duplicate() {
		CommandXRightArrow ret = new CommandXRightArrow();
		ret.hasopt = hasopt;
		ret.option = option;
		return ret;
	}

}
