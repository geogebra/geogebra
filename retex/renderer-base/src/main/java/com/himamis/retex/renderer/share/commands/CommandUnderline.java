package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.UnderlinedAtom;

public class CommandUnderline extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new UnderlinedAtom(a);
	}

}
