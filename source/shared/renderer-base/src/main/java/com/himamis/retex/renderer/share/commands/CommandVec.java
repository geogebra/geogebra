package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.AccentedAtom;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandVec extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new AccentedAtom(a, Symbols.VEC);
	}

	@Override
	public boolean close(TeXParser tp) {
		tp.closeConsumer(Symbols.VEC);
		return true;
	}

	@Override
	public boolean isClosable() {
		return true;
	}

}
