package org.geogebra.web.html5.main;

import java.util.LinkedList;
import java.util.Queue;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.gwtproject.timer.client.Timer;

class AltTextTimer extends Timer {
	public static final int DELAY_MILLIS = 700;
	private final ScreenReaderAdapter screenReader;
	private Queue<String> lines = new LinkedList<>();

	public AltTextTimer(ScreenReaderAdapter screenReader) {
		this.screenReader = screenReader;
	}

	@Override
	public void run() {
		for (String text: lines) {
			screenReader.readText(text);
			lines.remove();
		}
	}

	public void feed(String auralText) {
		lines.add(auralText);
		schedule(DELAY_MILLIS);
	}
}
