package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.BaseUnitTest;
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
		line.setToStringMode(GeoLine.EQUATION_EXPLICIT);
		assertThat(line, hasValue("y = 0.01x + 11.25"));
	}

	@Test
	public void testCoefficientRoundingSmall() {
		GeoLine line = add("Line((0,11.25), (1,11.2509))");
		assertThat(line, hasValue("0x + y = 11.25"));
		line.setToStringMode(GeoLine.EQUATION_EXPLICIT);
		assertThat(line, hasValue("y = 11.25"));
	}
}