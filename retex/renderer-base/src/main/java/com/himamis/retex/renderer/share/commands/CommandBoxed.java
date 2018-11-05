package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.FBoxAtom;
import com.himamis.retex.renderer.share.MathAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandBoxed extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new FBoxAtom(new MathAtom(a, TeXConstants.STYLE_DISPLAY));
	}

}
