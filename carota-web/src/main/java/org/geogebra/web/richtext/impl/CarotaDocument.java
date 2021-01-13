package org.geogebra.web.richtext.impl;

import com.google.gwt.core.client.JavaScriptObject;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaDocument implements HasContent {

	public native void draw(CanvasRenderingContext2D canvasElement);

	public native void select(int start, int end);

	public native void select(int start, int end, boolean takeFocus);

	public native void insert(String text);

	public native CarotaNode byCoordinate(int x, int y);

	public native CarotaRange selectedRange();

	public native CarotaRange documentRange();

	public native void insertHyperlink(String url, String text);

	public native void setWidth(int width);

	@JsProperty
	public native CarotaFrame getFrame();

	public native String urlByCoordinate(int x, int y);

	public native CarotaRange hyperlinkRange();

	public native void setHyperlinkUrl(String url);

	public native void switchListTo(CarotaRange range, String listType);

	public native void load(Object content, boolean focus);

	@Override
	public native void selectionChanged(EditorCallback editorCallback);

	@Override
	public native void contentChanged(EditorCallback editorCallback);

	@Override
	public native JavaScriptObject save();

}
