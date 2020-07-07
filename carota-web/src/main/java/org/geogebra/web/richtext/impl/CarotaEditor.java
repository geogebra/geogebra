package org.geogebra.web.richtext.impl;

import org.geogebra.web.richtext.Editor;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Inline text editor based on Carota.
 */
public class CarotaEditor implements Editor {

	private static final String INVISIBLE = "invisible";
	private static final String HYPERLINK_COLOR = "#1565C0";

	private Widget widget;
	private CarotaDocument editor;
	private EditorChangeListener listener;

	/**
	 * Create a new instance of Carota editor.
	 */
	public CarotaEditor(int padding, double defaultFontSize) {
		CarotaUtil.ensureInitialized(defaultFontSize);
		widget = createWidget(padding);
		editor = Carota.get().getEditor().create(widget.getElement());
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
		CarotaFormatting format = getHyperlinkRange().getFormatting();
		String color = getFormatNative(format, "color", HYPERLINK_COLOR);
		if (url == null) {
			if (HYPERLINK_COLOR.equals(color)) {
				color = null;
			}
			updateLinkStyle(color, false);
		} else {
			boolean isUnderline = getFormatNative(format,
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
	public void reload() {
		if (Carota.get() != null) {
			Carota.get().getText().getCache().clear();
		}
		editor.load(editor.save(), false);
	}

	@Override
	public Widget getWidget() {
		return widget;
	}

	@Override
	public void setContent(String content) {
		editor.load(parse(content), false);
	}

	@Override
	public void setListener(final EditorChangeListener listener) {
		this.listener = listener;
		final Timer updateTimer = new Timer() {
			@Override
			public void run() {
				listener.onContentChanged(getContent());
			}
		};

		editor.contentChanged(() -> {
			listener.onSizeChanged(editor.getFrame().getHeight());
			updateTimer.cancel();
			updateTimer.schedule(500);
		});

		editor.selectionChanged(listener::onSelectionChanged);
	}

	@Override
	public void deselect() {
		int end = editor.documentRange().getEnd() - 1;
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
		return getFormatNative(getRange().getFormatting(), key, fallback);
	}

	protected native <T> T getFormatNative(CarotaFormatting formatting, String key, T fallback) /*-{
		var format = formatting[key];
		if (typeof format == 'object') {
			return fallback;
		}
		return format;
	}-*/;

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
		return stringify(editor.save());
	}

	private native JavaScriptObject parse(String content) /*-{
		return JSON.parse(content);
	}-*/;

	private native String stringify(JavaScriptObject json) /*-{
		return JSON.stringify(json);
	}-*/;

	@Override
	public void switchListTo(String listType) {
		editor.switchListTo(getRange(), listType);
	}

	@Override
	public String getListStyle() {
		return getRange().getListStyle();
	}

	private CarotaRange getRange() {
		if (isEditing()) {
			return editor.selectedRange();
		} else {
			return editor.documentRange();
		}
	}

	@Override
	public String getHyperLinkURL() {
		return getFormatNative(editor.selectedRange().getFormatting(), "url", "");
	}

	private boolean isEditing() {
		return !getWidget().getElement().hasClassName(INVISIBLE);
	}
}
