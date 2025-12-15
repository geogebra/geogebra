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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.PlacementProperty.Placement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

/**
 * {@code Property} responsible for setting the point for which an image can be centered around.
 */
public class CenterImagePositionProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions, GeoElementDependentProperty {
	private final GeoImage geoImage;

	/**
	 * Constructs the property for the given element with the provided localization.
	 * @throws NotApplicablePropertyException if the element is not an image
	 */
	public CenterImagePositionProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "Center");
		if (!(geoElement instanceof GeoImage)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoImage = (GeoImage) geoElement;
	}

	@Override
	public String getValue() {
		return PositionPropertyCollection.getPointValue(geoImage.getStartPoint(3));
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return PositionPropertyCollection.validatePointExpression(
				geoImage.getKernel().getParser(), geoImage.getKernel().getLocalization(), value);
	}

	@Override
	protected void doSetValue(String value) {
		PositionPropertyCollection.setCornerPoint(geoImage, 3, value);
	}

	@Override
	public List<String> getSuggestions() {
		return PositionPropertyCollection.getSuggestedPointLabels(geoImage.getConstruction());
	}

	@Override
	public boolean isAvailable() {
		return Placement.of(geoImage) == Placement.CENTER_IMAGE;
	}

	@Override
	public GeoElement getGeoElement() {
		return geoImage;
	}
}
