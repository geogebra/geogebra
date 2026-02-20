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

package org.geogebra.common.gui.compositefocus;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractFocusableCompositeFocusTest {
	private final EchoScreenReader echo = new EchoScreenReader();
	private AbstractFocusableComposite<TestFocusablePart> compositeFocus;

	@BeforeEach
	void setUp() {

		compositeFocus = new AbstractFocusableComposite<>(echo) {

			@Override
			protected void onGainFocus() {

			}

			@Override
			public boolean isFocused() {
				return false;
			}

			@Override
			protected void readDebug(String text) {
				echo.readText(text);
			}
		};
		echo.clear();

	}

	@Test
	void testNoParts() {
		assertAll(
				() -> assertFalse(compositeFocus.focusFirst()),
				() -> assertFalse(compositeFocus.focusLast()),
				() -> assertFalse(compositeFocus.focusNext()),
				() -> assertFalse(compositeFocus.focusPrevious()),
				() -> assertFalse(compositeFocus.hasFocus()),
				() -> assertNull(compositeFocus.getSelectedKey())
		);
	}

	@Test
	void testFocusFirst() {
		addTestParts(3);
		assertTrue(compositeFocus.focusFirst(), "focusFirst should succeed with parts present");
		assertAll(
				() -> assertTrue(compositeFocus.hasFocus()),
				() -> assertEquals("Part0", compositeFocus.getSelectedKey()),
				() -> assertEquals("Test Part 0", echo.getAnnouncements())
		);
	}

	private void addTestParts(int count) {
		compositeFocus.clearParts();
		for (int i = 0; i < count; i++) {
			compositeFocus.addPart(new TestFocusablePart("Test Part " + i,
					"Part" + i, false));
		}
	}

	@Test
	void testFocusLast() {
		addTestParts(3);
		assertTrue(compositeFocus.focusLast(), "focusLast should succeed with parts present");
		assertAll(
				() -> assertTrue(compositeFocus.hasFocus()),
				() -> assertEquals("Part2", compositeFocus.getSelectedKey()),
				() -> assertEquals("Test Part 2", echo.getAnnouncements())
		);
	}

	@Test
	void testFocusNext() {
		addTestParts(3);
		compositeFocus.focusFirst();
		assertTrue(compositeFocus.focusNext(), "focusNext should succeed with parts present");
		assertAll(
				() -> assertTrue(compositeFocus.hasFocus()),
				() -> assertEquals("Part1", compositeFocus.getSelectedKey()),
				() -> assertEquals("Test Part 0;Test Part 1", echo.getAnnouncements())
		);
	}

	@Test
	void testFocusPrevious() {
		addTestParts(3);
		compositeFocus.focusLast();
		assertTrue(compositeFocus.focusPrevious(),
				"focusPrevious should succeed with parts present");
		assertAll(
				() -> assertTrue(compositeFocus.hasFocus()),
				() -> assertEquals("Part1", compositeFocus.getSelectedKey()),
				() -> assertEquals("Test Part 2;Test Part 1", echo.getAnnouncements())
		);
	}

	@Test
	void testFocusNextCyclingToFirst() {
		addTestParts(3);
		compositeFocus.focusLast();
		assertTrue(compositeFocus.focusNext(), "focusNext should succeed with parts present");
		assertAll(
				() -> assertTrue(compositeFocus.hasFocus()),
				() -> assertEquals("Part0", compositeFocus.getSelectedKey()),
				() -> assertEquals("Test Part 2;Test Part 0", echo.getAnnouncements())
		);
	}

	@Test
	void testFocusPreviousCyclingToLast() {
		addTestParts(3);
		compositeFocus.focusFirst();
		assertTrue(compositeFocus.focusPrevious(),
				"focusPrevious should succeed with parts present");
		assertAll(
				() -> assertTrue(compositeFocus.hasFocus()),
				() -> assertEquals("Part2", compositeFocus.getSelectedKey()),
				() -> assertEquals("Test Part 0;Test Part 2", echo.getAnnouncements())
		);
	}

	@Test
	void testMultipleNextTraversals() {
		addTestParts(3);
		compositeFocus.focusFirst();
		compositeFocus.focusNext();
		compositeFocus.focusNext();
		compositeFocus.focusNext(); // wraps
		assertEquals("Part0", compositeFocus.getSelectedKey());
	}

	@Test
	void testMultiplePreviousTraversals() {
		addTestParts(3);
		compositeFocus.focusFirst();
		compositeFocus.focusPrevious();
		compositeFocus.focusPrevious();
		assertEquals("Part1", compositeFocus.getSelectedKey());
	}

	@Test
	void testBlur() {
		addTestParts(3);
		compositeFocus.focusFirst();
		compositeFocus.blur();
		assertFalse(compositeFocus.hasFocus());
		assertEquals("Test Part 0", echo.getAnnouncements()); // only initial announcement
	}

	@Test
	void testRestoreSelectionAfterTraversal() {
		addTestParts(3);
		compositeFocus.focusFirst();
		compositeFocus.focusNext();
		String key = compositeFocus.getSelectedKey();
		compositeFocus.clearParts();
		addTestParts(3);
		compositeFocus.restoreSelection(key);
		assertEquals("Part1", compositeFocus.getSelectedKey());
	}

	@Test
	void testHandlesEnterForSelectedPart() {
		compositeFocus.addPart(new TestFocusablePart("A", "A", true));
		compositeFocus.addPart(new TestFocusablePart("B", "B", false));
		compositeFocus.focusFirst();
		assertTrue(compositeFocus.handlesEnterKeyForSelectedPart());
		compositeFocus.focusNext();
		assertFalse(compositeFocus.handlesEnterKeyForSelectedPart());
	}

	@Test
	void testBlurDoesNotAnnounceAgain() {
		addTestParts(2);
		compositeFocus.focusFirst();
		assertEquals("Test Part 0", echo.getAnnouncements());

		echo.clear();
		compositeFocus.blur();

		assertFalse(compositeFocus.hasFocus());
		assertEquals("", echo.getAnnouncements(),
				"removeFocus should not trigger additional announcements");
	}

	@Test
	void testSelectionPersistsAcrossMultipleRebuilds() {
		addTestParts(3);
		compositeFocus.focusFirst();
		compositeFocus.focusNext();
		String key = compositeFocus.getSelectedKey();

		// simulate multiple rebuilds
		for (int i = 0; i < 3; i++) {
			compositeFocus.clearParts();
			addTestParts(3);
			compositeFocus.restoreSelection(key);
		}

		assertEquals("Part1", compositeFocus.getSelectedKey(),
				"selection should persist after repeated rebuilds");
	}

	@Test
	void testAnnouncementsOrder() {
		addTestParts(3);
		compositeFocus.focusFirst();
		compositeFocus.focusNext();
		compositeFocus.focusNext();
		List<String> list = echo.getAnnouncementsAsList();

		assertEquals(List.of("Test Part 0", "Test Part 1", "Test Part 2"), list);
	}

	@Test
	void testForwardThenBackwardTraversal() {
		addTestParts(4);
		compositeFocus.focusFirst(); // 0
		compositeFocus.focusNext();  // 1
		compositeFocus.focusNext();  // 2
		compositeFocus.focusPrevious(); // back to 1

		assertEquals("Part1", compositeFocus.getSelectedKey());
		assertEquals("Test Part 0;Test Part 1;Test Part 2;Test Part 1",
				echo.getAnnouncements());
	}

	@Test
	void testTraversalWithSinglePart() {
		addTestParts(1);
		assertTrue(compositeFocus.focusFirst());

		// next/prev should wrap to the same
		assertTrue(compositeFocus.focusNext());
		assertEquals("Part0", compositeFocus.getSelectedKey());

		assertTrue(compositeFocus.focusPrevious());
		assertEquals("Part0", compositeFocus.getSelectedKey());

		assertEquals("Test Part 0;Test Part 0;Test Part 0", echo.getAnnouncements());
	}

	@Test
	void testClearPartsResetsState() {
		addTestParts(3);
		compositeFocus.focusFirst();

		compositeFocus.clearParts();
		assertFalse(compositeFocus.hasFocus());
		assertNull(compositeFocus.getSelectedKey());
	}
}
