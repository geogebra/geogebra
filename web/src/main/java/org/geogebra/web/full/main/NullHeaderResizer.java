package org.geogebra.web.full.main;

public class NullHeaderResizer implements HeaderResizer {
	@Override
	public void resizeHeader() {
		// nothing to do.
	}

	@Override
	public int getSmallScreenHeight() {
		return 0;
	}
}
