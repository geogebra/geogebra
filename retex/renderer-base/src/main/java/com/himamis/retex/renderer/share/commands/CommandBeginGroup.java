package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.TeXParser;

public class CommandBeginGroup extends Command {

	@Override
	public boolean init(TeXParser tp) {
		tp.processLBrace();
		return false;
	}

}
