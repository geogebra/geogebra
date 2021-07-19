package org.geogebra.common.euclidian;

import org.junit.Test;

public class MoveToolTest extends BaseControllerTest {

	@Test
	public void moveShouldChangeSegment() {
		add("A = (0,0)");
		add("f = Segment(A, (1,-1))");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (1, -2)", "f = 1.41421");
	}

	@Test
	public void moveShouldChangePolygon() {
		add("A = (0,0)");
		add("q = Polygon(A, (0,-1), 4)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (1, -2)", "q = 1", "f = 1", "g = 1", "B = (2, -3)",
				"C = (2, -2)", "h = 1", "i = 1");
	}

	@Test
	public void moveShouldNotChangeFixedSegment() {
		add("A = (0,0)");
		add("f = Segment(A, (1,-1))");
		add("SetFixed(f,true)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (0, 0)", "f = 1.41421");
	}

	@Test
	public void moveShouldNotChangeFixedPolygon() {
		add("A = (0,0)");
		add("q = Polygon(A, (0,-1), 4)");
		add("SetFixed(q,true)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (0, 0)", "q = 1", "f = 1", "g = 1", "B = (1, -1)",
				"C = (1, 0)", "h = 1", "i = 1");
	}
}
