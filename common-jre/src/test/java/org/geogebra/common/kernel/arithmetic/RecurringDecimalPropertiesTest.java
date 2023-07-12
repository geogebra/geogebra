package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

public class RecurringDecimalPropertiesTest extends BaseUnitTest {
	@Test
	public void testParse() {
		shouldParseAs("3.254̅", 3, 25, 4);
		shouldParseAs("1.23̅4̅5̅", 1, 2, 345);
		shouldParseAs("0.5̅1̅2̅", 0, 512);
		shouldParseAs("0.3̅", 0, 3);
	}

	private static void shouldParseAs(String representation, int integerPart,
			Integer nonRecurringPart,
			int recurringPart) {
		assertEquals(RecurringDecimalProperties.parse(representation, false),
				new RecurringDecimalProperties(integerPart, nonRecurringPart, recurringPart));
	}

	private static void shouldParseAs(String representation, int integerPart, int recurringPart) {
		assertEquals(RecurringDecimalProperties.parse(representation, false),
				new RecurringDecimalProperties(integerPart, recurringPart));
	}

	@Test
	public void testToString() {
		RecurringDecimalProperties properties =
				new RecurringDecimalProperties(1, 2, 34);
		assertEquals("1.23̅4̅", properties.toString(StringTemplate.defaultTemplate));
		assertEquals("1.2\\overline{34}", properties.toString(StringTemplate.latexTemplate));
	}
}
