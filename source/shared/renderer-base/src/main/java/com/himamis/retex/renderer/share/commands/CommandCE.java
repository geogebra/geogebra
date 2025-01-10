package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.mhchem.MhchemParser;

public class CommandCE extends Command {

	@Override
	public boolean init(TeXParser tp) {
		final String code = tp.getGroupAsArgument();
		final MhchemParser mp = new MhchemParser(code);
		mp.parse();
		tp.addToConsumer(new RomanAtom(mp.get()));
		return false;
	}

}
