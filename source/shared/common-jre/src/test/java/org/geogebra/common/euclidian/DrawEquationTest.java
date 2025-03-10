package org.geogebra.common.euclidian;

import static org.junit.Assert.assertTrue;
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
