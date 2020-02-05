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

	private static final String HYPERLINK_COLOR = "#1565C0";
	private Widget widget;
	private CarotaDocument editor;
	private EditorChangeListener listener;

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
			listener.@org.geogebra.web.richtext.Editor.EditorChangeListener::onSizeChanged(*)(editor.frame.height);

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
	public void setWidth(int width) {
		editor.setWidth(width);
		listener.onSizeChanged(editor.getFrame().getHeight());
	}

	@Override
	public String getHyperlinkRangeText() {
		return getHyperlinkRange().plainText();
	}

	@Override
	public String urlByCoordinate(int x, int y) {
		return editor.urlByCoordinate(x, y);
	}

	private CarotaRange getHyperlinkRange() {
		CarotaRange selection = editor.selectedRange();
		return editor.hyperlinkRange(selection.getStart(), selection.getEnd());
	}

	@Override
	public void setHyperlinkUrl(String url) {
		String color = getFormatNative(getHyperlinkRange(), "color", HYPERLINK_COLOR);
		if (url == null) {
			if (HYPERLINK_COLOR.equals(color)) {
				color = null;
			}
			updateLinkStyle(color, false);
		} else {
			boolean isUnderline = getFormatNative(getHyperlinkRange(),
					"underline", true);
			updateLinkStyle(color, isUnderline);
		}
		getHyperlinkRange().setFormatting("url", url);
	}

	private void updateLinkStyle(String color, boolean isUnderline) {
		getHyperlinkRange().setFormatting("underline", isUnderline);
		getHyperlinkRange().setFormatting("color", color);
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
	public void setListener(EditorChangeListener listener) {
		this.listener = listener;
		addListenerNative(widget, editor, listener);
	}

	@Override
	public void deselect() {
		int end = editor.documentRange().getEnd();
		editor.select(end, end);
	}

	@Override
	public void format(String key, Object val) {
		CarotaRange selection = editor.selectedRange();
		CarotaRange range = selection.getStart() == selection.getEnd() ? editor.documentRange()
				: selection;
		range.setFormatting(key, val);
	}

	@Override
	public <T> T getFormat(String key, T fallback) {
		return getFormatNative(editor.selectedRange(), key, fallback);
	}

	@Override
	public <T> T getDocumentFormat(String key, T fallback) {
		return getFormatNative(editor.documentRange(), key, fallback);
	}

	@Override
	public void insertHyperlink(String url, String text) {
		CarotaRange selectedRange = editor.selectedRange();
		int newCaretPosition = (text.length() == 0 ? url.length() : text.length())
				+ selectedRange.getStart();
		CarotaRange hyperlinkRange = getHyperlinkRange();
		if (hyperlinkRange.getEnd() > hyperlinkRange.getStart()) {
			editor.select(hyperlinkRange.getStart(), hyperlinkRange.getEnd());
		}
		editor.insertHyperlink(url, text);
		hyperlinkRange = getHyperlinkRange();
		hyperlinkRange.setFormatting("color", HYPERLINK_COLOR);
		hyperlinkRange.setFormatting("underline", true);
		editor.select(newCaretPosition, newCaretPosition, true);
	}

	@Override
	public String getContent() {
		return getContentNative(editor);
	}

	private native String getContentNative(CarotaDocument editor) /*-{
		return JSON.stringify(editor.save());
	}-*/;

	private native <T> T getFormatNative(CarotaRange range, String key,
			T fallback) /*-{
		var format = range.getFormatting()[key];
		if (typeof format == 'object') {
			return fallback;
		}
		if (fallback === true) {
			return !!format;
		}
		return format || fallback;
	}-*/;

	@Override
	public void switchListTo(String listType) {
		editor.switchListTo(listType);
	}

	@Override
	public String getListStyle() {
		CarotaRange selection = editor.selectedRange();
		return selection.getListStyle();
	}
}
