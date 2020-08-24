package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
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
	public MinProperty(AlgebraProcessor processor, Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(processor, localization, "Minimum.short");
		delegate = new SliderPropertyDelegate(element);
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		delegate.getElement().setIntervalMin(value);
	}

	@Override
	protected NumberValue getNumberValue() {
		return delegate.getElement().getIntervalMinObject();
	}
}
