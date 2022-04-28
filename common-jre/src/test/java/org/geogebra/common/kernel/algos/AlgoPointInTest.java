package org.geogebra.common.kernel.algos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class AlgoPointInTest extends BaseUnitTest {

	@Test
	public void reloadShouldNotCauseRoundingErrorsForPoly() {
		add("Apoly = (-11.630150708010094, 11.544376411902313)");
		add("Bpoly = (-11.630150708010094, -11.546479927913134)");
		add("Cpoly = (11.460705631805354, -11.546479927913134)");
		add("Dpoly = (11.460705631805354, 11.544376411902313)");
		add("X=PointIn(Polygon(Apoly,Bpoly,Cpoly,Dpoly))");
		add("SetCoords(X,-3,-2)");
		reload();
		assertEquals(-3.0, getApp().getGgbApi().getXcoord("X"), 0);
		assertEquals(-2.0, getApp().getGgbApi().getYcoord("X"), 0);
	}

	@Test
	public void reloadShouldNotCauseRoundingErrorsForCircle() {
		add("A = (0.71,0.43)");
		add("B = (1.89,2.003)");
		add("C = (60,100)");
		add("X=PointIn(Ellipse(A,B,C))");
		add("SetCoords(X,-3,-2)");
		reload();
		add("UpdateConstruction()");
		assertEquals(-3.0, getApp().getGgbApi().getXcoord("X"), 0);
		assertEquals(-2.0, getApp().getGgbApi().getYcoord("X"), 0);
	}
}
