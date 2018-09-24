package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.SmallCapAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandTextSc extends CommandText {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new SmallCapAtom(a);
	}

	@Override
	public Command duplicate() {
		CommandTextSc ret = new CommandTextSc();
		ret.mode = mode;
		return ret;
	}

}
