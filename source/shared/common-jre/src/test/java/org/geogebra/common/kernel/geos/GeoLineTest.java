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

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class GeoLineTest extends BaseUnitTest {

	@Test
	void getDescriptionMode() {
		getApp().setGraphingConfig();
		GeoLine line = addAvInput("Line((0,0),(1,1))");
		assertThat(line.getDescriptionMode(), equalTo(DescriptionMode.DEFINITION_VALUE));
	}

	@Test
	void testCopiedLineDescriptionMode() {
		getApp().setGraphingConfig();
		addAvInput("f: Line((0,0),(1,1))");
		GeoLine line = addAvInput("f");
		assertThat(line.getDescriptionMode(), equalTo(DescriptionMode.DEFINITION_VALUE));
	}

	@Test
	void testCoefficientRounding() {
		GeoLine line = add("Line((0,11.25), (1,11.259))");
		assertThat(line, hasValue("-0.01x + y = 11.25"));
		line.setEquationForm(LinearEquationRepresentable.Form.EXPLICIT);
		assertThat(line, hasValue("y = 0.01x + 11.25"));
	}

	@Test
	void testCoefficientRoundingSmall() {
		GeoLine line = add("Line((0,11.25), (1,11.2509))");
		assertThat(line, hasValue("0x + y = 11.25"));
		line.setEquationForm(LinearEquationRepresentable.Form.EXPLICIT);
		assertThat(line, hasValue("y = 11.25"));
	}

	@Test
	void noAmbiguousLabelInXml() {
		add("e1(x,y)=x+y");
		GeoLine line = add("e1+7=0");
		assertThat(line.getLabelSimple(), equalTo("eq1"));
		assertThat(getApp().getXML(),
				containsString("exp=\"eq1:=e1 + 7 = 0\" "));
		reload();
		assertThat(lookup("eq1"), instanceOf(GeoLine.class));
	}

	@Test
	@Issue("APPS-5567")
	void pointsAtInfinityShouldReload() {
		add("h:y=3");
		add("v:x=4");
		add("H_0=Point(h,0)");
		add("H_1=Point(h,1)");
		add("V_0=Point(v,0)");
		add("V_1=Point(v,1)");
		assertThat(lookup("H_0"), hasValue(unicode("(-@inf, 3)")));
		assertThat(lookup("H_1"), hasValue(unicode("(@inf, 3)")));
		assertThat(lookup("V_0"), hasValue(unicode("(4, @inf)")));
		assertThat(lookup("V_1"), hasValue(unicode("(4, -@inf)")));
		reload();
		assertThat(lookup("H_0"), hasValue(unicode("(-@inf, 3)")));
		assertThat(lookup("H_1"), hasValue(unicode("(@inf, 3)")));
		assertThat(lookup("V_0"), hasValue(unicode("(4, @inf)")));
		assertThat(lookup("V_1"), hasValue(unicode("(4, -@inf)")));
	}

	@Test
	@Issue("APPS-5567")
	void pointAtInfinityShouldKeepDirection() {
		add("d:y=2+x*sqrt(3)");
		assertThat(add("D_0:Point(d,0)"), hasValue("?"));
		assertThat(add("D_1:Point(d,1)"), hasValue("?"));
		assertThat(add("Angle(D_0)/deg"), hasValue(unicode("240")));
		assertThat(add("Angle(D_1)/deg"), hasValue(unicode("60")));
	}

	@Test
	void assignmentInLaTeXShouldHaveOnlyOneSpace() {
		assertEquals("f\\mathpunct{:}\\,y\\, = \\,x",
				add("y=x").toString(StringTemplate.latexTemplate));
		assertEquals("g\\mathpunct{:}\\,y\\, = \\,x",
				add("y=x").getLaTeXAlgebraDescription(false, StringTemplate.latexTemplate));
	}
}
