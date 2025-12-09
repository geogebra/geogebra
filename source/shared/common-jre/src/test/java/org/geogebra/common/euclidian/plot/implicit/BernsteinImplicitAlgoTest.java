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

package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.implicit.LinkSegments;
import org.junit.Ignore;
import org.junit.Test;

public class BernsteinImplicitAlgoTest extends BaseUnitTest {
	@Ignore
	@Test
	public void name() {
		EuclidianView view = getApp().getEuclidianView1();
		view.setPreferredSize(AwtFactory.getPrototype().newDimension(1280, 1920));
		view.setXmin(-0.048);
		view.setXmax(0.048);
		view.setYmax(1.0);
		view.setYmin(-1.0);
		EuclidianViewBounds bounds = new EuclidianViewBoundsImp(view);
		GeoImplicitCurve curve = add("(3x^2 - y^2)^2 * y^2 - (x^2 + y^2)^4 = 0");
		List<MyPoint> points = new ArrayList<>();
		List<BernsteinPlotCell> cells = new ArrayList<>();
		LinkSegments segments = new LinkSegments(points);
		BernsteinImplicitAlgo algo =
				new BernsteinImplicitAlgo(bounds, curve, cells,
						segments, 10);
		algo.compute();
		BernsteinPlotCell cell0 = cells.get(0);
		BernsteinMarchingRect rect = new BernsteinMarchingRect(cell0);
		BernsteinMarchingConfigProvider provider =
				new BernsteinMarchingConfigProvider(cell0);
		BernsteinMarchingConfig config = provider.getConfigFrom(rect);

		assertEquals(BernsteinMarchingConfig.T1100, config);
	}
}
