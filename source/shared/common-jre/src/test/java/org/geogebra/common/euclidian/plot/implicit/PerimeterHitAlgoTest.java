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

package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.jupiter.api.Test;

class PerimeterHitAlgoTest extends BaseContourTestSetup {
	private PerimeterHitAlgo algo;

	@Test
	void shouldCollectTwoOrderedTopHitsForCassiniViewport() {
		EuclidianViewBounds bounds = newBounds(-0.12727, 22.36, -22, 0.9, 1237, 1265);
		ContourInfo info = setupCassiniWithBounds(bounds);
		List<EdgeHit> hits = algo.collectHits(info.getContours());

		assertEquals(2, hits.size());

		EdgeHit first = hits.get(0);
		EdgeHit second = hits.get(1);
		double topY = new ClipRect(bounds, 10).getYmax();

		assertAll(
				() -> assertEquals(ClipEdge.TOP, first.edge()),
				() -> assertEquals(ClipEdge.TOP, second.edge()),
				() -> assertTrue(first.tOnEdge() < second.tOnEdge()),
				() -> assertTrue(first.tOnEdge() >= 0 && first.tOnEdge() <= 1),
				() -> assertTrue(second.tOnEdge() >= 0 && second.tOnEdge() <= 1),
				() -> assertEquals(topY, first.point().y, 1e-9),
				() -> assertEquals(topY, second.point().y, 1e-9));
	}

	@Test
	void shouldCollectFourOrderedTopHitsForCassiniViewport() {
		ContourInfo info = setupCassiniWithFourIntersectionOnTop();
		List<EdgeHit> hits = algo.collectHits(info.getContours());
		assertEquals(4, hits.size());
		double topY = new ClipRect(info.getBounds(), 10).getYmax();

		assertAll(
				() -> assertEquals(ClipEdge.TOP, hits.get(0).edge()),
				() -> assertEquals(ClipEdge.TOP, hits.get(1).edge()),
				() -> assertEquals(ClipEdge.TOP, hits.get(2).edge()),
				() -> assertEquals(ClipEdge.TOP, hits.get(3).edge()),
				() -> assertTrue(hits.get(0).tOnEdge() < hits.get(1).tOnEdge()),
				() -> assertTrue(hits.get(1).tOnEdge() < hits.get(2).tOnEdge()),
				() -> assertTrue(hits.get(2).tOnEdge() < hits.get(3).tOnEdge()),
				() -> assertTrue(hits.stream().allMatch(hit -> hit.tOnEdge() >= 0 && hit.tOnEdge() <= 1)),
				() -> assertEquals(topY, hits.get(0).point().y, 1e-9),
				() -> assertEquals(topY, hits.get(1).point().y, 1e-9),
				() -> assertEquals(topY, hits.get(2).point().y, 1e-9),
				() -> assertEquals(topY, hits.get(3).point().y, 1e-09));
	}

	private ContourInfo setupCassiniWithFourIntersectionOnTop() {
		return setupCassiniWithBounds(newBounds(-6.28311, 4.56737, -26.65782, 0.41718, 1237, 1265));
	}

	private ContourInfo setupCassiniWithBounds(EuclidianViewBounds bounds) {
		algo = new PerimeterHitAlgo(new ClipRect(bounds, 10), ClipEpsilon.fromBounds(bounds));
		GeoElement cassini = addCassini(2.9, 2.98);
		return builder.withImplicitCurve(cassini).withBounds(bounds).build();
	}

	@Test
	void testHitsOnBottomRightCorner() {
		ContourInfo info = setupCassiniWithBounds(newBounds(
				-16.59999999999983, 3.2399999999997466, -1.049999999999993, 24.250000000000057, 992, 1265));
		List<EdgeHit> collectedHits = algo.collectHits(info.getContours());
		assertEquals(6, collectedHits.size());
	}
}
