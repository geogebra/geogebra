package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Step
 */
public class StepProperty extends RangelessDecimalProperty {

	private SliderPropertyDelegate delegate;

	public StepProperty(GeoNumeric numeric) throws NotApplicablePropertyException {
		super("AnimationStep", numeric);
	}

	@Override
	public Double getValue() {
		return getElement().getAnimationStep();
	}

	@Override
	public void setValue(Double value) {
		GeoNumeric numeric = getElement();
		numeric.setAnimationStep(value);
	}

	@Override
	boolean isApplicableTo(GeoElement element) {
		return getDelegate().isSlider(element);
	}

	private SliderPropertyDelegate getDelegate() {
		if (delegate == null) {
			delegate = new SliderPropertyDelegate(this);
		}
		return delegate;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
