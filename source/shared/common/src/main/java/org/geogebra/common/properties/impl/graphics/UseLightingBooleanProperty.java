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

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class UseLightingBooleanProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private final EuclidianSettings3D euclidianSettings;
	private final EuclidianView euclidianView;

	/**
	 * Creates a property to use lighting
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 * @param euclidianView euclidian view
	 */
	public UseLightingBooleanProperty(Localization localization,
			EuclidianSettings3D euclidianSettings, EuclidianView euclidianView) {
		super(localization, "UseLighting");
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getUseLight();
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setUseLight(value);
		euclidianView.repaintView();
	}
}
