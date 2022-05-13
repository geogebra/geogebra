package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.MyPoint;

public class ClipAlgoSutherlandHodogman {

	public static final int EDGE_COUNT = 4;
	public static final double Y_LIMIT = 1E6;

	static class Edge {
		private final MyPoint start;
		private final MyPoint end;

		public Edge(MyPoint start, MyPoint end) {
			this.start = start;
			this.end = end;
		}
	}

	/**
	 * @param input input points
	 * @param clipPoints vertices of clipping polygon
	 * @return clipped points
	 */
	public List<MyPoint> process(List<MyPoint> input, double[][] clipPoints) {
		List<MyPoint> output = input;
		for (int i = 0; i < EDGE_COUNT; i++) {
			output = clipWithEdge(createEdge(clipPoints, i), output);
		}
		limitXYValues(output);
		return output;
	}

	private void limitXYValues(List<MyPoint> input) {
		input.forEach(pt -> {
			pt.x = getSafeNumber(pt.x);
			pt.y = getSafeNumber(pt.y);
		});
	}

	private Edge createEdge(double[][] clipPoints, int i) {
		return new Edge(createPoint(clipPoints[(i + 3) % EDGE_COUNT]),
				createPoint(clipPoints[i]));
	}

	private MyPoint createPoint(double[] value) {
		return new MyPoint(value[0], value[1]);
	}

	private List<MyPoint> clipWithEdge(Edge edge, List<MyPoint> input) {

		List<MyPoint> output = new ArrayList<>();

		for (int i = 0; i < input.size(); i++) {
			MyPoint prev = input.get((i > 0 ? i : input.size()) - 1);
			MyPoint current = input.get(i);
			addClippedOutput(edge, prev, current, output);
		}
		return output;
	}

	private void addClippedOutput(Edge edge,
			MyPoint prev, MyPoint current, List<MyPoint> output) {
		if (isInside(edge, current)) {
			if (!isInside(edge, prev)) {
				handleIntersectionPoint(edge, prev, current, output);
			}

			output.add(current);

		} else if (isInside(edge, prev)) {
			handleIntersectionPoint(edge, prev, current, output);
		}
	}

	private void handleIntersectionPoint(Edge edge, MyPoint prev,
			MyPoint current, List<MyPoint> output) {
		MyPoint intersection = intersection(edge, prev, current);
		if (intersection == null) {
			current.setLineTo(false);
		} else {
			output.add(intersection);
		}
	}

	private static boolean isInside(Edge edge, MyPoint c) {
		return (edge.start.x - c.x) * (edge.end.y - c.y)
				< (edge.start.y - c.y) * (edge.end.x - c.x);
	}

	private MyPoint intersection(Edge edge, MyPoint p,
			MyPoint q) {
		double x, y;
		if (edge.start.x == edge.end.x) {
			x = edge.start.x;
			y = intersectVal(p.x, p.y, q.x, q.y, x);
		} else {
			y = edge.start.y;
			x = intersectVal(p.y, p.x, q.y, q.x, y);
		}
		return Double.isNaN(x) || Double.isNaN(y) ? null : new MyPoint(x, y);
	}

	private double intersectVal(double p1, double p2, double q1, double q2, double at) {
		double slope = (q2 - p2) / (q1 - p1);
		return Math.abs(p2) < Math.abs(q2) ? p2 + slope * (at - p1) : q2 + slope * (at - q1);
	}

	private double getSafeNumber(double value) {
		return Math.abs(value) > Y_LIMIT ? Math.signum(value) * Y_LIMIT : value;
	}
}