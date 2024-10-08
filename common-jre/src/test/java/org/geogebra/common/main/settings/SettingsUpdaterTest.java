package org.geogebra.common.main.settings;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class SettingsUpdaterTest extends BaseUnitTest {

	@Test
	public void shouldChangeAngleUnit() {
		getApp().setGraphingConfig();
		GeoElement el = addAvInput("sin(40)");
		assertThat(el.getDefinitionForEditor(), equalTo(unicode("a=sin(40deg)")));
		getApp().setCasConfig();
		el = addAvInput("sin(40)");
		assertThat(el.getDefinitionForEditor(), equalTo(unicode("b=sin(40)")));
	}
}
