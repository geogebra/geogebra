package org.geogebra.desktop.euclidian;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.util.debug.Log;

public class ScreenReaderAdapterD implements ScreenReaderAdapter {

	@Override
	public void readText(String text) {
		Log.read("Reading text: " + text);
	}

	@Override
	public void readDelayed(String text) {
		readText(text);
	}
}
