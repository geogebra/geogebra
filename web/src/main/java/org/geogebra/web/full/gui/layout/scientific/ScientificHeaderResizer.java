package org.geogebra.web.full.gui.layout.scientific;

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
			reset(header);

			if (frame.hasSmallWindowOrCompactHeader()) {
				header.addClassName("compact");
			} else {
				header.removeClassName("compact");
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
	public void reset(Element header) {
		header.removeClassName("compact");
		header.addClassName("scientificHeader");
		frame.getApp().getAppletParameters().setAttribute("marginTop",
				String.valueOf(getHeaderHeight()));

		Element el = DOM.getElementById("undoRedoSettingsPanel");
		if (el != null) {
			el.getStyle().setDisplay(Display.BLOCK);
		}
	}

	@Override
	public int getHeaderHeight() {
		return 112;
	}
}
