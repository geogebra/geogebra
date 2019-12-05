package org.geogebra.web.richtext.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.geogebra.web.richtext.Editor;

/**
 * Inline text editor based on Carota.
 */
public class CarotaEditor implements Editor {

	private Widget widget;
	private JavaScriptObject editor;

	private static native JavaScriptObject createEditorNative(Element div) /*-{
		return $wnd.carota.editor.create(div);
	}-*/;

	private static native void focusNative(JavaScriptObject editor) /*-{
		editor.notifySelectionChanged(true);
	}-*/;

	/**
	 * Create a new instance of Carota editor.
	 */
	public CarotaEditor() {
		CarotaUtil.ensureJavascriptInjected();
		widget = createWidget();
	}

	private Widget createWidget() {
		HTML html = new HTML("<div></div>");
		editor = createEditorNative(html.getElement());
		return html;
	}

	@Override
	public void focus() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				focusNative(editor);
			}
		});
	}

	@Override
	public Widget getWidget() {
		return widget;
	}
}
