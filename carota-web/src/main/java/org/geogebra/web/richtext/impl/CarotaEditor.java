package org.geogebra.web.richtext.impl;

import org.geogebra.web.richtext.Editor;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Inline text editor based on Carota.
 */
public class CarotaEditor implements Editor {

	private Widget widget;
	private CarotaDocument editor;

	private static native CarotaDocument createEditorNative(Element div) /*-{
		return $wnd.carota.editor.create(div);
	}-*/;

	private native void setContentNative(CarotaDocument editor, String content) /*-{
		editor.load(JSON.parse(content), false);
	}-*/;

	private native void addListenerNative(Widget widget, CarotaDocument editor,
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
	public CarotaEditor(int padding, double defaultFontSize) {
		CarotaUtil.ensureInitialized(defaultFontSize);
		widget = createWidget(padding);
		editor = createEditorNative(widget.getElement());
	}

	private Widget createWidget(int padding) {
		HTML html = new HTML();
		html.setStyleName("mowWidget");
		String origin = "-" + padding + "px ";
		html.getElement().getStyle().setProperty("transformOrigin", origin + origin);
		html.getElement().getStyle().setProperty("boxSizing", "border-box");
		html.getElement().getStyle().setMargin(8, Style.Unit.PX);
		return html;
	}

	@Override
	public void focus(final int x, final int y) {
		int ordinal = editor.byCoordinate(x, y).getOrdinal();
		editor.select(ordinal, ordinal, true);
	}

	@Override
	public void draw(Context2d canvasElement) {
		editor.draw(canvasElement);
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
		int end = editor.documentRange().getEnd();
		editor.select(end, end);
	}

	@Override
	public void format(String key, Object val) {
		formatNative(editor, key, val);
	}

	private static native void formatNative(CarotaDocument editor, String key, Object val) /*-{
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
	public void insertHyperlink(String url, String text) {
		int newCaretPosition = (text.length() == 0 ? url.length() : text.length())
				+ editor.selectedRange().getStart();

		editor.insertHyperlink(url, text);
		editor.select(newCaretPosition, newCaretPosition, true);
	}

	@Override
	public String getContent() {
		return getContentNative(editor);
	}

	@Override
	public String getSelectedText() {
		return editor.selectedRange().plainText();
	}

	private native String getContentNative(CarotaDocument editor) /*-{
		return JSON.stringify(editor.save());
	}-*/;

	private native <T> T getFormatNative(CarotaDocument editorAPI, String key,
			T fallback) /*-{
		var format = editorAPI.selectedRange().getFormatting()[key];
		if (typeof format == 'object') {
			return fallback;
		}
		return format || fallback;
	}-*/;

	private native <T> T getDocumentFormatNative(CarotaDocument editorAPI, String key,
			T fallback) /*-{
		var format = editorAPI.documentRange().getFormatting()[key];
		if (typeof format == 'object') {
			return fallback;
		}
		return format || fallback;
	}-*/;
}
