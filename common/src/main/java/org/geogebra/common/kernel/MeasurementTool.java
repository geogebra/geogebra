package org.geogebra.common.kernel;

import java.util.function.BiFunction;

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

	public void refresh(BiFunction<Integer, String, GeoImage> addFunct) {
		image = addFunct.apply(id.getMode(), fileName);
	}

	public boolean isProtactor() {
		return id == MeasurementToolId.PROTRACTOR
				|| id == MeasurementToolId.TRIANGLE_PROTRACTOR;
	}
}
