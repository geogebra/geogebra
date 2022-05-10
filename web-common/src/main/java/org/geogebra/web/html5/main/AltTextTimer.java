package org.geogebra.web.html5.main;

import java.util.ArrayList;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.kernel.geos.HasAuralText;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.Localization;
import org.gwtproject.timer.client.Timer;

class AltTextTimer extends Timer {
	public static final int DELAY_MILLIS = 700;
	private final ScreenReaderAdapter screenReader;
	private final Localization loc;
	private final ArrayList<HasAuralText> queuedGeos = new ArrayList<>();
	private String lastReadText = null;

	public AltTextTimer(ScreenReaderAdapter screenReader, Localization loc) {
		this.screenReader = screenReader;
		this.loc = loc;
	}

	@Override
	public void run() {
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		for (HasAuralText textProvider: queuedGeos) {
			sb.append(textProvider.getAuralText());
			sb.endSentence();
		}
		String current = sb.toString();
		if (!current.equals(lastReadText)) {
			screenReader.readText(sb.toString());
			lastReadText = current;
		}
		queuedGeos.clear();
	}

	@Override
	public void cancel() {
		super.cancel();
		queuedGeos.clear();
	}

	public void feed(HasAuralText textProvider) {
		if (!queuedGeos.contains(textProvider)) {
			queuedGeos.add(textProvider);
			if (!isRunning()) {
				schedule(DELAY_MILLIS);
			}
		}
	}
}
