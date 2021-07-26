package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

public class DrawDynamicCaption {
	private final GeoElement geo;
	private final GeoText captionCopy;
	private final DrawText drawCaption;
	private final Drawable drawable;
	private int captionWidth;
	private int captionHeight;

	/**
	 *
	 * @param view {@link EuclidianView}
	 * @param drawable {@link Drawable}
	 */
	public DrawDynamicCaption(EuclidianView view, Drawable drawable) {
		this.drawable = drawable;
		this.geo = drawable.getGeoElement();
		captionCopy = new GeoText(geo.getConstruction());
		drawCaption = new DrawText(view, captionCopy);
	}

	public boolean isEnabled() {
		return geo.hasDynamicCaption();
	}

	/**
	 * draw dynamic caption
	 * @param g2 canvas
	 */
	public void draw(GGraphics2D g2) {
		if (noCaption()) {
			return;
		}

		update();
		measure(g2);
		highlight();
		position();
		drawCaption.draw(g2);
	}

	/**
	 * measure label
	 * @param g2 canvas
	 */
	public void measure(GGraphics2D g2) {
		String textString = getDynamicCaption().getTextString();
		if (getDynamicCaption().isLaTeX()) {
			App app = drawCaption.getView().getApplication();
			boolean serif = StringUtil.startsWithFormattingCommand(textString);
			GDimension size = app.getDrawEquation().measureEquation(app,
					drawCaption.getGeoElement(),
					textString, g2.getFont(), serif);
			if (size != null) {
				captionWidth = size.getWidth();
				captionHeight = size.getHeight();
			}
		} else {
			GFont font = drawCaption.getView().getFontPoint();
			GTextLayout layout = Drawable.getTextLayout(textString,
					font, g2);
			if (layout != null) {
				captionHeight = (int) layout.getBounds().getHeight();
				captionWidth = (int) layout.getBounds().getWidth();
			} else {
				captionWidth = 0;
				captionHeight = font.getSize();
			}
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
		if (geo instanceof GeoInputBox) {
			captionCopy.setFontSizeMultiplier(((GeoInputBox) geo).getFontSizeMultiplier());
		}
		captionCopy.setEuclidianVisible(true);
		captionCopy.setAbsoluteScreenLocActive(true);
		drawCaption.update();
	}

	private GeoText getDynamicCaption() {
		return geo.getDynamicCaption();
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

		if (drawable instanceof CanvasDrawable) {
			((CanvasDrawable) drawable).labelSize.x = captionWidth;
			((CanvasDrawable) drawable).labelSize.y = captionHeight;
			if (drawable instanceof DrawInputBox) {
				((CanvasDrawable) drawable).calculateBoxBounds();
			} else if (drawable instanceof DrawDropDownList) {
				((CanvasDrawable) drawable).calculateBoxBounds(getDynamicCaption().isLaTeX());
			}
		}
		return getDynamicCaption().isLaTeX();
	}

	private void position() {
		drawCaption.yLabel = drawable.getCaptionY(getDynamicCaption().isLaTeX(), captionHeight);
		if (drawable instanceof DrawInputBox) {
			drawCaption.xLabel = drawable.xLabel - captionWidth;
		} else if (drawable instanceof DrawBoolean) {
			int margin = getDynamicCaption().isLaTeX() ? DrawBoolean.LABEL_MARGIN_LATEX
					: DrawBoolean.LABEL_MARGIN_TEXT;
			drawCaption.xLabel = geo.labelOffsetX
					+ ((DrawBoolean) drawable).getCheckBoxIcon().getIconWidth()
					+ margin + DrawBoolean.LEGACY_OFFSET;
		} else {
			drawCaption.xLabel = drawable.xLabel;
		}
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
		return drawable.isHighlighted();
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

	public String getDynCaptionText() {
		return getDynamicCaption().getTextString();
	}
}
