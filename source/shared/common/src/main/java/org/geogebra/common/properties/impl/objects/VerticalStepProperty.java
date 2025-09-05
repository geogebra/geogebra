package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class VerticalStepProperty extends AbstractNumericProperty {

	private final GeoPointND element;

	/***/
	public VerticalStepProperty(AlgebraProcessor algebraProcessor,
			Localization localization, GeoElement element) throws NotApplicablePropertyException {
		super(algebraProcessor, localization, "IncrementVertical");
		if (!(element instanceof GeoPointND)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (GeoPointND) element;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		element.setVerticalIncrement(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	protected NumberValue getNumberValue() {
		return element.getVerticalIncrement();
	}
}

