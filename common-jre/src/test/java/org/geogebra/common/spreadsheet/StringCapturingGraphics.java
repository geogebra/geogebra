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
