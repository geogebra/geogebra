package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class GeoPointTest  extends BaseUnitTest {

	@Test
	public void testDefinitionForEdit() {
		getApp().setGraphingConfig();
		GeoPoint point = addAvInput("A=(1,2)");
		assertThat(point.getDefinitionForEditor(), equalTo("A=(1,2)"));
	}
}
