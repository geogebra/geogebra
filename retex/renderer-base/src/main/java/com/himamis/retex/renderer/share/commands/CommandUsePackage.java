package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.JLMPackage;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandUsePackage extends Command {

	@Override
	public boolean init(TeXParser tp) {
		final String name = tp.getArgAsString();
		JLMPackage.usePackage(name);
		return false;
	}

	@Override
	public Command duplicate() {
		return new CommandUsePackage();
	}

}
