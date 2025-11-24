package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
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
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoInlineText;

/**
 * Class that handles drawing inline text elements.
 */
public class DrawInlineText extends Drawable implements DrawInline {

	public static final int PADDING = 8;

	protected final GeoInline text;
	protected @CheckForNull InlineTextController textController;

	protected final TransformableRectangle rectangle;

	protected final static GBasicStroke border1 = AwtFactory.getPrototype().newBasicStroke(1f,
			GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER);
	protected final static GBasicStroke border3 = AwtFactory.getPrototype().newBasicStroke(3f,
			GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER);

	/**
	 * Create a new DrawInlineText instance.
	 *
	 * @param view view
	 * @param text geo element
	 */
	public DrawInlineText(EuclidianView view, GeoInline text) {
		super(view, text);
		rectangle = new TransformableRectangle(view, text, false);
		this.text = text;
		this.textController = view.getApplication().createInlineTextController(view, text);
		createEditor();
	}

	private void createEditor() {
		if (textController != null) {
			textController.create();
		}
	}

	@Override
	public void update() {
		text.zoomIfNeeded();
		rectangle.updateSelfAndBoundingBox();

		GPoint2D point = text.getLocation();
		if (textController != null && point != null) {
			double angle = text.getAngle();
			double width = text.getWidth();
			double height = text.getHeight();
			double contentWidth = text.getContentWidth();
			double contentHeight = text.getContentHeight();

			textController.setLocation(view.toScreenCoordX(point.getX()),
					view.toScreenCoordY(point.getY()));
			textController.setHeight((int) contentHeight - 2 * PADDING);
			textController.setWidth((int) contentWidth - 2 * PADDING);
			textController.setTransform(angle, width / contentWidth, height / contentHeight);
			if (textController.updateFontSize()) {
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
		draw(g2, 0);
	}

	@Override
	public boolean isInteractiveEditor() {
		return textController != null && textController.isEditing();
	}

	protected void draw(GGraphics2D g2, int borderRadius) {
		if (text.isEuclidianVisible() && textController != null
				&& rectangle.getDirectTransform() != null) {
			double contentWidth = text.getContentWidth();
			double contentHeight = text.getContentHeight();
			GAffineTransform tr =
					rectangle.scaleForZoom(contentWidth, contentHeight);
			g2.saveTransform();
			g2.transform(tr);

			if (geo.getBackgroundColor() != null) {
				g2.setPaint(geo.getBackgroundColor());
				g2.fillRoundRect(0, 0, (int) contentWidth, (int) contentHeight,
						2 * borderRadius, 2 * borderRadius);
			}
			if (geo.getLineThickness() != GeoInlineText.NO_BORDER) {
				g2.setPaint(text.getBorderColor());
				g2.setStroke(getBorderStroke());
				g2.drawRoundRect(0, 0, (int) contentWidth, (int) contentHeight,
						2 * borderRadius, 2 * borderRadius);
			}

			textController.draw(g2);

			g2.restoreTransform();
		}
	}

	protected GBasicStroke getBorderStroke() {
		if (geo.getLineThickness() == 1) {
			return border1;
		} else {
			return border3;
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

	@Override
	public InlineTextController getController() {
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
