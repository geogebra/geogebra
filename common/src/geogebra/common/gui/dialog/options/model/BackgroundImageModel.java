package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoImage;

public class BackgroundImageModel extends BooleanOptionModel {
	
	public BackgroundImageModel(IBooleanOptionListener listener) {
		super(listener);
	}

	@Override
	public boolean isValidAt(int index) {
		return (getGeoAt(index) instanceof GeoImage);
	}

	@Override
	public boolean getValueAt(int index) {
		// TODO Auto-generated method stub
		return ((GeoImage) getObjectAt(index)).isInBackground();
	}

	@Override
	public void apply(int index, boolean value) {
		GeoImage geo = (GeoImage) getObjectAt(index);
		geo.setInBackground(value);
		geo.updateRepaint();
	}
}


