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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScheduledPreviewFromInputBarTest extends BaseUnitTest {

	private ErrorAccumulator errorHandler;
	private ScheduledPreviewFromInputBar preview;

	@BeforeEach
	void setupPreview() {
		preview = new ScheduledPreviewFromInputBar(getKernel(),
				Integer.MAX_VALUE);
		errorHandler = new ErrorAccumulator();
	}

	@Test
	void shouldValidate() {
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
	void shouldRemovePreviousPreviewFromConstruction() {
		GeoElement r = add("R=(1,1)");
		assertEquals(0, getConstruction().getAlgoList().size());
		preview.updatePreviewFromInputBar("2R", errorHandler);
		preview.updatePreviewFromInputBar("Ri", errorHandler);
		assertEquals(1, getConstruction().getAlgoList().size());
		assertEquals(1, r.getAlgorithmList().size());
		preview.clear();
		assertEquals(0, getConstruction().getAlgoList().size());
		assertEquals(0, r.getAlgorithmList().size());
	}

	@Test
	void shouldValidateRedefinition() {
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
