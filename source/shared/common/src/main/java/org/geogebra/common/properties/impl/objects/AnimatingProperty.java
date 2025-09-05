package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class AnimatingProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final GeoElement element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public AnimatingProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Animating");
		if (!element.isAnimatable()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setAnimating(value);
		element.updateRepaint();
		if (value) {
			element.getKernel().getAnimationManager().startAnimation();
		}
	}

	@Override
	public Boolean getValue() {
		return element.isAnimating();
	}
}

