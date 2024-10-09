package org.geogebra.common.euclidian.draw.dropdown;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.draw.CanvasDrawable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

class OptionItem {
	private final GeoList list;
	private GFont font;
	private final GGraphics2D g2;
	private final int index;
	private GDimension dimension;
	private String text;
	private boolean latex;
	private GRectangle rect;

	public OptionItem(GeoList list, GGraphics2D g2, int idx) {
		this.list = list;
		this.g2 = g2;
		index = idx;
		formatText(list, idx);
		rect = null;
	}

	public void update(GFont font) {
		this.font = font;
		if (hasText()) {
			calculateDimensions();
		} else {
			setDimension(0, 0);
		}
	}

	private void setDimension(double width, double height) {
		dimension = AwtFactory.getPrototype().newDimension(
				(int) width, (int) height);
	}

	private void formatText(GeoList list, int idx) {
		GeoElement geoItem = list.get(idx);
		if (GeoList.needsLatex(geoItem)) {
			text = geoItem.toLaTeXString(false,
					StringTemplate.latexTemplate);
			latex = true;
		} else {
			text = list.getItemDisplayString(geoItem,
					StringTemplate.defaultTemplate);
			latex = CanvasDrawable.isLatexString(text);
		}
	}

	private boolean hasText() {
		return !"".equals(text);
	}

	private void calculateDimensions() {
		if (latex) {
			calculateDimensionsForLatex();
		} else {
			calculateDimensionsOfLayout();
		}
	}

	private void calculateDimensionsOfLayout() {
		GTextLayout layout = CanvasDrawable.getLayout(g2, text, font);
		setDimension(Math.round(layout.getBounds().getWidth()),
				 Math.round(layout.getBounds().getHeight()));
	}

	private void calculateDimensionsForLatex() {
		dimension = CanvasDrawable.measureLatex(list.getApp(),
				font, text, CanvasDrawable.shouldBeSerif(text, list.get(index),
						false));
	}

	public int getWidth() {
		return dimension.getWidth();
	}

	public int getHeight() {
		return dimension.getHeight();
	}

	public GRectangle getRect() {
		return rect;
	}

	public int getIndex() {
		return index;
	}

	public String getText() {
		return text;
	}

	public boolean isLatex() {
		return latex;
	}

	/**
	 * Two OptionItems are equal iff they indexes are the same.
	 * @param item to compare.
	 * @return if equal.
	 */
	public boolean isEqual(OptionItem item) {
		if (item == null) {
			return false;
		}
		return index == item.index;
	}

	public boolean isHit(int x, int y) {
		return rect != null && rect.contains(x, y - OptionTableDimension.VERTICAL_PADDING);
	}

	public void setRect(GRectangle rect) {
		this.rect = rect;
	}

	public int getLeft() {
		return (int) rect.getBounds().getX();
	}

	public int getTop() {
		return (int) rect.getBounds().getY() + OptionTableDimension.VERTICAL_PADDING;
	}

	public int getBottom() {
		return getTop() + (int) rect.getBounds().getHeight();
	}

	public boolean hasNoBounds() {
		return rect == null;
	}

	public double getBoundsWidth() {
		return rect.getWidth();
	}

	public double getBoundsHeight() {
		return rect.getHeight();
	}

	public boolean intersects(GRectangle rectangle) {
		return rect.intersects(rectangle);
	}
}
