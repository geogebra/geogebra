package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandOperatorName extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		a = new RomanAtom(a).changeType(TeXConstants.TYPE_BIG_OPERATOR);
		a.type_limits = TeXConstants.SCRIPT_NOLIMITS;
		return a;
	}
}
