package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.AccentedAtom;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandGrave2 extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new AccentedAtom(a, Symbols.GRAVE);
	}

	@Override
	public boolean close(TeXParser tp) {
		tp.closeConsumer(Symbols.GRAVE);
		return true;
	}

	@Override
	public boolean isClosable() {
		return true;
	}

}
