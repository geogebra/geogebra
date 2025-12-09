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

package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class StickToEdgeProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final int axis;
	private EuclidianSettings euclidianSettings;
	private EuclidianViewInterfaceCommon euclidianView;

	/** Creates a property enable/disable stick to edge property of axis.
	 * @param localization localization
	 * @param axis axis index
	 * @param euclidianSettings euclidian settings
	 * @param euclidianView euclidian view
	 */
	public StickToEdgeProperty(Localization localization, int axis,
			EuclidianSettings euclidianSettings, EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "StickToEdge");
		this.axis = axis;
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setDrawBorderAxes(axis, value);
		if (!value) {
			euclidianSettings.setAxisCross(axis, 0.0);
		}

		euclidianView.updateBackground();
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getDrawBorderAxes()[axis];
	}
}
