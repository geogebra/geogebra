package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.SliderPropertyDelegate;

/**
 * Min property
 */
public class MinProperty extends AbstractNumericProperty {

	private final SliderPropertyDelegate delegate;

	/***/
	public MinProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Minimum.short");
		delegate = new SliderPropertyDelegate(element);
	}

	@Override
	public Double getValue() {
		return delegate.getElement().getIntervalMin();
	}

	@Override
	public void setValue(Double value) {
		GeoNumeric numeric = delegate.getElement();
		numeric.setIntervalMin(value);
	}
}
