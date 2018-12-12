package org.geogebra.common.jre.headless;

import org.geogebra.common.euclidian.ScreenReaderAdapter;

public class ScreenReaderAccumulator implements ScreenReaderAdapter {

	private StringBuilder reader = new StringBuilder();

	public void readText(String text) {
		this.reader.append(text);
	}

	public boolean hasRead(String string) {
		return reader.toString().contains(string);
	}

}
