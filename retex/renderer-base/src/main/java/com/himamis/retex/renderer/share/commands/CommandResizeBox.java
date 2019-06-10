package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ResizeAtom;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandResizeBox extends Command1A {

	private TeXLength width;
	private TeXLength height;

	public CommandResizeBox() {
		//
	}

	@Override
	public boolean init(TeXParser tp) {
		width = tp.getArgAsLengthOrExcl();
		height = tp.getArgAsLengthOrExcl();
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new ResizeAtom(a, width, height);
	}
}
