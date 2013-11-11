package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoBoolean;

public class FixCheckboxModel extends BooleanOptionModel {

	public FixCheckboxModel(IBooleanOptionListener listener) {
		super(listener);
	}
	
	@Override
	public boolean getValueAt(int index) {
		return ((GeoBoolean) getObjectAt(index)).isCheckboxFixed();

	}
	
	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			Object geo = getGeoAt(i);
			if (geo instanceof GeoBoolean) {
				GeoBoolean bool = (GeoBoolean) geo;
				if (!bool.isIndependent()) {
					return false;
				}
			} else
				return false;
		}
		return true;
		}

	@Override
	public void apply(int index, boolean value) {
		GeoBoolean bool = (GeoBoolean) getObjectAt(index);
		bool.setCheckboxFixed(value);
		bool.updateRepaint();
		
	}
}

