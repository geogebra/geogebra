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

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class ObliqueAngleProperty extends AbstractValuedProperty<String>
	implements StringProperty, SettingsDependentProperty {
	private EuclidianView3DInterface euclidianView;

	/**
	 * Creates a distance from screen property used for
	 * {@link ProjectionsProperty} PROJECTION_OBLIQUE
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public ObliqueAngleProperty(Localization localization, EuclidianView3DInterface euclidianView) {
		super(localization, "Angle");
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(String value) {
		double val = Double.parseDouble(value);
		if (!Double.isNaN(val)) {
			euclidianView.getSettings().setProjectionObliqueAngle(val);
			euclidianView.repaintView();
		}
	}

	@Override
	public String getValue() {
		return String.valueOf(euclidianView.getProjectionObliqueAngle());
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		try {
			Double.parseDouble(value);
			return null;
		} catch (Exception e) {
			return getLocalization().getError("InputError.Enter_a_number");
		}
	}

	@Override
	public boolean isAvailable() {
		return euclidianView.getProjection() == EuclidianView3DInterface.PROJECTION_OBLIQUE;
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianView.getSettings();
	}
}
