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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.IsFixedObjectDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Corresponds to the {@link GeoElement#setFixed(boolean)} / {@link GeoElement#isLocked()} property.
 */
public class IsFixedObjectProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private final AbstractGeoElementDelegate delegate;

	/***/
	public IsFixedObjectProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "fixed");
		delegate = new IsFixedObjectDelegate(element);
	}

	@Override
	public Boolean getValue() {
		return delegate.getElement().isLocked();
	}

	@Override
	public void doSetValue(Boolean fixObject) {
		GeoElement element = delegate.getElement();
		element.setFixed(fixObject);
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}
