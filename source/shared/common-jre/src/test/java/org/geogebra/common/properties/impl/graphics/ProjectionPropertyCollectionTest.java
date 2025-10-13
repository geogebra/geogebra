package org.geogebra.common.properties.impl.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.Property;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProjectionPropertyCollectionTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.G3D);
	}

	@Test
	void testAvailability() {
		EuclidianSettings evSettings = getApp().getSettings().getEuclidian(-1);
		ProjectionPropertyCollection collection = new ProjectionPropertyCollection(getApp(),
				getApp().getLocalization(), (EuclidianSettings3D) evSettings);
		Property[] properties = collection.getProperties();
		ProjectionsProperty prop = (ProjectionsProperty) properties[0];
		assertEquals(List.of("Projection"), getAvailable(properties));
		prop.setValue(EuclidianView3DInterface.PROJECTION_GLASSES);
		assertEquals(List.of("Projection", "Distance between eyes", "Gray-scale", "Omit Green"),
				getAvailable(properties));
		prop.setValue(EuclidianView3DInterface.PROJECTION_OBLIQUE);
		assertEquals(List.of("Projection", "Angle", "Factor"), getAvailable(properties));
		prop.setValue(EuclidianView3DInterface.PROJECTION_PERSPECTIVE);
		assertEquals(List.of("Projection", "Distance from screen"), getAvailable(properties));

	}

	private List<String> getAvailable(Property[] properties) {
		return Arrays.stream(properties).filter(Property::isAvailable).map(Property::getName)
				.collect(Collectors.toList());
	}
}
