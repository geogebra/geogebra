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

package org.geogebra.web.geogebra3D.web.euclidian3D;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.euclidian.PopupMenuButtonWithDefault;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

/**
 * Popup for axes and coordinate plane
 *
 */
public class AxesAndPlanePopup extends PopupMenuButtonWithDefault {

	private EuclidianView3D ev;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            icons
	 * @param ev
	 *            view
	 */
	public AxesAndPlanePopup(AppW app, ImageOrText[] data, EuclidianView3D ev) {
		super(app, data);
		this.ev = ev;
		this.setIcon(data[getIndexFromEV()]);
	}

	private int getIndexFromEV() {
		int ret = 0;
		if (ev.getShowXaxis()) {
			ret++;
		}
		if (ev.getShowPlane()) {
			ret += 2;
		}
		return ret;
	}

	/**
	 * Select item based on current EV state
	 */
	public void setIndexFromEV() {
		setSelectedIndex(getIndexFromEV());
	}

	/**
	 * set euclidian view from index
	 * @return whether undoable change occurred
	 */
	public boolean setEVFromIndex() {
		int index = getSelectedIndex();
		ev.getSettings().beginBatch();
		boolean changed = ev.getSettings().setShowAxes(MyDouble.isOdd(index));
		changed = ev.getSettings().setShowPlate(index >= 2) || changed;
		ev.getSettings().endBatch();
		((EuclidianView3DW) ev).doRepaint();
		return changed;
	}

	@Override
	public void update(List<GeoElement> geos) {
		this.setVisible(
				geos.size() == 0 && !EuclidianView.isPenMode(app.getMode())
						&& app.getMode() != EuclidianConstants.MODE_DELETE);
	}

}
