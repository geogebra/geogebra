package org.geogebra.common.spreadsheet;

import org.geogebra.common.awt.GGraphicsCommon;

public class StringCapturingGraphics extends GGraphicsCommon {

	StringBuilder sb = new StringBuilder();

	@Override
	public void drawString(String str, int x, int y) {
		sb.append(str).append("\n");
	}

	@Override
	public void drawString(String str, double x, double y) {
		sb.append(str).append("\n");
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
