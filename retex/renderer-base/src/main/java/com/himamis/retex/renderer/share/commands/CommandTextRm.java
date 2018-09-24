package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandTextRm extends CommandText {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new RomanAtom(a);
	}

	@Override
	public Command duplicate() {
		CommandTextRm ret = new CommandTextRm();
		ret.mode = mode;
		return ret;
	}

}
