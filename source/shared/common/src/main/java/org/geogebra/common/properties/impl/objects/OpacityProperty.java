package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.OpacityPropertyDelegate;

/**
 * Line opacity
 */
public class OpacityProperty extends AbstractRangeProperty<Integer> {

	private final GeoElementDelegate delegate;

	/***/
	public OpacityProperty(Localization localization, GeoElement element) throws
			NotApplicablePropertyException {
		super(localization, "Opacity", 0, 100, 5);
		this.delegate = new OpacityPropertyDelegate(element);
	}

	@Override
	protected void setValueSafe(Integer value) {
		GeoElement element = delegate.getElement();
		App app = element.getApp();
		double alpha = value / 100.0;
		EuclidianStyleBarStatic.applyColor(
				element.getObjectColor(), alpha, app, app.getSelectionManager().getSelectedGeos());
	}

	@Override
	public Integer getValue() {
		double alpha = delegate.getElement().getAlphaValue();
		return (int) Math.round(alpha * 100);
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
