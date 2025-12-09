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

import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class DimensionMinMaxProperty extends AbstractValuedProperty<String>
		implements StringProperty {
	private App app;
	private EuclidianOptionsModel.MinMaxType type;
	private EuclidianSettings euclidianSettings;

	/**
	 * Creates a bounds property of graphics view
	 * @param app application
	 * @param localization localization
	 * @param name name of property
	 * @param euclidianSettings euclidian settings
	 * @param type
	 * {@link org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.MinMaxType}
	 */
	public DimensionMinMaxProperty(App app, Localization localization, String name,
			EuclidianSettings euclidianSettings, EuclidianOptionsModel.MinMaxType type) {
		super(localization, name);
		this.app = app;
		this.euclidianSettings = euclidianSettings;
		this.type = type;
	}

	@Override
	protected void doSetValue(String value) {
		NumberValue numberValue = app.getKernel().getAlgebraProcessor()
				.evaluateToNumeric(value, true);
		if (numberValue == null) {
			return;
		}
		switch (type) {
		case maxX:
			euclidianSettings.setXmaxObject(numberValue, true);
			break;
		case maxY:
			euclidianSettings.setYmaxObject(numberValue, true);
			break;
		case minX:
			euclidianSettings.setXminObject(numberValue, true);
			break;
		case minY:
			euclidianSettings.setYminObject(numberValue, true);
			break;
		case minZ:
			((EuclidianSettings3D) euclidianSettings).setZminObject(numberValue, true);
			break;
		case maxZ:
			((EuclidianSettings3D) euclidianSettings).setZmaxObject(numberValue, true);
			break;
		default:
		}
	}

	@Override
	public String getValue() {
		switch (type) {
		case maxX:
			return euclidianSettings.getXmaxObject().getLabel(StringTemplate.editTemplate);
		case maxY:
			return euclidianSettings.getYmaxObject().getLabel(StringTemplate.editTemplate);
		case minX:
			return euclidianSettings.getXminObject().getLabel(StringTemplate.editTemplate);
		case minY:
			return euclidianSettings.getYminObject().getLabel(StringTemplate.editTemplate);
		case minZ:
			return ((EuclidianSettings3D) euclidianSettings).getZminObject()
					.getLabel(StringTemplate.editTemplate);
		case maxZ:
			return ((EuclidianSettings3D) euclidianSettings).getZmaxObject()
					.getLabel(StringTemplate.editTemplate);
		default:
			return "";
		}
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		NumberValue numberValue = app.getKernel().getAlgebraProcessor()
					.evaluateToNumeric(value, true);
		return numberValue == null ? getLocalization().getError("InputError.Enter_a_number")
				: null;
	}
}
