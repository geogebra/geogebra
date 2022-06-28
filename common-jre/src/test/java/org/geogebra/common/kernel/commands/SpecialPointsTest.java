package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
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
		assertEquals(7, numberOfSpecialPoints());
	}

	@Test
	public void specialPointsForSegment() {
		Construction cons = getConstruction();
		GeoPoint a = new GeoPoint(cons, -1, -1, 0);
		GeoPoint b = new GeoPoint(cons, 1, 1, 0);
		GeoSegment segment = new GeoSegment(cons, a, b);
		segment.setLabel("s");
		updateSpecialPoints("s");
		assertEquals(0, numberOfSpecialPoints());
	}

	@Test
	public void specialPointsForTrig() {
		add("ZoomIn(-4pi-1,-2,4pi+1,2)");
		add("f(x)=sin(x)");
		updateSpecialPoints("f");
		assertEquals(19, numberOfSpecialPoints());
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
		assertEquals(4, numberOfSpecialPoints());
	}

	@Test
	public void specialPointsParentAlgoRemovedFromUpdateSet() {
		add("eq1: x^4+y^2=2");
		GeoElement element = add("eq2: x*y=3");
		updateSpecialPoints("eq2");
		assertTrue(element.getAlgorithmList().isEmpty());
		assertTrue(element.getAlgoUpdateSet().isEmpty());
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