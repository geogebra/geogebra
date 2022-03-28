package org.geogebra.common.main;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.settings.LabelVisibility;
import org.junit.Test;

public class AppLabelModeTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void polyhedronShouldOnlyLabelPointsInAutoMode() {
		add("A=(1,0,0)");
		add("B=(1,0,1)");
		getApp().getSettings().getLabelSettings().setLabelVisibility(LabelVisibility.UseDefaults);
		add("Cube(A,B)");
		assertTrue(lookup("C").isLabelVisible());
		assertFalse(lookup("edgeFG").isLabelVisible());
		assertFalse(lookup("faceABCD").isLabelVisible());
	}

	@Test
	public void polyhedronShouldNotLabelAnythingInAlwaysOffMode() {
		add("A=(1,0,0)");
		add("B=(1,0,1)");
		getApp().getSettings().getLabelSettings().setLabelVisibility(LabelVisibility.AlwaysOff);
		add("Cube(A,B)");
		assertFalse(lookup("C").isLabelVisible());
		assertFalse(lookup("edgeFG").isLabelVisible());
		assertFalse(lookup("faceABCD").isLabelVisible());
	}
}
