package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.SMatrixAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandPMatrix extends CommandMatrix {

	@Override
	public Atom newI(TeXParser tp) {
		return new FencedAtom(new SMatrixAtom(aoa, false), Symbols.LBRACK,
				Symbols.RBRACK);
	}

	@Override
	public Command duplicate() {
		CommandPMatrix ret = new CommandPMatrix();

		ret.hasLBrace = hasLBrace;
		ret.aoa = aoa;

		return ret;

	}

}
