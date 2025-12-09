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

package org.geogebra.common.euclidian;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.junit.Test;

public class ClipAlgoSutherlandHodogmanTest {
	private final ClipAlgoSutherlandHodogman algo = new ClipAlgoSutherlandHodogman();
	private final ArrayList< MyPoint > input = new ArrayList<>();
	private ArrayList< MyPoint > output = new ArrayList<>();
	private static final double[][] defaultClipPolygon = {
			{0, 0},
			{0, 100},
			{100, 100},
			{100, 0},
	};

	@Test
	public void allInsideTest() {
		addInput(10, 10);
		addInput(80, 10);
		addInput(80, 80);
		output = input; // just to be clear;
		assertOutput();
	}

	private void assertOutput() {
		List<MyPoint> actual = processAlgo();
		for (int i = 0; i < output.size(); i++) {
			MyPoint p = output.get(i);
			MyPoint q = actual.get(i);
			assertTrue(p.isEqual(q));
		}
	}

	@Test
	public void allOutsideTest() {
		addInput(-50, -50);
		addInput(100, -50);
		addInput(150, 150);
		addInput(-50, 150);

		addOutput(100, 100);
		addOutput(0, 100);
		addOutput(0, Kernel.STANDARD_PRECISION);
		addOutput(100, Kernel.STANDARD_PRECISION);

		assertOutput();
	}

	@Test
	public void rectTest() {
		addInput(20, -50);
		addInput(80, -50);
		addInput(80, 150);
		addInput(20, 150);
		addOutput(20, 100);
		addOutput(20, Kernel.STANDARD_PRECISION);
		addOutput(80, Kernel.STANDARD_PRECISION);
		addOutput(80, 100);
		assertOutput();
	}

	@Test
	public void clipRectTop() {
		addInput(20, -50);
		addInput(80, -70);
		addInput(80, 50);
		addInput(20, 70);

		addOutput(20, Kernel.STANDARD_PRECISION);
		addOutput(80, Kernel.STANDARD_PRECISION);
		addOutput(80, 50);
		addOutput(20, 70);

		assertOutput();
	}

	@Test
	public void clipRectRight() {
		addInput(20, 20);
		addInput(150, 20);
		addInput(150, 80);
		addInput(20, 80);

		addOutput(20, 20);
		addOutput(100, 20);
		addOutput(100, 80);
		addOutput(20, 80);
		assertOutput();
	}

	private void addInput(double x, double y) {
		input.add(new MyPoint(x, y));
	}

	private List<MyPoint> processAlgo() {
		return algo.process(input, defaultClipPolygon);
	}

	private void addOutput(double x, double y) {
		output.add(new MyPoint(x, y));
	}
}
