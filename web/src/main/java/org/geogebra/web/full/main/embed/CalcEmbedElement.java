package org.geogebra.web.full.main.embed;

import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.html5.util.JSON;

import com.google.gwt.dom.client.Style.Unit;

public class CalcEmbedElement extends EmbedElement {

	private GeoGebraFrameBoth frame;

	/**
	 * @param widget
	 *            calculator frame
	 */
	public CalcEmbedElement(GeoGebraFrameBoth widget) {
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

}
