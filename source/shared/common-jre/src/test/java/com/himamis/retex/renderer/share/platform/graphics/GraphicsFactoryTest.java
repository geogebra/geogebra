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

package com.himamis.retex.renderer.share.platform.graphics;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.awt.GColor;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class GraphicsFactoryTest {

	@Test
	@Issue("APPS-7807")
	void testCreateColorFromFractionalComponents() {
		GColor color = new TestGraphicsFactory().createColor(0.5, 0.5, 1, 0.5);

		assertAll(
				() -> assertEquals(127, color.getRed()),
				() -> assertEquals(127, color.getGreen()),
				() -> assertEquals(255, color.getBlue()),
				() -> assertEquals(127, color.getAlpha()));
	}

	private static final class TestGraphicsFactory extends GraphicsFactory {
		@Override
		public Image createImage(int width, int height, int type) {
			throw new UnsupportedOperationException();
		}
	}
}
