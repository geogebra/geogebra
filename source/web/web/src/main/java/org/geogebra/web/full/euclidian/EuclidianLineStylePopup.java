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

package org.geogebra.web.full.euclidian;

import java.util.List;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.util.LineStylePopup;
import org.geogebra.web.html5.main.AppW;

/**
 * Line style popup
 */
public class EuclidianLineStylePopup extends LineStylePopup {
	private final LineStyleModel model;

	/**
	 * @param app
	 *            application
	 */
	public EuclidianLineStylePopup(AppW app) {
		super(app, LineStylePopup.getLineStyleIcons(), -1, 5,
				SelectionTable.MODE_ICON, true, true);
		model = new LineStyleModel(app);
		this.setKeepVisible(false);
	}

	@Override
	public void update(List<GeoElement> geos) {
		if (geos.isEmpty()) {
			this.setVisible(false);
			return;
		}

		model.setGeos(geos.toArray());
		boolean geosOK = model.checkGeos();
		this.setVisible(geosOK);

		if (geosOK) {
			GeoElement geo0 = model.getGeoAt(0);
			if (hasSlider()) {
				setSliderValue(geo0.getLineThickness());
				getSlider().setMinimum(model.maxMinimumThickness());
			}
			selectLineType(geo0.getLineType());
		}
	}
}
