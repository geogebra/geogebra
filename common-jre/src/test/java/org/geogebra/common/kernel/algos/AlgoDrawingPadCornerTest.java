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
