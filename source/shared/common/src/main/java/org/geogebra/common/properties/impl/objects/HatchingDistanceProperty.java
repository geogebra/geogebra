package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class HatchingDistanceProperty extends AbstractRangeProperty<Integer>
		implements RangeProperty<Integer> {
	private final GeoElement element;

	/**
	 * Create a new property
	 */
	public HatchingDistanceProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Distance", 0, 90, 45);
		if (!element.getFillType().isHatch()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void setValueSafe(Integer value) {
		element.setHatchingDistance(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Integer getValue() {
		return element.getHatchingDistance();
	}
}
