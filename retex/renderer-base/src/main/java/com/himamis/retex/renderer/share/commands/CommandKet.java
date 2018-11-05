package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandKet extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new RowAtom(Symbols.VERT, a, Symbols.RANGLE);
	}
}
