package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.draw.DrawSegment;
import org.geogebra.common.euclidian.draw.DrawWidget;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoGebraColorConstants;

/**
 * bounding box construction for selected elements
 *
 * @author csilla
 *
 */
public class BoundingBox {
	private GRectangle2D rectangle;
	private ArrayList<GEllipse2DDouble> handlers;
	private ArrayList<GGeneralPath> cropHandlers;
	private int nrHandlers = 8;
	private boolean isCropBox = false;
	private boolean isImage = false;
	private GColor color;
	private boolean fixed;

	private static final int ROTATION_HANDLER_DISTANCE = 25;
	/**
	 * size of handler
	 */
	public static final int HANDLER_RADIUS = 5;
	/**
	 * minimum width and height for multi-selection
	 */
	public static final int SIDE_THRESHOLD = 50;

	/**
	 * Make new bounding box
	 *
	 * @param isImage
	 *            true if is boundingBox of image
	 * @param hasRotationHandler
	 *            has rotation handler
	 */
	public BoundingBox(boolean isImage, boolean hasRotationHandler) {
		setNrHandlers(hasRotationHandler ? 9 : 8);
		setHandlers(new ArrayList<GEllipse2DDouble>());
		if (isImage) {
			this.isImage = isImage;
			setCropHandlers(new ArrayList<GGeneralPath>());
		}
		setColor(GeoGebraColorConstants.GEOGEBRA_ACCENT);
	}

	/**
	 * New bounding box with defined rectangle
	 *
	 * @param rect
	 *            defined rectangle
	 * @param isImage
	 *            true is bounding box of image
	 * @param hasRotationHandler
	 *            has rotation handler
	 */
	public BoundingBox(GRectangle rect, boolean isImage,
			boolean hasRotationHandler) {
		this(isImage, hasRotationHandler);
		setRectangle(rect);
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
	 * @return handler points of bounding box construction
	 */
	public ArrayList<GEllipse2DDouble> getHandlers() {
		return handlers;
	}

	/**
	 * @param index
	 *            of handler
	 * @return handler
	 */
	public GEllipse2DDouble getHandler(int index) {
		return handlers.get(index);
	}

	/**
	 * @param index
	 *            of handler
	 * @return point with center coordinates of handler
	 */
	public GPoint getHandlerCenter(int index) {
		int x = (int) (handlers.get(index).getBounds().getMinX() + HANDLER_RADIUS);
		int y = (int) (handlers.get(index).getBounds().getMinY() + HANDLER_RADIUS);
		return new GPoint(x, y);
	}

	/**
	 * @param handlers
	 *            - points of bounding box construction
	 */
	public void setHandlers(ArrayList<GEllipse2DDouble> handlers) {
		this.handlers = handlers;
	}

	/**
	 * @return true if cropBox should be shown instead of boundingBox
	 */
	public boolean isCropBox() {
		return isCropBox;
	}

	/**
	 * @param isCropBox
	 *            set if boundingBox or cropBox should be shown
	 */
	public void setCropBox(boolean isCropBox) {
		this.isCropBox = isCropBox;
	}

	/**
	 * @return crop handlers
	 */
	public ArrayList<GGeneralPath> getCropHandlers() {
		return cropHandlers;
	}

	/**
	 * @param cropHandlers
	 *            list of crop handlers
	 */
	public void setCropHandlers(ArrayList<GGeneralPath> cropHandlers) {
		this.cropHandlers = cropHandlers;
	}

	/**
	 * @return number of needed handlers
	 */
	public int getNrHandlers() {
		return nrHandlers;
	}

	/**
	 * @param nrHandlers
	 *            - number of handlers
	 */
	public void setNrHandlers(int nrHandlers) {
		this.nrHandlers = nrHandlers;
	}

	private void createHandlers() {
		if (handlers == null) {
			handlers = new ArrayList<>();
		}
		handlers.clear();
		// init bounding box handler list
		for (int i = 0; i < /* = */nrHandlers; i++) {
			GEllipse2DDouble handler = AwtFactory.getPrototype()
					.newEllipse2DDouble();
			handlers.add(handler);
		}
		createBoundingBoxHandlers();
		if (isImage) {
			if (cropHandlers == null) {
				cropHandlers = new ArrayList<>();
			}
			cropHandlers.clear();
			for (int i = 0; i < /* = */nrHandlers; i++) {
				GGeneralPath cropHandler = AwtFactory.getPrototype()
						.newGeneralPath();
				cropHandlers.add(cropHandler);
			}
			createCropHandlers();
		}
	}

	private void createCropHandlers() {
		if (nrHandlers == 8) {
			// corner crop handlers
			cropHandlers.get(0).moveTo(rectangle.getX(), rectangle.getY() + 10);
			cropHandlers.get(0).lineTo(rectangle.getX(), rectangle.getY());
			cropHandlers.get(0).lineTo(rectangle.getX() + 10, rectangle.getY());
			cropHandlers.get(1).moveTo(rectangle.getX(),
					rectangle.getMaxY() - 10);
			cropHandlers.get(1).lineTo(rectangle.getX(), rectangle.getMaxY());
			cropHandlers.get(1).lineTo(rectangle.getX() + 10,
					rectangle.getMaxY());
			cropHandlers.get(2).moveTo(rectangle.getMaxX() - 10,
					rectangle.getMaxY());
			cropHandlers.get(2).lineTo(rectangle.getMaxX(),
					rectangle.getMaxY());

			cropHandlers.get(2).lineTo(rectangle.getMaxX(),
					rectangle.getMaxY() - 10);
			cropHandlers.get(3).moveTo(rectangle.getMaxX(),
					rectangle.getY() + 10);
			cropHandlers.get(3).lineTo(rectangle.getMaxX(), rectangle.getY());
			cropHandlers.get(3).lineTo(rectangle.getMaxX() - 10,
					rectangle.getY());
			// side handlers
			double centerX = (rectangle.getMinX() + rectangle.getMaxX()) / 2;
			double centerY = (rectangle.getMinY() + rectangle.getMaxY()) / 2;
			cropHandlers.get(4).moveTo(centerX - 5, rectangle.getMinY());
			cropHandlers.get(4).lineTo(centerX + 5, rectangle.getMinY());
			cropHandlers.get(5).moveTo(rectangle.getMinX(), centerY - 5);
			cropHandlers.get(5).lineTo(rectangle.getMinX(), centerY + 5);
			cropHandlers.get(6).moveTo(centerX - 5, rectangle.getMaxY());
			cropHandlers.get(6).lineTo(centerX + 5, rectangle.getMaxY());
			cropHandlers.get(7).moveTo(rectangle.getMaxX(), centerY - 5);
			cropHandlers.get(7).lineTo(rectangle.getMaxX(), centerY + 5);
		}
	}

	private void createBoundingBoxHandlers() {
		if (nrHandlers == 8 || nrHandlers == 9) {
			// corner handlers
			setHandlerFromCenter(0, rectangle.getX(), rectangle.getY());
			setHandlerFromCenter(1, rectangle.getX(), rectangle.getMaxY());
			setHandlerFromCenter(2, rectangle.getMaxX(), rectangle.getMaxY());
			setHandlerFromCenter(3, rectangle.getMaxX(), rectangle.getY());

			// side handlers
			double centerX = (rectangle.getMinX() + rectangle.getMaxX()) / 2;
			double centerY = (rectangle.getMinY() + rectangle.getMaxY()) / 2;
			// top
			setHandlerFromCenter(4, centerX, rectangle.getMinY());
			// left
			setHandlerFromCenter(5, rectangle.getMinX(), centerY);
			// bottom
			setHandlerFromCenter(6, centerX, rectangle.getMaxY());
			// right
			setHandlerFromCenter(7, rectangle.getMaxX(), centerY);
			if (nrHandlers == 9) {
				// rotation handler
				setHandlerFromCenter(8, centerX,
						rectangle.getMinY() - ROTATION_HANDLER_DISTANCE);
			}
		}
	}

	private void setHandlerFromCenter(int i, double x, double y) {
		handlers.get(i).setFrameFromCenter(x, y, x + HANDLER_RADIUS, y + HANDLER_RADIUS);
	}

	/**
	 * method to draw the bounding box construction for selected geo
	 *
	 * @param g2
	 *            - graphics
	 */
	public void draw(GGraphics2D g2) {
		// draw bounding box
		if (rectangle != null && nrHandlers > 2) {
			g2.setColor(GColor.newColor(192, 192, 192, 0.0));
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f,
					GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER));
			g2.fill(rectangle);
			g2.setColor(color);
			g2.draw(rectangle);
		}
		if (handlers != null && !handlers.isEmpty() && !isCropBox) {
			// join rotation handler and bounding box
			if (nrHandlers == 9) {
				GLine2D line = AwtFactory.getPrototype().newLine2D();
				line.setLine((rectangle.getMinX() + rectangle.getMaxX()) / 2,
						rectangle.getMinY(),
						(rectangle.getMinX() + rectangle.getMaxX()) / 2,
						rectangle.getMinY() - ROTATION_HANDLER_DISTANCE);
				g2.setColor(color);
				g2.draw(line);
			}

			for (int i = 0; i < nrHandlers; i++) {
				g2.setPaint(color);
				g2.fill(handlers.get(i));
				g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f,
						GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER));
				g2.setColor(GColor.GEOGEBRA_GRAY);
				g2.draw(handlers.get(i));
			}
		}
		if (cropHandlers != null && !cropHandlers.isEmpty() && isCropBox) {
			g2.setColor(GColor.WHITE);
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(6.0f,
					GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND));
			for (int i = 0; i < nrHandlers; i++) {
				g2.draw(cropHandlers.get(i));
			}
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(4.0f,
					GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND));
			g2.setColor(GColor.BLACK);
			for (int i = 0; i < nrHandlers; i++) {
				g2.draw(cropHandlers.get(i));
			}
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
	 *
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
		int index = -1;
		if (fixed) {
			return -1;
		}
		if (!handlers.isEmpty() && !isCropBox) {
			for (int i = 0; i < handlers.size(); i++) {
				GEllipse2DDouble point = handlers.get(i);
				if (hit(point, x, y, hitThreshold)) {
					return i;
				}
			}
		}
		if (cropHandlers != null && !cropHandlers.isEmpty() && isCropBox) {
			for (int i = 0; i < cropHandlers.size(); i++) {
				GGeneralPath cropHandler = cropHandlers.get(i);
				if (hit(cropHandler, x, y, hitThreshold)) {
					return i;
				}
			}
		}
		return index;
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
	 *            - threshold
	 * @return true if hits any side of boundingBox
	 */
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		if (rectangle == null || nrHandlers == 2) {
			return false;
		}
		return
		// left side
		onSegment(rectangle.getMinX(), rectangle.getMinY(), x, y,
				rectangle.getMinX(), rectangle.getMaxY(), hitThreshold)
				// top side
				|| onSegment(rectangle.getMinX(), rectangle.getMinY(), x, y,
						rectangle.getMaxX(), rectangle.getMinY(), hitThreshold)
				// bottom side
				|| onSegment(rectangle.getMinX(), rectangle.getMaxY(), x, y,
						rectangle.getMaxX(), rectangle.getMaxY(), hitThreshold)
				// right side
				|| onSegment(rectangle.getMaxX(), rectangle.getMinY(), x, y,
						rectangle.getMaxX(), rectangle.getMaxY(), hitThreshold)
				// rotation handler
				|| (nrHandlers == 9 && onSegment(
						(rectangle.getMinX() + rectangle.getMaxX()) / 2,
						rectangle.getMinY(), x, y,
						(rectangle.getMinX() + rectangle.getMaxX()) / 2,
						rectangle.getMinY() - ROTATION_HANDLER_DISTANCE,
						hitThreshold));
	}

	// check if intersection point is on segment
	private static boolean onSegment(double segStartX, double segStartY,
			int hitX, int hitY, double segEndX, double segEndY,
			int hitThreshold) {
		if (hitX <= Math.max(segStartX, segEndX) + 2 * hitThreshold
				&& hitX >= Math.min(segStartX, segEndX) - 2 * hitThreshold
				&& hitY <= Math.max(segStartY, segEndY) + 2 * hitThreshold
				&& hitY >= Math.min(segStartY, segEndY) - 2 * hitThreshold) {
			return true;
		}
		return false;
	}

	/**
	 * @param video
	 *            widget
	 * @param p
	 *            pointer position
	 * @param handler
	 *            hit handler
	 */
	public void resize(DrawWidget video, GPoint2D p,
			EuclidianBoundingBoxHandler handler) {
		int eventX = (int) p.getX();
		int eventY = (int) p.getY();
		int newWidth = 1;
		int newHeight = 1;
		boolean fixRatio = video.isFixedRatio();
		int sizeThreshold = ((Drawable) video).getWidthThreshold();
		switch (handler) {
		case TOP_RIGHT:
			newWidth = Math.max(eventX - video.getLeft(), sizeThreshold);
			newHeight = (int) (video.getOriginalRatio() * newWidth);
			video.setAbsoluteScreenLoc(video.getLeft(),
					video.getTop() - newHeight + video.getHeight());
			video.setWidth(newWidth);
			video.setHeight(newHeight);
			video.update();
			break;

		case BOTTOM_RIGHT:
			newWidth = Math.max(eventX - video.getLeft(), sizeThreshold);
			newHeight = (int) (video.getOriginalRatio() * newWidth);
			video.setWidth(newWidth);
			video.setHeight(newHeight);
			video.update();
			break;

		case TOP_LEFT:
			newWidth = video.getWidth() + video.getLeft() - eventX;
			if (newWidth <= sizeThreshold) {
				return;
			}
			newHeight = (int) (video.getOriginalRatio() * newWidth);
			video.setAbsoluteScreenLoc(eventX, video.getTop() - newHeight + video.getHeight());
			video.setWidth(newWidth);
			video.setHeight(newHeight);
			video.update();
			break;

		case BOTTOM_LEFT:
			newWidth = video.getWidth() + video.getLeft() - eventX;
			if (newWidth <= sizeThreshold) {
				return;
			}
			newHeight = (int) (video.getOriginalRatio() * newWidth);
			video.setAbsoluteScreenLoc(eventX, video.getTop());
			video.setWidth(newWidth);
			video.setHeight(newHeight);
			video.update();
			break;

		case RIGHT:
			newWidth = Math.max(eventX - video.getLeft(), sizeThreshold);
			video.setWidth(newWidth);
			if (fixRatio) {
				Double h = video.getOriginalRatio() * newWidth;
				newHeight = h.intValue();
				video.setAbsoluteScreenLoc(video.getLeft(),
						video.getTop() + video.getHeight() / 2 - newHeight / 2);
				video.setHeight(newHeight);
			} else {
				video.resetRatio();
			}
			video.update();
			break;

		case LEFT:
			newWidth = video.getWidth() + video.getLeft() - eventX;
			if (newWidth <= sizeThreshold) {
				return;
			}
			video.setWidth(newWidth);
			if (fixRatio) {
				Double h = video.getOriginalRatio() * newWidth;
				newHeight = h.intValue();
				video.setAbsoluteScreenLoc(eventX,
						video.getTop() + video.getHeight() / 2 - newHeight / 2);
				video.setHeight(newHeight);
			} else {
				video.setAbsoluteScreenLoc(eventX, video.getTop());
				video.resetRatio();
			}
			video.update();
			break;

		case TOP:
			newHeight = video.getHeight() + video.getTop() - eventY;
			if (newHeight <= sizeThreshold) {
				return;
			}
			video.setHeight(newHeight);
			if (fixRatio) {
				Double w = (newHeight / video.getOriginalRatio());
				newWidth = w.intValue();
				video.setAbsoluteScreenLoc(video.getLeft() + video.getWidth() / 2 - newWidth / 2,
						eventY);
				video.setWidth(newWidth);
			} else {
				video.setAbsoluteScreenLoc(video.getLeft(), eventY);
				video.resetRatio();
			}
			video.update();
			break;

		case BOTTOM:
			newHeight = Math.max(eventY - video.getTop(), sizeThreshold);
			video.setHeight(newHeight);
			if (fixRatio) {
				Double w = newHeight / video.getOriginalRatio();
				newWidth = w.intValue();
				video.setAbsoluteScreenLoc(video.getLeft() + video.getWidth() / 2 - newWidth / 2,
						video.getTop());
				video.setWidth(newWidth);
			} else {
				video.resetRatio();
			}
			video.update();
			break;
		case UNDEFINED:
		default:
			break;
		}

	}

	/**
	 * @param nrHandler
	 *            handler
	 * @param drawable
	 *            drawable for the bounding box
	 * @return resizing cursor or null
	 */
	public static EuclidianCursor getCursor(EuclidianBoundingBoxHandler nrHandler,
			Drawable drawable) {
		if (drawable instanceof DrawSegment) {
			return EuclidianCursor.DRAG;
		}
		switch (nrHandler) {
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
}
