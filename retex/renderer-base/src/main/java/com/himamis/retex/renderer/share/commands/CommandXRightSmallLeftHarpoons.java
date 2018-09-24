package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.XArrowAtom;

public class CommandXRightSmallLeftHarpoons extends Command1O1A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		return new XArrowAtom(b, a, XArrowAtom.Kind.RightSmallLeftHarpoons);
	}

	@Override
	public Command duplicate() {
		CommandXRightSmallLeftHarpoons ret = new CommandXRightSmallLeftHarpoons();
		ret.hasopt = hasopt;
		ret.option = option;
		return ret;
	}

}
