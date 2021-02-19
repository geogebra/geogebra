package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.kernel.MyPoint;

public class ClipAlgoSutherlandHodogman {

	public static final int EDGE_COUNT = 4;

	public ArrayList<MyPoint> process(ArrayList<MyPoint> input, double[][] clipPoints) {
		ArrayList<MyPoint> output = new ArrayList<>(input);

		for (int i = 0; i < EDGE_COUNT; i++) {
			double[] edgeStartPoint = clipPoints[(i + 3) % EDGE_COUNT];
			double[] edgeEndPoint = clipPoints[i];
			output = clipWithEdge(edgeStartPoint, edgeEndPoint, output);
		}

		return output;
	}

	private ArrayList<MyPoint> clipWithEdge(double[] a, double[] b,
			ArrayList<MyPoint> input) {

		ArrayList<MyPoint> output = new ArrayList<>();

		for (int i = 0; i < input.size(); i++) {
			MyPoint prev = i > 0 ?
					input.get((i - 1) % input.size())
					: input.get(input.size() - 1);
			MyPoint current = input.get(i);
			output.addAll(addClippedOutput(a, b, prev, current));
		}
		return output;
	}

	private ArrayList<MyPoint> addClippedOutput(double[] A, double[] B,
			MyPoint prev, MyPoint current) {
		ArrayList<MyPoint> output = new ArrayList<>();
		MyPoint intersectionPoint = intersection(A, B, prev, current);
		if (isInside(A, B, current)) {
			if (!isInside(A, B, prev)) {
				output.add(intersectionPoint);
			}
			output.add(current);

		} else if (isInside(A, B, prev)) {
			output.add(intersectionPoint);
		}
		return output;
	}

	private static boolean isInside(double[] a, double[] b, MyPoint c) {
		return (a[0] - c.x) * (b[1] - c.y) > (a[1] - c.y) * (b[0] - c.x);
	}

	private static MyPoint intersection(double[] a, double[] b, MyPoint p,
			MyPoint q) {
		double A1 = b[1] - a[1];
		double B1 = a[0] - b[0];
		double C1 = A1 * a[0] + B1 * a[1];

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
