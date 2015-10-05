package org.geogebra.common.awt;

import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;

public abstract class GFontRenderContext {
	public int measureTextWidth(String text, GFont font) {
		GTextLayout layout = getTextLayout(text, font);
		return layout != null ? (int) layout.getAdvance() : 0;
	}

	public GTextLayout getTextLayout(String text, GFont font) {
		if (text == null || text.isEmpty()) {
			return null;
		}
		return AwtFactory.prototype.newTextLayout(text, font, this);

	}
}
