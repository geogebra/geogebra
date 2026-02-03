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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class FillCategoryPropertyTest extends BaseAppTestSetup {
	@Test
	public void testFillCategoryRemembersPreviousFillType() throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement("Circle((0,0),10)");
		FillCategoryProperty property = new FillCategoryProperty(getLocalization(), element);

		assertEquals(FillType.STANDARD, element.getFillType());

		element.setFillType(FillType.DOTTED);
		property.setValue(FillCategoryProperty.FillCategory.SYMBOL);
		assertEquals(FillType.SYMBOLS, element.getFillType());

		property.setValue(FillCategoryProperty.FillCategory.PATTERN);
		assertEquals(FillType.DOTTED, element.getFillType());
	}

	@Test
	public void testCategoryMatchesFillType() throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement("Circle((0,0),10)");
		FillCategoryProperty property = new FillCategoryProperty(getLocalization(), element);

		assertEquals(FillCategoryProperty.FillCategory.PATTERN, property.getValue());

		element.setFillType(FillType.DOTTED);
		assertEquals(FillCategoryProperty.FillCategory.PATTERN, property.getValue());

		element.setFillType(FillType.SYMBOLS);
		assertEquals(FillCategoryProperty.FillCategory.SYMBOL, property.getValue());

		element.setFillType(FillType.IMAGE);
		assertEquals(FillCategoryProperty.FillCategory.IMAGE, property.getValue());
	}
}

