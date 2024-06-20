package org.geogebra.common.euclidian.measurement;

import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawImageResizable;
import org.geogebra.common.kernel.geos.GeoImage;

/**
 * Class to represent a measurement tool, like rulers or different types of protractors.
 */
public final class MeasurementTool {
	private final Double centerInPercent;
	private final PenTransformer transformer;
	private GeoImage image;
	private final MeasurementToolId id;
	private final String fileName;

	private final CreateToolImage toolImageFactory;

	/**
	 *
	 * @param id of the tool.
	 * @param fileName of the tool image.
	 * @param percent the y-position of the rotation point given by percent of the image height
	 * (x-position is centered)
	 */
	public MeasurementTool(MeasurementToolId id, String fileName,
			Double percent, CreateToolImage toolImageFactory,
			PenTransformer transformer) {
		this.id = id;
		this.fileName = fileName;
		this.centerInPercent = percent;
		this.transformer = transformer;
		this.toolImageFactory = toolImageFactory;
	}

	/**
	 *
	 * @return the image of the measurement tool.
	 */
	public GeoImage getImage() {
		return image;
	}

	/**
	 * Removes the image of the tool from Construction.
	 */
	public void remove() {
		if (image == null) {
			return;
		}
		image.remove();
	}

	/**
	 *
	 * @return the id of the measurement tool.
	 */
	public MeasurementToolId getId() {
		return id;
	}

	void refresh() {
		image = toolImageFactory.create(id.getMode(), fileName);
	}

	/**
	 * Calculates and gives back the rotation center of the tool.
	 * @param view {@link EuclidianView}
	 * @return the rotation center of the measurement tool.
	 */
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
		if (!id.isProtractor()) {
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

	/**
	 *
	 * @return {@link PenTransformer}
	 */
	public PenTransformer getTransformer() {
		return transformer;
	}

	@Override
	public String toString() {
		return id.toString();
	}

	boolean hasRotationCenter() {
		return centerInPercent != null;
	}
}
