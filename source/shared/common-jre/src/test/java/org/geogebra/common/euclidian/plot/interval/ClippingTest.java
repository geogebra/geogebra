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
