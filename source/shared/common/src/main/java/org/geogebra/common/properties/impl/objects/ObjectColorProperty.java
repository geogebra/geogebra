package org.geogebra.common.properties.impl.objects;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.ColorValues;
import org.geogebra.common.main.color.GeoColorValues;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.ObjectColorPropertyDelegate;

public class ObjectColorProperty extends ElementColorProperty {

	/**
	 * @param localization - localization
	 * @param element - element
	 * @throws NotApplicablePropertyException when one of the elements has no color
	 */
	public ObjectColorProperty(Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(localization, new ObjectColorPropertyDelegate(element));
	}

	@Nonnull
	@Override
	public List<GColor> getValues() {
		return Arrays.stream(GeoColorValues.values()).map(ColorValues::getColor)
				.collect(Collectors.toList());
	}
}
