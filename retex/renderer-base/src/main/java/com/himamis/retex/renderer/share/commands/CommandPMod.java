package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.PodAtom;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandPMod extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		final RowAtom ra = new RowAtom(3);
		ra.add(new RomanAtom(TeXParser.getAtomForLatinStr("mod", true)));
		ra.add(new SpaceAtom(TeXLength.Unit.MU, 6.));
		ra.add(a);
		return new PodAtom(ra, 8., true);
	}
}
