package org.geogebra.common.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasAuralText;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;

public class AltTextTimer implements GTimerListener {
	public static final int DELAY_MILLIS = 700;
	private final ScreenReaderAdapter screenReader;
	private final Localization loc;
	private final ArrayList<HasAuralText> queuedGeos = new ArrayList<>();
	private final HashMap<HasAuralText, String> lastReadText = new HashMap<>();
	private final GTimer timer;

	/**
	 * @param screenReader screen reader
	 * @param loc localization
	 */
	public AltTextTimer(ScreenReaderAdapter screenReader, Localization loc) {
		this.screenReader = screenReader;
		this.loc = loc;
		this.timer = UtilFactory.getPrototype() == null
				? null : UtilFactory.getPrototype().newTimer(this, DELAY_MILLIS);
	}

	@Override
	public void onRun() {
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

	/**
	 * Cancel the timer and clear queued texts
	 */
	public void cancel() {
		if (timer != null) {
			timer.stop();
		}
		queuedGeos.clear();
		lastReadText.clear();
	}

	/**
	 * Adds a readable element to read when time interval expires
	 *
	 * @param textProvider text provider
	 */
	public void feed(HasAuralText textProvider) {
		if (!queuedGeos.contains(textProvider) && textChanged(textProvider)) {
			queuedGeos.add(textProvider);
			lastReadText.remove(textProvider);
			if (timer != null && !timer.isRunning()) {
				timer.start();
			}
		}
	}

	private boolean textChanged(HasAuralText textProvider) {
		return !textProvider.getAuralText().equals(lastReadText.get(textProvider));
	}

	/**
	 * Adds a GeoText and its aural text to the {@link #lastReadText}
	 * @param geoText {@link GeoText}
	 */
	public void preload(GeoText geoText) {
		lastReadText.put(geoText, geoText.getAuralText());
	}
}
