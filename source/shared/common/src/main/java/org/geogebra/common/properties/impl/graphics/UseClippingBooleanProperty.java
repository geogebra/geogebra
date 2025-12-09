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

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class UseClippingBooleanProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private final EuclidianView3D euclidianView;

	/**
	 * Creates a boolean property to configure whether clipping should be used
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public UseClippingBooleanProperty(Localization localization, EuclidianView3D euclidianView) {
		super(localization, "UseClipping");
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianView.setUseClippingCube(value);
		euclidianView.repaintView();
	}

	@Override
	public Boolean getValue() {
		return euclidianView.useClippingCube();
	}
}
