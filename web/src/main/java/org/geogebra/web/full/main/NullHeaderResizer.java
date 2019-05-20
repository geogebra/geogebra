package org.geogebra.web.full.main;

public class NullHeaderResizer implements HeaderResizer {
	private static NullHeaderResizer INSTANCE = null;
	public static NullHeaderResizer get() {
		if (INSTANCE == null) {
			INSTANCE = new NullHeaderResizer();
		}
		return INSTANCE;
	}
	@Override
	public void resizeHeader() {
		// nothing to do.
	}

	@Override
	public int getSmallScreenHeight() {
		return 0;
	}
}
