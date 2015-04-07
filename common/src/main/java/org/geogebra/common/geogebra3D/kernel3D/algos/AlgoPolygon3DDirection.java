package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * AlgoElement creating a GeoPolygon3D
 * 
 * @author ggb3D
 *
 */
public class AlgoPolygon3DDirection extends AlgoPolygon {

	/**
	 * Constructor with an 2D coord sys and points
	 * 
	 * @param cons
	 *            the construction
	 * @param labels
	 *            names of the polygon and segments
	 * @param points
	 *            vertices of the polygon
	 * @param direction
	 *            normal direction
	 */
	public AlgoPolygon3DDirection(Construction cons, String[] labels,
			GeoPointND[] points, GeoDirectionND direction) {
		super(cons, labels, points, null, null, true, null, direction);

	}

	@Override
	protected GeoElement[] createEfficientInput() {

		GeoElement[] efficientInput;

		if (geoList != null) {
			// list as input
			efficientInput = new GeoElement[2];
			efficientInput[0] = geoList;
			efficientInput[1] = (GeoElement) direction;
		} else {
			// points as input
			efficientInput = new GeoElement[points.length + 1];
			for (int i = 0; i < points.length; i++)
				efficientInput[i] = (GeoElement) points[i];
			efficientInput[points.length] = (GeoElement) direction;
		}

		return efficientInput;
	}

	/**
	 * create the polygon
	 * 
	 * @param createSegments
	 *            says if the polygon has to creates its edges (3D only)
	 */
	@Override
	protected void createPolygon(boolean createSegments) {
		poly = new GeoPolygon3D(cons, points, cs2D, createSegments);
	}

	@Override
	public void compute() {

		CoordSys coordsys = poly.getCoordSys();

		// recompute the coord sys
		coordsys.resetCoordSys();

		coordsys.addPoint(points[0].getInhomCoordsInD3());
		Coords[] v = direction.getDirectionInD3().completeOrthonormal();
		coordsys.addVector(v[0]);
		coordsys.addVector(v[1]);

		coordsys.makeOrthoMatrix(false, false);

		// check if a coord sys is possible
		if (((GeoPolygon3D) poly).checkPointsAreOnCoordSys())
			super.compute();

	}

	@Override
	protected void createStringBuilder(StringTemplate tpl) {

		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}

		String label;

		// G.Sturr: get label from geoList (2010-3-15)
		if (geoList != null) {
			label = geoList.getLabel(tpl);
		} else {
			// use point labels

			int last = points.length - 1;
			for (int i = 0; i < last; i++) {
				sb.append(points[i].getLabel(tpl));
				sb.append(", ");
			}
			sb.append(points[last].getLabel(tpl));

			label = sb.toString();
			sb.setLength(0);
		}

		sb.append(getLoc().getPlain("PolygonAParallelToB", label,
				((GeoElement) direction).getLabel(tpl)));
	}

}
