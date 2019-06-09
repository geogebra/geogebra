package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.OoalignAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.Unit;

public class CommandPMB extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new OoalignAtom(a,
				new RowAtom(new SpaceAtom(Unit.MU, 0.4), a));
	}
}
