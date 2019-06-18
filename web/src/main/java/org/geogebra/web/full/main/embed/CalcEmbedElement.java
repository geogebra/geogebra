package org.geogebra.web.full.main.embed;

import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.html5.main.ScriptManagerW;
import org.geogebra.web.html5.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Embedded GeoGebra calculator for Notes
 */
public class CalcEmbedElement extends EmbedElement {

	private final GeoGebraFrameFull frame;

	/**
	 * @param widget
	 *            calculator frame
	 */
	public CalcEmbedElement(GeoGebraFrameFull widget) {
		super(widget);
		frame = widget;
	}

	@Override
	public void setSize(int contentWidth, int contentHeight) {
		frame.getApplication().getGgbApi().setSize(contentWidth, contentHeight);
		frame.getElement().getStyle().setWidth(contentWidth - 2, Unit.PX);
		frame.getElement().getStyle().setHeight(contentHeight - 2, Unit.PX);
		frame.getApplication().checkScaleContainer();
	}

	@Override
	public String getContentSync() {
		return JSON.stringify(
				frame.getApplication().getGgbApi().getFileJSON(false));
	}

	/**
	 * @return API
	 */
	public JavaScriptObject getApi() {
		ScriptManagerW sm = (ScriptManagerW) frame.getApplication()
				.getScriptManager();
		return sm.getApi();
	}
}
