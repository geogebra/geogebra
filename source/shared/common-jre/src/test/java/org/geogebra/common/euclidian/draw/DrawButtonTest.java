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

package org.geogebra.common.euclidian.draw;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.Objects;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DrawButtonTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GEOMETRY);
	}

	@Test
	@Issue("APPS-7549")
	void testInitialPosition() {
		GeoElement button = evaluateGeoElement("Button(\"OK\")");
		DrawButton btn = (DrawButton) getApp().getActiveEuclidianView().getDrawableFor(button);
		GGraphicsCommon graphics = spy(new GGraphicsCommon());
		Objects.requireNonNull(btn).draw(graphics);
		verify(graphics).drawString(eq("OK"), eq(10), eq(16));
	}
}
