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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

/** Characterize the existing rounded numeric and floored symbolic CDF boundaries. */
@Issue("APPS-7945")
class BinomialStepBoundaryTest extends BaseSymbolicTest {
	private static final String[] VALUES = {
		"-0.51", "-0.5", "-0.49", "0", "0.49", "0.5", "0.51", "0.99", "1", "1.49", "1.5", "1.51",
		"1.99", "2", "3"
	};
	private static final String[] ROUNDED = {
		"0", "1 / 4", "1 / 4", "1 / 4", "1 / 4", "3 / 4", "3 / 4", "3 / 4", "3 / 4", "3 / 4", "1", "1",
		"1", "1", "1"
	};
	private static final String[] FLOORED = {
		"0", "0", "0", "1 / 4", "1 / 4", "1 / 4", "1 / 4", "1 / 4", "3 / 4", "3 / 4", "3 / 4", "3 / 4",
		"3 / 4", "1", "1"
	};

	@Test
	void directNumericCasArgumentsAreRounded() {
		for (int i = 0; i < VALUES.length; i++) {
			t("BinomialDist(2,1/2," + VALUES[i] + ",true)", ROUNDED[i]);
		}
	}

	@Test
	void symbolicFunctionArgumentsAreFloored() {
		add("cdf(t):=BinomialDist(2,1/2,t,true)");
		for (int i = 0; i < VALUES.length; i++) {
			t("cdf(" + VALUES[i] + ")", FLOORED[i]);
		}
	}

	@Test
	void substitutingIntoStoredSymbolicSumUsesFloorBoundaries() {
		add("storedCdf:=BinomialDist(2,1/2,x,true)");
		for (int i = 0; i < VALUES.length; i++) {
			t("Substitute(storedCdf,x," + VALUES[i] + ")", FLOORED[i]);
		}
	}

	@Test
	void directAlgebraArgumentsAreRounded() {
		kernel.setSymbolicMode(SymbolicMode.NONE);
		for (int i = 0; i < VALUES.length; i++) {
			String decimal = ROUNDED[i].replace("1 / 4", "0.25").replace("3 / 4", "0.75");
			t("BinomialDist(2,1/2," + VALUES[i] + ",true)", decimal);
		}
	}
}
