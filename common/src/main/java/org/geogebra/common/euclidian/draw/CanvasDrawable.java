package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public abstract class CanvasDrawable extends Drawable {
	private static final int HIGHLIGHT_MARGIN = 2;
	private boolean drawingOnCanvas;
	private GFont labelFont;
	GPoint labelSize = new GPoint(0, 0);
	private int labelFontSize;
	GBox box;
	GDimension preferredSize;
	int boxLeft;
	int boxTop;
	int boxWidth;
	int boxHeight;

	public boolean isDrawingOnCanvas() {
		return drawingOnCanvas;
	}

	public void setDrawingOnCanvas(boolean drawOnCanvas) {
		this.drawingOnCanvas = drawOnCanvas;
	}

	public static boolean isLatexString(String text) {
		return text.startsWith("$") && text.trim().endsWith("$");
	}

	protected GDimension drawLatex(GGraphics2D g2, GeoElement geo0, GFont font,
			String text, int x, int y) {
		App app = view.getApplication();
		return app.getDrawEquation().drawEquation(app, geo0, g2, x, y, text,
				font,
 false, geo.getObjectColor(), geo.getBackgroundColor(),
				false,
				false, null);
	};
	
	protected boolean measureLabel(GGraphics2D g2, GeoElement geo0,
			String text) {
		boolean latex = false;
		if (geo.isLabelVisible()) {
			latex = isLatexString(text);
			// no drawing, just measuring.
			if (latex) {
				GDimension d = drawLatex(g2, geo0, getLabelFont(), text,
						xLabel, yLabel);
				labelSize.x = d.getWidth();
				labelSize.y = d.getHeight();
			} else {
				setLabelSize(EuclidianStatic

				.drawIndexedString(view.getApplication(), g2, text, 0, 0,
						false, false, false));
			}
			calculateBoxBounds(latex);
		} else {
			calculateBoxBounds();
		}
		return latex;
	}

	protected void calculateBoxBounds(boolean latex) {
		boxLeft = xLabel + labelSize.x + 2;
		boxTop = latex
				? yLabel + (labelSize.y - getPreferredSize().getHeight()) / 2
				: yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = getPreferredSize().getHeight();
	}

	protected void calculateBoxBounds() {
		boxLeft = xLabel + 2;
		boxTop = yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = getPreferredSize().getHeight();
	}

	protected void highlightLabel(GGraphics2D g2, boolean latex) {
		if (geo.isLabelVisible() && geo.doHighlighting()) {
			if (latex) {
				g2.fillRect(xLabel, yLabel, labelSize.x, labelSize.y);
			} else {
				g2.fillRect(xLabel, yLabel + ((boxHeight - getH()) / 2),
						labelSize.x, getH() + HIGHLIGHT_MARGIN);
			}
		}
	}

	protected int getTextBottom() {
		return (getPreferredSize().getHeight() / 2)
				+ (int) (getLabelFontSize() * 0.4);
	}

	protected int getH() {
		return (int) (getLabelFontSize() * 1.2 + HIGHLIGHT_MARGIN);

	}

	protected void drawLabel(GGraphics2D g2, GeoElement geo0, String text) {

		if (isLatexString(text)) {
			drawLatex(g2, geo0, getLabelFont(), text, xLabel, yLabel);
		} else {
			g2.setPaint(geo.getObjectColor());

			EuclidianStatic.drawIndexedString(view.getApplication(), g2,
 text,
					xLabel, yLabel + getTextBottom(), false, false);
		}

	}

	protected void drawOnCanvas(org.geogebra.common.awt.GGraphics2D g2,
			String text) {
		App app = view.getApplication();
		setPreferredSize(getPreferredSize());

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

	protected abstract void drawWidget(GGraphics2D g2);

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		boolean res = false;
		if (isDrawingOnCanvas()) {
			int left = xLabel;
			int top = boxTop;
			int right = left + labelSize.x + boxWidth;
			int bottom = top + boxHeight;
			//
			res = (x > left && x < right && y > top && y < bottom)
					|| (x > xLabel && x < xLabel + labelSize.x && y > yLabel
							&& y < yLabel + labelSize.y);
			;
			if (res) {
				App.debug("[CD] hit");
			}
		} else {
			res = box.getBounds().contains(x, y);
		}
		return res;
	}
	public GFont getLabelFont() {
		return labelFont;
	}

	public void setLabelFont(GFont labelFont) {
		this.labelFont = labelFont;
	}

	public GPoint getLabelSize() {
		return labelSize;
	}

	public void setLabelSize(GPoint labelSize) {
		this.labelSize = labelSize;
	}

	public int getLabelFontSize() {
		return labelFontSize;
	}

	public void setLabelFontSize(int labelFontSize) {
		this.labelFontSize = labelFontSize;
	}

	public GDimension getPreferredSize() {
		return preferredSize;
	}

	public void setPreferredSize(GDimension preferredSize) {
		this.preferredSize = preferredSize;
	}

	@Override
	public boolean isInside(org.geogebra.common.awt.GRectangle rect) {
		return rect.contains(labelRectangle);
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return box.getBounds().intersects(rect);
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

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

}
