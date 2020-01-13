package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;

/**
 * Class that handles drawing inline text elements.
 */
public class DrawInlineText extends Drawable implements RemoveNeeded, DrawWidget {

	private static final int padding = 8;
	private GeoInlineText text;
	private InlineTextController textController;

	/**
	 * Create a new DrawInlineText instance.
	 *
	 * @param view view
	 * @param text geo element
	 */
	public DrawInlineText(EuclidianView view, GeoInlineText text) {
		super(view, text);
		this.text = text;
		this.textController = view.createInlineTextController(text);
		createEditor();
		update();
	}

	private void createEditor() {
		if (textController != null) {
			textController.create();
		}
	}

	@Override
	public void update() {
		if (textController != null) {
			GPoint2D point = text.getLocation();
			textController.setLocation(view.toScreenCoordX(point.getX()) + padding,
					view.toScreenCoordY(point.getY()) + padding);
			textController.setHeight((int) (text.getHeight() - 2 * padding));
			textController.setWidth((int) (text.getWidth() - 2 * padding));
			textController.setAngle(text.getAngle());
			if (text.updateFontSize()) {
				textController.updateContent();
			}
		}
	}

	/**
	 * Send this to background
	 */
	public void toBackground() {
		if (textController != null) {
			textController.toBackground();
		}
	}

	/**
	 * Send this to foreground
	 * @param x x mouse coordinates in pixels
	 * @param y y mouse coordinates in pixels
	 */
	public void toForeground(int x, int y) {
		if (textController != null) {
			textController.toForeground(x, y);
		}
	}

	@Override
	public GRectangle getBounds() {
		return AwtFactory.getPrototype().newRectangle(getLeft(), getTop(), getWidth(), getHeight());
	}

	@Override
	public double getWidthThreshold() {
		return GeoInlineText.DEFAULT_WIDTH;
	}

	@Override
	public double getHeightThreshold() {
		return text.getMinHeight();
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (textController != null) {
			textController.draw(g2, getLeft(), getTop());
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return getBounds().contains(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(getBounds());
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void remove() {
		if (textController != null) {
			textController.discard();
		}
	}

	@Override
	public void setWidth(int newWidth) {
		text.setWidth(newWidth);
		if (textController != null) {
			textController.setWidth(newWidth);
		}
	}

	@Override
	public void setHeight(int newHeight) {
		text.setHeight(newHeight);
		if (textController != null) {
			textController.setHeight(newHeight);
		}
	}

	@Override
	public int getLeft() {
		GPoint2D point = text.getLocation();
		return view.toScreenCoordX(point.getX());
	}

	@Override
	public int getTop() {
		GPoint2D point = text.getLocation();
		return view.toScreenCoordY(point.getY());
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		// Not implemented
	}

	@Override
	public double getOriginalRatio() {
		return 0;
	}

	@Override
	public int getWidth() {
		return (int) text.getWidth();
	}

	@Override
	public int getHeight() {
		return (int) text.getHeight();
	}

	@Override
	public void resetRatio() {
		// Not implemented
	}

	@Override
	public boolean isFixedRatio() {
		return false;
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D point, EuclidianBoundingBoxHandler handler) {
		// Not implemented
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		double newWidth = Math.abs(points.get(1).getX() - points.get(0).getX());
		double newHeight = Math.abs(points.get(1).getY() - points.get(0).getY());

		if (Math.abs(newWidth - getWidth()) > 1 || Math.abs(newHeight - getHeight()) > 1) {
			text.setWidth(newWidth);
			text.setHeight(newHeight);
			text.setLocation(
					AwtFactory.getPrototype().newPoint2D(
							view.toRealWorldCoordX(Math.min(points.get(0).getX(),
									points.get(1).getX())),
							view.toRealWorldCoordY(Math.min(points.get(0).getY(),
									points.get(1).getY()))
					)
			);
		}
	}

	/**
	 * @param key
	 *            formatting option
	 * @param val
	 *            value (String, int or bool, depending on key)
	 */
	public void format(String key, Object val) {
		if (textController != null) {
			textController.format(key, val);
		}
	}

	/**
	 * @param key formatting option name
	 * @param fallback fallback when not set / indeterminate
	 * @param <T> option type
	 * @return formatting option value or fallback
	 */
	public <T> T getFormat(String key, T fallback) {
		if (textController != null) {
			return textController.getFormat(key, fallback);
		}
		return fallback;
	}
}
