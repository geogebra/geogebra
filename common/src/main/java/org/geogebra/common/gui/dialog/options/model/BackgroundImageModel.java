package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;

public class BackgroundImageModel extends BooleanOptionModel {

	public BackgroundImageModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	private GeoImage getImageAt(int index) {
		return (GeoImage) getObjectAt(index);
	}

	@Override
	public boolean isValidAt(int index) {
		return (getObjectAt(index) instanceof GeoImage);
	}

	@Override
	public boolean getValueAt(int index) {
		return getImageAt(index).isInBackground();
	}

	@Override
	public void apply(int index, boolean value) {
		GeoImage geo = getImageAt(index);
		geo.setInBackground(value);
		geo.updateVisualStyleRepaint(GProperty.HATCHING);
		storeUndoInfo();
	}
}
