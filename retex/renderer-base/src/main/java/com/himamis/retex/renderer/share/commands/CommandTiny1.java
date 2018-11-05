package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.MonoScaleAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandTiny1 extends CommandStyle {

	public CommandTiny1(RowAtom size) {
		this.size = size;
	}

	public CommandTiny1() {
		//
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new MonoScaleAtom(a, 0.5);
	}

}
