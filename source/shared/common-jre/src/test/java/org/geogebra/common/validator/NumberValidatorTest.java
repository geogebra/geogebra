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
