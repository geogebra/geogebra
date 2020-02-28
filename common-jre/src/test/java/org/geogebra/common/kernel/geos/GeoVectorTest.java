package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class GeoVectorTest extends BaseUnitTest {

	@Test
	public void testDefinitionForEdit() {
		getApp().setGraphingConfig();
		GeoVector vector = addAvInput("u=(1,2)");
		assertThat(vector.getDefinitionForEditor(), equalTo("u = {{1},{2}}"));
	}

	@Test
	public void testDefinitionForDisplay() {
		getApp().setGraphingConfig();
		GeoVector vector = addAvInput("u=(1,2)");
		assertThat(vector.getDefinitionForInputBar(), equalTo("u = {{1},{2}}"));
	}
}
