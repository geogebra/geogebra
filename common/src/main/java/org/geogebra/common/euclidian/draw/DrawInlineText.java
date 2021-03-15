package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MediaBoundingBox;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GeoInlineText;

/**
 * Class that handles drawing inline text elements.
 */
public class DrawInlineText extends Drawable implements DrawInline {

	public static final int PADDING = 8;

	private final GeoInlineText text;
	private InlineTextController textController;

	private final TransformableRectangle rectangle;

	/**
	 * Create a new DrawInlineText instance.
	 *
	 * @param view view
	 * @param text geo element
	 */
	public DrawInlineText(EuclidianView view, GeoInlineText text) {
		super(view, text);
		rectangle = new TransformableRectangle(view, text, false);
		this.text = text;
		this.textController = view.getApplication().createInlineTextController(view, text);
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
		rectangle.updateSelfAndBoundingBox();

		GPoint2D point = text.getLocation();
		if (textController != null && point != null) {
			double angle = text.getAngle();
			double width = text.getWidth();
			double height = text.getHeight();

			textController.setLocation(view.toScreenCoordX(point.getX()),
					view.toScreenCoordY(point.getY()));
			textController.setHeight((int) (height - 2 * PADDING));
			textController.setWidth((int) (width - 2 * PADDING));
			textController.setAngle(angle);
			if (text.updateFontSize()) {
				textController.updateContent();
			}
		}
	}

	@Override
	public void updateContent() {
		if (textController != null) {
			textController.updateContentIfChanged();
		}
	}

	@Override
	public void saveContent() {
		if (textController != null) {
			textController.saveContent();
		}
	}

	@Override
	public GAffineTransform getTransform() {
		return rectangle.getDirectTransform();
	}

	@Override
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

	@Override
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
	public MediaBoundingBox getBoundingBox() {
		return rectangle.getBoundingBox();
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (text.isEuclidianVisible() && textController != null
			&& rectangle.getDirectTransform() != null) {
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
	public void remove() {
		if (textController != null) {
			textController.discard();
		}
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D point, EuclidianBoundingBoxHandler handler) {
		rectangle.updateByBoundingBoxResize(point, handler);
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		rectangle.fromPoints(points);
	}

	@Override
	protected List<GPoint2D> toPoints() {
		return rectangle.toPoints();
	}

	@Override
	public BoundingBox<? extends GShape> getSelectionBoundingBox() {
		return getBoundingBox();
	}

	public InlineTextController getTextController() {
		return textController;
	}

	/**
	 * Setter to mock Carota.
	 * Nicer solutions are welcome.
	 *
	 * @param textController to set.
	 */
	public void setTextController(InlineTextController textController) {
		this.textController = textController;
	}
}
