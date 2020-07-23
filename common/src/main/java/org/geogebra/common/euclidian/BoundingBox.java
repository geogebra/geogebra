package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoGebraColorConstants;

/**
 * bounding box construction for selected elements
 *
 * @author csilla
 *
 */
public abstract class BoundingBox<T extends GShape> {

	protected static final int ROTATION_HANDLER_DISTANCE = 25;

	protected GRectangle2D rectangle;
	protected final ArrayList<T> handlers;
	protected GColor color;
	private boolean fixed;

	/**
	 * size of handler
	 */
	public static final int HANDLER_RADIUS = 5;

	/**
	 * Make new bounding box
	 */
	public BoundingBox() {
		handlers = new ArrayList<>();
		setColor(GeoGebraColorConstants.GEOGEBRA_ACCENT);
	}

	/**
	 * Get the color of the bounding box.
	 *
	 * @return color
	 */
	public GColor getColor() {
		return color;
	}

	/**
	 * Sets the color of the bounding box.
	 *
	 * @param color
	 *            box color
	 */
	public void setColor(GColor color) {
		this.color = color;
	}

	/**
	 * @return box part of bounding box construction
	 */
	public GRectangle2D getRectangle() {
		return rectangle;
	}

	/**
	 * @param rectangle
	 *            - box part of bounding box construction
	 */
	public void setRectangle(GRectangle2D rectangle) {
		this.rectangle = rectangle;
		if (rectangle != null) {
			createHandlers();
		}
	}

	/**
	 * @return true if cropBox should be shown instead of boundingBox
	 */
	public boolean isCropBox() {
		return false;
	}

	/**
	 * Create handlers for current rectangle
	 */
	protected abstract void createHandlers();

	/**
	 * Initialize the array of handlers, don't specify positions
	 *
	 * @param nrHandlers
	 *            rebuild the list of handlers
	 */
	protected void initHandlers(int nrHandlers) {
		handlers.clear();
		for (int i = 0; i < nrHandlers; i++) {
			handlers.add(createHandler());
		}
	}

	/**
	 * @return a single handler
	 */
	protected abstract T createHandler();

	/**
	 * method to draw the bounding box construction for selected geo
	 *
	 * @param g2
	 *            - graphics
	 */
	public abstract void draw(GGraphics2D g2);

	/**
	 * Draw and fill strokes
	 *
	 * @param g2
	 *            graphics
	 */
	protected void drawHandlers(GGraphics2D g2) {
		for (GShape handler : handlers) {
			g2.setPaint(color);
			g2.fill(handler);
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f, GBasicStroke.CAP_BUTT,
					GBasicStroke.JOIN_MITER));
			g2.setColor(GColor.GEOGEBRA_GRAY);
			g2.draw(handler);
		}

	}

	/**
	 * Draw the bounding box outline
	 *
	 * @param g2
	 *            graphics
	 */
	protected void drawRectangle(GGraphics2D g2) {
		if (rectangle != null) {
			g2.setColor(GColor.newColor(192, 192, 192, 0.0));
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f, GBasicStroke.CAP_BUTT,
					GBasicStroke.JOIN_MITER));
			g2.fill(rectangle);
			g2.setColor(color);
			g2.draw(rectangle);
		}
	}

	/**
	 * reset the parts of bounding box construction
	 */
	public void resetBoundingBox() {
		rectangle = null;
		handlers.clear();
	}

	/**
	 * @param threshold
	 *            controller threshold
	 * @return distance threshold to select a point
	 */
	public static final int getSelectionThreshold(int threshold) {
		return threshold + 12;
	}

	/**
	 * @param x
	 *            - mouse event x
	 * @param y
	 *            - mouse event y
	 * @param hitThreshold
	 *            - threshold
	 * @return number of handler which was hit
	 */
	public int hitHandlers(int x, int y, int hitThreshold) {
		if (fixed) {
			return -1;
		}

		for (int i = 0; i < handlers.size(); i++) {
			GShape point = handlers.get(i);
			if (hit(point, x, y, hitThreshold)) {
				return i;
			}
		}

		return -1;
	}

	private static boolean hit(GShape shape, int x, int y, int hitThreshold) {
		GRectangle bounds = shape.getBounds();
		int r = getSelectionThreshold(hitThreshold);
		double dx = bounds.getX() + bounds.getWidth() / 2 - x;
		double dy = bounds.getY() + bounds.getHeight() / 2 - y;
		return dx < r && dx > -r && dx * dx + dy * dy <= r * r;
	}

	/**
	 * Does the same as hitHandlers but returns a EuclidianBoundingBoxHandler
	 *
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @param hitThreshold
	 *            - threshold
	 * @return bounding box handler
	 */
	public EuclidianBoundingBoxHandler getHitHandler(int x, int y,
			int hitThreshold) {
		int hit = hitHandlers(x, y, hitThreshold);

		switch (hit) {
		case 0:
			return EuclidianBoundingBoxHandler.TOP_LEFT;
		case 1:
			return EuclidianBoundingBoxHandler.BOTTOM_LEFT;
		case 2:
			return EuclidianBoundingBoxHandler.BOTTOM_RIGHT;
		case 3:
			return EuclidianBoundingBoxHandler.TOP_RIGHT;
		case 4:
			return EuclidianBoundingBoxHandler.TOP;
		case 5:
			return EuclidianBoundingBoxHandler.LEFT;
		case 6:
			return EuclidianBoundingBoxHandler.BOTTOM;
		case 7:
			return EuclidianBoundingBoxHandler.RIGHT;
		case 8:
			return EuclidianBoundingBoxHandler.ROTATION;
		default:
			return EuclidianBoundingBoxHandler.UNDEFINED;
		}
	}

	/**
	 * @param x
	 *            - x coord of hit
	 * @param y
	 *            - y coord of hit
	 * @param hitThreshold
	 *            - threshold (without line thickness)
	 * @return true if hits any side of boundingBox
	 */
	public abstract boolean hitSideOfBoundingBox(int x, int y, int hitThreshold);

	/**
	 * @param x
	 *            screen x-coord
	 * @param y
	 *            screen y-coord
	 * @param hitThreshold
	 *            max distance between rectangle and event (includes line
	 *            thickness)
	 * @return whether rectangle was hit
	 */
	protected boolean hitRectangle(int x, int y, int hitThreshold) {
		GRectangle hitArea = AwtFactory.getPrototype().newRectangle(x - hitThreshold,
				y - hitThreshold, 2 * hitThreshold, 2 * hitThreshold);
		return rectangle.intersects(hitArea) && !rectangle.contains(hitArea);
	}

	/**
	 * @param handler
	 *            handler
	 * @return resizing cursor or null
	 */
	public EuclidianCursor getCursor(EuclidianBoundingBoxHandler handler) {
		switch (handler) {
		case TOP_LEFT:
		case BOTTOM_RIGHT:
			return EuclidianCursor.RESIZE_NWSE;
		case BOTTOM_LEFT:
		case TOP_RIGHT:
			return EuclidianCursor.RESIZE_NESW;
		case TOP:
		case BOTTOM:
			return EuclidianCursor.RESIZE_NS;
		case LEFT:
		case RIGHT:
			return EuclidianCursor.RESIZE_EW;
		case ROTATION:
			return EuclidianCursor.ROTATION;
		default:
			return null;
		}
	}

	/**
	 * @param hitX
	 *            screen x-coord
	 * @param hitY
	 *            screen y-coord
	 * @param hitThreshold
	 *            threshold
	 * @return whether side or handler was hit
	 */
	public boolean hit(int hitX, int hitY, int hitThreshold) {
		if (hitHandlers(hitX, hitY, hitThreshold) >= 0) {
			return true;
		}
		return getRectangle() != null
				&& getRectangle().intersects(hitX - hitThreshold,
				hitY - hitThreshold, 2 * hitThreshold, 2 * hitThreshold)
				&& hitSideOfBoundingBox(hitX, hitY, hitThreshold);
	}

	/**
	 * @param geo
	 *            selected element
	 */
	public void updateFrom(GeoElement geo) {
		fixed = geo.isLocked();
	}

	/**
	 * @param fixed
	 *            whether the box is fixed
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public void setTransform(GAffineTransform directTransform) {
		// only cropbox and rotatable box
	}
}
