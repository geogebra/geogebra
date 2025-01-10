package org.geogebra.common.kernel.commands;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.junit.Test;

public class RotatePolygon3DTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void test() {
		add("A=(1,0,0)");
		add("B=(1,0,1)");
		add("Cube(A,B)");
		GeoPolygon rotated = add("Rotate(faceABCD, 15)");
		assertThat(rotated, hasValue("1"));
	}
}