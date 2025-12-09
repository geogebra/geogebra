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

package org.geogebra.common.kernel.commands;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class ScientificCommandArgumentFilterTest extends BaseUnitTest {

	@Before
	public void setUp() {
		getApp().setScientificConfig();
	}

	@Test
	public void testNormalWithMeanStandardDeviationAndValueIsAllowed() {
		assertThat(addAvInput("Normal(2,0.5,1)"), is(notNullValue()));
	}

	@Test
	public void testNormalWithMeanStandardDeviationAndXIsFiltered() {
		assertThat(addAvInput("Normal(2,0.5,x)"), is(nullValue()));
	}

	@Test
	public void testNormalWithMeanStandardDeviationValueAndCumulativeBooleanIsAllowed() {
		assertThat(addAvInput("Normal(2,0.5,1,true)"), is(notNullValue()));
	}

	@Test
	public void testNormalWithMeanStandardDeviationValueAndCumulativeListIsFiltered() {
		assertThat(addAvInput("Normal(2,0.5,1,1...3)"), is(nullValue()));
	}

	@Test
	public void testNormalWithMeanStandardDeviationXAndCumulativeListIsFiltered() {
		assertThat(addAvInput("Normal(2,0.5,x,1...3)"), is(nullValue()));
	}

	@Test
	public void testNormalWithMeanStandardDeviationXAndCumulativeBooleanIsFiltered() {
		assertThat(addAvInput("Normal(2,0.5,x,true)"), is(nullValue()));
	}

	@Test
	public void testBinomialDistWithTrialsAndSuccessProbIsFiltered() {
		assertThat(addAvInput("BinomialDist(3, 0.2)"), is(nullValue()));
	}

	@Test
	public void testBinomialDistWithTrialsSuccessProbAndCumulativeListIsAllowed() {
		assertThat(addAvInput("BinomialDist(3, 0.2, 1...3)"), is(notNullValue()));
	}

	@Test
	public void testBinomialDistWithTrialsSuccessProbAndCumulativeBooleanIsAllowed() {
		assertThat(addAvInput("BinomialDist(3, 0.2, true)"), is(notNullValue()));
	}

	@Test
	public void testBinomialDistWithTrialsSuccessProbValueAndCumulativeListIsFiltered() {
		assertThat(addAvInput("BinomialDist(3, 0.2, 5, 1...3)"), is(nullValue()));
	}

	@Test
	public void testBinomialDistWithTrialsSuccessProbValueAndCumulativeBooleanIsAllowed() {
		assertThat(addAvInput("BinomialDist(3, 0.2, 5, true)"), is(notNullValue()));
	}

	@Test
	public void testBinomialDistWithTrialsSuccessProbXAndCumulativeBooleanIsFiltered() {
		assertThat(addAvInput("BinomialDist(3, 0.2, x, true)"), is(nullValue()));
	}
}
