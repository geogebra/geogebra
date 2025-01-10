package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.DoubleFramedAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandDoubleBox extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new DoubleFramedAtom(a);
	}
}
