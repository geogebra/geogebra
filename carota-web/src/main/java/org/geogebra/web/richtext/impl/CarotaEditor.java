package org.geogebra.web.richtext.impl;

import org.geogebra.web.richtext.Editor;
import org.geogebra.web.richtext.EditorChangeListener;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import elemental2.core.Global;
import elemental2.dom.CanvasRenderingContext2D;

/**
 * Inline text editor based on Carota.
 */
public class CarotaEditor implements Editor {

	private static final String INVISIBLE = "invisible";

	private Widget widget;
	private CarotaDocument editor;
	private EditorChangeListener listener;

	/**
	 * Create a new instance of Carota editor.
	 */
	public CarotaEditor(int padding) {
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
	public void draw(CanvasRenderingContext2D canvasElement) {
		editor.draw(canvasElement);
	}

	@Override
	public void setWidth(int width) {
		editor.setWidth(width);
		listener.onInput();
	}

	@Override
	public String getHyperlinkRangeText() {
		return editor.hyperlinkRange().plainText();
	}

	@Override
	public String getSelectionRangeText() {
		return editor.selectedRange().plainText();
	}

	@Override
	public void setSelection(String text) {
		editor.insert(text);
	}

	@Override
	public String urlByCoordinate(int x, int y) {
		return editor.urlByCoordinate(x, y);
	}

	@Override
	public void setHyperlinkUrl(String url) {
		editor.setHyperlinkUrl(url);
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
		editor.load(Global.JSON.parse(content), false);
	}

	@Override
	public void setListener(final EditorChangeListener listener) {
		this.listener = listener;
		new EventThrottle(editor).setListener(listener);
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
		return getRange().getFormattingValue(key, fallback);
	}

	@Override
	public void insertHyperlink(String url, String text) {
		editor.insertHyperlink(url, text);
	}

	@Override
	public String getContent() {
		return Global.JSON.stringify(editor.save());
	}

	@Override
	public void switchListTo(String listType) {
		editor.switchListTo(getRange(), listType);
	}

	@Override
	public String getListStyle() {
		return getRange().getListStyle();
	}

	@Override
	public int getMinHeight() {
		return editor.getFrame().getHeight();
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
		return editor.selectedRange().getFormattingValue("url", "");
	}

	private boolean isEditing() {
		return !getWidget().getElement().hasClassName(INVISIBLE);
	}
}
