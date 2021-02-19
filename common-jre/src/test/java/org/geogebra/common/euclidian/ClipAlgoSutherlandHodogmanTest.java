package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.geogebra.common.kernel.MyPoint;
import org.junit.Test;

public class ClipAlgoSutherlandHodogmanTest {
	private final ClipAlgoSutherlandHodogman algo = new ClipAlgoSutherlandHodogman();
	private final ArrayList< MyPoint > input = new ArrayList<>();
	private ArrayList< MyPoint > output = new ArrayList<>();
	private static final double[][] defaultClipPolygon = {
			{0, 0},
			{100, 0},
			{100, 100},
			{0, 100}
	};

	@Test
	public void allInsideTest() {
		addInput(50, 50);
		addInput(10, 80);
		addInput(90, 80);
		output = input; // just to be clear;
		assertOutput();
	}

	private void assertOutput() {
		assertEquals(output.toString(), processAlgo().toString());
	}

	@Test
	public void allOutsideTest() {
		addInput(-50, -50);
		addInput(100, -50);
		addInput(150, 150);
		addInput(-50, 150);

		addOutput(0, 100);
		addOutput(0, 0);
		addOutput(100, 0);
		addOutput(100, 100);

		assertOutput();
	}

	@Test
	public void upperRectTest() {
		addInput(20, -50);
		addInput(80, -50);
		addInput(80, 50);
		addInput(20, 50);

		addOutput(20, 0);
		addOutput(80, 0);
		addOutput(80, 50);
		addOutput(20, 50);

		assertOutput();
	}

	@Test
	public void rectTest() {
		addInput(20, -50);
		addInput(80, -50);
		addInput(80, 150);
		addInput(20, 150);
		addOutput(20, 100);
		addOutput(20, 0);
		addOutput(80, 0);
		addOutput(80, 100);
		assertOutput();
	}

	private ArrayList<MyPoint> processAlgo() {
		return algo.process(input, defaultClipPolygon);
	}

	private void addInput(double x, double y) {
		input.add(new MyPoint(x, y));
	}

	private void addOutput(double x, double y) {
		output.add(new MyPoint(x, y));
	}
}
