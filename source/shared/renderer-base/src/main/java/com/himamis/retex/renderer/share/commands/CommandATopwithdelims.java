package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandATopwithdelims extends CommandOverwithdelims {

	@Override
	public Atom newI(TeXParser tp, Atom num, Atom den) {
		return new FractionAtom(num, den, false);
	}
}
