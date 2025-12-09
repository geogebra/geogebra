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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

import com.google.j2objc.annotations.Weak;

/**
 * This property controls the distance of the axes numbering.
 */
public class AxesNumberingDistanceProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	@Weak
	private final EuclidianSettings euclidianSettings;
	@Weak
	private final Kernel kernel;
	@Weak
	private final EuclidianViewInterfaceCommon euclidianView;

	/**
	 * Constructs an Axes numbering distance property.
	 * @param localization localization for the title
	 */
	AxesNumberingDistanceProperty(Localization localization, EuclidianSettings
			euclidianSettings, EuclidianViewInterfaceCommon euclidianView, Kernel kernel) {
		super(localization, "Automatic");
		this.euclidianSettings = euclidianSettings;
		this.kernel = kernel;
		this.euclidianView = euclidianView;
	}

	@Override
	public Boolean getValue() {
		boolean[] axesAutomaticDistances = euclidianSettings.getAutomaticAxesNumberingDistances();

		for (int i = 0; i < euclidianSettings.getDimension(); i++) {
			if (!axesAutomaticDistances[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void doSetValue(Boolean automatic) {
		if (automatic) {
			setAutoDistance();
		} else {
			setCustomDistance();
		}
	}

	private void setCustomDistance() {
		double[] axesDistances = euclidianView.getAxesNumberingDistances();
		for (int i = 0; i < euclidianSettings.getDimension(); i++) {
			euclidianSettings.setAxisNumberingDistance(i, kernel.getAlgebraProcessor()
					.evaluateToNumeric("" + axesDistances[i] / 2, ErrorHelper.silent()));
		}
	}

	private void setAutoDistance() {
		for (int i = 0; i < euclidianSettings.getDimension(); i++) {
			euclidianSettings.setAutomaticAxesNumberingDistance(true, i, true);
		}
	}
}
