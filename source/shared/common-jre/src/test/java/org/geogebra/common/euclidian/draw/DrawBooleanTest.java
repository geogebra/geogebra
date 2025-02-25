package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.test.EventAccumulator;
import org.junit.Before;
import org.junit.Test;

public class DrawBooleanTest extends BaseEuclidianControllerTest {

	private GeoBoolean check;

	@Before
	public void setupCheckbox() {
		check = add("a=true");
		check.setEuclidianVisible(true);
		check.setAbsoluteScreenLoc(100, 100);
	}

	@Test
	public void hitBooleanWithDynamicCaptionShouldBeOneEvent() {
		GeoText caption = add("caption=\"foo\"");
		check.setDynamicCaption(caption);
		check.updateRepaint();
		EventAccumulator listener = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(listener);
		click(101, 101);
		assertEquals(Collections.singletonList("UPDATE a"), listener.getEvents().stream()
				.filter(evt -> evt.startsWith("UPDATE")).collect(
				Collectors.toList()));
	}

	@Test
	public void hideDynamicCaption() {
		GeoText caption = add("caption=\"foo\"");
		check.setDynamicCaption(caption);
		check.setLabelVisible(false);
		check.updateRepaint();
		Drawable drawable = getDrawable(check);
		GGraphics2D g2 = mock(GGraphics2D.class);
		drawable.draw(g2);
		verify(g2, never()).drawString(anyString(), anyInt(), anyInt());
		verify(g2, never()).drawString(anyString(), anyDouble(), anyDouble());
	}
}
