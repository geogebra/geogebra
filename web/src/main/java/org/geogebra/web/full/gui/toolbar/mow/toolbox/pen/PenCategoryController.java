package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.web.html5.main.AppW;

public class PenCategoryController {
	private final AppW appW;
	private GColor lastPenColor;
	private GColor lastHighlighterColor;

	public PenCategoryController(AppW appW) {
		this.appW = appW;
	}

	private EuclidianPen getPen() {
		return appW.getActiveEuclidianView().getEuclidianController()
				.getPen();
	}

	public void updatePenColor(GColor color) {
		getPen().setPenColor(color);
	}

	public GColor getLastPenColor() {
		return lastPenColor;
	}

	public void setLastPenColor(GColor lastPenColor) {
		this.lastPenColor = lastPenColor;
	}

	public GColor getLastHighlighterColor() {
		return lastHighlighterColor;
	}

	public void setLastHighlighterColor(GColor lastHighlighterColor) {
		this.lastHighlighterColor = lastHighlighterColor;
	}
}