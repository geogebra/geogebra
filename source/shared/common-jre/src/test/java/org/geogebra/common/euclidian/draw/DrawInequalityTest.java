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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DrawInequalityTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GEOMETRY);
	}

	@Test
	void singlePointInequalityShouldUpdateOnZoom() {
		GeoElement element = evaluateGeoElement("xx+yy>0");
		evaluate("ZoomIn(-3,-2,5,4)");
		SvgGraphics svg = new SvgGraphics(800, 600);
		Drawable drawable =
				assertInstanceOf(Drawable.class, getApp().getActiveEuclidianView().getDrawableFor(element));
		drawable.draw(svg);
		String svgContent = svg.toString();
		assertTrue(svgContent.matches("""
				<svg.*>
				<path d="M305.0,400.0C305.0,402.76142374915395,302.76142374915395,.* stroke-opacity="1.0"/>
				<path d="M300.0,395.0C302.76142374915395,395.0,305.0,397.23857625084605.*"/>
				</svg>""".stripIndent()), svgContent);
	}
}
