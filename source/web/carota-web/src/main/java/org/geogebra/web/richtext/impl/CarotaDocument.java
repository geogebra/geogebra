package org.geogebra.web.richtext.impl;

import org.geogebra.gwtutil.JsRunnable;

import elemental2.core.JsArray;
import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaDocument implements HasContent {

	public JsArray<Carota.InsertFilter> insertFilters;

	@Override
	public native void draw(CanvasRenderingContext2D canvasElement);

	public native void select(int start, int end);

	/**
	 * @param start selection offset
	 * @param end selection end
	 * @param takeFocus whether to move focus
	 */
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

	@Override
	public native void load(Object content);

	@Override
	public native void selectionChanged(JsRunnable editorCallback);

	@Override
	public native void contentChanged(JsRunnable editorCallback);

	@Override
	public native void onEscape(JsRunnable editorCallback);

	@Override
	public native Object save();

	@JsProperty
	public native void setExternalScale(double sx);

	@JsProperty
	public native void setExternalPaint(boolean externalPaint);
}
