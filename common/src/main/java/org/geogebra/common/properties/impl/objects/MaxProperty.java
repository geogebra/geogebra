package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.SliderPropertyDelegate;

/**
 * Max property
 */
public class MaxProperty extends AbstractNumericProperty {

	private final SliderPropertyDelegate delegate;

	/***/
	public MaxProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Maximum.short");
		delegate = new SliderPropertyDelegate(element);
	}

	@Override
	public Double getValue() {
		return delegate.getElement().getIntervalMax();
	}

	@Override
	public void setValue(Double value) {
		delegate.getElement().setIntervalMax(value);
	}
}
