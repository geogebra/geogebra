package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.PodAtom;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandPod extends Command1A {

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new PodAtom(a, 8., true);
	}
}
