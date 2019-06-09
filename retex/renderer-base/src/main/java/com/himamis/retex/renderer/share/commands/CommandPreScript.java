package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.PhantomAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.Unit;

public class CommandPreScript extends Command3A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b, Atom c) {
		final RowAtom ra = new RowAtom(
				new ScriptsAtom(new PhantomAtom(c, false, true, true), b, a,
						false),
				new SpaceAtom(Unit.MU, -0.3, 0., 0.),
				c.changeType(TeXConstants.TYPE_ORDINARY));
		ra.lookAtLast(true);
		return ra;
	}
}
