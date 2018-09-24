package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.XArrowAtom;

public class CommandXSmallRightLeftHarpoons extends Command1O1A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		return new XArrowAtom(b, a, XArrowAtom.Kind.SmallRightLeftHarpoons);
	}

	@Override
	public Command duplicate() {
		CommandXSmallRightLeftHarpoons ret = new CommandXSmallRightLeftHarpoons();
		ret.hasopt = hasopt;
		ret.option = option;
		return ret;
	}

}
