package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.XLongequalAtom;

public class CommandXLongEqual extends Command1O1A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		return new XLongequalAtom(b, a);
	}

	@Override
	public Command duplicate() {
		CommandXLongEqual ret = new CommandXLongEqual();
		ret.hasopt = hasopt;
		ret.option = option;
		return ret;
	}

}
