package org.geogebra.common.kernel.statistics;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class AlgoFitLineYTest extends BaseUnitTest {

	@Test
	public void testOutput() {
		getApp().setGraphingConfig();
		GeoElement fitLine = addAvInput("FitLine((0,0),(1,1),(2,2))");
		String outputString = fitLine.toOutputValueString(StringTemplate.editorTemplate);
		assertThat(outputString, equalTo("x - y = 0"));
	}
}