package org.geogebra.web.html5.main;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.gwtproject.timer.client.Timer;

class AltTextTimer extends Timer {
	public static final int DELAY_MILLIS = 700;
	private final ScreenReaderAdapter screenReader;
	private String text;

	public AltTextTimer(ScreenReaderAdapter screenReader) {
		this.screenReader = screenReader;
	}

	@Override
	public void run() {
		screenReader.readText(text);
	}

	public void read(ScreenReaderBuilder sb) {
		text = sb.toString();
		schedule(DELAY_MILLIS);
	}
}
