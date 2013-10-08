package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

public class ListAsComboModel extends OptionsModel {
	public interface IListAsComboListener {
		void updateComboBox(boolean isEqual);

		void drawListAsComboBox(GeoList geo, boolean value);
	}
	private IListAsComboListener listener;
	public ListAsComboModel(IListAsComboListener listener) {
		this.listener = listener;
	}
	@Override
	public void updateProperties() {
		GeoList temp, geo0 = (GeoList) getGeoAt(0);
		boolean equalVal = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = (GeoList) getGeoAt(0);
			// same object visible value
			if (geo0.drawAsComboBox() != temp.drawAsComboBox())
				equalVal = false;
		}
		listener.updateComboBox(equalVal);
	}

	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoList geo = (GeoList) getGeoAt(0);
			geo.setDrawAsComboBox(value);

			if (value) {
				geo.setEuclidianVisible(true);
			}

			listener.drawListAsComboBox(geo, value);

			geo.updateRepaint();
		}

		
	}
	
	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (!geo.isGeoList()) {
				return false;
			}
		}
		return true;
	}

}
