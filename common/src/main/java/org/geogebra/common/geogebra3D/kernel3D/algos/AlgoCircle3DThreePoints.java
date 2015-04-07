package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 * 
 *         Algo that creates a 3D circle joining three 3D points
 *
 */
public class AlgoCircle3DThreePoints extends AlgoCircleThreePoints {

	/** coord sys defined by the three points where the 3D circle lies */
	private CoordSys coordSys;

	/** 2D projection of the 3D points in the coord sys */
	private GeoPoint[] points2D;

	/** 3D points */
	private GeoPointND[] points;

	/**
	 * Basic constructor
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            name of the circle
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 */
	public AlgoCircle3DThreePoints(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C) {
		super(cons, label, A, B, C);

	}

	/**
	 * Basic constructor
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 */
	public AlgoCircle3DThreePoints(Construction cons, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		super(cons, A, B, C);

	}

	@Override
	protected void setPoints(GeoPointND A, GeoPointND B, GeoPointND C) {

		points = new GeoPointND[3];

		points[0] = A;
		points[1] = B;
		points[2] = C;

		coordSys = new CoordSys(2);

		points2D = new GeoPoint[3];
		for (int i = 0; i < 3; i++)
			points2D[i] = new GeoPoint(getConstruction());

		super.setPoints(points2D[0], points2D[1], points2D[2]);

	}

	/**
	 * 
	 * @param i
	 *            index
	 * @return i-th 2D point
	 */
	public GeoPoint getPoint2D(int i) {
		return points2D[i];
	}

	@Override
	protected void createCircle() {

		circle = new GeoConic3D(cons, coordSys);
	}

	@Override
	protected void setInput() {
		input = new GeoElement[3];
		for (int i = 0; i < 3; i++)
			input[i] = (GeoElement) points[i];

	}

	@Override
	protected void setOutput() {

		setOnlyOutput(circle);

	}

	@Override
	public void compute() {

		coordSys.resetCoordSys();
		for (int i = 0; i < 3; i++)
			coordSys.addPoint(points[i].getInhomCoordsInD3());

		/*
		 * if (!coordSys.makeOrthoMatrix(false,false)){ circle.setUndefined();
		 * return; }
		 */

		if (!coordSys.isMadeCoordSys()) {
			// force 2D coord sys (for line, or single point)
			coordSys.completeCoordSys2D();
		}

		coordSys.makeOrthoMatrix(false, false);

		// App.debug("coordSys=\n"+coordSys.getMatrixOrthonormal().toString());

		for (int i = 0; i < 3; i++) {
			// project the point on the coord sys
			// Coords[]
			// project=points[i].getCoordsInD3().projectPlane(coordSys.getMatrixOrthonormal());
			Coords[] project = coordSys.getNormalProjection(points[i]
					.getInhomCoordsInD3());
			// set the 2D points
			points2D[i].setCoords(project[1].getX(), project[1].getY(),
					project[1].getW());

		}

		super.compute();

	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("CircleThroughABC", points[0].getLabel(tpl),
				points[1].getLabel(tpl), points[2].getLabel(tpl));
	}
}
