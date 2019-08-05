package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.junit.Before;
import org.junit.Test;

public class SpecialPointsTest extends BaseUnitTest {

	@Before
	public void setupConfig() {
		getApp().setConfig(new AppConfigGraphing());
	}

	@Test
	public void specialPointsForPolynomials() {
		add("f(x)=x^3-x");
		updateSpecialPoints("f");
		assertEquals(6, numberOfSpecialPoints());
	}

	@Test
	public void specialPointsForSegment() {
		add("s:Segment((-1,-1),(1,1))");
		updateSpecialPoints("s");
		assertEquals(0, numberOfSpecialPoints());
	}

	@Test
	public void specialPointsForTrig() {
		add("ZoomIn(-4pi-1,-2,4pi+1,2)");
		add("f(x)=sin(x)");
		updateSpecialPoints("f");
		assertEquals(18, numberOfSpecialPoints());
	}

	@Test
	public void specialPointForLines() {
		add("f:x=2+y");
		add("g:x=2-y");
		add("c:xx+yy=10");
		updateSpecialPoints("f");
		assertEquals(5, numberOfSpecialPoints());
		updateSpecialPoints("g");
		assertEquals(5, numberOfSpecialPoints());
	}

	@Test
	public void specialPointForConics() {
		add("f:y=x^2-6x+8");
		updateSpecialPoints("f");
		// 4 visible, 1 undefined
		assertEquals(5, numberOfSpecialPoints());
	}

	@Test
	public void specialPointsRedefine() {
		add("f(x)=x^2");
		updateSpecialPoints("f");
		add("a=1");
		add("f(x)=x^2+a");
		updateSpecialPoints("f");
		assertEquals(3, numberOfSpecialPoints());
	}

	private int numberOfSpecialPoints() {
		List<GeoElement> specialPoints = getApp().getSpecialPointsManager()
				.getSelectedPreviewPoints();
		if (specialPoints == null) {
			return 0;
		}
		return specialPoints
				.size();
	}

	private void updateSpecialPoints(String string) {
		getApp().getSpecialPointsManager()
				.updateSpecialPoints(lookup(string));
	}

}