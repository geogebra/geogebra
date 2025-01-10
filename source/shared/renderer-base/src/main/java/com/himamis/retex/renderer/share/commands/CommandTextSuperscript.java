package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.MHeightAtom;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.StyleAtom;
import com.himamis.retex.renderer.share.SubSupCom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.TextStyle;
import com.himamis.retex.renderer.share.TextStyleAtom;

public class CommandTextSuperscript extends Command {

	boolean mode;

	@Override
	public boolean init(TeXParser tp) {
		mode = tp.setTextMode();
		return true;
	}

	@Override
	public void add(TeXParser tp, Atom a) {
		tp.setMathMode(mode);
		a = new TextStyleAtom(a, TextStyle.MATHNORMAL);
		tp.closeConsumer(SubSupCom.get(MHeightAtom.get(), null,
				new StyleAtom(TeXConstants.STYLE_TEXT, new RomanAtom(a))));
	}

}
