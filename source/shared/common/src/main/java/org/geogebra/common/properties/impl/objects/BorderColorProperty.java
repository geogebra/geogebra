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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.GeoColorValues;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextMindmapDelegate;

public class BorderColorProperty extends ElementColorProperty {
	private final GeoElement element;

	/**
	 * @param localization - localization
	 * @param element - element
	 * @throws NotApplicablePropertyException when one of the elements has no color
	 */
	public BorderColorProperty(Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(localization, new TextMindmapDelegate(element), "stylebar.Borders");
		this.element = element;
		setValues(GeoColorValues.values());
	}

	@Override
	public void doSetValue(GColor color) {
		GeoInline geoInline = (GeoInline) element;
		if (!geoInline.getBorderColor().equals(color)) {
			geoInline.setBorderColor(color);
			element.updateVisualStyle(GProperty.LINE_STYLE);
		}
	}

	@Override
	public GColor getValue() {
		GeoInline geoInline = (GeoInline) element;
		return geoInline.getBorderColor();
	}
}
