package org.geogebra.web.richtext.impl;

import org.geogebra.web.richtext.Editor;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
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
		return $wnd.murok = $wnd.carota.editor.create(div);
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
                listener.@org.geogebra.web.richtext.Editor.EditorChangeListener::onSizeChanged(*)();
            }, 0);

			if (updateTimer !== null) {
				clearTimeout(updateTimer);
			}
			updateTimer = setTimeout(function() {
				updateTimer = null;
				listener.@org.geogebra.web.richtext.Editor.EditorChangeListener::onContentChanged(*)(JSON.stringify(editor.save()));
			}, 500);
		})
	}-*/;

	/**
	 * Create a new instance of Carota editor.
	 */
	public CarotaEditor(double defaultFontSize) {
		CarotaUtil.ensureInitialized(defaultFontSize);
		widget = createWidget();
	}

	private Widget createWidget() {
		HTML html = new HTML("<div></div>");
		html.addStyleName("mowWidget");
		html.addStyleName("background");
		editor = createEditorNative(html.getElement());
		return html;
	}

	@Override
	public void focus(final int x, final int y) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				focusNative(editor, x, y);
			}
		});
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

	public void format(String key, Object val) {
		formatNative(editor, key, val);
	}

	private static native void formatNative(JavaScriptObject editor, String key, Object val) /*-{
		editor.documentRange().setFormatting(key, val);
	}-*/;

}
