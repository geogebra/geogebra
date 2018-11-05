package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.TeXParser;

public class CommandEndGroup extends Command {

	@Override
	public boolean init(TeXParser tp) {
		tp.processRBrace();
		return false;
	}

}
