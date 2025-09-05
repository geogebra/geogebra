package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class AnimationSpeedProperty extends AbstractNumericProperty {

	private final GeoElement element;

	/***/
	public AnimationSpeedProperty(AlgebraProcessor algebraProcessor,
			Localization localization, GeoElement element) throws NotApplicablePropertyException {
		super(algebraProcessor, localization, "AnimationSpeed");
		if (!element.isAnimatable()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		element.setAnimationSpeedObject(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	protected NumberValue getNumberValue() {
		return (NumberValue) element.getAnimationSpeedObject();
	}
}
