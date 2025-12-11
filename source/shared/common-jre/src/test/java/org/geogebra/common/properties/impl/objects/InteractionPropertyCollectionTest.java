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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InteractionPropertyCollectionTest extends BaseAppTestSetup {

	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testIncrementFieldsEnabledDependentOnSelectionAllowed() {
		GeoElement point = evaluateGeoElement("(1, 1)");
		InteractionPropertyCollection interactionProperty = assertDoesNotThrow(() ->
				new InteractionPropertyCollection(propertiesFactory, getAlgebraProcessor(),
						getLocalization(), List.of(point)));
		interactionProperty.getSelectionAllowedProperty().setValue(false);
		assertFalse(interactionProperty.getAnimationStepProperty().isEnabled());
		assertFalse(interactionProperty.getVerticalStepProperty().isEnabled());
		interactionProperty.getSelectionAllowedProperty().setValue(true);
		assertTrue(interactionProperty.getAnimationStepProperty().isEnabled());
		assertTrue(interactionProperty.getVerticalStepProperty().isEnabled());
	}
}
