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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyVecNodeTest {

	private Kernel kernel;
	
	@BeforeEach
	public void before() {
		kernel = AppCommonFactory.create().getKernel();
	}

	@Test
	public void testGiacSerialization() {
		MyVecNode point = new MyVecNode(kernel, new MyDouble(kernel, 1),
				new MyDouble(kernel, 2));
		MyVecNode vect = new MyVecNode(kernel, new MyDouble(kernel, 1),
				new MyDouble(kernel, 2));
		vect.setupCASVector();
		MyVecNode list = new MyVecNode(kernel,
				newMyList(new MyDouble(kernel, 1)),
				newMyList(new MyDouble(kernel, 2)));
		assertEquals("point(1,2)", point.toString(StringTemplate.giacTemplate));
		assertEquals("ggbvect[1,2]", vect.toString(StringTemplate.giacTemplate));
		assertEquals("zip((x,y)->point(x,y),{1},{2})",
				list.toString(StringTemplate.giacTemplate));
	}

	@Test
	public void testPolarSerialization() {
		MyVecNode point = new MyVecNode(kernel);
		point.setPolarCoords(new MyDouble(kernel, 1),
				new MyDouble(kernel, 2));
		assertEquals("(1; 2)", point.toString(StringTemplate.defaultTemplate));
		assertEquals("point((1)*exp(i*(2)))", point.toString(StringTemplate.giacTemplate));
	}

	private ExpressionValue newMyList(MyDouble myDouble) {
		MyList list = new MyList(kernel);
		list.addListElement(myDouble.wrap());
		return list;
	}

}