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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.exam.ExamState;
import org.junit.jupiter.api.Test;

class ColorHistoryTest {
	private static final GColor C1 = GColor.newColorRGB(0x111111);
	private static final GColor C2 = GColor.newColorRGB(0x222222);
	private static final GColor C3 = GColor.newColorRGB(0x333333);

	private final ColorHistory history = new ColorHistory();

	@Test
	void addInsertsNewColorsAtFrontAndKeepsExistingInPlace() {
		history.add(C1);
		history.add(C2);
		history.add(C1);
		assertEquals(List.of(C2, C1), history.getColors().get());
	}

	@Test
	void addIgnoresAlpha() {
		history.add(GColor.newColor(0x11, 0x11, 0x11, 0x80));
		history.add(C1);
		assertEquals(List.of(C1), history.getColors().get());
	}

	@Test
	void addDropsOldestBeyondCapacity() {
		for (int i = 0; i <= ColorHistory.CAPACITY; i++) {
			history.add(GColor.newColorRGB(i));
		}
		List<GColor> colors = history.getColors().get();
		assertEquals(ColorHistory.CAPACITY, colors.size());
		assertEquals(GColor.newColorRGB(ColorHistory.CAPACITY), colors.get(0));
		assertFalse(colors.contains(GColor.newColorRGB(0)));
	}

	@Test
	void liveSlotMayTemporarilyDuplicateAndCommitDeduplicates() {
		history.add(C1);
		history.add(C2);
		history.beginLiveSlot(C3);
		assertTrue(history.hasLiveSlot());
		assertEquals(List.of(C3, C2, C1), history.getColors().get());
		history.updateLiveSlot(C1);
		assertEquals(List.of(C1, C2, C1), history.getColors().get());
		history.commitLiveSlot();
		assertFalse(history.hasLiveSlot());
		assertEquals(List.of(C1, C2), history.getColors().get());
	}

	@Test
	void addCommitsLiveSlotAndLaterUpdatesAreIgnored() {
		history.beginLiveSlot(C1);
		history.add(C2);
		history.updateLiveSlot(C3);
		assertFalse(history.hasLiveSlot());
		assertEquals(List.of(C2, C1), history.getColors().get());
	}

	@Test
	void examBecomingActiveClearsHistory() {
		history.add(C1);
		history.beginLiveSlot(C2);
		history.examStateChanged(ExamState.PREPARING);
		assertEquals(List.of(C2, C1), history.getColors().get());
		history.examStateChanged(ExamState.ACTIVE);
		assertEquals(List.of(), history.getColors().get());
		assertFalse(history.hasLiveSlot());
	}
}
