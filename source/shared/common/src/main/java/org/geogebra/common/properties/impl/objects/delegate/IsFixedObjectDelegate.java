package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.settings.AlgebraSettings;

public class IsFixedObjectDelegate extends AbstractGeoElementDelegate {

	public IsFixedObjectDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (element instanceof GeoList) {
			return isApplicableToList((GeoList) element);
		}
		AlgebraSettings algebraSettings = element.getApp().getSettings().getAlgebra();
		if (hasFunctionProperties(element) && algebraSettings.isEquationChangeByDragRestricted()) {
			return false;
		}
		return element.showFixUnfix();
	}

	private boolean hasFunctionProperties(GeoElement element) {
		if (element instanceof GeoList && !checkIsApplicable(element)) {
			return false;
		} else {
			return element.isFunctionOrEquationFromUser();
		}
	}

	private boolean isApplicableToList(GeoList list) {
		GeoElement elementForProperties = list.getGeoElementForPropertiesDialog();
		return elementForProperties instanceof GeoFunction;
	}
}
