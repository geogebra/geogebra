package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.TextRenderer;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.util.MyMath;

public class InputBoxBounds {
	private GRectangle bounds;
	private final GeoInputBox geoInputBox;
	private TextRenderer renderer;

	public InputBoxBounds(GeoInputBox geoInputBox) {
		this.geoInputBox = geoInputBox;
	}

	/**
	 *
	 * @return the bounds.
	 */
	public GRectangle getBounds() {
		return bounds;
	}

	/**
	 *
	 * @param view {@link EuclidianView}
	 * @param labelTop top of the label.
	 * @param textFont font to display.
	 * @param labelDesc label description.
	 */
	public void update(EuclidianView view, double labelTop, GFont textFont, String labelDesc) {
		GGraphics2D g2 = view.getGraphicsForPen();
		bounds = renderer.measureBounds(g2, geoInputBox, textFont, labelDesc);
		int viewHeight = view.getHeight();
		if (labelTop > 0 && labelTop < viewHeight) { // window resized -> keep box offscreen
			bounds.setLocation((int) bounds.getX(),
					(int) MyMath.clamp(bounds.getMinY(), 0,
							viewHeight - bounds.getHeight()));
		}
	}

	/**
	 *
	 * @return y coord of the rectangle.
	 */
	public double getY() {
		return bounds.getY();
	}

	public void setRenderer(TextRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 *
	 * @param x coord.
	 * @param y coord.
	 * @return if bounds has (x, y) within.
	 */
	public boolean contains(int x, int y) {
		return bounds.contains(x, y);
	}
}
