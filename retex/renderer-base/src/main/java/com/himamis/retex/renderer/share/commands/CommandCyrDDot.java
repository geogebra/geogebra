package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.AccentedAtom;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandCyrDDot extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new AccentedAtom(a, "cyrddot");
	}

	@Override
	public boolean close(TeXParser tp) {
		tp.closeConsumer(SymbolAtom.get("cyrddot"));
		return true;
	}

	@Override
	public boolean isClosable() {
		return true;
	}

}
