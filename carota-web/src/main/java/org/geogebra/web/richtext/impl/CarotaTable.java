package org.geogebra.web.richtext.impl;

import com.google.gwt.core.client.JavaScriptObject;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true)
public class CarotaTable implements HasContent {

	@JsProperty
	public native int getTotalWidth();

	@JsProperty
	public native int getTotalHeight();

	@JsOverlay
	public final void reload() {
		if (Carota.get() != null) {
			Carota.get().getText().getCache().clear();
		}
		load(save());
	}

	public native void draw(CanvasRenderingContext2D ctx);

	public native void repaint();

	public native void init(int rows, int cols);

	@Override
	public native JavaScriptObject save();

	public native void load(Object data);

	@Override
	public native void contentChanged(EditorCallback editorCallback);

	public native void sizeChanged(EditorCallback editorCallback);

	@Override
	public native void selectionChanged(EditorCallback editorCallback);

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

	public native void setCellProperty(String property, String value);

	public native void setCellProperty(String property, String value, JsPropertyMap<Object> range);

	public native String getCellProperty(String property);

	public native void setBorderThickness(int borderThickness);

	public native void setBorderThickness(int borderThickness, JsPropertyMap<Object> range);

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

	@JsOverlay
	public final void insertRowAbove() {
		addRow(selectionY(), selectionY());
	}

	@JsOverlay
	public final void insertRowBelow() {
		addRow(selectionY() + 1, selectionY());
	}

	@JsOverlay
	public final void insertColumnLeft() {
		addColumn(selectionX(), selectionX());
	}

	@JsOverlay
	public final void insertColumnRight() {
		addColumn(selectionX() + 1, selectionX());
	}

	@JsOverlay
	public final void removeRow() {
		removeRow(selectionY());
	}

	@JsOverlay
	public final void removeColumn() {
		removeColumn(selectionX());
	}
}
