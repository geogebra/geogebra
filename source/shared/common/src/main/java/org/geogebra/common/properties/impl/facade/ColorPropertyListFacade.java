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

package org.geogebra.common.properties.impl.facade;

import java.util.List;
import java.util.Objects;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.jspecify.annotations.NonNull;

/**
 * Handles a collection of ColorProperty objects as a single ColorProperty.
 */
public class ColorPropertyListFacade<T extends ColorProperty>
		extends EnumeratedPropertyListFacade<T, GColor> implements ColorProperty {

	/**
	 * @param properties properties to handle
	 */
	public ColorPropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public @NonNull List<GColor> getValues() {
		return getFirstProperty().getValues();
	}

	@Override
	public boolean hasUniformValue() {
		GColor first = getFirstProperty().getValue();
		for (T property : getPropertyList()) {
			if (!property.hasUniformValue() || !Objects.equals(first, property.getValue())) {
				return false;
			}
		}
		return true;
	}
}
