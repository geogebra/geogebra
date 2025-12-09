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

package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.junit.Test;

public class AlgoDrawingPadCornerTest extends BaseUnitTest {

	@Test
	public void corner5ShouldBeNonzeroOnLoad() {
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setPreferredSize(new Dimension(0, 0));
		view.getSettings().setSizeFromFile(new Dimension(300, 200));
		GeoPoint corner = add("Corner(5)");
		assertThat(corner, hasValue("(300, 200)"));
	}
}
