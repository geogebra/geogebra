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
import org.geogebra.common.properties.impl.objects.delegate.IsFixedObjectDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for fixing objects. Counterpart of {@link IsFixedObjectProperty}
 * with changes to meet new settings view requirements without altering the original.
 */
public class FixObjectProperty extends AbstractValuedProperty<Boolean> implements BooleanProperty,
		GeoElementDependentProperty {
	private final IsFixedObjectDelegate delegate;

	/**
	 * Constructs the property for the given element with the provided localization.
	 */
	public FixObjectProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "FixObject");
		this.delegate = new IsFixedObjectDelegate(geoElement);
	}

	@Override
	public Boolean getValue() {
		return delegate.getElement().isLocked();
	}

	@Override
	public void doSetValue(Boolean fixObject) {
		delegate.getElement().setFixed(fixObject);
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}
}
