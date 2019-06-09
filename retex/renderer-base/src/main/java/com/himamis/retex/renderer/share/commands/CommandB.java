package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.UnderOverAtom;
import com.himamis.retex.renderer.share.Unit;

public class CommandB extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new UnderOverAtom(a, Symbols.BAR,
				new TeXLength(Unit.MU, 0.1), false, false);
	}

}
