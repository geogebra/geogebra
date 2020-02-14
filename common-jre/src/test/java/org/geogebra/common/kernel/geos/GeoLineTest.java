package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class GeoLineTest extends BaseUnitTest {

	@Test
	public void toOutputValueString() {
		getApp().setGraphingConfig();
		GeoElement line = addAvInput("Line((0,0),(1,1))");
		DescriptionMode descriptionMode = line.getDescriptionMode();
		assertThat(descriptionMode, equalTo(DescriptionMode.DEFINITION));
	}
}