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

package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class GeoFunctionNVarTest extends BaseUnitTest {

	@Test
	public void testNVarInequalityPreserved() {
		addAvInput("a: x < y");
		addAvInput("SetValue(a, ?)");
		getApp().setXML(getApp().getXML(), true);
		assertEquals("a: ?", lookup("a").toString(StringTemplate.defaultTemplate));
	}

	@Test
	public void shouldShowAsUndefinedOnReload() {
		addAvInput("user(x,y)=x+y");
		addAvInput("def=IsDefined(user)");
		addAvInput("SetValue(user,?)");
		getApp().setXML(getApp().getXML(), true);
		assertEquals("def = false", lookup("def").toString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-5759")
	public void malformedFunctionCallShouldNotCrash() {
		add("eq1:x+y");
		GeoElement element = add("eq1(x=5 x^(2)-4 y^(2)=5)");
		assertNotNull(element.toOutputValueString(StringTemplate.testTemplate));
	}
}
