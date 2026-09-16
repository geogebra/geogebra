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

package org.geogebra.common.euclidian.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.AlgoFitLineY;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FitLineToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(EuclidianConstants.MODE_FITLINE);
	}

	@Test
	void fitLineToolSelectionRectangle() {
		add("A = (2, -2)");
		add("B = (3, -5)");
		add("C = (4, -8)");
		dragStart(50, 50);
		dragEnd(500, 500);

		checkContent("A = (2, -2)", "B = (3, -5)", "C = (4, -8)", "f: y = -3x + 4");
	}

	@Test
	void fitLineToolTwoPointsByClick() {
		add("A = (1, -1)");
		add("B = (2, -3)");

		List<GeoElement> lines = applyFitLineTool(() -> {
			click(50, 50);
			click(100, 150);
		});

		assertFitLine(lines, "f: y = -2x + 1");
	}

	@Test
	void fitLineToolTwoPointsBySelectionRectangle() {
		add("A = (1, -1)");
		add("B = (2, -3)");

		List<GeoElement> lines = applyFitLineTool(() -> {
			dragStart(25, 25);
			dragEnd(125, 175);
		});

		assertFitLine(lines, "f: y = -2x + 1");
	}

	@Test
	void fitLineToolPointListSelection() {
		GeoElement list = add("l = {(1, -1), (2, -3), (3, -5)}");

		// Point lists are selected from Algebra View, not hit on the Euclidian canvas.
		List<GeoElement> lines = applyFitLineTool(() -> processHits(list));

		assertFitLine(lines, "f: y = -2x + 1");
	}

	@Test
	void fitLineToolSinglePointDoesNotCreateLine() {
		add("A = (1, -1)");

		List<GeoElement> lines = applyFitLineTool(() -> click(50, 50));

		assertTrue(lines.isEmpty());
	}

	@Test
	void fitLineToolSinglePointSelectionRectangleDoesNotCreateLine() {
		add("A = (1, -1)");

		List<GeoElement> lines = applyFitLineTool(() -> {
			dragStart(25, 25);
			dragEnd(75, 75);
		});

		assertTrue(lines.isEmpty());
	}

	@Test
	void fitLineToolPointPreviewDoesNotCreateLine() {
		GeoElement point = add("A = (1, -1)");
		int constructionSize = constructionSize();
		ec.wrapMouseMoved(new TestEvent(50, 50));

		assertTrue(ec.getHighlightedgeos().contains(point));
		assertEquals(constructionSize, constructionSize());
	}

	@Test
	void fitLineToolSelectionRectangleIgnoresNonPoints() {
		add("A = (1, -1)");
		add("B = (2, -3)");
		add("C = (3, -5)");
		add("c: (x - 2)^2 + (y + 3)^2 = 0.25");

		List<GeoElement> lines = applyFitLineTool(() -> {
			dragStart(25, 25);
			dragEnd(175, 275);
		});

		assertFitLine(lines, "f: y = -2x + 1");
	}

	@Test
	void fitLineToolEmptyClickDoesNothing() {
		List<GeoElement> lines = applyFitLineTool(() -> click(100, 100));

		assertTrue(lines.isEmpty());
	}

	@Test
	void fitLineToolUnsupportedObjectDoesNothing() {
		add("g: y = -1");

		List<GeoElement> lines = applyFitLineTool(() -> click(50, 50));

		assertTrue(lines.isEmpty());
	}

	private List<GeoElement> applyFitLineTool(Runnable toolAction) {
		Set<GeoElement> geosBeforeFitLine =
				new HashSet<>(getApp().getKernel().getConstruction().getGeoSetConstructionOrder());
		toolAction.run();

		List<GeoElement> lines = new ArrayList<>();
		for (GeoElement geo : getApp().getKernel().getConstruction().getGeoSetConstructionOrder()) {
			if (!geosBeforeFitLine.contains(geo) && geo.isGeoLine()) {
				lines.add(geo);
			}
		}
		return lines;
	}

	private void processHits(GeoElement... geos) {
		Hits hits = new Hits();
		Collections.addAll(hits, geos);
		ec.processMode(hits, false, false);
	}

	private void assertFitLine(List<GeoElement> lines, String expected) {
		assertEquals(1, lines.size());
		assertEquals(expected, lines.get(0).toString(StringTemplate.testTemplate));
		assertInstanceOf(AlgoFitLineY.class, lines.get(0).getParentAlgorithm());
	}

	private int constructionSize() {
		return getApp().getKernel().getConstruction().getGeoSetConstructionOrder().size();
	}
}
