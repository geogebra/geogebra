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

package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class NiedersachsenExamTests extends BaseExamTestSetup {
	@BeforeEach
	public void setupExam() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.startExam(ExamType.NIEDERSACHSEN, null);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"CSolve(x^2 = 0)",
			"CSolutions(x^2 = 0)",
	})
	public void testRestrictedCommands(String command) {
		assertNull(evaluate(command));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Solve(x^2 = 0)",
			"Solutions(x^2 = 0)",
			"NSolve(x^2 = 0)",
			"NSolutions(x^2 = 0)",
	})
	public void testUnrestrictedCommands(String command) {
		assertNotNull(evaluate(command));
	}
}
