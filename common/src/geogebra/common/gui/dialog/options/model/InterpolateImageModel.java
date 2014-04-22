package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoImage;

public class InterpolateImageModel extends BooleanOptionModel {

	public InterpolateImageModel(IBooleanOptionListener listener) {
		super(listener);
		// TODO Auto-generated constructor stub
	}

	private GeoImage getGeoImageAt(int index) {
		return (GeoImage)getObjectAt(index);
	}
	@Override
	public boolean getValueAt(int index) {
		return getGeoImageAt(index).isInterpolate();
	}

	@Override
	public void apply(int index, boolean value) {
		GeoImage image = getGeoImageAt(index);
		image.setInterpolate(value);
		image.updateRepaint();
	}

	@Override
	protected boolean isValidAt(int index) {
		return (getObjectAt(index) instanceof GeoImage);
	}

}
