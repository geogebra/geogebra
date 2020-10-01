package org.geogebra.common.kernel.parser.function;

import org.geogebra.common.plugin.Operation;
import org.junit.Assert;
import org.junit.Test;

public class ParserFunctionsFactoryTest {

	@Test
	public void testGraphingParserFunctions() {
		ParserFunctions functions = ParserFunctionsFactory
				.createGraphingParserFunctionsFactory().createParserFunctions();
		Assert.assertEquals(functions.get("sin", 1), Operation.SIN);
		Assert.assertNull(functions.get("alt", 1));
		Assert.assertNull(functions.get("arg", 1));
	}

	@Test
	public void testDefaultParserFunctions() {
		ParserFunctions functions = ParserFunctionsFactory
				.createParserFunctionsFactory().createParserFunctions();
		Assert.assertEquals(functions.get("sin", 1), Operation.SIN);
		Assert.assertEquals(functions.get("alt", 1), Operation.ALT);
		Assert.assertEquals(functions.get("arg", 1), Operation.ARG);
	}
}
