package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandMoveLeft extends Command1A {

	TeXLength left;

	@Override
	public boolean init(TeXParser tp) {
		left = tp.getArgAsLength();
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new RowAtom(new SpaceAtom(left.scale(-1.)), a);
	}
}
