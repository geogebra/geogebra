/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * Sets last pen color in the settings.
	 * @param lastPenColor last pen color
	 */
	public void setLastPenColor(GColor lastPenColor) {
		getPenSettings().setLastSelectedPenColor(lastPenColor);
	}

	public GColor getLastHighlighterColor() {
		return getPenSettings().getLastSelectedHighlighterColor();
	}

	/**
	 * Set last highlighter color in settings.
	 * @param lastHighlighterColor last highlighter color
	 */
	public void setLastHighlighterColor(GColor lastHighlighterColor) {
		getPenSettings().setLastSelectedHighlighterColor(lastHighlighterColor);
	}

	private PenToolsSettings getPenSettings() {
		return appW.getSettings().getPenTools();
	}
}