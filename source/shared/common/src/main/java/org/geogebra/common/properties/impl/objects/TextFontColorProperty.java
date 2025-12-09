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

package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.GeoColorValues;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextColorPropertyDelegate;

public class TextFontColorProperty extends ElementColorProperty
		implements ColorProperty {

	private final GeoElement element;

	/**
	 * @param localization - localization
	 * @param element - element
	 * @throws NotApplicablePropertyException when one of the elements has no color
	 */
	public TextFontColorProperty(Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(localization, new TextColorPropertyDelegate(element));
		this.element = element;
		setValues(GeoColorValues.values());
	}

	@Override
	public void doSetValue(GColor value) {
		EuclidianStyleBarStatic.applyColor(value, element.getAlphaValue(), element.getApp(),
				List.of(element));

		if (element instanceof HasTextFormatter) {
			((HasTextFormatter) element).format("color", value.toString());
		}
	}

	@Override
	public GColor getValue() {
		if (element instanceof HasTextFormatter) {
			return GColor.getGColor(((HasTextFormatter) element)
					.getFormat("color", null));
		}

		return super.getValue();
	}
}
