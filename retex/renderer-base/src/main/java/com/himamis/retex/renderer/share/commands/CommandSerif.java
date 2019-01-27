package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SerifAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandSerif extends CommandStyle {

	public CommandSerif() {
		//
	}

	public CommandSerif(RowAtom size) {
		this.size = size;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new SerifAtom(a);
	}
}
