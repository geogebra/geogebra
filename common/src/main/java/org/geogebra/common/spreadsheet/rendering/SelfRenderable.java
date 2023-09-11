package org.geogebra.common.spreadsheet.rendering;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.util.shape.Rectangle;

public final class SelfRenderable {
	private final CellRenderer renderer;
	private final Object renderable;

	public SelfRenderable(CellRenderer renderer, Object renderable) {
		this.renderer = renderer;
		this.renderable = renderable;
	}

	public void draw(GGraphics2D graphics, Rectangle cellBorder) {
		renderer.draw(renderable, graphics, cellBorder);
	}
}
