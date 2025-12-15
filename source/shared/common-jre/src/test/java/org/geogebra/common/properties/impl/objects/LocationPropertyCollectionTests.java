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
 * See https://www.geogebra.org/license for full licensing details'
 */
 
package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class LocationPropertyCollectionTests extends BaseAppTestSetup {
	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@Test
	public void testTogglingGraphicsViewVisibility() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPoint point = evaluateGeoElement("A = (0, 0)");
		LocationPropertyCollection locationPropertyCollection = new LocationPropertyCollection(
				propertiesFactory, getLocalization(), List.of(point));
		BooleanProperty graphicsViewLocationProperty =
				locationPropertyCollection.getProperties()[1];

		graphicsViewLocationProperty.setValue(false);
		assertFalse(graphicsViewLocationProperty.getValue());
		assertFalse(point.isVisibleInView(App.VIEW_EUCLIDIAN));

		graphicsViewLocationProperty.setValue(true);
		assertTrue(graphicsViewLocationProperty.getValue());
		assertTrue(point.isVisibleInView(App.VIEW_EUCLIDIAN));
	}

	@Test
	public void testTogglingGraphics3DViewVisibility() {
		setupApp(SuiteSubApp.G3D);
		GeoPoint point = evaluateGeoElement("A = (0, 0)");
		LocationPropertyCollection locationPropertyCollection = new LocationPropertyCollection(
				propertiesFactory, getLocalization(), List.of(point));
		BooleanProperty graphicsView3DLocationProperty =
				locationPropertyCollection.getProperties()[3];

		graphicsView3DLocationProperty.setValue(false);
		assertFalse(graphicsView3DLocationProperty.getValue());
		assertFalse(point.isVisibleInView3D());

		graphicsView3DLocationProperty.setValue(true);
		assertTrue(graphicsView3DLocationProperty.getValue());
		assertTrue(point.isVisibleInView3D());
	}
}
