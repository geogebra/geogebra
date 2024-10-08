package org.geogebra.web.full.main;

import org.gwtproject.dom.client.Element;

/**
 * Null Object implementation of header resizer.
 */
public final class NullHeaderResizer implements HeaderResizer {
	private static NullHeaderResizer INSTANCE = null;

	/**
	 *
	 * @return the NullHeaderResizer as a singleton.
	 */
	public static NullHeaderResizer get() {
		if (INSTANCE == null) {
			INSTANCE = new NullHeaderResizer();
		}

		return INSTANCE;
	}

	private NullHeaderResizer() {
		// singleton constructor
	}

	@Override
	public void resizeHeader() {
		// nothing to do.
	}

	@Override
	public int getSmallScreenHeight() {
		return 0;
	}

	@Override
	public void reset(Element header) {
		// nothing to do
	}

	@Override
	public int getHeaderHeight() {
		return 0;
	}
}
