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

package org.geogebra.common.euclidian;

import static junit.framework.TestCase.assertEquals;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EuclidianViewForNotesTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupNotesApp();
	}

	@Test
	public void showAllObjectsPortrait() {
		evaluate("Segment((0,0),(10,10))");
		getApp().getEuclidianView1().setViewShowAllObjects(false, true);
		assertEquals("(-2.64967, -0.31767)",
				evaluateGeoElement("Corner(1)").toValueString(StringTemplate.editTemplate));
		assertEquals("(11.519, 10.31767)",
				evaluateGeoElement("Corner(3)").toValueString(StringTemplate.editTemplate));
	}

	@Test
	public void showAllObjectsLandscape() {
		evaluate("Segment((0,0),(20,10))");
		getApp().getEuclidianView1().setViewShowAllObjects(false, true);
		assertEquals("(-2.47228, -3.67011)",
				evaluateGeoElement("Corner(1)").toValueString(StringTemplate.editTemplate));
		assertEquals("(20.6288, 13.67011)",
				evaluateGeoElement("Corner(3)").toValueString(StringTemplate.editTemplate));
	}
}
