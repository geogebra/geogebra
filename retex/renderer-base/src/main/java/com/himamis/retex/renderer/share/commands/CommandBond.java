package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.mhchem.MhchemBondParser;

public class CommandBond extends Command {

	@Override
	public boolean init(TeXParser tp) {
		final String code = tp.getGroupAsArgument();
		final MhchemBondParser mbp = new MhchemBondParser(code);
		tp.addToConsumer(mbp.get());
		return false;
	}
}
