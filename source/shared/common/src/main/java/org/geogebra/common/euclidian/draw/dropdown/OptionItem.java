/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.draw.dropdown;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.draw.CanvasDrawable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

class OptionItem {
	private final GeoList list;
	private final CanvasDrawable drawable;
	private GFont font;
	private final int index;
	private GDimension dimension;
	private String text;
	private boolean latex;
	private GRectangle rect;

	OptionItem(GeoList list, CanvasDrawable drawable, int idx) {
		this.list = list;
		this.drawable = drawable;
		index = idx;
		formatText(list, idx);
		rect = null;
	}

	void update(GFont font) {
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
		GTextLayout layout = drawable.getLayout(text, font);
		setDimension(Math.round(layout.getBounds().getWidth()),
				 Math.round(layout.getBounds().getHeight()));
	}

	private void calculateDimensionsForLatex() {
		dimension = CanvasDrawable.measureLatex(list.getApp(),
				font, text, CanvasDrawable.shouldBeSerif(text, list.get(index),
						false));
	}

	int getWidth() {
		return dimension.getWidth();
	}

	int getHeight() {
		return dimension.getHeight();
	}

	GRectangle getRect() {
		return rect;
	}

	int getIndex() {
		return index;
	}

	String getText() {
		return text;
	}

	boolean isLatex() {
		return latex;
	}

	/**
	 * Two OptionItems are equal iff they indexes are the same.
	 * @param item to compare.
	 * @return if equal.
	 */
	boolean isEqual(OptionItem item) {
		if (item == null) {
			return false;
		}
		return index == item.index;
	}

	boolean isHit(int x, int y) {
		return rect != null && rect.contains(x, y - OptionTableDimension.VERTICAL_PADDING);
	}

	void setRect(GRectangle rect) {
		this.rect = rect;
	}

	int getLeft() {
		return (int) rect.getBounds().getX();
	}

	int getTop() {
		return (int) rect.getBounds().getY() + OptionTableDimension.VERTICAL_PADDING;
	}

	int getBottom() {
		return getTop() + (int) rect.getBounds().getHeight();
	}

	boolean hasNoBounds() {
		return rect == null;
	}

	double getBoundsWidth() {
		return rect.getWidth();
	}

	double getBoundsHeight() {
		return rect.getHeight();
	}

	boolean intersects(GRectangle rectangle) {
		return rect.intersects(rectangle);
	}
}
