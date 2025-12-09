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

package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;

public class AbsoluteScreenLocationModel extends BooleanOptionModel {

	public AbsoluteScreenLocationModel(App app) {
		super(null, app);
	}

	private AbsoluteScreenLocateable getAbsoluteScreenLocateable(int index) {
		return (AbsoluteScreenLocateable) getObjectAt(index);
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isPinned();
	}

	@Override
	public void apply(int index, boolean value) {
		if (getObjectAt(index) instanceof AbsoluteScreenLocateable) {

			AbsoluteScreenLocateable geo = getAbsoluteScreenLocateable(index);
			setAbsolute(geo, value, app.getActiveEuclidianView());
		} else if (getGeoAt(index).isPinnable()) {
			ArrayList<GeoElement> al = new ArrayList<>();
			al.add(getGeoAt(index));

			// geo could be redefined, so need to change geos[i] to
			// new geo
			EuclidianStyleBarStatic.applyFixPosition(al, value,
					app.getActiveEuclidianView());
		}
		storeUndoInfo();
	}

	@Override
	public String getTitle() {
		return "AbsoluteScreenLocation";
	}

	public static void setAbsolute(AbsoluteScreenLocateable geo,
			boolean value, EuclidianViewInterfaceCommon ev) {
		if (value) {
			if (!geo.isAbsoluteScreenLocActive()) {
				// convert real world to screen coords
				int x = ev.toScreenCoordX(geo.getRealWorldLocX());
				int y = ev.toScreenCoordY(geo.getRealWorldLocY());
				geo.setAbsoluteScreenLoc(x, y);
			}
		} else {
			if (geo.isAbsoluteScreenLocActive()) {
				// convert screen coords to real world
				double x = ev.toRealWorldCoordX(geo.getAbsoluteScreenLocX());
				double y = ev.toRealWorldCoordY(geo.getAbsoluteScreenLocY());
				geo.setRealWorldLoc(x, y);
			}
		}
		geo.setAbsoluteScreenLocActive(value);
		if (geo.needsUpdatedBoundingBox()) {
			geo.updateCascade();
		}
		geo.updateVisualStyleRepaint(GProperty.POSITION);
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (geo instanceof AbsoluteScreenLocateable) {
			AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
			if (!absLoc.isAbsoluteScreenLocateable() || geo.isGeoBoolean()
					|| geo instanceof GeoList || (geo instanceof GeoImage
							&& ((GeoImage) geo).isCentered())) {

				return false;
			}
		} else if (!geo.isPinnable()) {
			return false;
		}
		// whiteboard: no abs position for texts or images
		return !geo.getKernel().getApplication().isWhiteboardActive();
	}

}
