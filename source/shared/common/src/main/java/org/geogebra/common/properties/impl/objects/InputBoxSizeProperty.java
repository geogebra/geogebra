package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class InputBoxSizeProperty extends AbstractNumericProperty {
	private final GeoInputBox inputBox;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public InputBoxSizeProperty(AlgebraProcessor processor,
			Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(processor, localization, "Size");
		if (!(element instanceof GeoInputBox)) {
			throw new NotApplicablePropertyException(element);
		}
		this.inputBox = (GeoInputBox) element;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		inputBox.setLength((int) value.getDouble());
	}

	@Override
	protected NumberValue getNumberValue() {
		return new MyDouble(inputBox.getKernel(), inputBox.getLength());
	}
}
