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

package org.geogebra.common.main;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.settings.LabelVisibility;
import org.junit.Test;

public class AppLabelModeTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void polyhedronShouldOnlyLabelPointsInAutoMode() {
		add("A=(1,0,0)");
		add("B=(1,0,1)");
		getApp().getSettings().getLabelSettings().setLabelVisibility(LabelVisibility.UseDefaults);
		add("Cube(A,B)");
		assertTrue(lookup("C").isLabelVisible());
		assertFalse(lookup("edgeFG").isLabelVisible());
		assertFalse(lookup("faceABCD").isLabelVisible());
	}

	@Test
	public void polyhedronShouldNotLabelAnythingInAlwaysOffMode() {
		add("A=(1,0,0)");
		add("B=(1,0,1)");
		getApp().getSettings().getLabelSettings().setLabelVisibility(LabelVisibility.AlwaysOff);
		add("Cube(A,B)");
		assertFalse(lookup("C").isLabelVisible());
		assertFalse(lookup("edgeFG").isLabelVisible());
		assertFalse(lookup("faceABCD").isLabelVisible());
	}
}
