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

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class FillCategoryPropertyTest extends BaseAppTestSetup {
	@Test
	void testFillCategoryRemembersPreviousFillType() throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement("Circle((0,0),10)");
		FillCategoryProperty property =
				new FillCategoryProperty(getLocalization(), element, new ElementSettingsData());

		assertEquals(FillType.STANDARD, element.getFillType());

		element.setFillType(FillType.DOTTED);
		property.setValue(FillCategory.SYMBOL);
		assertEquals(FillType.SYMBOLS, element.getFillType());

		property.setValue(FillCategory.PATTERN);
		assertEquals(FillType.DOTTED, element.getFillType());
	}

	@Test
	void testCategoryMatchesFillType() throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement("Circle((0,0),10)");
		FillCategoryProperty property =
				new FillCategoryProperty(getLocalization(), element, new ElementSettingsData());

		assertEquals(FillCategory.PATTERN, property.getValue());

		element.setFillType(FillType.DOTTED);
		assertEquals(FillCategory.PATTERN, property.getValue());

		element.setFillType(FillType.SYMBOLS);
		assertEquals(FillCategory.SYMBOL, property.getValue());

		element.setFillType(FillType.IMAGE);
		assertEquals(FillCategory.IMAGE, property.getValue());
	}

	@Test
	void testListRoundTrip() throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement list = evaluateGeoElement("{Circle((0,0),1), Circle((1,1),2)}");
		FillCategoryProperty property =
				new FillCategoryProperty(getLocalization(), list, new ElementSettingsData());

		list.setFillType(FillType.DOTTED);
		property.setValue(FillCategory.SYMBOL);
		assertEquals(FillType.SYMBOLS, list.getFillType());
		assertEquals(FillCategory.SYMBOL, property.getValue());
	}

	@Test
	void reconstructedPropertiesRestoreEachSelectedElementsPattern()
			throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement first = evaluateGeoElement("Circle((0, 0), 1)");
		GeoElement second = evaluateGeoElement("Circle((2, 0), 1)");
		first.setFillType(FillType.DOTTED);
		second.setFillType(FillType.BRICK);
		ObjectSettingsSession.Context context = new ObjectSettingsSession.Context();
		FillingPropertyCollection filling = new FillingPropertyCollection(
				new GeoElementPropertiesFactory(),
				getLocalization(),
				getApp().getImageManager(),
				List.of(first, second),
				context);
		((NamedEnumeratedProperty<?>) filling.getProperties()[1]).setIndex(1);
		FillingPropertyCollection reconstructed = new FillingPropertyCollection(
				new GeoElementPropertiesFactory(),
				getLocalization(),
				getApp().getImageManager(),
				List.of(first, second),
				context);
		((NamedEnumeratedProperty<?>) reconstructed.getProperties()[1]).setIndex(0);

		assertEquals(FillType.DOTTED, first.getFillType());
		assertEquals(FillType.BRICK, second.getFillType());
	}
}
