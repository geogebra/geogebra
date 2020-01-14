package org.geogebra.web.richtext.impl;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Style;
import org.geogebra.web.richtext.Editor;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Inline text editor based on Carota.
 */
public class CarotaEditor implements Editor {

	private Widget widget;
	private JavaScriptObject editor;

	private static native JavaScriptObject createEditorNative(Element div) /*-{
		return $wnd.carota.editor.create(div);
	}-*/;

	private static native void focusNative(JavaScriptObject editor, int x, int y) /*-{
		var ordinal = editor.byCoordinate(x, y).ordinal;
		editor.select(ordinal, ordinal, true);
	}-*/;

	private native void setContentNative(JavaScriptObject editor, String content) /*-{
		editor.load(JSON.parse(content), false);
	}-*/;

	private native void addListenerNative(Widget widget, JavaScriptObject editor,
			EditorChangeListener listener) /*-{
		var updateTimer = null;

		editor.contentChanged(function() {
			setTimeout(function() {
				listener.@org.geogebra.web.richtext.Editor.EditorChangeListener::onSizeChanged(*)(editor.frame.height);
			}, 0);

			if (updateTimer !== null) {
				clearTimeout(updateTimer);
			}
			updateTimer = setTimeout(function() {
				updateTimer = null;
				listener.@org.geogebra.web.richtext.Editor.EditorChangeListener::onContentChanged(*)(JSON.stringify(editor.save()));
			}, 500);
		});

		editor.selectionChanged(function() {
			listener.@org.geogebra.web.richtext.Editor.EditorChangeListener::onSelectionChanged()()
		});
	}-*/;

	/**
	 * Create a new instance of Carota editor.
	 */
	public CarotaEditor(double defaultFontSize) {
		CarotaUtil.ensureInitialized(defaultFontSize);
		widget = createWidget();
		editor = createEditorNative(widget.getElement());
	}

	private Widget createWidget() {
		HTML html = new HTML();
		html.setStyleName("mowWidget");
		html.getElement().getStyle().setProperty("transformOrigin", "-8px -8px");
		html.getElement().getStyle().setProperty("boxSizing", "border-box");
		html.getElement().getStyle().setMargin(8, Style.Unit.PX);
		return html;
	}

	@Override
	public void focus(final int x, final int y) {
		focusNative(editor, x, y);
	}

	@Override
	public Widget getWidget() {
		return widget;
	}

	@Override
	public void setContent(String content) {
		setContentNative(editor, content);
	}

	@Override
	public void addListener(EditorChangeListener listener) {
		addListenerNative(widget, editor, listener);
	}

	@Override
	public void deselect() {
		deselectNative(editor);
	}

	private static native void deselectNative(JavaScriptObject editor) /*-{
		editor.select(0, 0, false);
	}-*/;

	@Override
	public void format(String key, Object val) {
		formatNative(editor, key, val);
	}

	private static native void formatNative(JavaScriptObject editor, String key, Object val) /*-{
		var selection = editor.selectedRange();
		var range = selection.start === selection.end ? editor.documentRange()
				: selection;
		range.setFormatting(key, val);
	}-*/;

	@Override
	public <T> T getFormat(String key, T fallback) {
		return getFormatNative(editor, key, fallback);
	}

	@Override
	public <T> T getDocumentFormat(String key, T fallback) {
		return getDocumentFormatNative(editor, key, fallback);
	}

	@Override
	public String getContent() {
		return getContentNative(editor);
	}

	@Override
	public CanvasElement getCanvasElement() {
		return widget.getElement().getElementsByTagName("canvas").getItem(0).cast();
	}

	private native String getContentNative(JavaScriptObject editor) /*-{
		return JSON.stringify(editor.save());
	}-*/;

	private native <T> T getFormatNative(JavaScriptObject editorAPI, String key,
			T fallback) /*-{
		var format = editorAPI.selectedRange().getFormatting()[key];
		if (typeof format == 'object') {
			return fallback;
		}
		return format || fallback;
	}-*/;

	private native <T> T getDocumentFormatNative(JavaScriptObject editorAPI, String key,
			T fallback) /*-{
		var format = editorAPI.documentRange().getFormatting()[key];
		if (typeof format == 'object') {
			return fallback;
		}
		return format || fallback;
	}-*/;
}
