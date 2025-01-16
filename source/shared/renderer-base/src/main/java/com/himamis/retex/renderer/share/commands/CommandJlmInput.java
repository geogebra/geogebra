package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.InputAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandJlmInput extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return  new InputAtom(a, null, null);
	}
}
