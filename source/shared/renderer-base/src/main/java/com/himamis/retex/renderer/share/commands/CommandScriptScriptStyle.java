package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.StyleAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandScriptScriptStyle extends CommandStyle {

	public CommandScriptScriptStyle() {
		//
	}

	public CommandScriptScriptStyle(RowAtom size) {
		this.size = size;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new StyleAtom(TeXConstants.STYLE_SCRIPT_SCRIPT, a);
	}
}
