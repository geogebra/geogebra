package org.geogebra.web.html5.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScientificFormatTest {

	@Test
	public void testPrettyPrint() {
		ScientificFormat sf = new ScientificFormat(5, 10, false);
		assertEquals("1.0000", sf.prettyPrint("1.0000e+0"));
		assertEquals("10.000", sf.prettyPrint("1.0000e+1"));
		assertEquals(".10000", sf.prettyPrint("1.0000e-1"));
		assertEquals("-1.0000", sf.prettyPrint("-1.0000e+0"));
		assertEquals("-10.000", sf.prettyPrint("-1.0000e+1"));
		assertEquals("-.10000", sf.prettyPrint("-1.0000e-1"));
		assertEquals("1.2345", sf.prettyPrint("1.2345e+0"));
		assertEquals("1.0000E30", sf.prettyPrint("1.0000e+30"));
		assertEquals("1.0000E-30", sf.prettyPrint("1.0000e-30"));
		assertEquals("-1.0000E30", sf.prettyPrint("-1.0000e+30"));
		assertEquals("-1.0000E-30", sf.prettyPrint("-1.0000e-30"));
		assertEquals("0.0000", sf.prettyPrint("0.0000e+0"));
	}
}
