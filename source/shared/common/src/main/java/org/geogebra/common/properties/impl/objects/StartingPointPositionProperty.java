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

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.PlacementProperty.Placement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

/**
 * {@code Property} responsible for setting the starting point of an object used as an anchor.
 */
public class StartingPointPositionProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions, GeoElementDependentProperty {
	private final GeoElement geoElement;

	/**
	 * Constructs the property for the given element with the provided localization.
	 * @throws NotApplicablePropertyException if the element is an image
	 */
	public StartingPointPositionProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "StartingPoint");
		if (geoElement instanceof GeoImage || geoElement instanceof GeoBoolean
				|| !(geoElement instanceof Locateable)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = geoElement;
	}

	@Override
	public List<String> getSuggestions() {
		return PositionPropertyCollection.getSuggestedPointLabels(geoElement.getConstruction());
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return PositionPropertyCollection.validatePointExpression(geoElement.getKernel()
						.getParser(), geoElement.getKernel().getLocalization(), value);
	}

	@Override
	public String getValue() {
		return PositionPropertyCollection.getPointValue(((Locateable) geoElement).getStartPoint());
	}

	@Override
	protected void doSetValue(String value) {
		PositionPropertyCollection.setCornerPoint(geoElement, 0, value);
	}

	@Override
	public boolean isAvailable() {
		return Placement.of(geoElement) == Placement.STARTING_POINT;
	}

	@Override
	public GeoElement getGeoElement() {
		return geoElement;
	}
}
