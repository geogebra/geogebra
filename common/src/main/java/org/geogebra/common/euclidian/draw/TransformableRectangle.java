package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MediaBoundingBox;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.RectangleTransformable;
import org.geogebra.common.util.debug.Log;

public class TransformableRectangle {

	private final EuclidianView view;
	private final RectangleTransformable geo;
	private MediaBoundingBox boundingBox;
	private GAffineTransform directTransform;
	private GAffineTransform inverseTransform;
	private boolean keepAspectRatio;

	private GPoint2D corner0;
	private GPoint2D corner1;
	private GPoint2D corner2;
	private GPoint2D corner3;
	private double aspectRatio = Double.NaN;

	/**
	 * @param view view
	 * @param geo transformable geo
	 * @param keepAspectRatio whether to keep aspect ratio
	 */
	TransformableRectangle(EuclidianView view, RectangleTransformable geo,
			boolean keepAspectRatio) {
		this.view = view;
		this.geo = geo;
		this.keepAspectRatio = keepAspectRatio;
	}

	/**
	 * Update transforms
	 */
	public void update() {
		GPoint2D point = geo.getLocation();
		if (point == null) {
			return;
		}
		double angle = geo.getAngle();
		double width = geo.getWidth();
		double height = geo.getHeight();

		directTransform = AwtFactory.getPrototype().newAffineTransform();
		directTransform.translate(view.toScreenCoordXd(point.getX()),
				view.toScreenCoordYd(point.getY()));
		directTransform.rotate(angle);

		try {
			inverseTransform = directTransform.createInverse();
		} catch (Exception e) {
			Log.error(e.getMessage());
		}

		corner0 = directTransform.transform(new GPoint2D(0, 0), null);
		corner1 = directTransform.transform(new GPoint2D(width, 0), null);
		corner2 = directTransform.transform(new GPoint2D(width, height), null);
		corner3 = directTransform.transform(new GPoint2D(0, height), null);
	}

	/**
	 * @return height (in screen coords)
	 */
	public int getHeight() {
		return (int) (Math.max(Math.max(corner0.getY(), corner1.getY()),
				Math.max(corner2.getY(), corner3.getY()))
				- Math.min(Math.min(corner0.getY(), corner1.getY()),
				Math.min(corner2.getY(), corner3.getY())));
	}

	/**
	 * @return width (in screen coords)
	 */
	public int getWidth() {
		return (int) (Math.max(Math.max(corner0.getX(), corner1.getX()),
				Math.max(corner2.getX(), corner3.getX()))
				- Math.min(Math.min(corner0.getX(), corner1.getX()),
				Math.min(corner2.getX(), corner3.getX())));
	}

	/**
	 * @return left (in screen coords)
	 */
	public int getLeft() {
		return (int) Math.min(Math.min(corner0.getX(), corner1.getX()),
				Math.min(corner2.getX(), corner3.getX()));
	}

	/**
	 * @return top (in screen coords)
	 */
	public int getTop() {
		return (int) Math.min(Math.min(corner0.getY(), corner1.getY()),
				Math.min(corner2.getY(), corner3.getY()));
	}

	public List<GPoint2D> toPoints() {
		return Arrays.asList(corner0, corner1, corner3);
	}

	/**
	 * Transform the drawable based on the transformed points
	 * @param points
	 *            list of points defining the drawable
	 */
	public void fromPoints(ArrayList<GPoint2D> points) {
		double newAngle = Math.atan2(points.get(1).getY() - points.get(0).getY(),
				points.get(1).getX() - points.get(0).getX());

		double newWidth = Math.max(GeoInlineText.DEFAULT_WIDTH,
				points.get(1).distance(points.get(0)));

		double newHeight = points.get(2).distance(points.get(0));

		if (newHeight < geo.getMinHeight()) {
			return;
		}

		geo.setSize(newWidth, newHeight);
		geo.setAngle(newAngle);
		geo.setLocation(new GPoint2D(
						view.toRealWorldCoordX(points.get(0).getX()),
						view.toRealWorldCoordY(points.get(0).getY())
				)
		);
	}

	/**
	 * @return whether the rectangle was hit
	 */
	public boolean hit(int x, int y) {
		GPoint2D p = inverseTransform.transform(new GPoint2D(x, y), null);
		return 0 < p.getX() && p.getX() < geo.getWidth()
				&& 0 < p.getY() && p.getY() < geo.getHeight();
	}

	/**
	 * @return bounds on screen
	 */
	public GRectangle getBounds() {
		return AwtFactory.getPrototype().newRectangle(getLeft(), getTop(),
				getWidth(), getHeight());
	}

	/**
	 * Resize the text or formula by dragging a handler (simple selection)
	 * @param point new handler position
	 * @param handler handler id
	 */
	public void updateByBoundingBoxResize(GPoint2D point, EuclidianBoundingBoxHandler handler) {
		GPoint2D transformed = inverseTransform.transform(point, null);

		double x = 0;
		double y = 0;
		double width = geo.getWidth();
		double height = geo.getHeight();
		updateAspectRatio(geo, handler);

		if (handler.getDx() == 1) {
			width = transformed.getX();
		} else if (handler.getDx() == -1) {
			width = geo.getWidth() - transformed.getX();
			x = transformed.getX();
		}

		if (keepAspectRatio && handler.isDiagonal()) {
			double bottom = height + y;
			height = width * aspectRatio;
			y = handler.getDy() > 0 ? y : bottom - height;
		} else if (handler.getDy() == 1) {
			height = transformed.getY();
		} else if (handler.getDy() == -1) {
			height = geo.getHeight() - transformed.getY();
			y = transformed.getY();
		}

		if (geo instanceof  GeoInlineText
				&& height < geo.getMinHeight() && width < geo.getWidth()) {
			return;
		}

		if (height < geo.getMinHeight()) {
			if (y != 0) {
				y = geo.getHeight() - geo.getMinHeight();
			}
			height = geo.getMinHeight();
		}

		if (width < geo.getMinWidth()) {
			if (x != 0) {
				x = geo.getWidth() - geo.getMinWidth();
			}
			width = geo.getMinWidth();
		}

		GPoint2D origin = directTransform.transform(new GPoint2D(x, y), null);
		// setting size first, location second is important for images
		geo.setSize(width, height);
		geo.setLocation(new GPoint2D(view.toRealWorldCoordX(origin.getX()),
				view.toRealWorldCoordY(origin.getY())));

		geo.updateRepaint();
		updateSelfAndBoundingBox();
	}

	protected void updateAspectRatio(RectangleTransformable geo,
			EuclidianBoundingBoxHandler handler) {
		if (!handler.isDiagonal()) {
			aspectRatio = Double.NaN;
		} else if (Double.isNaN(aspectRatio)) {
			aspectRatio = geo.getHeight() / geo.getWidth();
		}
	}

	public GPoint2D getInversePoint(double x, double y) {
		return inverseTransform.transform(new GPoint2D(x, y), null);
	}

	public GAffineTransform getDirectTransform() {
		return directTransform;
	}

	/**
	 * Update the rectangle and the rotatable bounding box
	 */
	public void updateSelfAndBoundingBox() {
		if (geo.getLocation() == null) {
			return;
		}
		update();
		getBoundingBox().setRectangle(getBounds());
		getBoundingBox().setTransform(getDirectTransform());
	}

	/**
	 * Get the rotatable bounding box that is defined by this rectangle
	 * @return rotatable bounding box
	 */
	public MediaBoundingBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = new MediaBoundingBox();
			boundingBox.setRectangle(getBounds());
			boundingBox.setColor(view.getApplication().getPrimaryColor());
		}
		boundingBox.updateFrom(geo.toGeoElement());
		return boundingBox;
	}

	/**
	 * @return height/width when resizing diagonally, Double.NaN otherwise
	 */
	public double getAspectRatio() {
		return aspectRatio;
	}
}
