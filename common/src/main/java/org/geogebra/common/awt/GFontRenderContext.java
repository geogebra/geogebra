package org.geogebra.common.awt;

import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;

public abstract class GFontRenderContext {
	public int measureTextWith(String text, GFont font) {
		if (text == null || text.isEmpty()) {
			return 0;
		}
		GTextLayout layout = AwtFactory.prototype.newTextLayout(text, font,
				this);

		return (int) layout.getAdvance();
	}
}
