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

package org.geogebra.common.kernel.interval.samplers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.jupiter.api.Test;

class IntervalFunctionDomainInfoTest {

	@Test
	void updateSnapshotsTheDomain() {
		IntervalFunctionDomainInfo info = new IntervalFunctionDomainInfo();
		Interval domain = new Interval(0, 10);

		info.update(domain);
		domain.set(100, 110);

		assertAll(
				() -> assertEquals(10, info.getLength()),
				() -> assertTrue(info.hasPannedLeft(new Interval(-5, 10))),
				() -> assertTrue(info.hasPannedRight(new Interval(0, 15))),
				() -> assertTrue(info.hasZoomedOut(new Interval(-5, 15))));
	}

	@Test
	void pannedLeftDetectionSurvivesSourceMutation() {
		IntervalFunctionDomainInfo info = new IntervalFunctionDomainInfo();
		Interval shared = new Interval(0, 10);

		info.update(shared);
		shared.set(-5, 10);

		assertTrue(info.hasPannedLeft(shared));
	}

	@Test
	void pannedRightDetectionSurvivesSourceMutation() {
		IntervalFunctionDomainInfo info = new IntervalFunctionDomainInfo();
		Interval shared = new Interval(0, 10);

		info.update(shared);
		shared.set(0, 15);

		assertTrue(info.hasPannedRight(shared));
	}

	@Test
	void zoomedOutDetectionSurvivesSourceMutation() {
		IntervalFunctionDomainInfo info = new IntervalFunctionDomainInfo();
		Interval shared = new Interval(0, 10);

		info.update(shared);
		shared.set(-5, 15);

		assertTrue(info.hasZoomedOut(shared));
	}

	@Test
	void intersectsUsesStoredSnapshotNotAlias() {
		IntervalFunctionDomainInfo info = new IntervalFunctionDomainInfo();
		Interval shared = new Interval(0, 10);

		info.update(shared);
		shared.set(100, 110);

		assertAll(
				() -> assertTrue(info.intersects(new Interval(5, 6))),
				() -> assertFalse(info.intersects(new Interval(50, 60))));
	}

	@Test
	void updateRejectsNull() {
		IntervalFunctionDomainInfo info = new IntervalFunctionDomainInfo();
		assertThrows(IllegalArgumentException.class, () -> info.update(null));
	}
}
