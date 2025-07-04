package org.geogebra.common.euclidian.draw.dropdown;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;

class DropDownModel {
	private final OptionItemList items;
	private int colCount = 1;
	private int rowCount = 1;
	private int left;
	private int top;
	private int viewHeight = 0;
	private int viewWidth = 0;
	private final GFont baseFont;
	private final GeoList geoList;
	private final VisibleItemRange range;
	GFont font;
	private int fontSize;
	private static final int MIN_FONT_SIZE = 12;

	DropDownModel(GFont baseFont, GeoList geoList) {
		this.baseFont = baseFont;
		this.geoList = geoList;
		this.range = new VisibleItemRange(geoList);
		items = new OptionItemList(geoList);
	}

	int getRowCount() {
		return rowCount;
	}

	void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	int getColCount() {
		return colCount;
	}

	void setColCount(int colCount) {
		this.colCount = colCount;
	}

	void balanceTable(int itemCount) {
		int resultRow = 0;

		int maxMod = 0;
		boolean found = false;
		int row = rowCount;
		while (!found && row > 2) {
			row--;
			int mod = itemCount % row;
			if (mod == 0) {
				resultRow = row;
				maxMod = 0;
				found = true;
			} else if (mod > maxMod) {
				maxMod = mod;
				resultRow = row;
			}
		}

		rowCount = resultRow == 0 ? 1 : resultRow;
		colCount = itemCount / rowCount + (maxMod == 0 ? 0 : 1);
	}

	int getLeft() {
		return left;
	}

	void setLeft(int left) {
		this.left = left;
	}

	int getTop() {
		return top;
	}

	void setTop(int top) {
		this.top = top;
	}

	void applyFontTo(GGraphics2D g2) {
		if (g2.getFont().getSize() != fontSize) {
			g2.setFont(font.deriveFont(GFont.PLAIN, fontSize));
		}
	}

	GFont getFont() {
		return font;
	}

	boolean isFontSizeNotMinimal() {
		return fontSize > MIN_FONT_SIZE;
	}

	void decreaseFontSize() {
		fontSize--;
	}

	boolean resizeView(int w, int h) {
		int dW = viewWidth - w;
		int dH = viewHeight - h;

		if (dW != 0 || dH != 0) {
			viewHeight = h;
			viewWidth = w;
			return true;
		}
		return false;
	}

	void setFontSize(int size) {
		fontSize = size;
	}

	int viewHeight() {
		return viewHeight;
	}

	int viewWidth() {
		return viewWidth;
	}

	OptionItemList items() {
		return items;
	}

	void updateFont() {
		font = baseFont.deriveFont(GFont.PLAIN, fontSize);
	}

	GeoList getGeoList() {
		return geoList;
	}

	VisibleItemRange getRange() {
		return range;
	}

	int getStartIdx() {
		return getColCount() == 1 ? range.getStart() : 0;
	}

	int getEndIdx() {
		return getColCount() == 1 ? range.getEnd() : geoList.size();
	}

	boolean isScrollBoundsValid() {
		return getStartIdx() > 0 && getEndIdx() < geoList.size();
	}

	void setPosition(int left, int top) {
		this.left = left;
		this.top = top;
	}

	GColor getBackgroundColor() {
		return geoList.getBackgroundColor();
	}

	App getApp() {
		return geoList.getApp();
	}

	GPaint getItemColor() {
		return geoList.getObjectColor();
	}

	int itemCount() {
		return items.size();
	}
}
