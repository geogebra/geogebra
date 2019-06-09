package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.NewCommandMacro;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandRenewCommand extends Command {

	@Override
	public boolean init(TeXParser tp) {
		final String name = tp.getArgAsCommand();
		final int nbargs = tp.getOptionAsPositiveInteger(0);

		if (TeXLength.isLengthName(name) && nbargs == 0) {
			TeXLength length = tp.getArgAsLength();
			TeXLength.setLength(name, length);
			return false;
		}

		if (TeXLength.isFactorName(name) && nbargs == 0) {
			double factor = tp.getArgAsDecimal();
			TeXLength.setFactor(name, factor);
			return false;
		}

		final String code = tp.getGroupAsArgument();

		NewCommandMacro.addNewCommand(tp, name, code, nbargs, true);
		return false;
	}
}
