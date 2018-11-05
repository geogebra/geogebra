package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.ArrayOptions;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.MulticolumnAtom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.exception.ParseException;

public class CommandMulticolumn extends Command1A {
	int n;
	ArrayOptions options;

	public CommandMulticolumn() {
		//
	}

	public CommandMulticolumn(int n2, ArrayOptions options2) {
		this.n = n2;
		this.options = options2;
	}

	@Override
	public boolean init(TeXParser tp) {
		if (!tp.isArrayMode()) {
			throw new ParseException(tp,
					"The macro \\multicolumn is only available in array mode !");
		}
		n = tp.getArgAsPositiveInteger();
		if (n == -1) {
			throw new ParseException(tp,
					"The macro \\multicolumn requires a positive integer");
		}
		options = tp.getArrayOptions();
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new MulticolumnAtom(n, options, a);
	}

}
