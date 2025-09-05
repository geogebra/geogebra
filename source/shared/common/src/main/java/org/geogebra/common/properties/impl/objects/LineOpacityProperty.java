package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class LineOpacityProperty extends AbstractRangeProperty<Integer> {
	private final GeoElement element;

	/**
	 * Create a new AbstractRangeProperty.
	 * @param localization localization
	 * @param element element
	 */
	public LineOpacityProperty(Localization localization, GeoElement element) throws
			NotApplicablePropertyException {
		super(localization, "LineOpacity", 0, 100, 1);
		if (element.showLineProperties()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void setValueSafe(Integer value) {
		element.setLineOpacity(value);
	}

	@Override
	public Integer getValue() {
		return element.getLineOpacity();
	}
}
