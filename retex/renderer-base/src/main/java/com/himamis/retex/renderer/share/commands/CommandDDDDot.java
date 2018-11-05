package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.BuildrelAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandDDDDot extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new BuildrelAtom(a,
				new RowAtom(Symbols.TEXTNORMALDOT, Symbols.TEXTNORMALDOT,
						Symbols.TEXTNORMALDOT, Symbols.TEXTNORMALDOT));
	}

}
