package org.geogebra.web.full.gui.layout;

import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;

public class BaseHeaderResizer implements HeaderResizer {

	private final GeoGebraFrameW frame;

	public BaseHeaderResizer(AppW app) {
		frame = app.getAppletFrame();
	}

	@Override
	public void resizeHeader() {
		Element header = Dom.querySelector("GeoGebraHeader");
		if (header != null) {
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
}
