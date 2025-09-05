package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class ButtonWidthProperty extends AbstractNumericProperty {
	private final GeoButton button;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public ButtonWidthProperty(AlgebraProcessor processor, Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(processor, localization, "Width");
		if (!(element instanceof GeoButton) || element instanceof GeoInputBox) {
			throw new NotApplicablePropertyException(element);
		}
		this.button = (GeoButton) element;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		button.setWidth(value.getDouble());
	}

	@Override
	protected NumberValue getNumberValue() {
		return new MyDouble(button.getKernel(), button.getHeight());
	}
}
