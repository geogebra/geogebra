package org.geogebra.common.jre.headless;

import org.geogebra.common.euclidian.ScreenReaderAdapter;

public class ScreenReaderAccumulator implements ScreenReaderAdapter {

	private StringBuilder reader = new StringBuilder();

	@Override
	public void readText(String text) {
		reader.append(text);
	}

	@Override
	public void readDelayed(String text) {
		readText(text);
	}

	public boolean hasRead(String string) {
		return reader.toString().contains(string);
	}
}
