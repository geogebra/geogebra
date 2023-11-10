package org.geogebra.common.euclidian.draw;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class DrawParametricCurveTest extends BaseUnitTest {

	@Test
	public void conditionalShouldHaveLabel() {
		add("ZoomIn(-5,-5,5,5)");
		GeoFunction f = add("If(x<1,1/0,x-1)");
		f.setLabelVisible(true);
		f.updateRepaint();
		Drawable drawable = (Drawable) getApp().getActiveEuclidianView().getDrawableFor(f);
		assertNotNull(drawable);
		assertThat(drawable.getLabelX(), equalTo(480.0));
		assertThat(drawable.getLabelY(), equalTo(300.0));
	}
}
