package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.MonoScaleAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandLarge2 extends CommandStyle {

	public CommandLarge2() {
		//
	}

	public CommandLarge2(RowAtom size) {
		this.size = size;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new MonoScaleAtom(a, 1.4);
	}

}
