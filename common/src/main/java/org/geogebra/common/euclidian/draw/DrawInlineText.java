package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.RotatableBoundingBox;
import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;

/**
 * Class that handles drawing inline text elements.
 */
public class DrawInlineText extends Drawable implements RemoveNeeded, DrawWidget, DrawMedia {

	public static final int PADDING = 8;

	private GeoInlineText text;
	private InlineTextController textController;

	private TransformableRectangle rectangle;

	/**
	 * Create a new DrawInlineText instance.
	 *
	 * @param view view
	 * @param text geo element
	 */
	public DrawInlineText(EuclidianView view, GeoInlineText text) {
		super(view, text);
		rectangle = new TransformableRectangle(view, text);
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
		GPoint2D point = text.getLocation();

		rectangle.updateSelfAndBoundingBox();

		double angle = text.getAngle();
		double width = text.getWidth();
		double height = text.getHeight();

		if (textController != null && point != null) {
			textController.setLocation(view.toScreenCoordX(point.getX()),
					view.toScreenCoordY(point.getY()));
			textController.setHeight((int) (height - 2 * PADDING));
			textController.setWidth((int) (width - 2 * PADDING));
			textController.setAngle(angle);
			if (text.updateFontSize()) {
				updateContent();
			}
		}
	}

	@Override
	public void updateContent() {
		if (textController != null) {
			textController.updateContent();
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

	@Override
	public void toForeground(int x, int y) {
		if (textController != null) {
			GPoint2D p = rectangle.getInversePoint(x - PADDING, y - PADDING);
			textController.toForeground((int) p.getX(), (int) p.getY());
		}
	}

	/**
	 * @param x x mouse coordinate in pixels
	 * @param y y mouse coordinate in pixels
	 * @return the url of the current coordinate, or null, if there is
	 * nothing at (x, y), or it has no url set
	 */
	public String urlByCoordinate(int x, int y) {
		if (textController != null) {
			GPoint2D p = rectangle.getInversePoint(x - PADDING, y - PADDING);
			return textController.urlByCoordinate((int) p.getX(), (int) p.getY());
		}

		return "";
	}

	@Override
	public GRectangle getBounds() {
		return rectangle.getBounds();
	}

	@Override
	public RotatableBoundingBox getBoundingBox() {
		return rectangle.getBoundingBox();
	}

	@Override
	public double getWidthThreshold() {
		if (text.getHeight() - text.getMinHeight() < 2) {
			return text.getWidth();
		}

		return GeoInlineText.DEFAULT_WIDTH;
	}

	@Override
	public double getHeightThreshold() {
		return text.getMinHeight();
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (textController != null) {
			textController.draw(g2, rectangle.getDirectTransform());
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return rectangle.hit(x, y);
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
			textController.setHeight(newHeight - 2 * PADDING);
		}
	}

	@Override
	public int getLeft() {
		return rectangle.getLeft();
	}

	@Override
	public int getTop() {
		return rectangle.getTop();
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
		return rectangle.getWidth();
	}

	@Override
	public int getHeight() {
		return rectangle.getHeight();
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
		rectangle.updateByBoundingBoxResize(point, handler);
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		double newAngle = Math.atan2(points.get(1).getY() - points.get(0).getY(),
				points.get(1).getX() - points.get(0).getX());

		double newWidth = Math.max(GeoInlineText.DEFAULT_WIDTH,
				points.get(1).distance(points.get(0)));

		double newHeight = points.get(2).distance(points.get(0));

		if (newHeight < text.getMinHeight()) {
			return;
		}

		text.setWidth(newWidth);
		text.setHeight(newHeight);
		text.setAngle(newAngle);
		text.setLocation(new GPoint2D(
						view.toRealWorldCoordX(points.get(0).getX()),
						view.toRealWorldCoordY(points.get(0).getY())
				)
		);
	}

	@Override
	protected List<GPoint2D> toPoints() {
		return rectangle.getCorners();
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

	/**
	 * @return hyperlink of selected part, or at the end of text element if no selection
	 */
	public String getHyperLinkURL() {
		if (textController != null) {
			return textController.getHyperLinkURL();
		}
		return "";
	}

	/**
	 * Switch the list type of selected text
	 * @param listType - numbered or bullet list
	 */
	public void switchListTo(String listType) {
		if (textController != null) {
			textController.switchListTo(listType);
		}
	}

	/**
	 * @return list style of selected text
	 */
	public String getListStyle() {
		if (textController != null) {
			return textController.getListStyle();
		}
		return "";
	}

	public InlineTextController getTextController() {
		return textController;
	}
}
