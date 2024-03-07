package org.geogebra.common.euclidian.measurement;

import java.util.List;
import java.util.function.BiFunction;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawImageResizable;
import org.geogebra.common.kernel.MeasurementToolId;
import org.geogebra.common.kernel.geos.GeoImage;

public final class MeasurementTool {
	private final Double centerInPercent;
	private final PenTransformer transformer;
	private GeoImage image;
	private final MeasurementToolId id;
	private final String fileName;


	public MeasurementTool(MeasurementToolId id, String fileName,
			PenTransformer transformer) {
		this(id, fileName, null, transformer);
	}

	public MeasurementTool(MeasurementToolId id, String fileName, Double percent,
			PenTransformer transformer) {
		this.id = id;
		this.fileName = fileName;
		this.centerInPercent = percent;
		this.transformer = transformer;
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

	public GPoint2D getRotationCenter(EuclidianView view) {
		List<GPoint2D> points = getProtractorPoints(view);
		if (points == null || points.size() < 3) {
			return new GPoint2D(0, 0);
		}

		GPoint2D p0 = points.get(0);
		GPoint2D p1 = points.get(1);
		GPoint2D p2 = points.get(2);

		return new GPoint2D(getRotatedCoord(p0.x, p1.x, p2.x),
				getRotatedCoord(p0.y, p1.y, p2.y));
	}

	private List<GPoint2D> getProtractorPoints(EuclidianView view) {
		if (!id.isProtactor()) {
			return null;
		}

		DrawImageResizable drawable =
				(DrawImageResizable) view.getDrawableFor(image);
		return drawable != null ? drawable.toPoints() : null;
	}

	private double getRotatedCoord(double v0, double v1, double v2) {
		return ((v0 + v1) / 2) * centerInPercent
				+ ((2 * v2 + v1 - v0) / 2)
				* (1 - centerInPercent);
	}

	public boolean hasRotationCenter() {
		return centerInPercent != null;
	}

	public PenTransformer getTransformer() {
		return transformer;
	}
}
