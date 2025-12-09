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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class GeoVectorTest extends BaseUnitTest {

	@Test
	public void testEditorDefinitionForIndependent() {
		GeoVector vector = addAvInput("v = (1, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.editorTemplate),
				is("$vector(1,2)"));
	}

	@Test
	public void testLatexDefinitionForIndependent() {
		GeoVector vector = addAvInput("v = (1, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.latexTemplate),
				is("\\left( \\begin{align}1 \\\\ 2 \\end{align} \\right)"));
	}

	@Test
	public void testEditorDefinitionForDependent() {
		addAvInput("a = 1");
		GeoVector vector = addAvInput("v = (a, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.editorTemplate),
				is("$vector(a,2)"));
	}

	@Test
	public void testLatexDefinitionForDependent() {
		addAvInput("a = 1");
		GeoVector vector = addAvInput("v = (a, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.latexTemplate),
				is("\\left( \\begin{align}a \\\\ 2 \\end{align} \\right)"));
	}

	@Test
	public void testDefaultHeadShouldNotAppearInXml() {
		GeoVector vector = addAvInput("v = (1, 2)");
		assertThat(vector.getXML(), not(containsString("\t<headStyle")));
	}

	@Test
	public void testGetXmlWithArrowHead() {
		GeoVector vector = addAvInput("v = (1, 2)");
		vector.setHeadStyle(VectorHeadStyle.ARROW);
		assertThat(vector.getXML(), containsString("\t<headStyle val=\"1\"/>"));
	}

	@Test
	@Issue("APPS-1470")
	public void elementShouldNotBeConvertedToVectorIfLocalVariableExists() {
		addAvInput("l1 = {(1, 2), (3, 4)}");
		addAvInput("l2 = {(0, 2), (1, 0)}");
		addAvInput("l3 = Zip(Vector(aa, bb), aa, l1, bb, l2)");
		GeoElement list = addAvInput("KeepIf(Length(vv) < 2,vv,l3)");

		String definition = list.getDefinition(StringTemplate.defaultTemplate);
		assertFalse(definition.contains("Vector(vv)"));
	}
}
