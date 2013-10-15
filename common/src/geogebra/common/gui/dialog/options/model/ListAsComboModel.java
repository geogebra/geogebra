package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;

public class ListAsComboModel extends BooleanOptionModel {
	public interface IListAsComboListener extends IBooleanOptionListener {
		void drawListAsComboBox(GeoList geo, boolean value);
	}
	
	private App app;
	private IListAsComboListener listener;
	
	public ListAsComboModel(App app, IListAsComboListener listener) {
		super(listener);
		this.app = app;
		this.listener = listener;
	}
	@Override
	public void updateProperties() {
		GeoList temp, geo0 = (GeoList) getGeoAt(0);
		boolean equalVal = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = (GeoList) getGeoAt(0);
			if (geo0.drawAsComboBox() != temp.drawAsComboBox()) {
				equalVal = false;
			}
		}
		listener.updateCheckbox(equalVal ? geo0.drawAsComboBox(): false);
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
		app.refreshViews();
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
