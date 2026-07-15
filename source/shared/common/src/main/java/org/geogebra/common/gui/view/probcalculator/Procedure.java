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

package org.geogebra.common.gui.view.probcalculator;

/***/
public enum Procedure {
	ZMEAN_TEST("ZMeanTest"),
	ZMEAN2_TEST("ZTestDifferenceOfMeans"),
	TMEAN_TEST("TMeanTest"),
	TMEAN2_TEST("TTestDifferenceOfMeans"),
	ZPROP_TEST("ZProportionTest"),
	ZPROP2_TEST("ZTestDifferenceOfProportions"),
	ZMEAN_CI("ZMeanInterval"),
	ZMEAN2_CI("ZEstimateDifferenceOfMeans"),
	TMEAN_CI("TMeanInterval"),
	TMEAN2_CI("TEstimateDifferenceOfMeans"),
	ZPROP_CI("ZProportionInterval"),
	ZPROP2_CI("ZEstimateDifferenceOfProportions"),
	GOF_TEST("GoodnessOfFitTest"),
	CHISQ_TEST("ChiSquaredTest");

	private final String name;

	Procedure(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
