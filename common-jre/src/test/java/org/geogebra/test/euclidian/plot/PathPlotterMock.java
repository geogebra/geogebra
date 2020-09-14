package org.geogebra.test.euclidian.plot;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.PathPlotter;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.util.StringUtil;

public class PathPlotterMock implements PathPlotter {
	private List<String> log = new ArrayList<>();

	@Override
	public void drawTo(double[] pos, SegmentType lineTo) {
		addLog("DRAWTO " + lineTo, pos);
	}

	@Override
	public void lineTo(double[] pos) {
		addLog("LINETO", pos);
	}

	protected void addLog(String message, double[] pos) {
		log.add(message + " " + pos[0] + ", " + pos[1]);
	}

	@Override
	public void moveTo(double[] pos) {
		addLog("MOVETO", pos);
	}

	@Override
	public void corner() {
		log.add("CORNER");
	}

	@Override
	public void corner(double[] pos) {
		addLog("CORNER", pos);
	}

	@Override
	public void firstPoint(double[] pos, Gap moveToAllowed) {
		addLog("FIRSTPOINT " + moveToAllowed, pos);
	}

	@Override
	public double[] newDoubleArray() {
		return new double[2];
	}

	@Override
	public boolean copyCoords(MyPoint point, double[] ret, CoordSys transformSys) {
		return false;
	}

	@Override
	public void endPlot() {
		log.add("ENDPLOT");
	}

	@Override
	public boolean supports(CoordSys transformSys) {
		return true;
	}

	String result() {
		return StringUtil.join(",", log);
	}
}
