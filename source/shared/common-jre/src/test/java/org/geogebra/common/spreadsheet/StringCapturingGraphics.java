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

package org.geogebra.common.spreadsheet;

import java.util.Comparator;
import java.util.TreeMap;

import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GPoint;

public final class StringCapturingGraphics extends GGraphicsCommon {

	private static class StringCoords {
		int x;
		int y;
		int layer;
		static int counter = 0;

		StringCoords(int x, int y) {
			this.x = x;
			this.y = y;
			this.layer = counter++;
		}
	}

	static Comparator<StringCoords> lexicographic = Comparator
			.<StringCoords>comparingInt(list -> list.y)
			.thenComparingInt(list -> list.x)
			.thenComparingInt(list -> list.layer);

	TreeMap<StringCoords, String> sb = new TreeMap<>(lexicographic);
	GPoint origin = new GPoint(0, 0);

	@Override
	public void drawString(String str, int x, int y) {
		sb.put(new StringCoords(x + origin.x, y + origin.y), str);
	}

	@Override
	public void drawString(String str, double x, double y) {
		drawString(str, (int) x, (int) y);
	}

	@Override
	public String toString() {
		return String.join(",", sb.values());
	}

	@Override
	public void translate(double tx, double ty) {
		origin.x += (int) tx;
		origin.y += (int) ty;
	}
}
