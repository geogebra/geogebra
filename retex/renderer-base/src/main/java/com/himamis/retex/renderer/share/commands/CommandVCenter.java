package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.VCenteredAtom;

public class CommandVCenter extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new VCenteredAtom(a);
	}

}
