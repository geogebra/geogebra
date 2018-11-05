package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.AccentedAtom;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.exception.ParseException;

public class CommandSkew extends Command {

	private double skew;

	@Override
	public boolean init(TeXParser tp) {
		skew = tp.getArgAsDecimal();
		return true;
	}

	@Override
	public void add(TeXParser tp, Atom a) {
		if (a instanceof AccentedAtom) {
			((AccentedAtom) a).setSkew(skew);
			tp.closeConsumer(a);
			return;
		}
		throw new ParseException(tp,
				"skew command is only working with an accent as second argument");
	}
}
