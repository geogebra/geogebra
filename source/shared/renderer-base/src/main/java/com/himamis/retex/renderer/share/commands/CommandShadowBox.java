package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ShadowAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandShadowBox extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new ShadowAtom(a);
	}
}
