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
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class ImageInterpolationProperty  extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final GeoImage image;

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param geo the construction element
	 */
	public ImageInterpolationProperty(Localization localization, GeoElement geo)
			throws NotApplicablePropertyException {
		super(localization, "Interpolate");
		if (!(geo instanceof GeoImage)) {
			throw new NotApplicablePropertyException(geo);
		}
		this.image = (GeoImage) geo;
	}

	@Override
	protected void doSetValue(Boolean value) {
		image.setInterpolate(value);
		image.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return image.isInterpolate();
	}
}
