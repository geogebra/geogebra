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

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class FillImageProperty extends AbstractValuedProperty<String> implements StringProperty {

	private final GeoElement element;

	/**
	 * @param loc localization
	 * @param element element
	 * @throws NotApplicablePropertyException if not filled by image
	 */
	public FillImageProperty(Localization loc, GeoElement element) throws
			NotApplicablePropertyException {
		super(loc, "Image");
		if (!element.isFillable() || element.getFillType() != FillType.IMAGE) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	@Override
	protected void doSetValue(String value) {
		element.setImageFileName(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public String getValue() {
		return element.getImageFileName();
	}
}
