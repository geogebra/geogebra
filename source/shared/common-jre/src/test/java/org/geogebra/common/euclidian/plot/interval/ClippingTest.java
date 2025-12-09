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

package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Before;
import org.junit.Test;

public class ClippingTest extends BaseUnitTest {
	private EuclidianView view;
	private GRectangle clipRectangle;

	@Before
	public void setupApp() {
		view = getApp().getActiveEuclidianView();
		clipRectangle = AwtFactory.getPrototype()
				.newRectangle(0, 0, view.getViewWidth(), view.getViewHeight());
	}

	@Test
	public void testNoArtifact() {
		GeoFunction f = add("sec(cot(x))");
		view.update(f);
		view.setRealWorldCoordSystem(-10, 10, -0.5, 0.5);
		Drawable drawable = getDrawable(f);
		if (drawable == null) {
			fail();
		}

		assertFalse(drawable.intersectsRectangle(clipRectangle));
	}
}
