package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextMindmapDelegate;

public class BorderThicknessProperty extends AbstractRangeProperty<Integer> {
	private final GeoElementDelegate delegate;

	/**
	 * Constructor
	 * @param localization - localization
	 * @param element - geo
	 * @throws NotApplicablePropertyException - exception
	 */
	public BorderThicknessProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Thickness", 0, 3, 1);
		delegate = new TextMindmapDelegate(element);
	}

	@Override
	protected void setValueSafe(Integer value) {
		GeoElement geo = delegate.getElement();
		geo.setLineThickness(value);
		geo.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Integer getValue() {
		return delegate.getElement().getLineThickness();
	}
}
