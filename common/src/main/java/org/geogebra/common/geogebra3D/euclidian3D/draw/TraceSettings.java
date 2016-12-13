package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;

final public class TraceSettings
		implements Comparable<TraceSettings>, Cloneable {

	private GColor c;

	public TraceSettings() {

	}

	public TraceSettings(GColor c, int a) {
		setColor(c, a);
	}

	@Override
	public TraceSettings clone() {
		return new TraceSettings(c, c.getAlpha());
	}

	public void setColor(GColor c, int a) {
		this.c = c.deriveWithAlpha(a);
	}

	public GColor getColor() {
		return c;
	}

	public int getAlpha() {
		return c.getAlpha();
	}

	@Override
	public int compareTo(TraceSettings settings) {

		// compare colors
		int v1 = this.c.hashCode();
		int v2 = settings.c.hashCode();
		if (v1 < v2) {
			return -1;
		}
		if (v1 > v2) {
			return 1;
		}

		return 0;
	}

	@Override
	public boolean equals(Object settings) {
		if (settings instanceof TraceSettings) {
			int v1 = this.c.hashCode();
			int v2 = ((TraceSettings) settings).c.hashCode();
			return v1 == v2;
		}
		return false;
	}
}