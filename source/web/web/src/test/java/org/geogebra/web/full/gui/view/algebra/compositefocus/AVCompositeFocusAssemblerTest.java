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

package org.geogebra.web.full.gui.view.algebra.compositefocus;

import static org.geogebra.common.main.settings.AlgebraStyle.DEFINITION;
import static org.geogebra.common.main.settings.AlgebraStyle.DEFINITION_AND_VALUE;
import static org.geogebra.common.main.settings.AlgebraStyle.DESCRIPTION;
import static org.geogebra.common.main.settings.AlgebraStyle.LINEAR_NOTATION;
import static org.geogebra.common.main.settings.AlgebraStyle.VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.gui.compositefocus.EchoScreenReader;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.web.full.gui.view.algebra.FocusableCompositeW;
import org.geogebra.web.full.gui.view.algebra.ItemFactory;
import org.geogebra.web.full.gui.view.algebra.LinearNotationFocusAccess;
import org.geogebra.web.full.gui.view.algebra.LinearNotationTreeItem;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItemFocusAccess;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AVCompositeFocusAssemblerTest extends AddGeosSetup {
	public static final int MAX_TRAVERSAL_STEP = 50;
	private AVCompositeFocusAssembler assembler;
	private FocusableCompositeW focus;
	private EchoScreenReader echo;
	private ItemFactory itemFactory;

	@Before
	public void setUp() {
		initApp();
		echo = new EchoScreenReader();
		itemFactory = new ItemFactory();
		getApp().getKernel().setPrintDecimals(10);
	}

	@Test
	public void testFocusLoopFraction() {
		GeoElement geo = add("1/2");
		cycle(geo, DEFINITION, "a = 1 / 2", "more");
		cycle(geo, VALUE, "a = 0.5", "more");
		cycle(geo, DEFINITION_AND_VALUE,
				"a = 1 / 2", "0.5", "FormatFraction", "more");
		cycle(geo, LINEAR_NOTATION, "a = 1 / 2", "0.5", "FormatFraction", "more");
	}

	@Test
	public void testFocusLoopPoint() {
		// TestFormatFactory formats (1, 1) as (1.0, 1.0), but we want to
		// keep the input and output format the same here.
		GeoElement geo = add("(1.0, 1.0)");
		cycle(geo, DEFINITION, "A = (1.0, 1.0)", "more");
		cycle(geo, DESCRIPTION, "Point A", "more");
		cycle(geo, VALUE, "A = (1.0, 1.0)", "more");
		cycle(geo, DEFINITION_AND_VALUE, "A = (1.0, 1.0)", "more");
		cycle(geo, LINEAR_NOTATION, "A = (1.0, 1.0)", "more");
	}

	@Test
	public void testFocusLoopPointInTwoRows() {
		GeoElement geo = add("(1.0, sqrt(4))");
		cycle(geo, DEFINITION, "A = (1.0, sqrt(4))", "more");
		cycle(geo, DESCRIPTION, "Point A", "more");
		cycle(geo, VALUE, "A = (1.0, 2.0)", "more");
		cycle(geo, DEFINITION_AND_VALUE,
				"A = (1.0, sqrt(4))", "(1.0, 2.0)", "more");
		cycle(geo, LINEAR_NOTATION, "A = (1.0, sqrt(4))", "(1.0, 2.0)", "more");
	}

	@Test
	public void testCircleLoop() {
		add("A = (1, 1)");
		GeoElement geo = add("Circle(A, 2)");
		cycle(geo, DEFINITION, "c = Circle(A, 2)", "more");
		cycle(geo, DESCRIPTION, "c = Circle with center A and radius 2", "more");
		cycle(geo, VALUE, "c: (x - 1.0)\u00B2 + (y - 1.0)\u00B2 = 4.0", "more");
		cycle(geo, DEFINITION_AND_VALUE, "c: Circle(A, 2)",
				"(x - 1.0)\u00B2 + (y - 1.0)\u00B2 = 4.0", "more");
		cycle(geo, LINEAR_NOTATION, "c: Circle(A, 2)",
				"(x - 1.0)\u00B2 + (y - 1.0)\u00B2 = 4.0", "more");
	}

	@Test
	public void testTextLoop() {
		String cmd = "\"This is a text\"";
		GeoElement geo = add(cmd);

		String expected = "text1 = \u201cThis is a text\u201d";
		cycle(geo, DEFINITION, expected, "more");
		cycle(geo, DESCRIPTION, expected, "more");
		cycle(geo, VALUE, expected, "more");

		String expectedWithSingleQuote = "text1 = " + cmd;
		cycle(geo, DEFINITION_AND_VALUE, expectedWithSingleQuote, "more");
		cycle(geo, LINEAR_NOTATION, expectedWithSingleQuote, "more");
	}

	@Test
	public void testFunctionLoop() {
		GeoElement geo = add("f(x)=x^2 + 1");
		cycle(geo, DEFINITION, "f(x) = x\u00B2 + 1", "more");
		cycle(geo, DESCRIPTION, "f(x) = x\u00B2 + 1", "more");
		cycle(geo, VALUE, "f(x) = x\u00B2 + 1", "more");
		cycle(geo, DEFINITION_AND_VALUE, "f(x) = x\u00B2 + 1", "more");
		cycle(geo, LINEAR_NOTATION, "f(x) = x\u00B2 + 1", "more");
	}

	@Test
	public void wip() {
		// work in progress: for debugging
		cycle(add("1/2 + 3/4"), DESCRIPTION, "a = 1 / 2 + 3 / 4", "1.25",
				"FormatFraction", "more");
	}

	private void cycle(GeoElement geo, AlgebraStyle algebraStyle, String... announcements) {
		echo.clear();
		createCompositeFor(geo, algebraStyle);
		cycleFocusOnce();
		shouldAnnounceFocusCycle(announcements);
	}

	private void shouldAnnounceFocusCycle(String... announcements) {
		int length = announcements.length;
		assertTrue("No announcements", length > 0);
		List<String> list = new ArrayList<>(length + 1);
		String first = announcements[0];
		list.add(first);
		if (length > 1) {
			Collections.addAll(list, Arrays.copyOfRange(announcements, 1, length));
		}
		list.add(first);
		assertEquals(list, echo.getAnnouncementsAsList());
	}

	private void cycleFocusOnce() {
		assertTrue("Focus first failed", focus.focusFirst());
		String firstKey = focus.getSelectedKey();
		assertNotNull("First key should not be null", firstKey);
		for (int guard = 0; guard < MAX_TRAVERSAL_STEP; guard++) {
			assertTrue("Focus next failed", focus.focusNext());
			if (firstKey.equals(focus.getSelectedKey())) {
				return;
			}
		}
		fail("Focus did not wrap back to first: firstKey=" + firstKey
				+ ", currentKey=" + focus.getSelectedKey());
	}

	private void setAlgebraStyle(AlgebraStyle algebraStyle) {
		getApp().getSettings().getAlgebra().setStyle(algebraStyle);
	}

	private void createCompositeFor(GeoElement geo, AlgebraStyle algebraStyle) {
		setAlgebraStyle(algebraStyle);
		focus = new FocusableCompositeW(getApp().getAccessibilityManager(),
				() -> true) {
			@Override
			protected void readDebug(String text) {
				echo.readText(text);
			}
		};
		boolean ln = LINEAR_NOTATION.equals(algebraStyle);
		RadioTreeItem item = itemFactory.createAVItem(geo);
		RadioTreeItemFocusAccess fa = ln ? new LinearNotationFocusAccess(
				(LinearNotationTreeItem) item)
		: new RadioTreeItemFocusAccess(item);
		assembler = new AVCompositeFocusAssembler(focus, fa,
				getApp().getAccessibilityManager());
		assembler.rebuild(AVFocusContributorFactory.forItem(item));
	}
}
