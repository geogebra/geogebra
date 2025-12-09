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

package org.geogebra.web.full.gui.toolbar;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.util.HasResource;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SVGResourcePrototype;

/**
 * @author csilla
 *
 */
public class ToolButton extends StandardButton {

	private final int mode;
	private final AppW appW;
	private final String selectedColor;

	/**
	 * @param mode
	 *            tool mode
	 * @param app
	 *            see {@link AppW}
	 */
	public ToolButton(int mode, AppW app) {
		super(SVGResourcePrototype.EMPTY, app.getToolName(mode), 24);
		this.mode = mode;
		this.appW = app;
		this.selectedColor = app.getGeoGebraElement().getPrimaryColor(app.getFrameElement());

		setStyleName("toolButton");
		setAccessible();
		setSelected(false); // update icon
	}

	private void setAccessible() {
		setLabel();
		getElement().setAttribute("mode", mode + "");
		getElement().setId("mode" + mode);
	}

	/**
	 * Switch between default and tinted icon
	 * @param selected
	 *            true if tool is selected
	 */
	public void setSelected(final boolean selected) {
		GGWToolBar.getImageResource(mode, appW, selected ? getFillAdapter() : this);
	}

	private HasResource getFillAdapter() {
		return resource -> {
			if (resource instanceof SVGResource) {
				SVGResource filled = ((SVGResource) resource).withFill(selectedColor);
				this.setResource(filled);
			}
		};
	}

	/**
	 * set localized label of buttons
	 */
	public void setLabel() {
		setLabel(appW.getToolName(mode));
		setAltText(appW.getToolName(mode) + ". " + appW.getToolHelp(mode));
	}

	/**
	 * @return associated mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param appMode current mode of the app
	 */
	public void updateSelected(int appMode) {

		boolean selected = (mode == appMode) || isMeasurementToolSelected();
		getElement().setAttribute("selected",
				String.valueOf(selected));
		setSelected(selected);
	}

	/**
	 *
	 * @return if a ruler or one of the protractors are selected.
	 */
	private boolean isMeasurementToolSelected() {
		return mode == EuclidianConstants.MODE_RULER
				|| mode == EuclidianConstants.MODE_PROTRACTOR;
	}
}
