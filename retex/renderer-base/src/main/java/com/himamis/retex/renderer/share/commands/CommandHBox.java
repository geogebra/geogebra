package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandHBox extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		if (a instanceof RowAtom) {
			return a;
		}
		return new RowAtom(a);
	}

}
