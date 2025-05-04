package org.geogebra.web.richtext.impl;

import org.geogebra.gwtutil.JsRunnable;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaTable implements HasContent {

	@JsProperty
	public native int getTotalWidth();

	@JsProperty
	public native int getTotalHeight();

	/**
	 * Serialize and deserialize the content
	 */
	@JsOverlay
	public final void reload() {
		if (Carota.get() != null) {
			Carota.get().getText().getCache().clear();
		}
		load(save());
	}

	@Override
	public native void draw(CanvasRenderingContext2D ctx);

	public native void repaint();

	public native void init(int rows, int cols);

	@Override
	public native Object save();

	@Override
	public native void load(Object data);

	@Override
	public native void contentChanged(JsRunnable editorCallback);

	public native void sizeChanged(JsRunnable editorCallback);

	@Override
	public native void selectionChanged(JsRunnable editorCallback);

	@Override
	public native void onEscape(JsRunnable editorCallback);

	/**
	 * Start editing.
	 * @param x cursor x-coordinate
	 * @param y cursor y-coordinate
	 */
	@JsOverlay
	public final void startEditing(int x, int y) {
		startEditing(getHitCell(x, y), x, y);
	}

	@JsProperty
	public native CarotaSelection getSelection();

	@JsOverlay
	private int selectionX() {
		return getSelection() == null ? 0 : getSelection().col0;
	}

	@JsOverlay
	private int selectionY() {
		return getSelection() == null ? 0 : getSelection().row0;
	}

	@JsProperty
	public native int getRows();

	@JsProperty
	public native int getCols();

	private native CarotaSelection getHitCell(int x, int y);

	private native void startEditing(CarotaSelection selection, int x, int y);

	public native void stopEditing();

	public native void removeSelection();

	public native void setFormatting(String key, Object val);

	public native <T> T getFormatting(String key, T fallback);

	/**
	 * @param property property name
	 * @param value new value
	 */
	public native void setCellProperty(String property, String value);

	/**
	 * Apply style change to a selected range.
	 * @param property property name
	 * @param value new value
	 * @param range selected range
	 */
	public native void setCellProperty(String property, String value, CarotaSelection range);

	public native String getCellProperty(String property);

	public native void setBorderThickness(int borderThickness);

	public native void setBorderThickness(int borderThickness, CarotaSelection range);

	public native int getBorderThickness();

	public native void setBorderStyle(String borderType);

	public native String getBorderStyle();

	public native void setWidth(double width);

	public native void setHeight(double height);

	public native int getMinWidth();

	public native int getMinHeight();

	public native void insert(String text);

	public native CarotaRange selectedRange();

	public native CarotaRange hyperlinkRange();

	public native void insertHyperlink(String url, String text);

	public native void setHyperlinkUrl(String url);

	public native String getListStyle();

	public native void switchListTo(String listType);

	public native String urlByCoordinate(int x, int y);

	private native void addRow(int i, int source);

	private native void addColumn(int j, int source);

	private native void removeRow(int i);

	private native void removeColumn(int j);

	/**
	 * Insert row above selection.
	 */
	@JsOverlay
	public final void insertRowAbove() {
		addRow(selectionY(), selectionY());
	}

	/**
	 * Insert row below selection.
	 */
	@JsOverlay
	public final void insertRowBelow() {
		addRow(selectionY() + 1, selectionY());
	}

	/**
	 * Insert column left of selection.
	 */
	@JsOverlay
	public final void insertColumnLeft() {
		addColumn(selectionX(), selectionX());
	}

	/**
	 * Insert column right of selection.
	 */
	@JsOverlay
	public final void insertColumnRight() {
		addColumn(selectionX() + 1, selectionX());
	}

	/**
	 * Remove the row that contains the selected cell.
	 */
	@JsOverlay
	public final void removeRow() {
		removeRow(selectionY());
	}

	/**
	 * Remove the column that contains the selected cell.
	 */
	@JsOverlay
	public final void removeColumn() {
		removeColumn(selectionX());
	}

	public native void setExternalScale(double sx);

	public native void setExternalPaint(boolean externalPaint);

	public native void setHitCell(double x, double y);

	public native void addInsertFilter(Carota.InsertFilter filter);
}
