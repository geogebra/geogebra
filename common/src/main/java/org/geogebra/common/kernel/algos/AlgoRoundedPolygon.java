package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoRoundedPolygon extends AlgoElement {

	private GeoPointND[] points;
	private GeoLocus locus;
	private GeoNumberValue radius;

	public AlgoRoundedPolygon(Construction c, GeoPointND[] points,
			GeoNumberValue radius) {
		super(c);
		this.points = points;
		this.radius = radius;
		this.locus = new GeoLocus(c);
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		this.input = new GeoElement[points.length + 1];
		for (int i = 0; i < points.length; i++) {
			input[i] = points[i].toGeoElement();
		}
		input[input.length - 1] = radius.toGeoElement();
		setOnlyOutput(locus);
		setDependencies();

	}

	@Override
	public void compute() {
		locus.clearPoints();
		locus.insertPoint(cropX(0, 1), cropY(0, 1), SegmentType.MOVE_TO);
		for (int i = 0; i < points.length; i++) {
			int j = i == points.length - 1 ? 0 : i + 1;
			int k = j == points.length - 1 ? 0 : j + 1;
			locus.insertPoint(cropX(j, i), cropY(j, i), SegmentType.LINE_TO);
			locus.insertPoint(points[j].getInhomX(), points[j].getInhomY(),
					SegmentType.AUXILIARY);
			locus.insertPoint(cropX(j, k), cropY(j, k), SegmentType.ARC_TO);

		}

		locus.setDefined(true);
	}

	private double cropX(int i, int j) {
		return points[i].getInhomX()
				+ (points[j].getInhomX() - points[i].getInhomX())
				* radius.getDouble() / points[i].distance(points[j]);
	}

	private double cropY(int i, int j) {
		return points[i].getInhomY()
				+ (points[j].getInhomY() - points[i].getInhomY())
				* radius.getDouble() / points[i].distance(points[j]);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.RoundedPolygon;
	}

}
