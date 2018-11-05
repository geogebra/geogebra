package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.TtAtom;

public class CommandTextTt extends CommandText {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new TtAtom(a);
	}

}
