package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.TypedAtom;

public class CommandMathRel extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new TypedAtom(TeXConstants.TYPE_RELATION, a);
	}

}
