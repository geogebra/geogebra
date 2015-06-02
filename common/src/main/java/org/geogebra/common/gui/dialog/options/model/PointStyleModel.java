package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.plugin.EuclidianStyleConstants;

public class PointStyleModel extends NumberOptionsModel {
	private IComboListener listener;
	
	public PointStyleModel(IComboListener listener) {
		this.listener = listener;
	}
	
	private PointProperties getPointPropertiesAt(int index) {
		return (PointProperties)getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		if (!hasGeos()) {
			return;
		}
		
		PointProperties geo0 = getPointPropertiesAt(0);
		if (listener == null) {
			return;
		}
		
		if ((geo0 == null) || (geo0.getPointStyle() == -1)) {
			// select default button
			listener.setSelectedIndex(EuclidianStyleConstants.POINT_STYLE_DOT);
		
		} else {
			// select custom button and set combo box selection
			listener.setSelectedIndex(geo0.getPointStyle());
		}

	}

	@Override
	public boolean checkGeos() {
		if (!hasGeos()) {
			return false;
		}
		return super.checkGeos();
	}
	
	@Override
	public boolean isValidAt(int index) {
		boolean valid = true;
		GeoElement geo = getGeoAt(index);
		if ((geo.isGeoElement3D() && !(geo.isGeoPoint()))
				|| // TODO add point style to 3D points
				(!geo.getGeoElementForPropertiesDialog().isGeoPoint() && (!(geo
						.isGeoList() && ((GeoList) geo)
						.showPointProperties())))) {
			valid = false;
		}
		return valid;

	}

	public boolean is3D() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (!(geo.isGeoPoint() && geo.isGeoElement3D())) {
				return false;
			}
		}
		return true;
	}
	@Override
	protected void apply(int index, int value) {
		PointProperties point = getPointPropertiesAt(index);
		point.setPointStyle(value);
		point.updateRepaint();
	}

	@Override
	protected int getValueAt(int index) {
		// not used
		return 0;//getPointPropertiesAt(index).getPointStyle();
	}

	@Override
	public boolean updatePanel(Object[] geos2) {
		return listener.updatePanel(geos2) != null;
	}

}
