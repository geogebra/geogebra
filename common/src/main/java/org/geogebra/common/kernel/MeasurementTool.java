package org.geogebra.common.kernel;

import org.geogebra.common.kernel.geos.GeoImage;

public class MeasurementTool {
	private GeoImage image;

	public MeasurementTool(GeoImage image) {
		this.image = image;
	}

	public GeoImage getImage() {
		return image;
	}

	public void setImage(GeoImage image) {
		this.image = image;
	}

	public void remove() {
		image.remove();
	}
}
