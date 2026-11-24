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

package org.geogebra.common.kernel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction.GeoElementReplacementListener;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConstructionReplacementTest extends BaseAppTestSetup {
	private final Map<GeoElement, GeoElement> replacements = new HashMap<>();

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void notifiesWhenElementIsReplaced() {
		GeoElement boxPlot = evaluateGeoElement("b = BoxPlot(1, 2, {1, 2, 3})");
		AtomicReference<GeoElement> liveElementAtNotification = new AtomicReference<>();
		getConstruction().addGeoElementReplacementListener(replaced -> {
			replacements.putAll(replaced);
			liveElementAtNotification.set(lookup("b"));
		});

		editGeoElement(boxPlot, "BoxPlot(1, 2, {1, 2, 3}, true)");

		assertNotSame(boxPlot, lookup("b"));
		assertSame(lookup("b"), replacements.get(boxPlot));
		assertSame(replacements.get(boxPlot), liveElementAtNotification.get());
		assertEquals(1, replacements.size());
	}

	@Test
	void addingPieChartRadiusPreservesSliceStyle() {
		GeoPieChart chart = evaluateGeoElement("p = PieChart({1, 2, 3})");
		chart.getStyle().setBarColor(GColor.RED, 2);
		chart.getStyle().setBarAlpha(0.25, 2);
		chart.setFillType(FillType.BRICK);
		chart.getStyle().setBarFillType(FillType.HATCH, 2);
		chart.getStyle().setBarHatchAngle(45, 2);
		chart.getStyle().setBarHatchDistance(12, 2);

		editGeoElement(chart, "PieChart({1, 2, 3}, (0, 0), 5)");

		GeoPieChart replacement = (GeoPieChart) lookup("p");
		assertNotSame(chart, replacement);
		assertEquals(5, replacement.getRadius());
		chart.getStyle().setBarAlpha(0.75, 2);
		assertAll(
				() -> assertEquals(GColor.RED, replacement.getStyle().getBarColor(2)),
				() -> assertEquals(0.25, replacement.getStyle().getBarAlpha(2)),
				() -> assertEquals(FillType.HATCH, replacement.getStyle().getBarFillType(2)),
				() -> assertEquals(45, replacement.getStyle().getBarHatchAngle(2)),
				() -> assertEquals(12, replacement.getStyle().getBarHatchDistance(2)),
				() -> assertEquals(
						FillType.BRICK, replacement.getStyle().getBarFillType(1, replacement.getFillType())));
	}

	@Test
	void reportsRecreatedElementsAfterXmlRebuildInOneNotification() {
		GeoElement boxPlot = evaluateGeoElement("b = BoxPlot(1, 2, {1, 2, 3})");
		GeoElement point = evaluateGeoElement("A = (1, 2)");
		evaluateGeoElement("dependent = b + 1");
		List<Map<GeoElement, GeoElement>> notifications = new ArrayList<>();
		getConstruction().addGeoElementReplacementListener(notifications::add);

		editGeoElement(boxPlot, "BoxPlot(1, 2, {1, 2, 3}, true)");

		assertEquals(1, notifications.size());
		assertNotSame(boxPlot, lookup("b"));
		assertNotSame(point, lookup("A"));
		assertSame(lookup("b"), notifications.get(0).get(boxPlot));
		assertSame(lookup("A"), notifications.get(0).get(point));
	}

	@Test
	void reportsRecreatedElementsAfterFailedRedefinition() {
		GeoElement function = evaluateGeoElement("f(x) = x^2");
		evaluateGeoElement("Integral(f, 0, 1)");
		getConstruction().addGeoElementReplacementListener(replacements::putAll);

		editGeoElement(function, "(1, 2)");

		assertFalse(errorAccumulator.getErrors().isEmpty());
		assertNotSame(function, lookup("f"));
		assertTrue(lookup("f").isGeoFunction());
		assertSame(lookup("f"), replacements.get(function));
	}

	@Test
	void doesNotNotifyForSoftRedefinition() {
		GeoElement boxPlot = evaluateGeoElement("b = BoxPlot(1, 2, {1, 2, 3})");
		getConstruction().addGeoElementReplacementListener(replacements::putAll);

		editGeoElement(boxPlot, "BoxPlot(1, 2, {4, 5, 6})");

		assertSame(boxPlot, lookup("b"));
		assertTrue(replacements.isEmpty());
	}

	@Test
	void doesNotNotifyForCircularRedefinition() {
		GeoElement original = evaluateGeoElement("c = Circle((0, 0), 1)");
		GeoElement dependent = evaluateGeoElement("d = Translate(c, (1, 1))");
		getConstruction().addGeoElementReplacementListener(replacements::putAll);

		assertThrows(
				CircularDefinitionException.class, () -> getConstruction().replace(original, dependent));

		assertSame(original, lookup("c"));
		assertTrue(replacements.isEmpty());
	}

	@Test
	void doesNotNotifyWhileCollectingRedefinitions() {
		evaluateGeoElement("b = BoxPlot(1, 2, {1, 2, 3})");
		evaluateGeoElement("c = BoxPlot(1, 2, {4, 5, 6})");
		evaluateGeoElement("dependent = b + c");
		getConstruction().addGeoElementReplacementListener(replacements::putAll);
		getConstruction().startCollectingRedefineCalls();

		evaluateGeoElement("b = BoxPlot(1, 2, {1, 2, 3}, true)");
		evaluateGeoElement("c = BoxPlot(1, 2, {4, 5, 6}, true)");

		assertTrue(replacements.isEmpty());
	}

	@Test
	void notifiesAfterProcessingCollectedRedefinitions() throws Exception {
		GeoElement first = evaluateGeoElement("b = BoxPlot(1, 2, {1, 2, 3})");
		GeoElement second = evaluateGeoElement("c = BoxPlot(1, 2, {4, 5, 6})");
		evaluateGeoElement("dependent = b + c");
		getConstruction().addGeoElementReplacementListener(replacements::putAll);
		getConstruction().startCollectingRedefineCalls();

		evaluateGeoElement("b = BoxPlot(1, 2, {1, 2, 3}, true)");
		evaluateGeoElement("c = BoxPlot(1, 2, {4, 5, 6}, true)");

		getConstruction().processCollectedRedefineCalls();

		assertSame(lookup("b"), replacements.get(first));
		assertSame(lookup("c"), replacements.get(second));
	}

	@Test
	void doesNotNotifyRemovedListener() {
		GeoElement boxPlot = evaluateGeoElement("b = BoxPlot(1, 2, {1, 2, 3})");
		GeoElementReplacementListener listener = replacements::putAll;
		getConstruction().addGeoElementReplacementListener(listener);
		getConstruction().removeGeoElementReplacementListener(listener);

		editGeoElement(boxPlot, "BoxPlot(1, 2, {1, 2, 3}, true)");

		assertNotSame(boxPlot, lookup("b"));
		assertTrue(replacements.isEmpty());
	}
}
