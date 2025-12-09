/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
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
