package org.geogebra.web.full.gui.layout;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Display;
import org.gwtproject.user.client.DOM;

public class BaseHeaderResizer implements HeaderResizer {

	private final GeoGebraFrameW frame;

	public BaseHeaderResizer(GeoGebraFrameW frame) {
		this.frame = frame;
	}

	@Override
	public void resizeHeader() {
		Element header = Dom.querySelector(".GeoGebraHeader");
		if (header != null) {
			resetHeaderStyle(header);

			boolean smallScreen = frame.shouldHaveSmallScreenLayout();
			if (smallScreen) {
				header.addClassName("compact");
			} else {
				header.removeClassName("compact");
			}
			frame.updateArticleHeight();
		}
	}

	@Override
	public int getSmallScreenHeight() {
		return 48;
	}

	@Override
	public void resetHeaderStyle(Element header) {
		frame.getApp().getAppletParameters().setAttribute("marginTop", "64");
		header.removeClassName("scientificHeader");

		Element el = DOM.getElementById("undoRedoSettingsPanel");
		if (el != null) {
			el.getStyle().setDisplay(Display.NONE);
		}
	}
}
