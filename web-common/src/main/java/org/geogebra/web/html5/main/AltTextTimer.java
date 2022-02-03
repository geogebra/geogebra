package org.geogebra.web.html5.main;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.Localization;
import org.gwtproject.timer.client.Timer;

class AltTextTimer extends Timer {
	public static final int DELAY_MILLIS = 700;
	private final ScreenReaderAdapter screenReader;
	private final Queue<String> lines = new LinkedList<>();
	private final Localization loc;
	private final HashSet<GeoElement> queuedGeos = new HashSet<>();

	public AltTextTimer(ScreenReaderAdapter screenReader, Localization loc) {
		this.screenReader = screenReader;
		this.loc = loc;
	}

	@Override
	public void run() {
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		for (String text: lines) {
			sb.append(text);
			sb.endSentence();
		}
		screenReader.readText(sb.toString());
		lines.clear();
		queuedGeos.clear();
	}

	@Override
	public void cancel() {
		super.cancel();
		lines.clear();
		queuedGeos.clear();
	}

	public void feed(String auralText, GeoElement geo) {
		if (!queuedGeos.contains(geo)) {
			queuedGeos.add(geo);
			lines.add(auralText);
			if (!isRunning()) {
				schedule(DELAY_MILLIS);
			}
		}
	}
}
