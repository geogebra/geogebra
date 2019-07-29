package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

/**
 * Furniture drawbale on canvas
 */
public abstract class CanvasDrawable extends Drawable {
	private static final int HIGHLIGHT_MARGIN = 2;

	// private boolean drawingOnCanvas;
	private GFont labelFont;
	GPoint labelSize = new GPoint(0, 0);
	private int labelFontSize;
	private final GRectangle hitRect = AwtFactory.getPrototype().newRectangle();
	int boxLeft;
	int boxTop;
	int boxWidth;
	int boxHeight;

	@Override
	public boolean isHighlighted() {
		return super.isHighlighted();
	}

	/**
	 * @param text
	 *            text
	 * @return whether text starts and ends with $
	 */
	public static boolean isLatexString(String text) {
		return text.length() > 1 && text.startsWith("$")
				&& text.trim().endsWith("$");
	}

	/**
	 * @param g2
	 *            graphics
	 * @param geo0
	 *            geoelement
	 * @param font
	 *            font
	 * @param text
	 *            content
	 * @return size
	 */
	protected GDimension measureLatex(GGraphics2D g2, GeoElement geo0,
			GFont font, String text) {
		return drawLatex(g2, geo0, font, text, Integer.MIN_VALUE,
				Integer.MIN_VALUE);
	}

	/**
	 * @param g2
	 *            graphics
	 * @param geo0
	 *            geoelement
	 * @param font
	 *            font
	 * @param text
	 *            content
	 * @param x
	 *            screen x-coord
	 * @param y
	 *            screen y-coord
	 * @return size
	 */
	protected GDimension drawLatex(GGraphics2D g2, GeoElement geo0, GFont font,
			String text, int x, int y) {
		App app = view.getApplication();

		// eg $\math{x}$ for nice x
		boolean serif = StringUtil.startsWithFormattingCommand(text);

		if (!serif && geo0 instanceof TextProperties) {
			serif = ((TextProperties) geo0).isSerifFont();
		}

		GDimension ret = app.getDrawEquation().drawEquation(app, geo0, g2, x, y,
				text,
				font, serif, geo.getObjectColor(), geo.getBackgroundColor(),
				false, false, view.getCallBack(geo, firstCall));
		firstCall = false;
		return ret;
	}

	/**
	 * @param app
	 *            application
	 * @param geo0
	 *            related geo
	 * @param font
	 *            font
	 * @param text
	 *            text
	 * @return size of text with given font
	 */
	public static GDimension measureLatex(App app,
			GeoElement geo0, GFont font, String text) {
		return app.getDrawEquation().measureEquation(app, geo0, text, font,
				false);
	}

	/**
	 * @param app
	 *            application
	 * @param geo0
	 *            related geo
	 * @param font
	 *            font
	 * @param text
	 *            text
	 * @param serif
	 *            serif or sans-serif
	 * @return size of text with given font
	 */
	public static GDimension measureLatex(App app, GeoElement geo0, GFont font,
			String text, boolean serif) {
		return app.getDrawEquation().measureEquation(app, geo0, text, font,
				serif);
	}

	/**
	 * @param g2
	 *            graphics
	 * @param geo0
	 *            geoelement
	 * @param text
	 *            text
	 * @return whether it's LaTeX
	 */
	protected boolean measureLabel(GGraphics2D g2, GeoElement geo0,
			String text) {
		boolean latex = false;
		if (geo.isLabelVisible()) {
			latex = isLatexString(text);
			// no drawing, just measuring.
			if (latex) {
				GDimension d = measureLatex(g2, geo0, getLabelFont(), text);
				labelSize.x = d.getWidth();
				labelSize.y = d.getHeight();
			} else {
				g2.setFont(getLabelFont());
				setLabelSize(
						EuclidianStatic.drawIndexedString(view.getApplication(),
								g2, text, 0, 0, false, false, null, null));
			}
			calculateBoxBounds(latex);
		} else {
			calculateBoxBounds();
		}
		return latex;
	}

	/**
	 * Update box bounds.
	 * 
	 * @param latex
	 *            whether the caption is latex
	 */
	protected void calculateBoxBounds(boolean latex) {
		boxLeft = xLabel + labelSize.x + 2;
		boxTop = latex
				? yLabel + (labelSize.y - getPreferredHeight()) / 2
				: yLabel;
		boxWidth = getPreferredWidth();
		boxHeight = getPreferredHeight();
	}

	/**
	 * Update box bounds.
	 */
	protected void calculateBoxBounds() {
		boxLeft = xLabel + 2;
		boxTop = yLabel;
		boxWidth = getPreferredWidth();
		boxHeight = getPreferredHeight();
	}

	/**
	 * @param g2
	 *            graphics
	 * @param latex
	 *            whether the label is latex
	 */
	protected void highlightLabel(GGraphics2D g2, boolean latex) {
		if (geo.isLabelVisible() && isHighlighted()) {
			if (!view.getApplication().isDesktop()) {
				g2.setPaint(GColor.LIGHT_GRAY);
			}
			if (latex) {
				g2.fillRect(xLabel, yLabel, labelSize.x, labelSize.y);
			} else {
				g2.fillRect(xLabel, yLabel + ((boxHeight - getH()) / 2),
						labelSize.x, getH() + HIGHLIGHT_MARGIN);
			}
		}
	}

	private int getH() {
		return (int) (getLabelFontSize() * 1.2 + HIGHLIGHT_MARGIN);
	}

	/**
	 * @param g2
	 *            graphics
	 * @param text
	 *            text
	 */
	protected void drawOnCanvas(GGraphics2D g2, String text) {
		App app = view.getApplication();

		GFont vFont = view.getFont();
		setLabelFont(app.getFontCanDisplay(text, false, vFont.getStyle(),
				getLabelFontSize()));

		g2.setFont(getLabelFont());
		g2.setStroke(EuclidianStatic.getDefaultStroke());

		g2.setPaint(geo.getObjectColor());

		if (geo.isVisible()) {
			drawWidget(g2);
		}
	}

	/**
	 * @param g2
	 *            graphics
	 * @param text
	 *            text
	 * @param font
	 *            font
	 * @return layout of text for given font
	 */
	protected GTextLayout getLayout(GGraphics2D g2, String text, GFont font) {
		// make sure layout won't be null ("" makes it null).
		return getTextLayout("".equals(text) ? "A" : text, font, g2);
	}

	/**
	 * Draw the shape and update the widget.
	 * 
	 * @param g2
	 *            graphics
	 */
	protected abstract void drawWidget(GGraphics2D g2);

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return hitLabelBounds(x, y) || hitWidgetBounds(x, y);
	}

	protected boolean hitWidgetBounds(int x, int y) {
		int left = xLabel;
		int top = boxTop;
		int right = left + labelSize.x + boxWidth;
		int bottom = top + boxHeight;
		return x > left && x < right && y > top && y < bottom;
	}

	/**
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @return if label rectangle was hit by (x, y) pointer.
	 */
	protected boolean hitLabelBounds(int x, int y) {
		return x > xLabel && x < xLabel + labelSize.x && y > yLabel
				&& y < yLabel + labelSize.y;
	}

	/**
	 * @return font for label
	 */
	public GFont getLabelFont() {
		// deriveFont() as quick fix for GGB-2094
		return labelFont.deriveFont(GFont.PLAIN);
	}

	private void setLabelFont(GFont labelFont) {
		this.labelFont = labelFont;
	}

	private void setLabelSize(GPoint labelSize) {
		this.labelSize = labelSize;
	}

	/**
	 * @return label font size
	 */
	public int getLabelFontSize() {
		return labelFontSize;
	}

	/**
	 * @param labelFontSize
	 *            label font size
	 */
	public void setLabelFontSize(int labelFontSize) {
		this.labelFontSize = labelFontSize;
	}

	/**
	 * @return dimension on screen
	 */
	public abstract int getPreferredWidth();

	/**
	 * @return height on screen
	 */
	public abstract int getPreferredHeight();

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(labelRectangle);
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		updateHitRect();
		return hitRect.intersects(rect);
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
	}

	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	/**
	 * @param show
	 *            whether to show or hide the widget
	 */
	public void setWidgetVisible(boolean show) {
		// only for InputBox
	}

	/**
	 * @return hit rectangle
	 */
	public GRectangle getHitRect() {
		return labelRectangle;
	}

	@Override
	public GRectangle getBounds() {
		updateHitRect();
		return hitRect;
	}

	private void updateHitRect() {
		hitRect.setBounds(geo.labelOffsetX, boxTop,
				boxWidth + boxLeft - geo.labelOffsetX, boxHeight);
	}

}
