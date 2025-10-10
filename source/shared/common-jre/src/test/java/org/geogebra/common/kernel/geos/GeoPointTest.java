package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.PathRegionHandling;
import org.geogebra.common.main.settings.CoordinatesFormat;
import org.junit.Test;

public class GeoPointTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void testDefinitionForEditor() {
		getApp().setGraphingConfig();
		GeoPoint point = addAvInput("A=(1,2)");
		assertThat(point.getDefinitionForEditor(), equalTo("A=$point(1,2)"));
	}

	@Test
	public void testDefinitionForEditorAustrianCoords() {
		getSettings().getGeneral().setCoordFormat(CoordinatesFormat.COORD_FORMAT_AUSTRIAN);
		GeoPoint point = getKernel().getAlgoDispatcher().point(1, 2, false);
		assertThat(point.getDefinitionForEditor(), endsWith("(1,2)"));
	}

	@Test
	public void testMovedPointPrintsCorrectAmountOfDecimals1() {
		getKernel().setPrintDecimals(5);
		GeoPoint point = getKernel().getAlgoDispatcher().point(1.23456, 2, false);
		assertThat(GeoPoint.pointMovedAural(getLocalization(), point), containsString("1.23456 "));
	}

	@Test
	public void testMovedPointPrintsCorrectAmountOfDecimals2() {
		getKernel().setPrintDecimals(3);
		GeoPoint point = getKernel().getAlgoDispatcher().point(1.23456, 2, false);
		assertThat(GeoPoint.pointMovedAural(getLocalization(), point), containsString("1.235 "));
	}

	@Test
	public void testMovedPointPrintsCorrectAmountOfDecimals3() {
		getKernel().setPrintDecimals(4);
		GeoPoint point = getKernel().getAlgoDispatcher().point(1.23456, 2, false);
		assertThat(GeoPoint.pointMovedAural(getLocalization(), point), containsString("1.2346 "));
	}

	@Test
	public void testMoveGrid() {
		add("grid=Sequence(Sequence[(i, j), i, 0, 10],j,0,10)");
		GeoPoint pt = add("A=Point(grid)");
		pt.setCoords(4, 4, 1);

		assertEquals(4, pt.getX(), 0);
		getKernel().updateConstruction();
		assertEquals(4, pt.getX(), 0);
		getKernel().updateConstruction();
		assertEquals(4, pt.getX(), 0);
	}
}
