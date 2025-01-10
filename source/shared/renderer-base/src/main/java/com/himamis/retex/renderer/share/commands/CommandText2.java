package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandText2 extends CommandText {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return a;
	}

}
