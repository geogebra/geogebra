package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Before;
import org.junit.Test;

public class ClippingTest extends BaseUnitTest {
	private GGraphicsCommon graphics;
	private EuclidianView view;

	@Before
	public void setupApp() {
		graphics = spy(new GGraphicsCommon());
		view = getApp().getActiveEuclidianView();
	}

	@Test
	public void testNoArtifact() {
		doAnswer(inv -> {
			assertNull(((GeneralPathClipped) inv.getArgument(0)).firstPoint());
			return null;
		}).when(graphics).draw(any());
		GeoFunction f = add("sec(cot(x))");
		view.update(f);
		view.setRealWorldCoordSystem(-10, 10, -0.5, 0.5);
		Drawable drawable = (Drawable) view.getDrawableFor(f);
		drawable.draw(graphics);

		verify(graphics, times(1)).draw(any());
	}
}
