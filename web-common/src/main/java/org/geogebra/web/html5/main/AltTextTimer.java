package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.HashMap;

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
	private HashMap<HasAuralText, String> lastReadText = new HashMap<>();

	public AltTextTimer(ScreenReaderAdapter screenReader, Localization loc) {
		this.screenReader = screenReader;
		this.loc = loc;
	}

	@Override
	public void run() {
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		for (HasAuralText textProvider: queuedGeos) {
			String line = textProvider.getAuralText();
			lastReadText.put(textProvider, line);
			sb.append(line);
			sb.endSentence();
		}
		String current = sb.toString();
		if (!current.isEmpty()) {
			screenReader.readText(sb.toString());
		}
		queuedGeos.clear();
	}

	@Override
	public void cancel() {
		super.cancel();
		queuedGeos.clear();
		lastReadText.clear();
	}

	public void feed(HasAuralText textProvider) {
		if (!queuedGeos.contains(textProvider) && textChanged(textProvider)) {
			queuedGeos.add(textProvider);
			lastReadText.remove(textProvider);
			if (!isRunning()) {
				schedule(DELAY_MILLIS);
			}
		}
	}

	private boolean textChanged(HasAuralText textProvider) {
		return !textProvider.getAuralText().equals(lastReadText.get(textProvider));
	}
}
