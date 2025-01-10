package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.OgonekAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandOgonek extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new OgonekAtom(a);
	}
}
