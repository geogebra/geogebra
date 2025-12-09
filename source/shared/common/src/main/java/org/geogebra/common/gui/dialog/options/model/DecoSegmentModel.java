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
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.SegmentProperties;
import org.geogebra.common.main.App;

public class DecoSegmentModel extends IconOptionsModel {

	private IComboListener listener;

	public DecoSegmentModel(App app) {
		super(app);
	}

	private SegmentProperties getGeoSegmentAt(int index) {
		return (SegmentProperties) getObjectAt(index);
	}

	@Override
	public void setListener(IComboListener listener) {
		this.listener = listener;
	}

	@Override
	public String getTitle() {
		return "Decoration";
	}

	@Override
	public void updateProperties() {

		SegmentProperties geo0 = getGeoSegmentAt(0);
		listener.setSelectedIndex(geo0.getDecorationType());

	}

	@Override
	public boolean isValidAt(int index) {
		return getObjectAt(index) instanceof SegmentProperties;
	}

	@Override
	protected void apply(int index, int value) {
		SegmentProperties geo = getGeoSegmentAt(index);
		geo.setDecorationType(value);
		geo.updateVisualStyleRepaint(GProperty.DECORATION);
	}

	@Override
	protected int getValueAt(int index) {
		return getGeoSegmentAt(index).getDecorationType();
	}

	public static int getDecoTypeLength() {
		return GeoSegment.getDecoTypes().length;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}
