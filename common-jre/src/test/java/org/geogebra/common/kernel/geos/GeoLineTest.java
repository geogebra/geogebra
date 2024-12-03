package org.geogebra.common.kernel.geos;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class GeoLineTest extends BaseUnitTest {

	@Test
	public void getDescriptionMode() {
		getApp().setGraphingConfig();
		GeoLine line = addAvInput("Line((0,0),(1,1))");
		assertThat(line.getDescriptionMode(), equalTo(DescriptionMode.DEFINITION_VALUE));
	}

	@Test
	public void testCopiedLineDescriptionMode() {
		getApp().setGraphingConfig();
		addAvInput("f: Line((0,0),(1,1))");
		GeoLine line = addAvInput("f");
		assertThat(line.getDescriptionMode(), equalTo(DescriptionMode.DEFINITION_VALUE));
	}

	@Test
	public void testCoefficientRounding() {
		GeoLine line = add("Line((0,11.25), (1,11.259))");
		assertThat(line, hasValue("-0.01x + y = 11.25"));
		line.setEquationForm(LinearEquationRepresentable.Form.EXPLICIT);
		assertThat(line, hasValue("y = 0.01x + 11.25"));
	}

	@Test
	public void testCoefficientRoundingSmall() {
		GeoLine line = add("Line((0,11.25), (1,11.2509))");
		assertThat(line, hasValue("0x + y = 11.25"));
		line.setEquationForm(LinearEquationRepresentable.Form.EXPLICIT);
		assertThat(line, hasValue("y = 11.25"));
	}

	@Test
	public void noAmbiguousLabelInXml() {
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
	public void pointsAtInfinityShouldReload() {
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
	public void pointAtInfinityShouldKeepDirection() {
		add("d:y=2+x*sqrt(3)");
		assertThat(add("D_0:Point(d,0)"), hasValue("?"));
		assertThat(add("D_1:Point(d,1)"), hasValue("?"));
		assertThat(add("Angle(D_0)/deg"), hasValue(unicode("240")));
		assertThat(add("Angle(D_1)/deg"), hasValue(unicode("60")));
	}
}