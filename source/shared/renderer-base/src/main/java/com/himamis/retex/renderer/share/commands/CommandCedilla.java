package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CedillaAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandCedilla extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new CedillaAtom(a);
	}

}
