package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.arithmetic.RecurringDecimalModel.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class RecurringDecimalModelTest extends BaseUnitTest {
	@Test
	public void testParse() {
		shouldParseAs("3.25", "4", 3, 25, 4);
		shouldParseAs("1.2", "345", 1, 2, 345);
		shouldParseAs("0.", "512", 0, 512);
		shouldParseAs("0.", "3", 0, 3);
		shouldParseAs("24.", "3", 24, 3);
	}

	@Test(expected = NumberFormatException.class)
	public void testInvalidFormat() {
		parse("3.2̅", "412");
	}

	@Test(expected = NumberFormatException.class)
	public void testIntegerInvalid() {
		parse("x.25", "412");
	}

	@Test(expected = NumberFormatException.class)
	public void testDotMissing() {
		parse("325", "412");
	}

	@Test(expected = NumberFormatException.class)
	public void testNonRecurringInvalid() {
		parse("0.wasd", "123");
	}

	@Test(expected = NumberFormatException.class)
	public void testRecurringPartInvalid() {
		parse("1.2", "3.4");
	}

	private static void shouldParseAs(String representation, String recurring, int integerPart,
			Integer nonRecurringPart,
			int recurringPart) {
		assertThat(parse(representation, recurring),
				new RecurringDecimalModelMatcher(integerPart, nonRecurringPart, recurringPart));
	}

	private static void shouldParseAs(String representation, String recurring,
			int integerPart, int recurringPart) {
		assertThat(parse(representation, recurring),
				new RecurringDecimalModelMatcher(integerPart, null, recurringPart));
	}

	private static class RecurringDecimalModelMatcher
			extends TypeSafeMatcher<RecurringDecimalModel> {

		private final int integerPart;
		private final Object nonrecurring;
		private final int recurring;

		public RecurringDecimalModelMatcher(int integerPart, Object nonrecurring,
				int recurringPart) {
			this.integerPart = integerPart;
			this.nonrecurring = nonrecurring;
			this.recurring = recurringPart;
		}

		@Override
		protected boolean matchesSafely(RecurringDecimalModel item) {
			return item.integerPart == integerPart
					&& Objects.equals(item.nonRecurring.value, nonrecurring)
					&& item.recurring.value == recurring;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(integerPart + "." + nonrecurring + "[" + recurring + "]");
		}
	}

	@Test
	public void testToString() {
		RecurringDecimalModel model = newModel(1, "2", "34");
		assertEquals("1.23̅4̅", model.toString(StringTemplate.defaultTemplate));
		assertEquals("1.2\\overline{34}", model.toString(StringTemplate.latexTemplate));
	}

	/**
	 * @param integerPart integer part
	 * @param nonrecurring nonrecurring decimal part
	 * @param recurring recurring decimal part
	 * @return recurring decimal model
	 */
	public static RecurringDecimalModel newModel(int integerPart,
			String nonrecurring, String recurring) {
		return new RecurringDecimalModel(integerPart,
				nonrecurring == null ? new DecimalPart() : new DecimalPart(nonrecurring),
				new DecimalPart(recurring));
	}

}
