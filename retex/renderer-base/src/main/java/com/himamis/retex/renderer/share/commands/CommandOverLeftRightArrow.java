package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.UnderOverArrowAtom;

public class CommandOverLeftRightArrow extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new UnderOverArrowAtom(a, true);
	}

	@Override
	public Command duplicate() {
		// TODO Auto-generated method stub
		return new CommandOverLeftRightArrow();
	}

}
