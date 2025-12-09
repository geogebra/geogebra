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

package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class RecurringDecimalTest extends BaseUnitTest {

	@Test
	public void testToFraction() {
		shouldBeAsFraction("3.25", "4", "2929 / 900");
		shouldBeAsFraction("0.", "3", "1 / 3");
		shouldBeAsFraction("1.", "3", "4 / 3");
		shouldBeAsFraction("0.", "512", "512 / 999");
		shouldBeAsFraction("1.", "234", "137 / 111");
	}

	@Test
	public void testToFractionWithLeadingZeros() {
		shouldBeAsFraction("0.0", "3", "1 / 30");
		shouldBeAsFraction("0.", "03", "1 / 33");
	}

	private void shouldBeAsFraction(String preperiod, String recurring, String fraction) {
		RecurringDecimal recurringDecimal = RecurringDecimal.parse(getKernel(),
				preperiod, recurring);
		assertThat(recurringDecimal.toFraction(StringTemplate.defaultTemplate),
				is(fraction));
	}

	@Test
	public void testToDouble() {
		shouldBeDouble("3.25", "4", 3.25444444444444443);
		shouldBeDouble("0.", "3", 0.333333333333333333);
		shouldBeDouble("1.", "3", 1.333333333333333333);
		shouldBeDouble("0.", "512", 0.512512512512512512512);
		shouldBeDouble("1.", "234", 1.234234234234234234234);
		shouldBeDouble(".", "234", 0.234234234234234234234);
		shouldBeDouble("1.2", "34", 1.2343434343434343);
	}

	private void shouldBeDouble(String preperiod, String recurring, double value) {
		RecurringDecimal recurringDecimal = RecurringDecimal.parse(getKernel(),
				preperiod, recurring);
		assertThat(recurringDecimal.toDouble(), is(value));
	}

	@Test
	public void testSymbolicOutputOfRecurringNumber() {
		GeoNumeric rd = add("1.2\u03053\u03054\u0305");
		rd.setSymbolicMode(true, true);
		assertThat(rd.toOutputValueString(StringTemplate.algebraTemplate), is("137 / 111"));
	}

	@Test
	public void testToString() {
		assertThat(createRecurringDecimal(1, "2", "34")
						.toString(StringTemplate.defaultTemplate),
				is("1.23\u03054\u0305"));
		assertThat(createRecurringDecimal(0, null, "3")
						.toString(StringTemplate.latexTemplate),
				is("0.\\overline{3}"));
		assertThat(createRecurringDecimal(1, "2", "34")
						.toString(StringTemplate.giacTemplate),
				is("(611)/(495)"));
	}

	@Test
	public void testToStringWithLeadingZeros() {
		assertThat(parse("1.02", "03").toString(StringTemplate.defaultTemplate),
				is("1.020\u03053\u0305"));
		assertThat(parse("1.00002", "0003")
						.toString(StringTemplate.defaultTemplate),
				is("1.000020\u03050\u03050\u03053\u0305"));
		assertThat(parse("1.0304", "05")
				.toString(StringTemplate.latexTemplate),
				is("1.0304\\overline{05}"));
	}

	private RecurringDecimal parse(String preperiod, String recurring) {
		return RecurringDecimal.parse(getKernel(), preperiod, recurring);
	}

	private RecurringDecimal createRecurringDecimal(int integerPart, String nonRecurringPart,
			String recurringPart) {
		return new RecurringDecimal(getKernel(), RecurringDecimalModelTest.newModel(integerPart,
				nonRecurringPart, recurringPart));
	}

	@Test
	public void testFormulaTextNonSymbolic() {
		String recurringString = "1.23\u03054\u0305";
		GeoNumeric a = add("a = " + recurringString);
		getKernel().setPrintDecimals(7);
		a.setSymbolicMode(false, true);
		String decimalString = "1.2343434";
		textShouldBe("a + \"\"", decimalString);
		textShouldBe("Text(a,true)", decimalString);
		textShouldBe("FormulaText(a,true)", decimalString);
		textShouldBe("Text(a,false)", recurringString);
		textShouldBe("FormulaText(a,false)", "1.2\\overline{34}");
	}

	@Test
	public void testAsRecurringDecimal() {
		assertThat(this.<GeoNumeric>add("1.02\u03053\u0305").asRecurringDecimal().getModel(),
				CoreMatchers.is(RecurringDecimal.parse(getKernel(), "1.0", "23").getModel()));
	}

	private void textShouldBe(String command, String value) {
		GeoText text = add(command);
		assertThat(text.toValueString(StringTemplate.defaultTemplate), is(value));
	}
}
