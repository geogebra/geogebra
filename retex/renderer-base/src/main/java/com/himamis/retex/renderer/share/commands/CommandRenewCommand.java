package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.NewCommandMacro;
import com.himamis.retex.renderer.share.SetLengthAtom;
import com.himamis.retex.renderer.share.TeXLengthSettings;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandRenewCommand extends Command {

	@Override
	public boolean init(TeXParser tp) {
		final String name = tp.getArgAsCommand();
		final int nbargs = tp.getOptionAsPositiveInteger(0);

		if (TeXLengthSettings.isLengthName(name) && nbargs == 0) {
			tp.addToConsumer(new SetLengthAtom(name, tp.getArgAsLength()));
			return false;
		}

		if (TeXLengthSettings.isFactorName(name) && nbargs == 0) {
			tp.addToConsumer(new SetLengthAtom(name, tp.getArgAsDecimal()));
			return false;
		}

		final String code = tp.getGroupAsArgument();

		NewCommandMacro.addNewCommand(tp, name, code, nbargs, true);
		return false;
	}
}
