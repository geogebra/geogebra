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

package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.jre.headless.MyImageCommon;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ImageProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.util.ImageManagerCommon;
import org.geogebra.test.BaseAppTestSetup;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ButtonIconPropertyCollectionTests extends BaseAppTestSetup {
	private BooleanProperty iconShownProperty;
	private IconsEnumeratedProperty<?> iconProperty;
	private ImageProperty customImageProperty;

	@BeforeEach
	void setUp() throws Exception {
		setupApp(SuiteSubApp.GRAPHING);
		ImageManagerCommon imageManager = new ImageManagerCommon() {
			@Override
			public @NonNull String getButtonIconPath(@NonNull String fileName) {
				return "button/" + fileName;
			}
		};
		ButtonIconPropertyCollection propertyCollection = new ButtonIconPropertyCollection(
				new GeoElementPropertiesFactory(),
				getLocalization(),
				imageManager,
				getKernel(),
				List.of(evaluateGeoElement("Button[]")));
		iconShownProperty = propertyCollection.leadProperty;
		iconProperty = (IconsEnumeratedProperty<?>) propertyCollection.getProperties()[0];
		customImageProperty = (ImageProperty) propertyCollection.getProperties()[1];
	}

	@Test
	void testChangingIcon() {
		iconProperty.setIndex(1);

		assertEquals(1, iconProperty.getIndex());
	}

	@Test
	void testChangingCustomImage() {
		customImageProperty.setValue(
				new ImageProperty.Value(new MyImageCommon(10, 10), "custom/play.svg"));

		assertEquals(-1, iconProperty.getIndex());
		assertEquals("custom/play.svg", customImageProperty.getValue().path());
	}

	@Test
	void testChangingCustomImageToIcon() {
		customImageProperty.setValue(new ImageProperty.Value(new MyImageCommon(10, 10), "custom.png"));
		iconProperty.setIndex(1);

		assertEquals(1, iconProperty.getIndex());
		assertNull(customImageProperty.getValue());
	}

	@Test
	void testChangingIconToCustomImage() {
		iconProperty.setIndex(1);
		customImageProperty.setValue(new ImageProperty.Value(new MyImageCommon(10, 10), "custom.png"));

		assertEquals(-1, iconProperty.getIndex());
		assertEquals("custom.png", customImageProperty.getValue().path());
	}

	@Test
	void testRemovingCustomImageSetsDefaultIcon() {
		customImageProperty.setValue(new ImageProperty.Value(new MyImageCommon(10, 10), "custom.png"));
		customImageProperty.setValue(null);

		assertTrue(iconShownProperty.getValue());
		assertEquals(0, iconProperty.getIndex());
		assertNull(customImageProperty.getValue());
	}
}
