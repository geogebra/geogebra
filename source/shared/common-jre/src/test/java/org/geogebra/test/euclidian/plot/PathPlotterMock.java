package org.geogebra.test.euclidian.plot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.PathPlotter;
import org.geogebra.common.jre.util.NumberFormat;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.util.StringUtil;

public class PathPlotterMock implements PathPlotter {
	private final List<String> log = new ArrayList<>();
	NumberFormat nf = new NumberFormat("#.###", 5);
	private String delimiter = "|";

	@Override
	public void drawTo(double[] pos, SegmentType lineTo) {
		addLog("D " + lineTo, pos);
	}

	@Override
	public void lineTo(double[] pos) {
		addLog("L ", pos);
	}

	protected void addLog(String message, double[] pos) {
		log.add(message + " " + nf.format(pos[0]) + ", " + nf.format(pos[1]));
	}

	@Override
	public void moveTo(double[] pos) {
		addLog("M ", pos);
	}

	@Override
	public void corner() {
		log.add("COR ");
	}

	@Override
	public void corner(double[] pos) {
		addLog("COR ", pos);
	}

	@Override
	public void firstPoint(double[] pos, Gap moveToAllowed) {
		addLog("1ST " + (moveToAllowed == Gap.MOVE_TO ? "M " : "L "), pos);
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
		log.add("END");
	}

	@Override
	public boolean supports(CoordSys transformSys) {
		return true;
	}

	String result() {
		return StringUtil.join(delimiter, log);
	}

	@Override
	public String toString() {
		return result();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PathPlotterMock) {
			PathPlotterMock that = (PathPlotterMock) obj;
			return that.result().equals(result());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return result().hashCode();
	}

	public int size() {
		return log.size();
	}

	public String filter(Predicate<String> test) {
		return log.stream().filter(test).collect(Collectors.joining(","));
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}
