package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.SliderPropertyDelegate;

/**
 * Step
 */
public class AnimationStepProperty extends AbstractNumericProperty {

	private final SliderPropertyDelegate delegate;

	/***/
	public AnimationStepProperty(AlgebraProcessor algebraProcessor,
			Localization localization, GeoElement element) throws NotApplicablePropertyException {
		super(algebraProcessor, localization, "AnimationStep");
		delegate = new SliderPropertyDelegate(element);
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		GeoNumeric element = delegate.getElement();
		element.setAnimationStep(value);
		element.setAutoStep(false);
	}

	@Override
	protected NumberValue getNumberValue() {
		return delegate.getElement().getAnimationStepObject();
	}
}
