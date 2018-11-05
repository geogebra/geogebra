package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.AccentSetAtom;
import com.himamis.retex.renderer.share.AccentedAtom;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandAccentSet extends Command2A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		if (a instanceof SymbolAtom) {
			return new AccentedAtom(b, (SymbolAtom) a);
		} else {
			return new AccentSetAtom(b, a);
		}
	}


}
