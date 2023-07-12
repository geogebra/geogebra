package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RecurringDecimalPropertiesTest {
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
}
