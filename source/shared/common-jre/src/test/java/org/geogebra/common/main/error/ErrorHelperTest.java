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

package org.geogebra.common.main.error;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.Test;

public class ErrorHelperTest extends BaseUnitTest {

	@Test
	public void functionShouldTakePrecedence() {
		ErrorAccumulator accumulator = new ErrorAccumulator();
		accumulator.setCurrentCommand("nPr");
		ErrorHelper.handleException(new IllegalStateException("generic"), getApp(), accumulator);
		assertThat(accumulator.getErrors(), equalTo("Please check your input"));
	}

	@Test
	public void shouldIncludeCommand() {
		ErrorAccumulator accumulator = new ErrorAccumulator();
		accumulator.setCurrentCommand("Midpoint");
		ErrorHelper.handleException(new IllegalStateException("generic"), getApp(), accumulator);
		assertThat(accumulator.getErrors(), containsString("Syntax:\nMidpoint"));
	}
}
