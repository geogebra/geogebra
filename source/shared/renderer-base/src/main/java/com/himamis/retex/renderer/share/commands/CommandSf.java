package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SsAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandSf extends CommandStyle {

	public CommandSf() {
		//
	}

	public CommandSf(RowAtom size) {
		this.size = size;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new SsAtom(a);
	}
}
