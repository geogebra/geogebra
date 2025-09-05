package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class AngleArcSizeProperty extends AbstractValuedProperty<Integer>
		implements RangeProperty<Integer> {

	private AngleProperties geoElement;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public AngleArcSizeProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Size");
		if (element instanceof AngleProperties) {
			geoElement = (AngleProperties) element;
		} else {
			throw new NotApplicablePropertyException(element);
		}
	}

	@Override
	public Integer getMin() {
		return 10;
	}

	@Override
	public Integer getMax() {
		return 100;
	}

	@Override
	public Integer getStep() {
		return 1;
	}

	@Override
	public Integer getValue() {
		return geoElement.getArcSize();
	}

	@Override
	protected void doSetValue(Integer value) {
		geoElement.setArcSize(value);
	}
}
