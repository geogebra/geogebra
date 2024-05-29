package org.geogebra.web.full.gui.layout;

import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.shared.GlobalHeader;
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
			reset(header);

			boolean smallScreen = frame.shouldHaveSmallScreenLayout();
			if (smallScreen) {
				header.addClassName("compact");
			} else {
				header.removeClassName("compact");
			}
			GlobalHeader.INSTANCE.updateHeaderButtonVisibility(smallScreen);
			frame.updateArticleHeight();
		}
	}

	@Override
	public int getSmallScreenHeight() {
		return 48;
	}

	@Override
	public void reset(Element header) {
		frame.getApp().getAppletParameters().setAttribute("marginTop",
				String.valueOf(getHeaderHeight()));
		header.removeClassName("scientificHeader");

		Element el = DOM.getElementById("undoRedoSettingsPanel");
		if (el != null) {
			el.getStyle().setDisplay(Display.NONE);
		}
	}

	@Override
	public int getHeaderHeight() {
		return 64;
	}
}
