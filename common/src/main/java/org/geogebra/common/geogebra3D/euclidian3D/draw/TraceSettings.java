package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;

final public class TraceSettings
		implements Comparable<TraceSettings> {

	private GColor c;
	private int alpha;

	/**
	 * @return copy of these settings
	 */
	public TraceSettings copy() {
		TraceSettings tr = new TraceSettings();
		tr.setColor(c, alpha);
		return tr;
	}

	/**
	 * @param c
	 *            color
	 * @param a
	 *            alpha
	 */
	public void setColor(GColor c, int a) {
		this.c = c;
		this.alpha = a;
	}

	public GColor getColor() {
		return c;
	}

	public int getAlpha() {
		return alpha;
	}

	@Override
	public int compareTo(TraceSettings settings) {

		// compare alpha
		int v1 = this.alpha;
		int v2 = settings.alpha;
		if (v1 < v2) {
			return -1;
		}
		if (v1 > v2) {
			return 1;
		}

		// compare colors
		v1 = this.c.hashCode();
		v2 = settings.c.hashCode();
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
			// compare alpha
			if (alpha != ((TraceSettings) settings).alpha) {
				return false;
			}
			// compare colors
			int v1 = this.c.hashCode();
			int v2 = ((TraceSettings) settings).c.hashCode();
			return v1 == v2;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return c.hashCode();
	}
}