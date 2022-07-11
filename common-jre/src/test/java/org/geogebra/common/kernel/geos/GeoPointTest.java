package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.settings.CoordinatesFormat;
import org.junit.Test;

public class GeoPointTest  extends BaseUnitTest {

	@Test
	public void testDefinitionForEditor() {
		getApp().setGraphingConfig();
		GeoPoint point = addAvInput("A=(1,2)");
		assertThat(point.getDefinitionForEditor(), equalTo("A=(1,2)"));
	}

	@Test
	public void testDefinitionForEditorAustrianCoords() {
		getSettings().getGeneral().setCoordFormat(CoordinatesFormat.COORD_FORMAT_AUSTRIAN);
		GeoPoint point = getKernel().getAlgoDispatcher().point(1, 2, false);
		assertThat(point.getDefinitionForEditor(), endsWith("(1,2)"));
	}
}
