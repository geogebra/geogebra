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

package org.geogebra.test.euclidian.plot;

import java.util.ArrayList;
import java.util.List;

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

	protected int size() {
		return log.size();
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}
