package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

public class CenterImageModel extends BooleanOptionModel {
	private boolean centerImage;

	public CenterImageModel(App app) {
		super(null, app);
		centerImage = app.has(Feature.CENTER_IMAGE);
	}

	private GeoImage getImageAt(int index) {
		return (GeoImage) getGeoAt(index);
	}

	@Override
	public boolean getValueAt(int index) {
		return getImageAt(index).isCentered();
	}

	@Override
	public void apply(int index, boolean value) {
		getImageAt(index).setCentered(value);
	}

	@Override
	protected boolean isValidAt(int index) {
		return centerImage && getGeoAt(index) instanceof GeoImage;
	}

}
