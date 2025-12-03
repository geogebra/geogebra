package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Step
 */
public class AnimationStepProperty extends AbstractNumericProperty {

	private final GeoElement element;

	/***/
	public AnimationStepProperty(AlgebraProcessor algebraProcessor,
			Localization localization, GeoElement element, boolean forSliders)
			throws NotApplicablePropertyException {
		super(algebraProcessor, localization, forSliders ? "Step" : "AnimationStep");
		if (!isValid(element, forSliders)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		element.setAnimationStep(value);
		if (element instanceof GeoNumeric) {
			((GeoNumeric) element).setAutoStep(false);
		}
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	protected NumberValue getNumberValue() {
		return element.getAnimationStepObject();
	}

	/**
	 * @param geo element
	 * @param acceptNumbers whether the property is for numbers (sliders)
	 * @return whether this property is applicable to geo
	 */
	public static boolean isValid(GeoElement geo, boolean acceptNumbers) {
		return geo.isPointerChangeable()
				&& !geo.isGeoText()
				&& !geo.isGeoImage()
				&& !geo.isGeoList()
				&& !geo.isGeoBoolean()
				&& !geo.isGeoButton()
				&& (acceptNumbers == (geo.isGeoNumeric() && geo.isIndependent()));
	}
}
