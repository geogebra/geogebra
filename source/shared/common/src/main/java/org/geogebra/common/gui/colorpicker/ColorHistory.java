/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.colorpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.annotation.TestOnly;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.states.MutableState;
import org.geogebra.common.states.State;
import org.jspecify.annotations.NonNull;

/**
 * Recently used colors of the color picker ("Your colors"), newest first, without duplicates.
 * <p>
 * While a color is being edited, it is shown in a <i>live slot</i> at the front, which may
 * duplicate another entry until it is committed. Cleared when an exam starts or ends.
 */
public final class ColorHistory implements ExamListener {

	/** Maximum number of colors in the history. */
	public static final int CAPACITY = 15;

	private final MutableState<@NonNull List<GColor>> colors =
			new MutableState<>(Collections.emptyList());
	private boolean hasLiveSlot;

	/**
	 * @return the colors, newest first
	 */
	public @NonNull State<@NonNull List<GColor>> getColors() {
		return colors;
	}

	/**
	 * Adds a color at the front, unless it is already present. Commits the live slot first.
	 * @param color the color to add (alpha is ignored)
	 */
	public void add(@NonNull GColor color) {
		commitLiveSlot();
		GColor opaque = opaque(color);
		if (colors.get().contains(opaque)) {
			return;
		}
		List<GColor> next = new ArrayList<>(colors.get());
		next.add(0, opaque);
		publish(next);
	}

	/**
	 * Inserts a live slot at the front. Commits the previous live slot first.
	 * @param color the initial color of the live slot (alpha is ignored)
	 */
	public void beginLiveSlot(@NonNull GColor color) {
		commitLiveSlot();
		List<GColor> next = new ArrayList<>(colors.get());
		next.add(0, opaque(color));
		hasLiveSlot = true;
		publish(next);
	}

	/**
	 * Replaces the color of the live slot, if there is one.
	 * @param color the new color (alpha is ignored)
	 */
	public void updateLiveSlot(@NonNull GColor color) {
		if (!hasLiveSlot) {
			return;
		}
		List<GColor> next = new ArrayList<>(colors.get());
		next.set(0, opaque(color));
		publish(next);
	}

	/**
	 * Turns the live slot into a regular entry, removing duplicates of it.
	 */
	public void commitLiveSlot() {
		if (!hasLiveSlot) {
			return;
		}
		hasLiveSlot = false;
		List<GColor> current = colors.get();
		GColor live = current.get(0);
		List<GColor> next = new ArrayList<>(current.size());
		next.add(live);
		for (int i = 1; i < current.size(); i++) {
			if (!current.get(i).equals(live)) {
				next.add(current.get(i));
			}
		}
		publish(next);
	}

	/**
	 * Removes all colors, including the live slot.
	 */
	public void clear() {
		hasLiveSlot = false;
		publish(Collections.emptyList());
	}

	private static @NonNull GColor opaque(@NonNull GColor color) {
		return color.getAlpha() == 255 ? color : color.deriveWithAlpha(255);
	}

	private void publish(@NonNull List<GColor> next) {
		List<GColor> trimmed = next.size() > CAPACITY ? next.subList(0, CAPACITY) : next;
		colors.set(trimmed);
	}

	// - ExamListener

	@Override
	public void examStateChanged(ExamState newState) {
		if (newState == ExamState.ACTIVE || newState == ExamState.FINISHED) {
			clear();
		}
	}

	// Test support

	/**
	 * @return whether a live slot is currently active (always at index 0)
	 */
	@TestOnly
	public boolean hasLiveSlot() {
		return hasLiveSlot;
	}
}
