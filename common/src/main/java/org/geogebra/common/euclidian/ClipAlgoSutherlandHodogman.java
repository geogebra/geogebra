package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.MyPoint;

public class ClipAlgoSutherlandHodogman {

	public static final int EDGE_COUNT = 4;
	public static final double Y_LIMIT = 1E4;

	private static class Edge {
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
		limitYValues(output);
		for (int i = 0; i < EDGE_COUNT; i++) {
			output = clipWithEdge(createEdge(clipPoints, i), output);
		}

		return output;
	}

	private void limitYValues(List<MyPoint> input) {
		input.stream().filter(pt -> pt.y > Y_LIMIT)
				.forEach(pt -> pt.y = Math.signum(pt.y) * Y_LIMIT);
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
				MyPoint intersection = intersection(edge, prev, current);
				output.add(intersection);
			}
			output.add(current);

		} else if (isInside(edge, prev)) {
			output.add(intersection(edge, prev, current));
		}
	}

	private static boolean isInside(Edge edge, MyPoint c) {
		return (edge.start.x - c.x) * (edge.end.y - c.y)
				< (edge.start.y - c.y) * (edge.end.x - c.x);
	}

	private static MyPoint intersection(Edge edge, MyPoint p,
			MyPoint q) {
		double a1 = edge.end.y - edge.start.y;
		double b1 = edge.start.x - edge.end.x;
		double c1 = a1 * edge.start.x + b1 * edge.start.y;

		double a2 = q.y - p.y;
		double b2 = p.x - q.x;
		double c2 = a2 * p.x + b2 * p.y;

		double det = a1 * b2 - a2 * b1;

		double x = (b2 * c1 - b1 * c2) / det;
		double y = (a1 * c2 - a2 * c1) / det;

		// add 0.0 to avoid -0.0 problem.
		return new MyPoint(x + 0.0, y + 0.0, q.getSegmentType());
	}
}