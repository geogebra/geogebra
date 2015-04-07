package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;

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

	private GeoList getGeoListAt(int index) {
		return (GeoList) getObjectAt(index);
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
		return getGeoListAt(0).drawAsComboBox();
	}
	
	@Override
	public void apply(int index, boolean value) {
		GeoList geo = getGeoListAt(index);
		geo.setDrawAsComboBox(value);

		if (value) {
			geo.setEuclidianVisible(true);
		}

		listener.drawListAsComboBox(geo, value);

		geo.updateRepaint();

	}

}
