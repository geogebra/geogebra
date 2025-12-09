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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.geogebra3D.euclidianFor3D.DrawAngleFor3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.editor.share.util.Unicode;
import org.junit.Test;
import org.mockito.Mockito;

public class AlgoAngleLines3DTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void angleShouldWorkInIf() {
		t("a=If(true,Angle(xAxis,zAxis))", "90*" + Unicode.DEGREE_STRING);
		t("SetVisibleInView(a,1,true)");
		DrawableND drawable = getDrawable(lookup("a"));
		GGraphics2D g2 = mock(GGraphics2D.class);
		assertNotNull(drawable);
		((DrawAngleFor3D) drawable).draw(g2);
		Mockito.verifyNoInteractions(g2);
	}
}
