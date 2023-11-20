package org.geogebra.desktop.gui.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

enum JSCGInterpolation {
	NEAREST_NEIGHBOR,
	BILINEAR,
	BICUBIC;

	void apply(Graphics2D g) {
		switch (this) {
		case NEAREST_NEIGHBOR:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			break;
		case BILINEAR:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			break;
		case BICUBIC:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			break;
		}
	}
}
