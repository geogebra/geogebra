package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.NewCommandMacro;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandNewCommand extends Command {

	@Override
	public boolean init(TeXParser tp) {
		final String name = tp.getArgAsCommand();
		final int nbargs = tp.getOptionAsPositiveInteger(0);
		final String code = tp.getGroupAsArgument();
		NewCommandMacro.addNewCommand(tp, name, code, nbargs, false);
		return false;
	}
}
