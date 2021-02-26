package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
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
		assertEquals(output.toString(), processAlgo().toString());
	}

	@Test
	public void allOutsideTest() {
		addInput(-50, -50);
		addInput(100, -50);
		addInput(150, 150);
		addInput(-50, 150);

		addOutput(100, 100);
		addOutput(0, 100);
		addOutput(0, 0);
		addOutput(100, 0);

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

	@Test
	public void clipRectTop() {
		addInput(20, -50);
		addInput(80, -70);
		addInput(80, 50);
		addInput(20, 70);

		addOutput(20, 0);
		addOutput(80, 0);
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

	@Test
	public void clipSkipPoint() {
		addInputMoveTo(-30, 20);
		addInput(130, 20);
		addInputMoveTo(-50, 40);
		addInput(150, 40);

		addOutputMoveTo(0, 20);
		addOutput(100, 20);
		// extra move to -- artifact of the algorithm, not worth filtering
		addOutputMoveTo(0, 310 / 9.0);
		addOutputMoveTo(0, 40);
		addOutput(100, 40);
		assertOutput();
	}

	private void addInputMoveTo(int x, int y) {
		addInput(x, y, SegmentType.MOVE_TO);
	}

	private void addInput(double x, double y, SegmentType segmentType) {
		input.add(new MyPoint(x, y, segmentType));
	}

	private void addInput(double x, double y) {
		addInput(x, y, SegmentType.LINE_TO);
	}

	private ArrayList<MyPoint> processAlgo() {
		return algo.process(input, defaultClipPolygon);
	}

	private void addOutputMoveTo(double x, double y) {
		addOutput(x, y, SegmentType.MOVE_TO);
	}

	private void addOutput(double x, double y, SegmentType segmentType) {
		output.add(new MyPoint(x, y, segmentType));
	}

	private void addOutput(double x, double y) {
		addOutput(x, y, SegmentType.LINE_TO);
	}
}
