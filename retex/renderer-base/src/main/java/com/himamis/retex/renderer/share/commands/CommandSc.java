package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SmallCapAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandSc extends CommandStyle {

	public CommandSc() {
		//
	}

	public CommandSc(RowAtom size) {
		this.size = size;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new SmallCapAtom(a);
	}
}
