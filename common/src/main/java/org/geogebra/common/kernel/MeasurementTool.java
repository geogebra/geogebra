package org.geogebra.common.kernel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoImage;

public final class MeasurementTool {
	private GeoImage image;
	private final MeasurementToolId id;
	private final String fileName;

	public MeasurementTool(MeasurementToolId id, String fileName) {
		this.id = id;
		this.fileName = fileName;
	}

	public GeoImage getImage() {
		return image;
	}

	public void remove() {
		if (image == null) {
			return;
		}
		image.remove();
	}

	public MeasurementToolId getId() {
		return id;
	}

	public void refresh(EuclidianView view) {
		image = view.addMeasurementTool(id.getMode(), fileName);
	}
}
