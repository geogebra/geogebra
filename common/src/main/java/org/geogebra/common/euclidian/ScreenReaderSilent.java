package org.geogebra.common.euclidian;

public class ScreenReaderSilent implements ScreenReaderAdapter {

	/**
	 * Singleton instance
	 */
	static ScreenReaderSilent INSTANCE = new ScreenReaderSilent();

	@Override
	public void readText(String textString) {
		// silent
	}

	@Override
	public void readTextImmediate(String textString) {
		// still silent
	}

}
