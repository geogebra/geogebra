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

package org.geogebra.common.euclidian;

import java.util.List;

import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.ownership.SuiteScope;

/**
 * An {@link EuclidianStyleBar} implementation backed by a {@link QuickStyleBarModel}.
 */
public class QuickStyleBar implements EuclidianStyleBar {

	private final App app;
	private final QuickStyleBarModel model;
	private boolean shouldShow;

	/**
	 * @param app the app
	 * @param localization localization for property names
	 * @param positioner calculates the popup position on the canvas
	 */
	public QuickStyleBar(App app, Localization localization, StylebarPositioner positioner) {
		this.app = app;
		SuiteScope suiteScope = GlobalScope.getSuiteScope(app);
		assert suiteScope != null;
		this.model = new QuickStyleBarModel(app, suiteScope.geoElementPropertiesFactory,
				localization, positioner);
	}

	/**
	 * @return the underlying model
	 */
	public QuickStyleBarModel getModel() {
		return model;
	}

	@Override
	public void setMode(int mode) {
		// not needed
	}

	@Override
	public void setLabels() {
		// not needed
	}

	@Override
	public void restoreDefaultGeo() {
		// not needed
	}

	@Override
	public void updateStyleBar() {
		List<GeoElement> selectedElements = app.getSelectionManager().getSelectedGeos();
		if (shouldShow && !selectedElements.isEmpty()) {
			model.show(selectedElements);
		} else {
			model.hide();
		}
	}

	@Override
	public void updateButtonPointCapture(int mode) {
		// not needed
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		updateStyleBar();
	}

	@Override
	public int getPointCaptureSelectedIndex() {
		return 0;
	}

	@Override
	public void updateGUI() {
		// not needed
	}

	@Override
	public void hidePopups() {
		// not needed
	}

	@Override
	public void resetFirstPaint() {
		// not needed
	}

	@Override
	public void reinit() {
		// not needed
	}

	@Override
	public void setVisible(boolean visible) {
		shouldShow = visible;
		updateStyleBar();
	}

	@Override
	public boolean isVisible() {
		return model.getButtons() != null;
	}
}
