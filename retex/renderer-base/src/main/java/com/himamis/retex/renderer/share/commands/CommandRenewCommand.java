package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.NewCommandMacro;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandRenewCommand extends Command {

	@Override
	public boolean init(TeXParser tp) {
		final String name = tp.getArgAsCommand();
		final int nbargs = tp.getOptionAsPositiveInteger(0);
		final String code = tp.getGroupAsArgument();
		NewCommandMacro.addNewCommand(tp, name, code, nbargs, true);
		return false;
	}

	@Override
	public Command duplicate() {
		return new CommandRenewCommand();
	}

}
