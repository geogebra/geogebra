package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandDBinom extends Command2A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		final SymbolAtom left = Symbols.LBRACK;
		final SymbolAtom right = Symbols.RBRACK;
		return CommandGenfrac.get(left, a, b, right, TeXLength.getZero(), 0);
	}

}
