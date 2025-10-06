package org.geogebra.common.properties.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DefaultPropertiesFactoryTest {

	@BeforeAll
	public static void setupFlag() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
	}

	@AfterAll
	public static void removeFlag() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@Test
	public void testPropertiesGraphingWeb() {
		AppCommon graphingApp = AppCommonFactory.create(new AppConfigGraphing());
		graphingApp.setPlatform(GeoGebraConstants.Platform.WEB);
		List<PropertiesArray> props = new DefaultPropertiesFactory().createProperties(
				graphingApp, graphingApp.getLocalization(), null);
		assertEquals(3, props.size());
		assertEquals(List.of("Language", "Rounding", "Coordinates", "Angle Unit", "Font Size",
						"ActionablePropertyCollection"), getNames(props.get(0)));
		assertEquals(List.of("Display", "Auxiliary Objects"),
				getNames(props.get(1)));
		assertEquals(List.of("Grid", "Axes", "Dimensions", "xAxis", "yAxis", "Advanced"),
				getNames(props.get(2)));
	}

	protected static List<String> getNames(PropertiesArray props) {
		return Arrays.stream(props.getProperties())
				.map(p -> p.getName().isEmpty() ? p.getClass().getSimpleName() : p.getName())
				.collect(Collectors.toList());
	}
}
