package org.geogebra.test.euclidian.plot;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.matrix.CoordSys;

class GeneralPathClippedForCurvePlotterMock
		extends GeneralPathClippedForCurvePlotter {

	private final PathPlotterMock plotterMock;

	public GeneralPathClippedForCurvePlotterMock(EuclidianView view,
			PathPlotterMock plotterMock) {
		super(view);
		this.plotterMock = plotterMock;
	}

	@Override
	public void drawTo(double[] pos, SegmentType segmentType) {
		plotterMock.drawTo(pos, segmentType);
	}

	@Override
	public void lineTo(double[] pos) {
		plotterMock.lineTo(pos);
	}

	@Override
	public void moveTo(double[] pos) {
		plotterMock.moveTo(pos);
	}

	@Override
	public void corner() {
		plotterMock.corner();
	}

	@Override
	public void corner(double[] pos) {
		plotterMock.corner(pos);
	}

	@Override
	public void firstPoint(double[] pos, Gap moveToAllowed) {
		plotterMock.firstPoint(pos, moveToAllowed);
	}

	@Override
	public double[] newDoubleArray() {
		return plotterMock.newDoubleArray();
	}

	@Override
	public void endPlot() {
		plotterMock.endPlot();
	}

	@Override
	public boolean supports(CoordSys transformSys) {
		return plotterMock.supports(transformSys);
	}

	@Override
	public String toString() {
		return plotterMock.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeneralPathClippedForCurvePlotterMock) {
			return false;
		}
		return plotterMock.equals(obj);
	}

	@Override
	public int hashCode() {
		return plotterMock.hashCode();
	}

	public void setDelimiter(String delimiter) {
		plotterMock.setDelimiter(delimiter);
	}
}
