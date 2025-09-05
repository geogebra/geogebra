package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.DefaultPropertiesFactoryTest.getNames;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.junit.jupiter.api.Test;

public class G3DPropertiesFactoryTest {

	@Test
	public void testPropertiesSuite3D() {
		AppCommon app = AppCommonFactory.create3D(new AppConfigGraphing3D());
		app.setActiveView(AppCommon.VIEW_EUCLIDIAN);
		List<PropertiesArray> props = new G3DPropertiesFactory().createProperties(
				app, app.getLocalization(), null);
		assertEquals(3, props.size());
		assertEquals(List.of("Grid", "Axes", "Dimension", "xAxis", "yAxis", "zAxis", "Projection",
						"Advanced"),
				getNames(props.get(2)));
	}
}
