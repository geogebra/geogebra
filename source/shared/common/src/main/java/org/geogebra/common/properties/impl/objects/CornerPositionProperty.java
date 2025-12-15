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
 * {@code Property} responsible for setting the corners of an image.
 */
public class CornerPositionProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions, GeoElementDependentProperty {
	private final GeoImage geoImage;
	private final int cornerIndex;

	/**
	 * Constructs the property
	 * @param localization localization to use for the name of the property
	 * @param geoElement the element to create the property for
	 * @param cornerIndex the index of the corner (either 0, 1, or 2, representing corner 1, 2, or 4)
	 * @throws NotApplicablePropertyException if the given element is not an image
	 */
	public CornerPositionProperty(Localization localization, GeoElement geoElement, int cornerIndex)
			throws NotApplicablePropertyException {
		super(localization, "CornerPoint");
		if (!(geoElement instanceof GeoImage)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoImage = (GeoImage) geoElement;
		this.cornerIndex = cornerIndex;
	}

	@Override
	public String getName() {
		switch (cornerIndex) {
		case 0: return super.getName() + " 1";
		case 1: return super.getName() + " 2";
		case 2: return super.getName() + " 4";
		default: return "";
		}
	}

	@Override
	public List<String> getSuggestions() {
		return PositionPropertyCollection.getSuggestedPointLabels(geoImage.getConstruction());
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return PositionPropertyCollection.validatePointExpression(
				geoImage.getKernel().getParser(), geoImage.getKernel().getLocalization(), value);
	}

	@Override
	public String getValue() {
		return PositionPropertyCollection.getPointValue(geoImage.getStartPoint(cornerIndex));
	}

	@Override
	protected void doSetValue(String value) {
		PositionPropertyCollection.setCornerPoint(geoImage, cornerIndex, value);
	}

	@Override
	public boolean isAvailable() {
		return Placement.of(geoImage) == Placement.CORNERS;
	}

	@Override
	public GeoElement getGeoElement() {
		return geoImage;
	}
}

