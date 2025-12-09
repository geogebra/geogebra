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

package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class GeoBooleanTest extends BaseUnitTest {

	@Test
	public void conditionToShowObjectShouldHideGeos() throws CircularDefinitionException {
		GeoBoolean bool = add("true");
		add("(1,1)");
		GeoElement point = add("(2,1)");
		point.setShowObjectCondition(bool);
		getApp().getSelectionManager().addSelectedGeo(point);
		bool.setValue(false);
		bool.updateRepaint();
		assertThat(point, hasProperty("visible", GeoElement::isEuclidianVisible, false));
	}

	@Test
	public void checkboxShouldAlwaysShowLabel() {
		GeoElement bool = add("true");
		assertTrue("Newly created checkboxes should have a visible label per default!",
				bool.isLabelVisible());
	}

	@Test
	@Issue("APPS-6365")
	public void testMoveDependencies() throws CircularDefinitionException {
		GeoPoint pt = add("(1,1)");
		GeoBoolean condition = add("condition=true");
		pt.setShowObjectCondition(condition);
		add("other=true");
		add("condition=!other");
		assertNotSame(lookup("condition"), condition);
		assertEquals(lookup("condition"), pt.getShowObjectCondition());
		assertFalse(pt.isEuclidianVisible());
	}
}
