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
