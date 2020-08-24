package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public class PointStylePropertyDelegate extends AbstractGeoElementDelegate {

	public PointStylePropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
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
		EuclidianView euclidianView = element.getApp().getActiveEuclidianView();
		return PointStyleModel.match(element) && euclidianView.canShowPointStyle();
	}
}
