package org.geogebra.web.richtext.impl;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaTable {
	public native void draw(Context2d ctx);

	public native void init(int rows, int cols);

	public native JavaScriptObject save();

	public native void load(Object data);

	public native void contentChanged(EditorCallback editorCallback);

	public native void selectionChanged(EditorCallback editorCallback);

	@JsOverlay
	public final void startEditing(int x, int y) {
		startEditing(getHitCell(x, y), x, y);
	}

	private native JavaScriptObject getHitCell(int x, int y);

	private native void startEditing(JavaScriptObject selection, int x, int y);

	public native void stopEditing();

	public native void removeSelection();

	public native void setFormatting(String key, Object val);

	public native <T> T getFormatting(String key, T fallback);

	public native void setBgcolor(String bgcolor);

	public native void setWidth(double width);

	public native void setHeight(double height);

	public native CarotaRange hyperlinkRange();

	public native void insertHyperlink(String url, String text);

	public native void setHyperlinkUrl(String url);

	public native String getListStyle();

	public native void switchListTo(String listType);

	public native String urlByCoordinate(int x, int y);
}
