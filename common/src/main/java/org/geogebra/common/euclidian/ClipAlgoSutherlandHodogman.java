package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.MyPoint;

public class ClipAlgoSutherlandHodogman {

	public static final int EDGE_COUNT = 4;

	public ClipAlgoSutherlandHodogman() {
	}

	public ArrayList<MyPoint> process(ArrayList<MyPoint> pathPoinst, double[][] clipPoints) {
		ArrayList<MyPoint> output = new ArrayList<>(pathPoinst);
		for (int i = 0; i < EDGE_COUNT; i++) {
			int len2 = output.size();
			List<MyPoint> input = output;
			output = new ArrayList<>(len2);

			double[] A = clipPoints[(i + 3) % EDGE_COUNT];
			double[] B = clipPoints[i];
			double[] C = clipPoints[(i + 1) % EDGE_COUNT];

			boolean inside = isInside(A, B, new MyPoint(C[0], C[1]));

			for (int j = 0; j < len2; j++) {
				MyPoint P = input.get((j + len2 - 1) % len2);
				MyPoint Q = input.get(j);

				if (isInside(A, B, P) == inside) {
					if (isInside(A, B, Q) == inside) {
						output.add(Q);
					} else {
						output.add(intersection(A, B, P, Q));
					}
				} else if (isInside(A, B, Q) == inside) {
					output.add(intersection(A, B, P, Q));
					output.add(Q);
				}
			}
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

		return new MyPoint(x, y, q.getSegmentType());
	}
}
