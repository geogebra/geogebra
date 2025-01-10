package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.MathAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandDisplayMath extends Command1A {
	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new MathAtom(a, TeXConstants.STYLE_DISPLAY);
	}

}
