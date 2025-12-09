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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.main.PreviewFeature;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CmdImplicitSurfaceTest extends BaseCommandTest {

	@BeforeClass
	public static void enablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
	}

	@AfterClass
	public static void disablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@Test
	public void testCommand() {
		t("ImplicitSurface[sin(x)+sin(y)+sin(z)]",
				"sin(x) + sin(y) + sin(z) = 0");
	}

	@Test
	public void testIntersect() {
		intersect("x^4+y^4+z^4=2", "x=y", false, "(-1, -1, 0)",
				"(1, 1, 0)");
	}
}
