package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoText;

public class DrawDynamicCaption {
	private final GeoInputBox inputBox;
	private final GeoText captionCopy;
	private final DrawText drawCaption;
	private final DrawInputBox drawInputBox;
	private int captionWidth;
	private int captionHeight;

	/**
	 *
	 * @param view {@link EuclidianView}
	 * @param drawInputBox {@link DrawInputBox}
	 */
	public DrawDynamicCaption(EuclidianView view,
			DrawInputBox drawInputBox) {
		this.drawInputBox = drawInputBox;
		this.inputBox = drawInputBox.getGeoInputBox();
		captionCopy = new GeoText(inputBox.cons);
		drawCaption = new DrawText(view, captionCopy);
	}

	public boolean isEnabled() {
		return inputBox.hasDynamicCaption();
	}

	void draw(GGraphics2D g2) {
		if (noCaption()) {
			return;
		}

		update();
		measure(g2);
		highlight();
		position();
		drawCaption.draw(g2);
	}

	private void measure(GGraphics2D g2) {
		drawCaption.xLabel = Integer.MIN_VALUE;
		drawCaption.yLabel = Integer.MIN_VALUE;

		drawCaption.draw(g2);
		GRectangle bounds = drawCaption.getBounds();
		if (bounds != null) {
			captionWidth = (int) bounds.getWidth();
			captionHeight = (int) bounds.getHeight();
		}
	}

	private boolean noCaption() {
		return getDynamicCaption() == null;
	}

	/**
	 * Update dynamic caption
	 */
	public void update() {
		if (noCaption() || !isEnabled()) {
			return;
		}

		updateCaptionCopy();
		setLabelSize();
	}

	private void updateCaptionCopy() {
		captionCopy.set(getDynamicCaption());
		captionCopy.setAllVisualPropertiesExceptEuclidianVisible(getDynamicCaption(),
				false, false);
		captionCopy.setFontSizeMultiplier(inputBox.getFontSizeMultiplier());
		captionCopy.setEuclidianVisible(true);
		captionCopy.setAbsoluteScreenLocActive(true);
		drawCaption.update();
	}

	private GeoText getDynamicCaption() {
		return inputBox.getDynamicCaption();
	}

	/**
	 * Sets the dimension of the inputbox label.
	 *
	 * @return if label is latex or not.
	 */
	public boolean setLabelSize() {
		if (drawCaption == null) {
			return false;
		}

		drawInputBox.labelSize.x = captionWidth;
		drawInputBox.labelSize.y = captionHeight;
		drawInputBox.calculateBoxBounds();
		return getDynamicCaption().isLaTeX();
	}

	private void position() {
		drawCaption.xLabel = drawInputBox.xLabel - captionWidth;
		int middle = drawInputBox.boxTop + drawInputBox.boxHeight / 2;
		drawCaption.yLabel = getDynamicCaption().isLaTeX()
				? middle - captionHeight / 2
				: drawInputBox.yLabel + drawInputBox.getTextBottom();
	}

	/**
	 * Highlight the caption.
	 */
	public void highlight() {
		captionCopy.setBackgroundColor(
				isHighlighted()
				? GColor.LIGHT_GRAY
				: getDynamicCaption().getBackgroundColor());
	}

	private boolean isHighlighted() {
		return drawInputBox.isHighlighted();
	}

	public int getHeight() {
		return captionHeight;
	}

	/**
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @param hitThreshold
	 *            pixel threshold
	 * @return true if dynamic caption is hit
	 */
	public boolean hit(int x, int y, int hitThreshold) {
		if (!isEnabled()) {
			return false;
		}
		return drawCaption.hit(x, y, hitThreshold);
	}
}
