package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.main.settings.PenToolsSettings;
import org.geogebra.web.html5.main.AppW;

public class PenCategoryController {
	private final AppW appW;

	public PenCategoryController(AppW appW) {
		this.appW = appW;
	}

	/**
	 * @return euclidian pen
	 */
	public EuclidianPen getPen() {
		return appW.getActiveEuclidianView().getEuclidianController()
				.getPen();
	}

	/**
	 * update pen (color, thickness, opacity)
	 * @param color - chosen color
	 */
	public void updatePenColor(GColor color) {
		getPen().setPenColor(color);
		getPen().updateMode();
	}

	public GColor getLastPenColor() {
		return getPenSettings().getLastSelectedPenColor();
	}

	public void setLastPenColor(GColor lastPenColor) {
		getPenSettings().setLastSelectedPenColor(lastPenColor);
	}

	public GColor getLastHighlighterColor() {
		return getPenSettings().getLastSelectedHighlighterColor();
	}

	public void setLastHighlighterColor(GColor lastHighlighterColor) {
		getPenSettings().setLastSelectedHighlighterColor(lastHighlighterColor);
	}

	private PenToolsSettings getPenSettings() {
		return appW.getSettings().getPenTools();
	}
}