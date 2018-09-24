package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.XArrowAtom;

public class CommandXRightLeftHarpoons extends Command1O1A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		return new XArrowAtom(b, a, XArrowAtom.Kind.RightLeftHarpoons);
	}

	@Override
	public Command duplicate() {
		CommandXRightLeftHarpoons ret = new CommandXRightLeftHarpoons();
		ret.hasopt = hasopt;
		ret.option = option;
		return ret;
	}

}
