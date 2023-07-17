package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class RecurringDecimalTest extends BaseUnitTest {

	@Test
	public void testToFraction() {
		shouldBeAsFraction("3.254\u0305", "2929 / 900");
		shouldBeAsFraction("0.3\u0305", "1 / 3");
		shouldBeAsFraction("1.3\u0305", "4 / 3");
		shouldBeAsFraction("0.5\u03051\u03052\u0305", "512 / 999");
		shouldBeAsFraction("1.2\u03053\u03054\u0305", "137 / 111");
	}

	@Test
	public void testToFractionWithLeadingZeros() {
		shouldBeAsFraction("0.03\u0305", "1 / 30");
		shouldBeAsFraction("0.0\u03053\u0305", "1 / 33");
	}

	private void shouldBeAsFraction(String input, String fraction) {
		RecurringDecimal recurringDecimal = new RecurringDecimal(getKernel(),
				RecurringDecimalModel.parse(input, false));
		assertThat(recurringDecimal.toFraction(StringTemplate.defaultTemplate),
				is(fraction));
	}

	@Test
	public void testToDouble() {
		shouldBeDouble("3.254\u0305", 3.25444444444444443);
		shouldBeDouble("0.3\u0305", 0.333333333333333333);
		shouldBeDouble("1.3\u0305", 1.333333333333333333);
		shouldBeDouble("0.5\u03051\u03052\u0305", 0.512512512512512512512);
		shouldBeDouble("1.2\u03053\u03054\u0305", 1.234234234234234234234);
		shouldBeDouble("1.2\u03053\u03054\u0305", 1.234234234234234234234);
		shouldBeDouble("1.23" + Unicode.OVERLINE + "4" + Unicode.OVERLINE, 1.2343434343434343);
	}

	private void shouldBeDouble(String input, double value) {
		RecurringDecimal recurringDecimal = new RecurringDecimal(getKernel(),
				RecurringDecimalModel.parse(input, false));
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
		assertThat(createRecurringDecimal(1, 2, 34)
						.toString(StringTemplate.defaultTemplate),
				is("1.23\u03054\u0305"));
		assertThat(createRecurringDecimal(0, null, 3)
						.toString(StringTemplate.latexTemplate),
				is("0.\\overline{3}"));
		assertThat(createRecurringDecimal(1, 2, 34)
						.toString(StringTemplate.giacTemplate),
				is("(611)/(495)"));
	}

	@Test
	public void testToStringWithLeadingZeros() {
		assertThat(parse("1.020\u03053\u0305").toString(StringTemplate.defaultTemplate),
				is("1.020\u03053\u0305"));
		assertThat(parse("1.000020\u03050\u03050\u03053\u0305")
						.toString(StringTemplate.defaultTemplate),
				is("1.000020\u03050\u03050\u03053\u0305"));
		assertThat(parse("1.03040\u03055\u0305")
				.toString(StringTemplate.latexTemplate),
				is("1.0304\\overline{05}"));
	}

	private RecurringDecimal parse(String str) {
		return RecurringDecimal.parse(getKernel(), getLocalization(), str, false);
	}

	private RecurringDecimal createRecurringDecimal(int integerPart, Integer nonRecurringPart,
			int recurringPart) {
		return new RecurringDecimal(getKernel(), new RecurringDecimalModel(integerPart,
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

	private void textShouldBe(String command, String value) {
		GeoText text = add(command);
		assertThat(text.toValueString(StringTemplate.defaultTemplate), is(value));
	}
}
