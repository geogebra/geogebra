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
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

/**
 * Grid style popup
 *
 */
public class GridPopup extends PopupMenuButtonWithDefault {

	/**
	 * @param app
	 *            app
	 * @param data
	 *            icons
	 * @param ev
	 *            view
	 */
	public GridPopup(AppW app, ImageOrText[] data, EuclidianView ev) {
		super(app, data);
		this.setIcon(data[EuclidianStyleBarW.gridIndex(ev)]);
	}

	@Override
	public void update(List<GeoElement> geos) {
		this.setVisible(
				geos.size() == 0 && !EuclidianView.isPenMode(app.getMode())
						&& app.getMode() != EuclidianConstants.MODE_DELETE);
	}

}
