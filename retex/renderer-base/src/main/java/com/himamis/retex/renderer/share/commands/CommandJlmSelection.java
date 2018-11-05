package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.SelectionAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandJlmSelection extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new SelectionAtom(a, ColorAtom.getColor("#CCCCFF"), null);
	}

}
