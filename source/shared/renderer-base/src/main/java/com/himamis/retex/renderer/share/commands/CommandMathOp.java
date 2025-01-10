package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.TypedAtom;

public class CommandMathOp extends Command1A {
	@Override
	public Atom newI(TeXParser tp, Atom a) {
		a = new TypedAtom(TeXConstants.TYPE_BIG_OPERATOR, a);
		a.type_limits = TeXConstants.SCRIPT_NORMAL;
		return a;
	}

}
