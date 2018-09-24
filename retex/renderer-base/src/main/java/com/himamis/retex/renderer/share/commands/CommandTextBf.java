package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.BoldAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandTextBf extends CommandText {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new BoldAtom(a);
	}

	@Override
	public Command duplicate() {
		CommandTextBf ret = new CommandTextBf();
		ret.mode = mode;
		return ret;
	}

}
