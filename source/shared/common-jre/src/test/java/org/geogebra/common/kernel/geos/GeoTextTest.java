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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class GeoTextTest extends BaseUnitTest {

	@Test
	public void definitionForEditorShouldBeTheStringItself() {
		String value = "GeoGebra rocks";
		GeoText text = new GeoText(getConstruction(), value);
		assertEquals(value, text.getDefinitionForEditor());
	}

	@Test
	@Issue("APPS-6352")
	public void undefinedTextToLaTeX() {
		GeoText text = new GeoText(getConstruction());
		text.setUndefined();
		assertEquals("", text.toLaTeXString(false, StringTemplate.latexTemplate));
	}

	@Test
	public void copyAbsLocFlag() {
		GeoText text = new GeoText(getConstruction(), "text");
		text.setAbsoluteScreenLocActive(true);
		GeoText textCopy = new GeoText(getConstruction(), "textCopy");
		textCopy.setAllVisualPropertiesExceptEuclidianVisible(text, true, true);
		assertTrue(textCopy.isAbsoluteScreenLocActive());
	}

	@Test
	public void alignmentForUndefinedText() {
		GeoText text = add("Text(If(false,7),(1,1),false,false,0,0)");
		assertThat(getDrawable(text), notNullValue());
	}

	@Test
	@Issue("APPS-5450")
	public void shouldBeFixable() {
		GeoText text = add("Text(\"T\",(1,2))");
		assertThat("All texts should be fixable", text.isFixable());
	}
}
