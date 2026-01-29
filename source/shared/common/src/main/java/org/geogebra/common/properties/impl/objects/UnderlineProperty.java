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
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextFormatterDelegate;

public class UnderlineProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, IconAssociatedProperty {
	private final GeoElementDelegate delegate;

	/**
	 * Underline property
	 * @param localization localization
	 * @param element element
	 */
	public UnderlineProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Underline");
		delegate = new TextFormatterDelegate(element);
	}

	@Override
	protected void doSetValue(Boolean value) {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		if (getLocalization() != null && !value.equals(element
				.getFormat("underline", false))) {
			element.format("underline", value);
		}
		((GeoElement) element).updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		return element.getFormat("underline", false);
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_UNDERLINE;
	}
}
