package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CancelAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandBCancel extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new CancelAtom(a, CancelAtom.Type.BACKSLASH);
	}

}
