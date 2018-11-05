package com.himamis.retex.renderer.share.commands;

import java.util.Map;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RotateAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandRotateBox extends Command1A {

	double angle;
	Map<String, String> options;

	@Override
	public boolean init(TeXParser tp) {
		options = tp.getOptionAsMap();
		angle = tp.getArgAsDecimal();
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new RotateAtom(a, angle, options);
	}
}
