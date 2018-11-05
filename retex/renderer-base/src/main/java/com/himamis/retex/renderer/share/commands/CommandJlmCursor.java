package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.CursorAtom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;

public class CommandJlmCursor extends Command {

	@Override
	public boolean init(TeXParser tp) {
		double size = tp.getArgAsDecimal();

		CursorAtom atom = new CursorAtom(FactoryProvider.getInstance()
				.getGraphicsFactory().createColor(GraphicsFactory.CURSOR_RED,
						GraphicsFactory.CURSOR_GREEN,
						GraphicsFactory.CURSOR_BLUE),
				size);

		tp.addToConsumer(atom);
		return false;

	}

}
