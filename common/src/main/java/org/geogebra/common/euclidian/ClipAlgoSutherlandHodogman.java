package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.kernel.MyPoint;

public class ClipAlgoSutherlandHodogman {

	public static final int EDGE_COUNT = 4;

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
	public ArrayList<MyPoint> process(ArrayList<MyPoint> input, double[][] clipPoints) {
		ArrayList<MyPoint> output = new ArrayList<>(input);
		for (MyPoint pt: output) {
			if (Double.isInfinite(pt.y)) {
				pt.y = Math.signum(pt.y) * 1E6;
			}
		}
		for (int i = 0; i < EDGE_COUNT; i++) {
			output = clipWithEdge(createEdge(clipPoints, i), output);
		}

		return output;
	}

	private Edge createEdge(double[][] clipPoints, int i) {
		return new Edge(createPoint(clipPoints[(i + 3) % EDGE_COUNT]),
				createPoint(clipPoints[i]));
	}

	private MyPoint createPoint(double[] value) {
		return new MyPoint(value[0], value[1]);
	}

	private ArrayList<MyPoint> clipWithEdge(Edge edge, ArrayList<MyPoint> input) {

		ArrayList<MyPoint> output = new ArrayList<>();

		for (int i = 0; i < input.size(); i++) {
			MyPoint prev = input.get((i > 0 ? i : input.size()) - 1);
			MyPoint current = input.get(i);
			addClippedOutput(edge, prev, current, output);
		}
		return output;
	}

	private void addClippedOutput(Edge edge,
			MyPoint prev, MyPoint current, ArrayList<MyPoint> output) {
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
		double A1 = edge.end.y - edge.start.y;
		double B1 = edge.start.x - edge.end.x;
		double C1 = A1 * edge.start.x + B1 * edge.start.y;

		double A2 = q.y - p.y;
		double B2 = p.x - q.x;
		double C2 = A2 * p.x + B2 * p.y;

		double det = A1 * B2 - A2 * B1;

		double x = (B2 * C1 - B1 * C2) / det;
		double y = (A1 * C2 - A2 * C1) / det;

		// add 0.0 to avoid -0.0 problem.
		return new MyPoint(x + 0.0, y + 0.0, q.getSegmentType());
	}
}
