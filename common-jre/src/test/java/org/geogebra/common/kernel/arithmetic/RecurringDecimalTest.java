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
		shouldBeAsFraction("3.254\u0305", "2929/900");
		shouldBeAsFraction("0.3\u0305", "3/9");
		shouldBeAsFraction("1.3\u0305", "12/9");
		shouldBeAsFraction("0.5\u03051\u03052\u0305", "512/999");
		shouldBeAsFraction("1.2\u03053\u03054\u0305", "1233/999");
	}

	private void shouldBeAsFraction(String input, String fraction) {
		RecurringDecimal recurringDecimal = new RecurringDecimal(getKernel(),
				RecurringDecimalProperties.parse(input, false));
		assertThat(recurringDecimal.toFractionSting(), is(fraction));
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
				RecurringDecimalProperties.parse(input, false));
		assertThat(recurringDecimal.toDouble(), is(value));
	}

	@Test
	public void testNumerator() {
		assertThat(RecurringDecimal.numerator(3, 25, 4), is(2929));
		assertThat(RecurringDecimal.numerator(0, 0, 512), is(512));
		assertThat(RecurringDecimal.numerator(1, 0, 91), is(1081));
		assertThat(RecurringDecimal.numerator(0, 3, 789), is(3786));
		assertThat(RecurringDecimal.numerator(1, 2, 34), is(1234 - 12));
		assertThat(RecurringDecimal.numerator(1, null, 3), is(12));
	}

	@Test
	public void testDenominator() {
		assertThat(RecurringDecimal.denominator(1, 0), is(9));
		assertThat(RecurringDecimal.denominator(2, 0), is(99));
		assertThat(RecurringDecimal.denominator(5, 0), is(99999));
		assertThat(RecurringDecimal.denominator(1, 1), is(90));
		assertThat(RecurringDecimal.denominator(1, 2), is(900));
		assertThat(RecurringDecimal.denominator(1, 5), is(900000));
		assertThat(RecurringDecimal.denominator(2, 5), is(9900000));
		assertThat(RecurringDecimal.denominator(0, 3), is(1000));
	}

	@Test
	public void testSymbolicOutputOfRecurringNumber() {
		GeoNumeric rd = add("1.2\u03053\u03054\u0305");
		rd.setSymbolicMode(true, true);
		assertThat(rd.toOutputValueString(StringTemplate.algebraTemplate), is("137 / 111"));
	}

	@Test
	public void testToString() {
		assertThat(createRecurringDecimal(1, 2, 34).toString(),
				is("1.23\u03054\u0305"));
	}

	private RecurringDecimal createRecurringDecimal(int i, int nr, int r) {
		return new RecurringDecimal(getKernel(), new RecurringDecimalProperties(i, nr, r));
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
