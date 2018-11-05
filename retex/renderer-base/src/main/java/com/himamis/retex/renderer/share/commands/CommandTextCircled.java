package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.TextCircledAtom;

public class CommandTextCircled extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new TextCircledAtom(new RomanAtom(a));
	}

}
