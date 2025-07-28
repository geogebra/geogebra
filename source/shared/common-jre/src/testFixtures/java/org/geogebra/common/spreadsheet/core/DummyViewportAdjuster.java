package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.shape.Size;

public class DummyViewportAdjuster implements ViewportAdjusterDelegate {

	@Override
	public void setScrollPosition(double x, double y) {
		// no UI to update
	}

	@Override
	public double getScrollBarWidth() {
		return 5;
	}

	@Override
	public void updateScrollableContentSize(Size size) {
		// no UI to update
	}
}
