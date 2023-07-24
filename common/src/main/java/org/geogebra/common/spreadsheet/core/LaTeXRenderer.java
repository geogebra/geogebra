package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GGraphics2D;

import com.himamis.retex.renderer.share.TeXIcon;

/**
 * Renderer for LaTeX cells
 */
public class LaTeXRenderer implements CellRenderer {
	private final TeXIcon icon;

	public LaTeXRenderer(TeXIcon ti) {
		this.icon = ti;
	}

	@Override
	public void draw(GGraphics2D graphics, int x, int y) {
		icon.paintIcon(() -> null, graphics.getGraphicsForLaTeX(), x, y);
	}
}
