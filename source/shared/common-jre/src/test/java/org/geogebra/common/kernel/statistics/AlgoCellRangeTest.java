package org.geogebra.common.kernel.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlgoCellRangeTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testLaTeX() {
		assertEquals("A1\\mathpunct{:}A2", evaluateGeoElement("A1:A2")
				.toLaTeXString(true, StringTemplate.latexTemplate));
	}
}
