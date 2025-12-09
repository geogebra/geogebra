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
 
package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.Before;
import org.junit.Test;

public class ScheduledPreviewFromInputBarTest extends BaseUnitTest {

	private ErrorAccumulator errorHandler;
	private ScheduledPreviewFromInputBar preview;

	@Before
	public void setupPreview() {
		preview = new ScheduledPreviewFromInputBar(getKernel(),
				Integer.MAX_VALUE);
		errorHandler = new ErrorAccumulator();
	}

	@Test
	public void shouldValidate() {
		preview.updatePreviewFromInputBar("a=1", errorHandler);
		assertEquals("", errorHandler.getErrors());
		preview.updatePreviewFromInputBar("a=", errorHandler);
		assertEquals("Please check your input", errorHandler.getErrors());
		preview.updatePreviewFromInputBar("a=2", errorHandler);
		assertEquals("", errorHandler.getErrorsSinceReset());
		preview.updatePreviewFromInputBar("a=1/(1,1,1)", errorHandler);
		assertEquals("Illegal division \n"
				+ "1 / (1, 1, 1) ", errorHandler.getErrorsSinceReset());
	}

	@Test
	public void shouldValidateRedefinition() {
		add("a=2");
		preview.updatePreviewFromInputBar("a=1", errorHandler);
		assertEquals("", errorHandler.getErrors());
		preview.updatePreviewFromInputBar("a=1+", errorHandler);
		assertEquals("Please check your input", errorHandler.getErrors());
		preview.updatePreviewFromInputBar("a=1/(1,1,1)", errorHandler);
		// TODO with APPS-76 we should notice the invalid syntax
		assertEquals("", errorHandler.getErrorsSinceReset());
	}
}
