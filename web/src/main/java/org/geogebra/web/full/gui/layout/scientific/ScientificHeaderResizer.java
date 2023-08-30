package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Display;
import org.gwtproject.user.client.DOM;

public class ScientificHeaderResizer implements HeaderResizer {

	private final GeoGebraFrameW frame;

	public ScientificHeaderResizer(GeoGebraFrameW frame) {
		this.frame = frame;
	}

	@Override
	public void resizeHeader() {
		Element header = Dom.querySelector(".GeoGebraHeader");
		if (header != null) {
			resetHeaderStyle(header);

			if (frame.hasSmallWindowOrCompactHeader()) {
				header.addClassName("smallScreen");
			} else {
				header.removeClassName("smallScreen");
			}

			Dom.toggleClass(header, "portrait", "landscape",
					frame.getApp().isPortrait());
		}
	}

	@Override
	public int getSmallScreenHeight() {
		return frame.shouldHideHeader() ? 0 : 80;
	}

	@Override
	public void resetHeaderStyle(Element header) {
		header.removeClassName("compact");
		header.addClassName("scientificHeader");
		frame.getApp().getAppletParameters().setAttribute("marginTop", "112");

		Element el = DOM.getElementById("undoRedoSettingsPanel");
		if (el != null) {
			el.getStyle().setDisplay(Display.BLOCK);
		}
	}
}
