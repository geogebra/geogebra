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

package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

public class MySpecialDoubleTest extends BaseUnitTest {

	@Test
	public void testUnaryMinus() {
		MySpecialDouble value = new MySpecialDouble(getKernel(), 0, "1E0");
		ExpressionValue negatedValue = value.unaryMinus(getKernel());
		assertFalse(negatedValue instanceof ExpressionNode);
	}

	@Test
	public void toFractionForNumbers() {
		assertEquals(Arrays.asList("12", "10"), create("1.2"));
		assertEquals(Arrays.asList("5", "10"), create("0.5"));
		assertEquals(Arrays.asList("-5", "100"), create("-0.05"));
	}

	@Test
	public void toFractionForENotation() {
		assertEquals(Arrays.asList("12", "100"), create("1.2E-1"));
		assertEquals(Arrays.asList("12", "1000000"), create("1.2E-5"));
		assertEquals(Arrays.asList("5000", "10"), create("0.5E3"));
	}

	@Test
	public void toFractionForPercentages() {
		assertEquals(Arrays.asList("70", "1000"), percentage("7.0"));
		assertEquals(Arrays.asList("7000", "1000"), percentage("700.0"));
		assertEquals(Arrays.asList("7", "1000"), percentage("0.7"));
		assertEquals(Arrays.asList("7", "100"), percentage("7"));
	}

	private List<String> create(String val) {
		return create(val, Double.parseDouble(val));
	}

	private List<String> percentage(String val) {
		return create(val + "%", Double.parseDouble(val) * .01);
	}

	private List<String> create(String val, double value) {
		ExpressionValue[] fraction = new ExpressionValue[2];
		new MySpecialDouble(getKernel(), value,
				val).asFraction(fraction);
		return Arrays.stream(fraction)
				.map(el -> el == null ? null : el.toString(StringTemplate.latexTemplate))
				.collect(Collectors.toList());
	}
}
