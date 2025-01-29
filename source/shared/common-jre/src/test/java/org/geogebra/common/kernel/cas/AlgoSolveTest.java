package org.geogebra.common.kernel.cas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.cas.MockCASGiac;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Before;
import org.junit.Test;

public class AlgoSolveTest extends BaseUnitTest {
	private MockCASGiac giac;

	@Before
	public void setupCas() {
		giac = new MockCASGiac(getApp());
	}

	@Test
	public void shouldNotSendTooManyZerosToCas() {
		giac.memorizeWithCheck("{x=165.0}", input -> !input.contains("00"));
		add("f(x)=5.4+sin(pi/180*(x-75))");
		GeoElementND solve = add("NSolve(f(x)=17.4)");
		assertEquals("{x = 165}", solve.toValueString(StringTemplate.defaultTemplate));
	}
}