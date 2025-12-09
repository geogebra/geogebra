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

package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.PreviewFeature;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeoImplicitSurfaceTest extends BaseUnitTest {

	@BeforeClass
	public static void enablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
	}

	@AfterClass
	public static void disablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void implicitSurface() {
		GeoImplicitSurface surface = add("x^3+y^3+z^3=1");
		assertThat(surface, isDefined());
	}

}
