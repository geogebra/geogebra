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

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.ToggleableIconProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.FontStyleDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the text style to use serif or not.
 */
public class SerifProperty extends AbstractValuedProperty<Boolean>
		implements ToggleableIconProperty {
	private final GeoElementDelegate delegate;

	/**
	 * Constructs the property for the given element.
	 * @param localization localization for translating the property name
	 * @param geoElement the element to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public SerifProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "Serif");
		if (!(geoElement instanceof TextProperties) || geoElement instanceof HasTextFormatter) {
			throw new NotApplicablePropertyException(geoElement);
		}
		delegate = new FontStyleDelegate(geoElement);
	}

	@Override
	protected void doSetValue(Boolean value) {
		GeoElement element = delegate.getElement();
		if (element instanceof TextProperties textProperties) {
			if (textProperties.isSerifFont() != value) {
				textProperties.setSerifFont(value);
				textProperties.updateVisualStyleRepaint(GProperty.FONT);
			}
		}
	}

	@Override
	public Boolean getValue() {
		GeoElement element = delegate.getElement();
		if (element instanceof TextProperties textProperties) {
			return textProperties.isSerifFont();
		}
		return false;
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_SERIF;
	}
}
