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

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.SegmentStyle;
import org.geogebra.common.main.App;

public class SegmentStyleModel extends IconOptionsModel {
	private IComboListener listener;
	private boolean isStartStyle;

	public SegmentStyleModel(App app, boolean isStartStyle) {
		super(app);
		this.isStartStyle = isStartStyle;
	}

	@Override
	public void setListener(IComboListener listener) {
		this.listener = listener;
	}

	@Override
	public String getTitle() {
		return isStartStyle ? "stylebar.LineStartStyle" : "stylebar.LineEndStyle";
	}

	@Override
	protected boolean isValidAt(int index) {
		boolean isValid = false;
		GeoElement geo = getGeoAt(index);
		if (geo instanceof GeoSegment) {
			isValid = true;
		}
		return isValid;
	}

	@Override
	public void updateProperties() {
		GeoElement geo = getGeoAt(0);
		if (geo instanceof GeoSegment) {
			SegmentStyle style = isStartStyle ? ((GeoSegment) geo).getStartStyle()
					: ((GeoSegment) geo).getEndStyle();
			listener.setSelectedIndex(style.ordinal());
		}
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

	@Override
	protected void apply(int index, int value) {
		GeoElement geo = getGeoAt(index);
		if (geo instanceof GeoSegment) {
			if (isStartStyle) {
				((GeoSegment) geo).setStartStyle(SegmentStyle.values()[value]);
			} else {
				((GeoSegment) geo).setEndStyle(SegmentStyle.values()[value]);
			}
			geo.updateVisualStyleRepaint(GProperty.COMBINED);
			app.updateStyleBars();
		}
	}

	@Override
	protected int getValueAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (geo instanceof GeoSegment) {
			return isStartStyle ? ((GeoSegment) geo).getStartStyle().ordinal()
					: ((GeoSegment) geo).getEndStyle().ordinal();
		}
		return 0;
	}

	public boolean isStartStyle() {
		return isStartStyle;
	}

}
