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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class PenStrokeAbsolutePositionProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, GeoElementDependentProperty {

	private GeoElement element;
	private Set<RedefinitionObserver> redefinitionObservers = new HashSet<>();

	/**
	 * Constructor.
	 * @apiNote Public for testing only.
	 * @param localization localization
	 * @param element an element
	 * @throws NotApplicablePropertyException if element is not an instance of
	 * {@link GeoLocusStroke}.
	 */
	public PenStrokeAbsolutePositionProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "AbsoluteScreenLocation");
		if (!(element instanceof GeoLocusStroke)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	public Boolean getValue() {
		return element.isPinned();
	}

	@Override
	protected void doSetValue(Boolean value) {
		if (value.equals(getValue())) {
			return;
		}
		GeoElement newElement = EuclidianStyleBarStatic.applyFixPosition(List.of(element), value,
				element.getApp().getActiveEuclidianView());
		if (newElement != null) {
			redefinitionObservers.forEach(observer ->
					observer.onGeoElementRedefined(element, newElement));
			element = newElement;
		}
	}

	@Override
	public GeoElement getGeoElement() {
		return element;
	}

	@Override
	public void addRedefinitionObserver(RedefinitionObserver observer) {
		redefinitionObservers.add(observer);
	}
}
