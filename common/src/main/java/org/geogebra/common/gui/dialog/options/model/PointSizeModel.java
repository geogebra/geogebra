package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.PointProperties;


public class PointSizeModel extends SliderOptionsModel {

	public PointSizeModel() {
	}

	private PointProperties getPointPropertiesAt(int index) {
		return (PointProperties)getObjectAt(index);
	}

	@Override
	public boolean isValidAt(int index) {
		boolean valid = true;
		GeoElement geo = getGeoAt(index);
		if (!(geo.getGeoElementForPropertiesDialog().isGeoPoint())
				&& (!(geo.isGeoList() && ((GeoList) geo)
						.showPointProperties()))) {
			valid = false;
		}
		return valid;
	}

	@Override
	public void apply(int index, int value) {
		PointProperties point = getPointPropertiesAt(index);
		point.setPointSize(value);
		point.updateRepaint();		
	}

	@Override
	public int getValueAt(int index) {
		return getPointPropertiesAt(index).getPointSize();
	}

}
