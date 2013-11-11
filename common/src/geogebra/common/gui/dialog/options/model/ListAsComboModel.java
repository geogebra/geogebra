package geogebra.common.gui.dialog.options.model;

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

	public void applyChanges(boolean value) {
		super.applyChanges(value);
		app.refreshViews();
	}
	
	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isGeoList();
	}
	
	@Override
	public boolean getValueAt(int index) {
		return ((GeoList) getObjectAt(0)).drawAsComboBox();
	}
	
	@Override
	public void apply(int index, boolean value) {
		GeoList geo = (GeoList) getGeoAt(index);
		geo.setDrawAsComboBox(value);

		if (value) {
			geo.setEuclidianVisible(true);
		}

		listener.drawListAsComboBox(geo, value);

		geo.updateRepaint();

	}

}
