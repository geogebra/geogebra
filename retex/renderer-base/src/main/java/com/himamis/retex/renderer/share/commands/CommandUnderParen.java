package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.OverUnderDelimiter;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.Unit;

public class CommandUnderParen extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new OverUnderDelimiter(a, null, Symbols.RBRACK,
				Unit.EX, 0, false);
	}

}
