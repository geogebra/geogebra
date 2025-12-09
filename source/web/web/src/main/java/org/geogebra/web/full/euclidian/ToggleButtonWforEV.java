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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.resources.SVGResource;

/**
 * Toggle button that should be visible if no geos are selected or to be
 * created and no special icons appear in stylebar (eg. delete mode)
 */
public class ToggleButtonWforEV extends ToggleButton {
	private EuclidianStyleBarW stylebar;

	/**
	 * @param img - image
	 * @param stylebar - parent stylebar
	 */
	public ToggleButtonWforEV(SVGResource img,
			EuclidianStyleBarW stylebar) {
		super(img);
		this.stylebar = stylebar;
	}

	@Override
	public void update(List<GeoElement> geos) {
		if (stylebar.app.isUnbundledOrWhiteboard()) {
			this.setVisible(geos.size() == 0);
		} else {
			int mode = stylebar.mode;
			this.setVisible(geos.size() == 0 && !EuclidianView.isPenMode(mode)
					&& mode != EuclidianConstants.MODE_DELETE
					&& mode != EuclidianConstants.MODE_ERASER);
		}
	}
}
