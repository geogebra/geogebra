package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.SsAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandTextSf extends CommandText {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new SsAtom(a);
	}

}
