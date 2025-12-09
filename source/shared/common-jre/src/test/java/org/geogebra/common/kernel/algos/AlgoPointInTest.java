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
