package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoPolygonRegularND;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Regular polygon with 3D points
 * 
 * @author mathieu
 *
 */
public class AlgoPolygonRegular3D extends AlgoPolygonRegularND {

	private GeoDirectionND direction;

	/**
	 * constructor
	 * 
	 * @param c
	 * @param labels
	 * @param A1
	 * @param B1
	 * @param num
	 * @param direction
	 */
	public AlgoPolygonRegular3D(Construction c, String[] labels, GeoPointND A1,
			GeoPointND B1, NumberValue num, GeoDirectionND direction) {
		super(c, labels, A1, B1, num, direction);
	}

	@Override
	protected GeoPolygon newGeoPolygon(Construction cons) {
		return new GeoPolygon3D(cons);
	}

	@Override
	protected GeoElement newGeoPoint(Construction cons) {
		GeoPoint3D newPoint = new GeoPoint3D(cons);
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

	private Coords coordsA, coordsB, vAB, vDirection;

	@Override
	protected void setCenterPoint(int n, double beta) {

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
		((GeoPoint3D) point).rotate(rotAngle, centerPoint, direction);
	}

	@Override
	protected boolean checkUnDefined(int n) {

		boolean ret = super.checkUnDefined(n);

		coordsA = A.getInhomCoordsInD3();
		coordsB = B.getInhomCoordsInD3();
		vAB = coordsB.sub(coordsA);
		vDirection = direction.getDirectionInD3();

		if (!Kernel.isZero(vAB.dotproduct(vDirection))) {
			getPoly().setUndefined();

			// set also points (and thus segments) undefined
			for (int i = 0; i < outputPoints.size(); i++) {
				outputPoints.getElement(i).setUndefined();
			}

			numOld = 2;
			return true;
		}

		return ret;
	}

	public void calcCentroid(GeoPoint p) {
		// TODO Auto-generated method stub

	}

}
