package org.geogebra.common.euclidian;

public class ScreenReaderSilent implements ScreenReaderAdapter {

	static ScreenReaderSilent INSTANCE = new ScreenReaderSilent();

	@Override
	public void readText(String textString) {
		// silent
	}

}
