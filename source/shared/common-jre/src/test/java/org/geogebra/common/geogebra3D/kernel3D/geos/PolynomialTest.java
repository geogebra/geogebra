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

package org.geogebra.common.geogebra3D.kernel3D.geos;

import static org.geogebra.editor.share.util.Unicode.EULER_CHAR;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.editor.share.util.Unicode;
import org.junit.Test;

public class PolynomialTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D(new AppConfigDefault());
	}

	@Test
	public void polynomialHandle0Degree() {
		add("b = 3");
		GeoLine f = add("f:y-3=x+" + EULER_CHAR + "^(b)");
		assertThat(f, hasValue("y - 3 = x + " + EULER_CHAR + Unicode.SUPERSCRIPT_3));
	}
}
