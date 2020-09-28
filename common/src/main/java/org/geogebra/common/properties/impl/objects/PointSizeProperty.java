package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.PointSizePropertyDelegate;

/**
 * Point size
 */
public class PointSizeProperty extends AbstractRangeProperty<Integer> {

	private final GeoElementDelegate delegate;

	/***/
	public PointSizeProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Size", 1, 9, 1);
		delegate = new PointSizePropertyDelegate(element);
	}

	@Override
	protected void setValueSafe(Integer value) {
		GeoElement element = delegate.getElement();
		setSize(element, value);
		element.updateRepaint();
	}

	@Override
	public Integer getValue() {
		GeoElement element = delegate.getElement();
		if (element instanceof PointProperties) {
			return ((PointProperties) element).getPointSize();
		}
		return EuclidianStyleConstants.DEFAULT_POINT_SIZE;
	}

	private void setSize(GeoElement element, int size) {
		if (element instanceof GeoList) {
			GeoList list = (GeoList) element;
			for (int i = 0; i < list.size(); i++) {
				setSize(list.get(i), size);
			}
		} else if (element instanceof PointProperties) {
			((PointProperties) element).setPointSize(size);
		}
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
