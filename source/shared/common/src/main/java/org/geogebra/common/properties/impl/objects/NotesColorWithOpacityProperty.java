package org.geogebra.common.properties.impl.objects;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.ColorValues;
import org.geogebra.common.main.color.GeoColorValues;
import org.geogebra.common.properties.impl.objects.delegate.FillingStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class NotesColorWithOpacityProperty extends ElementColorProperty {

	/**
	 * @param localization - localization
	 * @param element - element
	 * @throws NotApplicablePropertyException when one of the elements has no color
	 */
	public NotesColorWithOpacityProperty(Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(localization, new FillingStylePropertyDelegate(element));
		setValues(Arrays.stream(GeoColorValues.values()).map(ColorValues::getColor)
				.collect(Collectors.toList()));
	}
}
