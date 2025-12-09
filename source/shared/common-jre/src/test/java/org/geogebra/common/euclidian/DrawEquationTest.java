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

package org.geogebra.common.euclidian;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.geogebra.common.io.FactoryProviderCommon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class DrawEquationTest {

	@Test
	public void testCustomColor() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		String latex = "\\definecolor{octarineD}{RGB}{42,42,42}";
		assertNotNull(new TeXFormula(latex).createTeXIcon(TeXConstants.STYLE_DISPLAY, 12));
	}

	@ParameterizedTest
	@CsvSource(value = {
			"\\definecolor{octarineD}{RGB}{42,42,42;Expect a '}'",
			"\\definecolor{octarineD}{RGB}{42,42,};Expect a positive integer",
			"\\definecolor{octarineD}{RGB}{42,42};Expect 3 numbers",
			"\\definecolor{octarineD}{rgb}{.42,.42,.42;Expect a '}'",
			"\\definecolor{octarineD}{rgb}{.42,.42,x};Invalid character 'x' in list of numbers",
			"\\definecolor{octarineD}{rgb}{.42,.42};Expect 3 numbers",
			"\\definecolor{octarineD}{rgba}{.42,.42};Expect 4 numbers"
	}, delimiter = ';')
	public void testCustomColorWrong(String input, String exception) {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		Exception ex = assertThrows(Exception.class, () -> new TeXFormula(input));
		Assertions.assertTrue(ex.getMessage().contains(exception), ex.getMessage());
	}
}
