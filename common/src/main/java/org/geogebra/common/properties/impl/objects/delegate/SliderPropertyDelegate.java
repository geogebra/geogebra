package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class SliderPropertyDelegate extends AbstractGeoElementDelegate {

	public SliderPropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (isTextOrInput(element)) {
			return false;
		}
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		return hasSliderProperties(element);
	}

	private boolean hasSliderProperties(GeoElement geo) {
		return geo instanceof GeoNumeric
				&& ((GeoNumeric) geo).getIntervalMinObject() != null
				&& geo.isIndependent();
	}

	@Override
	public GeoNumeric getElement() {
		return (GeoNumeric) super.getElement();
	}
}
