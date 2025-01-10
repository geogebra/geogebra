package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.DBoxAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandDBox extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new DBoxAtom(a);
	}
}
