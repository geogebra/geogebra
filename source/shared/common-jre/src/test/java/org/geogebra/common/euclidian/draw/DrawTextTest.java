package org.geogebra.common.euclidian.draw;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Test;

public class DrawTextTest extends BaseUnitTest {

	@Test
	public void testAlignLeft() {
		add("ZoomIn(-5,-5,5,5)");
		GeoText txt = add("Text(\"short text\",(0,0),false,false,-1,0)");
		DrawText drawable = (DrawText) getDrawable(txt);
		assertThat(drawable, notNullValue());
		GRectangle bounds = Objects.requireNonNull(drawable.getBounds());
		assertThat(drawable.xLabel + bounds.getWidth(), equalTo(401.0));
	}

	@Test
	public void testAlignLeftVerticalDefault() {
		add("ZoomIn(-5,-5,5,5)");
		GeoText txt = add("Text(\"short text\",(0,0),false,false,-1)");
		DrawText drawable = (DrawText) getDrawable(txt);
		assertThat(drawable, notNullValue());
		GRectangle bounds = Objects.requireNonNull(drawable.getBounds());
		assertThat(drawable.xLabel + bounds.getWidth(), equalTo(401.0));
	}

	@Test
	public void testCenter() {
		add("ZoomIn(-5,-5,5,5)");
		GeoText txt = add("Text(\"short text\",(0,0),false,false,0,0)");
		DrawText drawable = (DrawText) getDrawable(txt);
		assertThat(drawable, notNullValue());
		GRectangle bounds = Objects.requireNonNull(drawable.getBounds());
		assertThat(drawable.xLabel + bounds.getWidth() / 2, equalTo(403.0));
	}

	@Test
	public void testAlignRight() {
		add("ZoomIn(-5,-5,5,5)");
		GeoText txt = add("Text(\"short text\",(0,0),false,false,1,0)");
		DrawText drawable = (DrawText) getDrawable(txt);
		assertThat(drawable, notNullValue());
		assertThat(drawable.xLabel, equalTo(406));
	}

}
