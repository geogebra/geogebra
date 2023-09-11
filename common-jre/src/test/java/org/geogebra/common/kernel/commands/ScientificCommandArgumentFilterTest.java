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
