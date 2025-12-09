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
 
package org.geogebra.common.kernel.interval.operators;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.SamplerTest;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.Test;

public class PowerEquivalenceTest extends SamplerTest {

	@Test
	public void sqrtAndPOWOneHalfShouldBeEqual() {
		shouldBeEquivalent("sqrt(x)", "(x)^(1/2)");
	}

	@Test
	public void root3OfXPOW5AndPOW5Under3ShouldBeEqual() {
		shouldBeEquivalent("nroot(x^5, 3)", "(x)^(5/3)");
	}

	@Test
	public void nrootOfXInverseAndXOnPowMinus1ShouldBeEqual() {
		shouldBeEquivalent("nroot(1/x, 9)^2", "nroot(x^-1, 9)^2");
	}

	@Test
	public void nrootAndPowFractionShouldBeEqual() {
		shouldBeEquivalent("nroot(x, 9)", "x^(1/9)");
	}

	@Test
	public void nrootOfXInverseAndPowFractionShouldBeEqual() {
		shouldBeEquivalent("nroot(1/x, 9)", "(1/x)^(1/9)");
	}

	@Test
	public void xInverseAndXPOWMinus1ShouldBeEqual() {
		shouldBeEquivalent("x^-1", "1/x");
	}

	@Test
	public void xInverseAndXPOWDoubleApply() {
		shouldBeEquivalent("(x^-1)^-1", "1/(1/x)");
	}

	private void shouldBeEquivalent(String description1, String description2) {
		IntervalTupleList samples = samplesOf(description1);
		IntervalTupleList other = samplesOf(description2);
		boolean ok = true;
		for (int i = 0; i < samples.count(); i++) {
			IntervalTuple tuple1 = samples.get(i);
			IntervalTuple tuple2 = other.get(i);
			ok = ok && (tuple1.x().equals(tuple2.x()) && tuple1.y().almostEqual(tuple2.y(), 1E-7));
		}
		assertTrue(ok);
	}

	private IntervalTupleList samplesOf(String description) {
		return functionValues(description, -10, 10, 10, 10);
	}
}
