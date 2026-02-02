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
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.FontStyleDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class SerifProperty extends AbstractValuedProperty<Boolean> implements BooleanProperty,
		IconAssociatedProperty {
	private final GeoElementDelegate delegate;

	/**
	 * Serif property
	 * @param localization localization
	 * @param element element
	 */
	public SerifProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Serif");
		delegate = new FontStyleDelegate(element);
	}

	@Override
	protected void doSetValue(Boolean value) {
		GeoElement element = delegate.getElement();
		if (element instanceof TextProperties) {
			TextProperties textProperties = (TextProperties) element;
			if (textProperties.isSerifFont() != value) {
				textProperties.setSerifFont(value);
				textProperties.updateVisualStyleRepaint(GProperty.FONT);
			}
		}
	}

	@Override
	public Boolean getValue() {
		GeoElement element = delegate.getElement();
		if (element instanceof TextProperties) {
			TextProperties textProperties = (TextProperties) element;
			return textProperties.isSerifFont();
		}
		return false;
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_SERIF;
	}
}
