package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.kernel.Matrix.Coords;

public class TraceSettings implements Comparable<TraceSettings> {

	private Coords c;
	private double a;

	public TraceSettings() {

	}

	public TraceSettings(Coords c, double a) {
		this.c = c;
		this.a = a;
	}

	public TraceSettings clone() {
		Coords c1 = this.c.copyVector();
		return new TraceSettings(c1, a);
	}

	public void setColor(Coords c) {
		this.c = c;
	}

	public Coords getColor() {
		return c;
	}

	public double getAlpha() {
		return a;
	}

	public void setAlpha(double a) {
		this.a = a;
	}

	private int getInt(double value) {
		return (int) (256 * value);
	}

	@Override
	public int compareTo(TraceSettings settings) {

		// compare colors (r,g,b)
		for (int i = 1; i <= 3; i++) {
			int v1 = getInt(this.c.get(i));
			int v2 = getInt(settings.c.get(i));
			if (v1 < v2) {
				return -1;
			}
			if (v1 > v2) {
				return 1;
			}
		}

		// compare alpha
		int v1 = getInt(this.a);
		int v2 = getInt(settings.a);
		if (v1 < v2) {
			return -1;
		}
		if (v1 > v2) {
			return 1;
		}

		return 0;
	}
}