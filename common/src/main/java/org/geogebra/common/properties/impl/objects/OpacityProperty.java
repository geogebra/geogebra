package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.RangeProperty;

/**
 * Line opacity
 */
public class OpacityProperty
		extends AbstractGeoElementProperty implements RangeProperty<Integer> {

	public OpacityProperty(GeoElement geoElement) throws NotApplicablePropertyException {
		super("Opacity", geoElement);
	}

	@Override
	public Integer getMin() {
		return 0;
	}

	@Override
	public Integer getMax() {
		return 100;
	}

	@Override
	public Integer getValue() {
		double alpha = getElement().getAlphaValue();
		return (int) (alpha * 100);
	}

	@Override
	public void setValue(Integer opacity) {
		GeoElement element = getElement();
		App app = element.getApp();
		double alpha = opacity / 100.0;
		EuclidianStyleBarStatic.applyColor(
				element.getObjectColor(), alpha, app, app.getSelectionManager().getSelectedGeos());
	}

	@Override
	public Integer getStep() {
		return 5;
	}

	@Override
	boolean isApplicableTo(GeoElement element) {
		if (isTextOrInput(element)) {
			return false;
		}
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		return element.isFillable();
	}
}
