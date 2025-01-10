package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoPolygonRegularND;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Regular polygon with 3D points
 * 
 * @author mathieu
 *
 */
public class AlgoPolygonRegular3D extends AlgoPolygonRegularND {

	private GeoDirectionND direction;
	private Coords coordsA;
	private Coords coordsB;
	private Coords vAB;
	private Coords vDirection;

	/**
	 * constructor
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param A1
	 *            first vertex
	 * @param B1
	 *            second vertex
	 * @param num
	 *            number of vertices
	 * @param direction
	 *            direction of the plane
	 */
	public AlgoPolygonRegular3D(Construction c, String[] labels, GeoPointND A1,
			GeoPointND B1, GeoNumberValue num, GeoDirectionND direction) {
		super(c, labels, A1, B1, num, direction);
	}

	@Override
	protected GeoPolygon newGeoPolygon(Construction cons1) {
		return new GeoPolygon3D(cons1);
	}

	@Override
	protected GeoElement newGeoPoint(Construction cons1) {
		GeoPoint3D newPoint = new GeoPoint3D(cons1);
		newPoint.setCoords(0, 0, 0, 1);
		return newPoint;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = (GeoElement) A;
		input[1] = (GeoElement) B;
		input[2] = num.toGeoElement();
		input[3] = (GeoElement) direction;
		// set dependencies
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
		cons.addToAlgorithmList(this);

		// setOutput(); done in compute

		// parent of output
		getPoly().setParentAlgorithm(this);

	}

	@Override
	protected void setDirection(GeoDirectionND direction) {
		this.direction = direction;
	}

	@Override
	protected void setCenterPoint(double beta) {

		// some temp values
		Coords m = coordsA.add(coordsB).mul(0.5);

		// normal vector of AB and direction
		Coords vn = vDirection.crossProduct4(vAB);

		// center point of regular polygon
		double tanBetaHalf = Math.tan(beta) / 2;
		((GeoPoint3D) centerPoint).setCoords(m.add(vn.mul(tanBetaHalf)));

	}

	@Override
	protected void rotate(GeoPointND point) {
		((GeoPoint3D) point).rotate(rotAngle, centerPoint.getInhomCoordsInD3(), direction);
	}

	@Override
	protected boolean checkUnDefined(int n) {

		boolean ret = super.checkUnDefined(n);

		coordsA = A.getInhomCoordsInD3();
		coordsB = B.getInhomCoordsInD3();
		vAB = coordsB.sub(coordsA);
		vDirection = direction.getDirectionInD3();

		if (!DoubleUtil.isZero(vAB.dotproduct(vDirection))) {
			getPoly().setUndefined();

			// set also points (and thus segments) undefined
			for (int i = 0; i < outputPoints.size(); i++) {
				outputPoints.getElement(i).setUndefined();
			}
			if (getPoly().getPointsND() == null) {
				getPoly().setPoints(new GeoPointND[] { A, B }, null, false);
			}

			numOld = 2;
			return true;
		}

		return ret;
	}

	@Override
	public void calcCentroid(GeoPoint p) {
		// TODO Auto-generated method stub

	}

}
