package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.AlignedAtom;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandShoveRight extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new AlignedAtom(a, TeXConstants.Align.RIGHT);
	}
}
