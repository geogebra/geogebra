package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandAbovewithdelims extends CommandOverwithdelims {

	TeXLength l;

	@Override
	public void add(TeXParser tp, Atom a) {
		if (left == null) {
			left = a;
		} else if (right == null) {
			right = a;
			l = tp.getArgAsLength();
		} else {
			den.add(a);
		}
	}

	@Override
	public Atom newI(TeXParser tp, Atom num, Atom den) {
		return new FractionAtom(num, den, l);
	}

	@Override
	public Command duplicate() {
		CommandAbovewithdelims ret = new CommandAbovewithdelims();

		ret.num = num;
		ret.den = den;
		ret.left = left;
		ret.right = right;
		ret.l = l;

		return ret;

	}

}
