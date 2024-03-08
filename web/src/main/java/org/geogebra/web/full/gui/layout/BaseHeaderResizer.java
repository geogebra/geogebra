package org.geogebra.web.full.gui.layout;

import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.dom.client.Element;

public class BaseHeaderResizer implements HeaderResizer {

	private final GeoGebraFrameW frame;

	public BaseHeaderResizer(GeoGebraFrameW frame) {
		this.frame = frame;
	}

	@Override
	public void resizeHeader() {
		Element header = Dom.querySelector(".GeoGebraHeader");
		if (header != null) {
			boolean smallScreen = frame.shouldHaveSmallScreenLayout();
			if (smallScreen) {
				header.addClassName("compact");
			} else {
				header.removeClassName("compact");
			}
			updateHeaderButtonVisibility(smallScreen);
			frame.updateArticleHeight();
		}
	}

	private void updateHeaderButtonVisibility(boolean smallScreen) {
		updateButtonVisibility(smallScreen, "#shareButton");
		updateButtonVisibility(smallScreen, "#signInTextID");
		updateButtonVisibility(!smallScreen, "#signInIconID");
	}

	private void updateButtonVisibility(boolean smallScreen, String buttonID) {
		Element button = Dom.querySelector(buttonID);
		if (button != null) {
			if (smallScreen) {
				button.addClassName("hideButton");
			} else {
				button.removeClassName("hideButton");
			}
		}
	}

	@Override
	public int getSmallScreenHeight() {
		return 48;
	}
}
