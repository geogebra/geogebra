/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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