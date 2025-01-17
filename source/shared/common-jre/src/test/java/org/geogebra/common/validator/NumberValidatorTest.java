package org.geogebra.common.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.validator.NumberValidator;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;
import org.junit.Before;
import org.junit.Test;

public class NumberValidatorTest extends BaseUnitTest {

	private NumberValidator numberValidator;

	@Before
	public void setupNumberValidatorTest() {
		numberValidator = new NumberValidator(getKernel().getAlgebraProcessor());
	}

	@Test
	public void testExceptionThrowing() {
		assertThrows(NumberFormatException.class,
				() -> numberValidator.getDouble("a", null));

		assertThrows(NumberValueOutOfBoundsException.class,
				() -> numberValidator.getDouble("-1", 0.0));
	}

	@Test
	public void testConversion() {
		assertEquals(
				-1.0, numberValidator.getDouble("-1", -2.0), DELTA);
	}
}
