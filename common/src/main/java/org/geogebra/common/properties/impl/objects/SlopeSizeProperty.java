package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.SlopeSizePropertyDelegate;

/**
 * Property for triangle size of the Slope() command output
 **/
public class SlopeSizeProperty extends AbstractRangeProperty<Integer> {

	private final SlopeSizePropertyDelegate delegate;

	/***/
	public SlopeSizeProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Size", 1, 10, 1);
		delegate = new SlopeSizePropertyDelegate(element);
	}

	@Override
	protected void setValueSafe(Integer value) {
		GeoNumeric slope = delegate.getElement();
		slope.setSlopeTriangleSize(value);
		slope.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Integer getValue() {
		return delegate.getElement().getSlopeTriangleSize();
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
