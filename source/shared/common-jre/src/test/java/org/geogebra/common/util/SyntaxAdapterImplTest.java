package org.geogebra.common.util;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.junit.Test;

public class SyntaxAdapterImplTest {

	SyntaxAdapterImpl syntaxAdapter = new SyntaxAdapterImpl(AppCommonFactory.create().getKernel());

	@Test
	public void testConvertLaTeXtoGGB() {
		assertEquals("x^(2)", syntaxAdapter.convertLaTeXtoGGB("x^{2}"));
	}

	@Test
	public void testConvert() {
		assertEquals("x^{2}", syntaxAdapter.convert("x^{2}"));
	}
}