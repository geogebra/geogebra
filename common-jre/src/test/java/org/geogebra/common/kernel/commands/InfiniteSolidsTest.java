package org.geogebra.common.kernel.commands;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Locale;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class InfiniteSolidsTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void shouldBeLocalized() {
		GeoElement cone = add("InfiniteCone[(1,1),(1,1,2),45deg]");
		GeoElement cylinder = add("InfiniteCylinder[(1,1),(1,1,2),4]");
		getApp().setLocale(new Locale("de"));
		assertThat(cylinder.getDefinitionForEditor(), containsString("Rotationszylinder"));
		assertThat(cone.getDefinitionForEditor(), containsString("Rotationskegel"));
	}
}
