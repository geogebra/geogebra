package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.main.settings.PenToolsSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.web.html5.main.AppW;

public class PenCategoryController {
	private final AppW appW;

	/**
	 * Constructor
	 * @param appW - application
	 * @param listener - settings listener
	 */
	public PenCategoryController(AppW appW, SettingListener listener) {
		this.appW = appW;
		getPenSettings().addListener(listener);
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