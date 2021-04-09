package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawConic3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDependentQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolver;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.geos.XMLBuilder;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.kernel.kernelND.Region3D;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * class describing quadric for 3D space
 * 
 * @author Mathieu
 * 
 *                  ( A[0] A[4] A[5] A[7]) 
 *         matrix = ( A[4] A[1] A[6] A[8]) 
 *                  ( A[5] A[6] A[2] A[9]) 
 *                  ( A[7] A[8] A[9] A[3])
 * 
 */
public class GeoQuadric3D extends GeoQuadricND implements Functional2Var,
		Region3D, Translateable, RotateableND, MirrorableAtPlane, Transformable,
		Dilateable, HasVolume, GeoQuadric3DInterface, EquationValue {

	private static String[] vars3D = { "x\u00b2", "y\u00b2", "z\u00b2", "x y",
			"x z", "y z", "x", "y", "z" };

	private static String[] vars3DCAS = { "x^2", "y^2", "z^2",
			"x*y", "x*z", "y*z", "x", "y", "z" };

	private CoordMatrix4x4 eigenMatrix = CoordMatrix4x4.identity();
	/** helper for 2d projection */
	protected double[] tmpDouble2 = new double[2];
	private double detS;

	private CoordMatrix tmpMatrix3x3;

	private GeoPlane3D[] planes;

	private GeoLine3D line;

	private CoordMatrix eigenvecNDMatrix;
	private CoordMatrix semiDiagMatrix;

	private Coords tmpCoords2;
	private Coords tmpCoords3;
	private Coords tmpCoords4;
	private Coords tmpCoords5;
	private CoordMatrix tmpMatrix4x2;
	private CoordMatrix tmpMatrix2x4;
	private CoordMatrix4x4 tmpMatrix4x4;
	private double[] tmpEqn;

	private Coords tmpCoords = new Coords(4);

	private double[] lastHitParameters = null;

	private Coords[] eigenvec;

	private double volume = Double.NaN;

	private boolean showUndefinedInAlgebraView = false;
	private Coords tmpCoords6;

	/**
	 * @param c
	 *            construction
	 */
	public GeoQuadric3D(Construction c) {
		super(c, 3);

		// TODO merge with 2D eigenvec
		eigenvecND = new Coords[3];
		for (int i = 0; i < 3; i++) {
			eigenvecND[i] = new Coords(4);
			eigenvecND[i].set(i + 1, 1);
		}

		// diagonal (diagonalized matrix)
		diagonal = new double[4];

	}

	/**
	 * Creates new GeoQuadric3D
	 * 
	 * @param c
	 *            construction
	 * @param coeffs
	 *            coefficients
	 */
	public GeoQuadric3D(Construction c, double[] coeffs) {
		this(c);
		setMatrix(coeffs);
	}

	/**
	 * sets quadric's matrix from coefficients of equation from array
	 * 
	 * @param coeffs
	 *            Array of coefficients
	 */
	final public void setMatrix(double[] coeffs) {
		for (int i = 0; i < 10; i++) {
			matrix[i] = coeffs[i];
		}

		// try to classify quadric
		classifyQuadric();
	}

	@Override
	final public void setMatrixFromXML(double[] coeffs) {
		for (int i = 0; i < 10; i++) {
			matrix[i] = coeffs[i];
		}
		ensureClassified();
	}

	@Override
	public void ensureClassified() {
		if (type == GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED) {
			classifyQuadric();
		}
	}

	@Override
	public void hideIfNotSphere() {
		if (type != QUADRIC_SPHERE && type != QUADRIC_SINGLE_POINT) {
			setEuclidianVisible(false);
		}
	}

	/**
	 * Update quadric type and properties
	 */
	protected void classifyQuadric() {
		defined = checkDefined();
		if (!defined) {
			return;
		}

		double max = Math.abs(matrix[0]);
		for (int i = 1; i < 3; i++) {
			double v = Math.abs(matrix[i]);
			if (v > max) {
				max = v;
			}
		}
		for (int i = 4; i < 7; i++) {
			double v = Math.abs(matrix[i]);
			if (v > max) {
				max = v;
			}
		}

		// det of S
		detS = matrix[0] * matrix[1] * matrix[2]
				- matrix[0] * matrix[6] * matrix[6]
				- matrix[1] * matrix[5] * matrix[5]
				- matrix[2] * matrix[4] * matrix[4]
				+ 2 * matrix[4] * matrix[5] * matrix[6];

		double v =  max * max * max;

		if (DoubleUtil.isEpsilonWithPrecision(detS, v, Kernel.STANDARD_PRECISION_CUBE)) {
			classifyNoMidpointQuadric();
		} else {
			classifyMidpointQuadric();
		}

	}

	private void classifyNoMidpointQuadric() {

		// no midpoint, detS == 0

		// set eigenvalues
		eigenval[0] = -matrix[0] * matrix[1] - matrix[1] * matrix[2]
				- matrix[2] * matrix[0] + matrix[4] * matrix[4]
				+ matrix[5] * matrix[5] + matrix[6] * matrix[6];
		eigenval[1] = matrix[0] + matrix[1] + matrix[2];
		eigenval[2] = -1;

		int nRoots = EquationSolver.solveQuadraticS(eigenval, eigenval,
				Kernel.STANDARD_PRECISION);

		if (nRoots == 1) {
			eigenval[1] = eigenval[0];
		}

		eigenval[2] = 0;

		// Log.debug("eigenvals = " + eigenval[0] + "," + eigenval[1] + ","
		// + eigenval[2]);

		// check if others eigenvalues are 0
		if (DoubleUtil.isZero(eigenval[0])) {
			if (DoubleUtil.isZero(eigenval[1])) {
				// three eigenvalues = 0: one plane
				getPlanes();
				planes[0].setEquation(matrix[7], matrix[8], matrix[9],
						matrix[3] / 2);
				planes[0].getCoordSys().makeEquationVector();
				type = GeoQuadricNDConstants.QUADRIC_PLANE;

			} else {
				// two eigenvalues = 0
				twoZeroEigenvalues(eigenval[1]);
			}
		} else if (DoubleUtil.isZero(eigenval[1])) {
			// two eigenvalues = 0
			twoZeroEigenvalues(eigenval[0]);
		} else {
			// only one eigenvalue = 0

			// find eigenvectors
			if (tmpCoords2 == null) {
				tmpCoords2 = new Coords(4);
			}
			if (tmpCoords3 == null) {
				tmpCoords3 = new Coords(4);
			}
			tmpCoords.setValues(eigenvecND[0], 3);
			tmpCoords2.setValues(eigenvecND[2], 3);

			if (DoubleUtil.isRatioEqualTo1(eigenval[0], eigenval[1])) {
				// find from eigenvalue = 0, since both others are equal
				findEigenvector(eigenval[2], eigenvecND[2]);
				eigenvecND[2].normalize();
				eigenvecND[2].completeOrthonormal(eigenvecND[0], eigenvecND[1]);
			} else {
				findEigenvector(eigenval[0], eigenvecND[0]);
				eigenvecND[0].normalize();
				findEigenvector(eigenval[1], eigenvecND[1]);
				eigenvecND[1].normalize();
				eigenvecND[2].setCrossProduct4(eigenvecND[0], eigenvecND[1]);
			}

			// check eigenvec continuity
			boolean reverse = false;
			if (tmpCoords2.dotproduct3(eigenvecND[2]) < 0) { // reverse
				eigenvecND[2].mulInside3(-1);
				reverse = true;
			}
			double dot0 = tmpCoords.dotproduct3(eigenvecND[0]);
			double dot1 = tmpCoords.dotproduct3(eigenvecND[1]);
			if (Math.abs(dot0) < Math.abs(dot1)) { // swap
				double e = eigenval[0];
				eigenval[0] = eigenval[1];
				eigenval[1] = e;
				tmpCoords.setValues(eigenvecND[0], 3);
				eigenvecND[0].setValues(eigenvecND[1], 3);
				eigenvecND[1].setValues(tmpCoords, 3);
				reverse = !reverse;
				dot0 = dot1;
			}

			if (reverse) {
				if (dot0 < 0) { // flip first
					eigenvecND[0].mulInside3(-1);
				} else { // flip second
					eigenvecND[1].mulInside3(-1);
				}
			} else {
				if (dot0 < 0) { // flip first and second
					eigenvecND[0].mulInside3(-1);
					eigenvecND[1].mulInside3(-1);
				}
			}

			// compute semi-diagonalized matrix
			setSemiDiagonalizedMatrix();

			double x = semiDiagMatrix.get(1, 4);
			double y = semiDiagMatrix.get(2, 4);
			double z = semiDiagMatrix.get(3, 4);
			double d = semiDiagMatrix.get(4, 4);

			// check other eigenvalues
			if (eigenval[0] * eigenval[1] > 0) {
				double m = x * x / eigenval[0] + y * y / eigenval[1] - d;
				if (DoubleUtil.isZero(z)) {
					// cylinder
					if (DoubleUtil.isZero(m)) {
						// single line
						singleLine(-x / eigenval[0], -y / eigenval[1]);
					} else if (eigenval[0] * m < 0) {
						// empty
						defined = false;
						empty();
					} else {
						// cylinder
						cylinder(-x / eigenval[0], -y / eigenval[1], m);
					}
				} else {
					// z = xx+yy
					paraboloid(-x / eigenval[0], -y / eigenval[1], z, m);
				}

			} else { // x and y eigenvalue of different signs
				double m = x * x / eigenval[0] + y * y / eigenval[1] - d;
				if (DoubleUtil.isZero(z)) {
					// cylinder
					if (DoubleUtil.isZero(m)) {
						// intersecting planes
						intersectingPlanes(-x / eigenval[0], -y / eigenval[1]);
					} else {
						// xx - yy = c : hyperbolic cylinder
						hyperbolicCylinder(-x / eigenval[0], -y / eigenval[1],
								m);
					}
				} else {
					// z = xx - yy
					hyperbolicParaboloid(-x / eigenval[0], -y / eigenval[1], z,
							m);
				}
			}

		}

	}

	private void paraboloid(double x, double y, double z, double m) {

		// set midpoint
		midpoint.set(Coords.O);
		midpoint.addInside(tmpCoords.setMul(eigenvecND[0], x));
		midpoint.addInside(tmpCoords.setMul(eigenvecND[1], y));
		midpoint.addInside(tmpCoords.setMul(eigenvecND[2], m / (2 * z)));

		// set halfAxes = radius
		halfAxes[0] = Math.sqrt(Math.abs(2 * z / eigenval[0]));
		halfAxes[1] = Math.sqrt(Math.abs(2 * z / eigenval[1]));
		if (z * eigenval[0] < 0) {
			halfAxes[2] = 1;
		} else {
			halfAxes[2] = -1;
		}

		// set the diagonal values
		diagonal[0] = eigenval[0];
		diagonal[1] = eigenval[1];
		diagonal[2] = eigenval[2];
		diagonal[3] = 0;

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], halfAxes[2]);

		// set type
		type = QUADRIC_PARABOLOID;
	}

	private void singleLine(double x, double y) {

		// set midpoint
		midpoint.set(Coords.O);
		midpoint.addInside(tmpCoords.setMul(eigenvecND[0], x));
		midpoint.addInside(tmpCoords.setMul(eigenvecND[1], y));

		// set line
		getLine();
		line.setCoord(midpoint, eigenvecND[2]);

		type = GeoQuadricNDConstants.QUADRIC_LINE;
	}

	private void findEigenvector(double value, Coords ret) {
		if (tmpMatrix3x3 == null) {
			tmpMatrix3x3 = new CoordMatrix(3, 3);
		}

		tmpMatrix3x3.set(1, 1, matrix[0] - value);
		tmpMatrix3x3.set(2, 2, matrix[1] - value);
		tmpMatrix3x3.set(3, 3, matrix[2] - value);

		tmpMatrix3x3.set(1, 2, matrix[4]);
		tmpMatrix3x3.set(2, 1, matrix[4]);
		tmpMatrix3x3.set(1, 3, matrix[5]);
		tmpMatrix3x3.set(3, 1, matrix[5]);
		tmpMatrix3x3.set(2, 3, matrix[6]);
		tmpMatrix3x3.set(3, 2, matrix[6]);

		// Log.debug("\n=================================\nvalue = " + value);

		ret.setX(0);
		ret.setY(0);
		ret.setZ(0);
		ret.setW(0);
		tmpMatrix3x3.pivotDegenerate(ret, Coords.ZERO);

		// Log.debug("\nvalue = " + value + "\nmatrix = \n" + tmpMatrix3x3
		// + "\nsol = \n" + ret);
	}

	/**
	 * 
	 * @return planes (for degenerate cases)
	 */
	public GeoPlane3D[] getPlanes() {
		if (planes == null) {
			planes = new GeoPlane3D[2];
			planes[0] = new GeoPlane3D(cons);
			planes[1] = new GeoPlane3D(cons);
		}

		return planes;
	}

	/**
	 * 
	 * @return line (for degenerate case)
	 */
	public GeoLine3D getLine() {
		if (line == null) {
			line = new GeoLine3D(cons);
		}
		return line;
	}

	private void setSemiDiagonalizedMatrix() {
		if (eigenvecNDMatrix == null) {
			eigenvecNDMatrix = new CoordMatrix(eigenvecND[0], eigenvecND[1],
					eigenvecND[2], Coords.O);
		}
		if (semiDiagMatrix == null) {
			semiDiagMatrix = new CoordMatrix(4, 4);
		}
		eigenvecNDMatrix.transposeCopy(semiDiagMatrix);
		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		tmpMatrix4x4.setMul(semiDiagMatrix, getSymetricMatrix());
		semiDiagMatrix.setMul(tmpMatrix4x4, eigenvecNDMatrix);
	}

	private void twoZeroEigenvalues(double value) {

		// get main eigenvector
		tmpCoords.setValues(eigenvecND[2], 3);
		findEigenvector(value, eigenvecND[2]);
		eigenvecND[2].normalize();
		if (tmpCoords.dotproduct3(eigenvecND[2]) < 0) {
			eigenvecND[2].mulInside3(-1);
		}

		// compute other eigenvectors
		completeOrthonormalRatioEqualTo1(eigenvecND[0], eigenvecND[1],
				eigenvecND[2]);

		// compute semi-diagonalized matrix
		setSemiDiagonalizedMatrix();

		// check degree 1 coeffs
		if (!DoubleUtil.isZero(semiDiagMatrix.get(1, 4))
				|| !DoubleUtil.isZero(semiDiagMatrix.get(2, 4))) {
			// parabolic cylinder
			parabolicCylinder(value);
		} else {
			// parallel planes or empty
			double a = semiDiagMatrix.get(3, 4);
			double b = semiDiagMatrix.get(4, 4);

			// get case
			double c = a / value;
			double m = c * c - b / value;
			if (DoubleUtil.isZero(m)) {
				parallelPlanes(0, c);
			} else if (m > 0) {
				parallelPlanes(Math.sqrt(m), c);
			} else { // m < 0
				defined = false;
				empty();
			}
		}

	}

	private void parallelPlanes(double shift, double c) {

		// update planes
		getPlanes();

		CoordSys cs = planes[0].getCoordSys();
		cs.resetCoordSys();
		tmpCoords.setMul(eigenvecND[2], -shift - c);
		tmpCoords.setW(1);
		cs.setOrigin(tmpCoords);
		cs.setVx(eigenvecND[0]);
		cs.setVy(eigenvecND[1]);
		cs.setVz(eigenvecND[2]);
		cs.setMatrixOrthonormalAndDrawingMatrix();
		cs.setMadeCoordSys();
		cs.makeEquationVector();

		cs = planes[1].getCoordSys();
		cs.resetCoordSys();
		tmpCoords.setMul(eigenvecND[2], shift - c);
		tmpCoords.setW(1);
		cs.setOrigin(tmpCoords);
		cs.setVx(eigenvecND[0]);
		cs.setVy(eigenvecND[1]);
		cs.setVz(eigenvecND[2]);
		cs.setMatrixOrthonormalAndDrawingMatrix();
		cs.setMadeCoordSys();
		cs.makeEquationVector();

		type = GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES;
	}

	private void parabolicCylinder(double value) {

		final double a = semiDiagMatrix.get(1, 4);
		final double b = semiDiagMatrix.get(2, 4);
		final double c = semiDiagMatrix.get(3, 4);
		final double d = semiDiagMatrix.get(4, 4);

		// set ev0 = (a*ev0+b*ev1)/norm and ev1 = (a*ev0-b*ev1)/norm
		double norm = Math.sqrt(a * a + b * b);
		eigenvecND[0].setMul(eigenvecND[0], a / norm);
		tmpCoords.setMul(eigenvecND[1], b / norm);
		eigenvecND[0].setAdd(eigenvecND[0], tmpCoords);
		double valSgn = -1;
		if (value > 0) {
			valSgn = 1;
			eigenvecND[0].mulInside3(-1);
		}
		eigenvecND[1].setCrossProduct4(eigenvecND[2], eigenvecND[0]);

		// for (int i = 0; i < 3; i++) {
		// Log.debug("eigenvecND[" + i + "]=\n" + eigenvecND[i]);
		// }

		// set eigenvalues
		eigenval[0] = 0;
		eigenval[1] = 0;
		eigenval[2] = value;

		// set midpoint
		midpoint.set(Coords.O);
		midpoint.addInside(tmpCoords.setMul(eigenvecND[0],
				valSgn * (d - c * c / value) / (2 * norm)));
		midpoint.addInside(tmpCoords.setMul(eigenvecND[2], -c / value));

		// set halfAxes = radius
		halfAxes[0] = 1;
		halfAxes[1] = 1;
		halfAxes[2] = Math.sqrt(2 * norm / Math.abs(value));
		//
		// // set the diagonal values
		diagonal[0] = eigenval[0];
		diagonal[1] = eigenval[1];
		diagonal[2] = eigenval[2];
		diagonal[3] = 0;

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], halfAxes[2]);

		// set type
		type = QUADRIC_PARABOLIC_CYLINDER;

	}

	private void hyperbolicCylinder(double x0, double y0, double m) {

		mu[0] = eigenval[0] / m;
		double x, y;

		if (mu[0] > 0) {
			mu[1] = eigenval[1] / m;

			x = x0;
			y = y0;
		} else { // flip eigens
			mu[1] = mu[0];
			mu[0] = eigenval[1] / m;

			double v = eigenval[1];
			eigenval[1] = eigenval[0];
			eigenval[0] = v;

			tmpCoords.setValues(eigenvecND[1], 3);
			eigenvecND[1].setValues(eigenvecND[0], 3);
			eigenvecND[0].setValues(tmpCoords, 3);
			eigenvecND[2].mulInside3(-1);

			x = y0;
			y = x0;
		}

		// set midpoint
		midpoint.set(Coords.O);
		midpoint.addInside(tmpCoords.setMul(eigenvecND[0], x));
		midpoint.addInside(tmpCoords.setMul(eigenvecND[1], y));

		// set halfAxes = radius
		halfAxes[0] = Math.sqrt(1.0d / mu[0]);
		halfAxes[1] = Math.sqrt(-1.0d / mu[1]);
		halfAxes[2] = 1;

		// set the diagonal values
		diagonal[0] = eigenval[0];
		diagonal[1] = eigenval[1];
		diagonal[2] = 0;
		diagonal[3] = -m;

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], 1);

		// set type
		type = QUADRIC_HYPERBOLIC_CYLINDER;

	}

	private void hyperbolicParaboloid(double x, double y, double z, double m) {

		// set midpoint
		midpoint.set(Coords.O);
		midpoint.addInside(tmpCoords.setMul(eigenvecND[0], x));
		midpoint.addInside(tmpCoords.setMul(eigenvecND[1], y));
		midpoint.addInside(tmpCoords.setMul(eigenvecND[2], m / (2 * z)));

		// set halfAxes = radius
		halfAxes[0] = eigenval[0] / (-2 * z);
		halfAxes[1] = eigenval[1] / (-2 * z);
		halfAxes[2] = 1;

		// set the diagonal values
		diagonal[0] = eigenval[0];
		diagonal[1] = eigenval[1];
		diagonal[2] = eigenval[2];
		diagonal[3] = 0;

		// eigen matrix
		setEigenMatrix(1, 1, 1);

		// set type
		type = QUADRIC_HYPERBOLIC_PARABOLOID;

	}

	private void intersectingPlanes(double dx, double dy) {

		// update planes
		getPlanes();

		if (tmpCoords2 == null) {
			tmpCoords2 = new Coords(4);
		}
		if (tmpCoords3 == null) {
			tmpCoords3 = new Coords(4);
		}
		if (tmpCoords4 == null) {
			tmpCoords4 = new Coords(4);
		}
		if (tmpCoords5 == null) {
			tmpCoords5 = new Coords(4);
		}

		tmpCoords4.setMul(eigenvecND[0], dx);
		tmpCoords5.setMul(eigenvecND[1], dy);
		tmpCoords4.addInside(tmpCoords5);
		tmpCoords4.setW(1);

		CoordSys cs = planes[0].getCoordSys();
		cs.resetCoordSys();
		cs.addPoint(tmpCoords4);
		cs.addVectorWithoutCheckMadeCoordSys(eigenvecND[2]);
		tmpCoords.setMul(eigenvecND[0], Math.sqrt(-eigenval[1] / eigenval[0]));
		tmpCoords2.setMul(eigenvecND[1], 1);
		tmpCoords3.setAdd(tmpCoords, tmpCoords2);
		cs.addVectorWithoutCheckMadeCoordSys(tmpCoords3);
		cs.makeOrthoMatrix(false, false);
		cs.makeEquationVector();

		cs = planes[1].getCoordSys();
		cs.resetCoordSys();
		cs.addPoint(tmpCoords4);
		cs.addVectorWithoutCheckMadeCoordSys(eigenvecND[2]);
		tmpCoords3.setSub(tmpCoords, tmpCoords2);
		cs.addVectorWithoutCheckMadeCoordSys(tmpCoords3);
		cs.makeOrthoMatrix(false, false);
		cs.makeEquationVector();

		type = GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES;

	}

	private void classifyMidpointQuadric() {

		// set midpoint
		double x = (-matrix[1] * matrix[2] * matrix[7]
				+ matrix[1] * matrix[5] * matrix[9]
				+ matrix[2] * matrix[4] * matrix[8]
				- matrix[4] * matrix[6] * matrix[9]
				- matrix[5] * matrix[6] * matrix[8]
				+ matrix[6] * matrix[6] * matrix[7]) / detS;
		double y = (-matrix[0] * matrix[2] * matrix[8]
				+ matrix[0] * matrix[6] * matrix[9]
				+ matrix[2] * matrix[4] * matrix[7]
				- matrix[4] * matrix[5] * matrix[9]
				+ matrix[5] * matrix[5] * matrix[8]
				- matrix[5] * matrix[6] * matrix[7]) / detS;
		double z = (-matrix[0] * matrix[1] * matrix[9]
				+ matrix[0] * matrix[6] * matrix[8]
				+ matrix[1] * matrix[5] * matrix[7]
				+ matrix[4] * matrix[4] * matrix[9]
				- matrix[4] * matrix[5] * matrix[8]
				- matrix[4] * matrix[6] * matrix[7]) / detS;
		double[] coords = { x, y, z, 1 };
		setMidpoint(coords);

		// Log.debug("\nmidpoint = " + x + "," + y + "," + z);

		// set eigenvalues
		eigenval[0] = detS;
		eigenval[1] = -matrix[0] * matrix[1] - matrix[1] * matrix[2]
				- matrix[2] * matrix[0] + matrix[4] * matrix[4]
				+ matrix[5] * matrix[5] + matrix[6] * matrix[6];
		eigenval[2] = matrix[0] + matrix[1] + matrix[2];
		eigenval[3] = -1;

		int nRoots = EquationSolver.solveCubicS(eigenval,
				eigenval, Kernel.STANDARD_PRECISION);

		if (nRoots == 1) {
			eigenval[1] = eigenval[0];
			nRoots++;
		}

		if (nRoots == 2) {
			eigenval[2] = eigenval[1];
		}

		// degenerate ? (beta is det of 4x4 matrix)
		double beta = matrix[7] * x + matrix[8] * y + matrix[9] * z + matrix[3];

		if (DoubleUtil.isZero(beta)) {
			cone();
		} else {
			if (eigenval[0] > 0) {
				if (eigenval[1] > 0) {
					if (eigenval[2] > 0) { // xx+yy+zz=-beta
						if (beta > 0) {
							empty();
						} else {
							ellipsoid(beta);
						}
					} else { // xx+yy-zz=-beta
						if (beta > 0) { // zz-xx-yy=1
							hyperboloidTwoSheets(eigenval[2], eigenval[0],
									eigenval[1], beta);
						} else { // xx+yy-zz=1
							hyperboloidOneSheet(eigenval[0], eigenval[1],
									eigenval[2], beta);
						}
					}
				} else { // eigenval[1] < 0
					if (eigenval[2] > 0) { // xx-yy+zz=-beta
						if (beta > 0) { // yy-zz-xx=1
							hyperboloidTwoSheets(eigenval[1], eigenval[2],
									eigenval[0], beta);
						} else { // zz+xx-yy=1
							hyperboloidOneSheet(eigenval[2], eigenval[0],
									eigenval[1], beta);
						}
					} else { // xx-yy+zz=-beta
						if (beta > 0) { // yy-zz-xx=1
							hyperboloidTwoSheets(eigenval[1], eigenval[2],
									eigenval[0], beta);
						} else { // zz+xx-yy=1
							hyperboloidOneSheet(eigenval[2], eigenval[0],
									eigenval[1], beta);
						}
					}
				}
			} else { // eigenval[0] < 0
				if (eigenval[1] > 0) {
					if (eigenval[2] > 0) { // -xx+yy+zz=-beta
						if (beta > 0) { // xx-yy-zz=1
							hyperboloidTwoSheets(eigenval[0], eigenval[1],
									eigenval[2], beta);
						} else { // yy+zz-xx=1
							hyperboloidOneSheet(eigenval[1], eigenval[2],
									eigenval[0], beta);
						}
					} else { // -xx+yy-zz=-beta
						if (beta > 0) { // zz+xx-yy=1
							hyperboloidOneSheet(eigenval[2], eigenval[0],
									eigenval[1], beta);
						} else { // yy-zz-xx=1
							hyperboloidTwoSheets(eigenval[1], eigenval[2],
									eigenval[0], beta);
						}
					}
				} else { // eigenval[1] < 0
					if (eigenval[2] > 0) { // -xx-yy+zz=-beta
						if (beta > 0) { // xx+yy-zz=1
							hyperboloidOneSheet(eigenval[0], eigenval[1],
									eigenval[2], beta);
						} else { // zz-xx-yy=1
							hyperboloidTwoSheets(eigenval[2], eigenval[0],
									eigenval[1], beta);
						}
					} else { // -xx-yy-zz=-beta
						if (beta > 0) {
							ellipsoid(beta);
						} else {
							empty();
						}
					}
				}
			}
		}

	}

	/**
	 * xx+yy-zz=1
	 * 
	 * @param val0
	 *            xx coef
	 * @param val1
	 *            yy coef
	 * @param val2
	 *            zz coef
	 * @param beta
	 *            constant coef
	 */
	private void hyperboloidOneSheet(double val0, double val1, double val2,
			double beta) {
		// Log.debug("hyperboloidOneSheet : " + val0 + "," + val1 + "," + val2);

		eigenval[0] = val0;
		eigenval[1] = val1;
		eigenval[2] = val2;

		setHyperboloidEigenvectors();

		mu[0] = -eigenval[0] / beta;
		mu[1] = -eigenval[1] / beta;
		mu[2] = -eigenval[2] / beta;

		// set halfAxes = radius
		halfAxes[0] = Math.sqrt(1.0d / mu[0]);
		halfAxes[1] = Math.sqrt(1.0d / mu[1]);

		double min = Math.min(mu[0], mu[1]);
		double e3;
		if (DoubleUtil.isEpsilonToX(mu[2], min)) {
			halfAxes[2] = Double.POSITIVE_INFINITY;
			e3 = 1;
		} else {
			halfAxes[2] = Math.sqrt(-1.0d / mu[2]);
			e3 = halfAxes[2];
		}

		// set the diagonal values
		for (int i = 0; i < 3; i++) {
			diagonal[i] = mu[i];
		}
		diagonal[3] = -1;

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], e3);

		// set type
		type = QUADRIC_HYPERBOLOID_ONE_SHEET;

	}

	private void hyperboloidTwoSheets(double val0, double val1, double val2,
			double beta) {
		// Log.debug("hyperboloidTwoSheets : " + val0 + "," + val1 + "," +
		// val2);
		eigenval[0] = val1;
		eigenval[1] = val2;
		eigenval[2] = val0;

		setHyperboloidEigenvectors();

		mu[0] = -eigenval[0] / beta;
		mu[1] = -eigenval[1] / beta;
		mu[2] = -eigenval[2] / beta;

		// set halfAxes = radius
		halfAxes[0] = Math.sqrt(-1.0d / mu[0]);
		halfAxes[1] = Math.sqrt(-1.0d / mu[1]);
		halfAxes[2] = Math.sqrt(1.0d / mu[2]);

		// set the diagonal values
		for (int i = 0; i < 3; i++) {
			diagonal[i] = mu[i];
		}
		diagonal[3] = -1;

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], halfAxes[2]);

		// set type
		type = QUADRIC_HYPERBOLOID_TWO_SHEETS;
	}

	/**
	 * 1st and 2nd eigenvalues are equal
	 * 
	 * @param ev0
	 *            old eigenvector, 1st value
	 * @param ev1
	 *            old eigenvector, 2nd value
	 * @param ev2
	 *            updated eigenvector, 3rd value
	 */
	private void completeOrthonormalRatioEqualTo1(Coords ev0, Coords ev1,
			Coords ev2) {
		// try to keep ev0
		tmpCoords.setCrossProduct4(ev2, ev0);
		if (!tmpCoords.isZero(3)) {
			// we can set ev1 to this cross product
			ev1.setValues(tmpCoords, 3);
			ev1.normalize();
			// update ev0 to get orthonormal vectors
			ev0.setCrossProduct3(ev1, ev2);
			ev0.normalize();
		} else if (!ev1.isZero()) { // ev1 and ev2 are already orthogonal
			// since ev1 was orthogonal to ev0 and ev0 and ev2 are parallel
			ev0.setCrossProduct3(ev1, ev2);
			// in some cases ev1 and ev2 are parallel
			if (ev0.isZero()) {
				ev2.completeOrthonormal3(ev0, ev1);
			} else {
				ev0.normalize();
			}
		} else {
			ev2.completeOrthonormal3(ev0, ev1);
		}
	}

	private void setHyperboloidEigenvectors() {
		// mu[2] can't be equal to mu[0] and mu[1] -- not same sign
		tmpCoords.setValues(eigenvecND[2], 3);
		findEigenvector(eigenval[2], eigenvecND[2]);
		eigenvecND[2].normalize();
		if (tmpCoords.dotproduct3(eigenvecND[2]) < 0) {
			eigenvecND[2].mulInside3(-1);
		}

		if (DoubleUtil.isRatioEqualTo1(eigenval[0], eigenval[1])) {
			// eigenval[0] == eigenval[1]
			completeOrthonormalRatioEqualTo1(eigenvecND[0], eigenvecND[1],
					eigenvecND[2]);
		} else {
			tmpCoords.setValues(eigenvecND[0], 3);
			findEigenvector(eigenval[0], eigenvecND[0]);
			eigenvecND[0].normalize();
			eigenvecND[1].setCrossProduct4(eigenvecND[2], eigenvecND[0]);
			double dot0 = tmpCoords.dotproduct3(eigenvecND[0]);
			double dot1 = tmpCoords.dotproduct3(eigenvecND[1]);
			if (Math.abs(dot1) > Math.abs(dot0)) { // swap
				tmpCoords.setValues(eigenvecND[0], 3);
				eigenvecND[0].setValues(eigenvecND[1], 3);
				eigenvecND[1].setValues(tmpCoords, 3);
				double tmp = eigenval[0];
				eigenval[0] = eigenval[1];
				eigenval[1] = tmp;
				if (dot1 < 0) { // reverse
					eigenvecND[0].mulInside3(-1);
				} else {
					eigenvecND[1].mulInside3(-1);
				}
			} else {
				if (dot0 < 0) { // reverse
					eigenvecND[0].mulInside3(-1);
					eigenvecND[1].mulInside3(-1);
				}
			}
		}
	}

	private void cone() {

		if (eigenval[0] > 0 && eigenval[1] > 0 && eigenval[2] > 0) {
			singlePoint();
		} else if (eigenval[0] < 0 && eigenval[1] < 0 && eigenval[2] < 0) {
			singlePoint();
		} else {
			// set eigenvectors
			findEigenvectors();

			// check what vector has direction
			int directionIndex, ellipseIndex0, ellipseIndex1;
			if (eigenval[0] * eigenval[1] > 0) {
				directionIndex = 2;
				ellipseIndex0 = 0;
				ellipseIndex1 = 1;
			} else if (eigenval[0] * eigenval[2] > 0) {
				directionIndex = 1;
				ellipseIndex0 = 2;
				ellipseIndex1 = 0;
			} else {
				directionIndex = 0;
				ellipseIndex0 = 1;
				ellipseIndex1 = 2;
			}

			// set direction
			tmpCoords.setValues(eigenvecND[2], 3);
			eigenvecND[2].setValues(eigenvec[directionIndex], 3);
			if (tmpCoords.dotproduct3(eigenvecND[2]) < 0) { // check continuity
				eigenvecND[2].mulInside3(-1);
				int index = ellipseIndex0;
				ellipseIndex0 = ellipseIndex1;
				ellipseIndex1 = index;
			}

			// set others eigen vecs
			tmpCoords.setValues(eigenvecND[0], 3);
			double dot0 = tmpCoords.dotproduct3(eigenvec[ellipseIndex0]);
			double dot1 = tmpCoords.dotproduct3(eigenvec[ellipseIndex1]);
			if (Math.abs(dot0) < Math.abs(dot1)) {
				int index = ellipseIndex0;
				ellipseIndex0 = ellipseIndex1;
				ellipseIndex1 = index;
				if (dot1 < 0) {
					eigenvec[ellipseIndex0].mulInside3(-1);
				} else {
					eigenvec[ellipseIndex1].mulInside3(-1);
				}
			} else {
				if (dot0 < 0) {
					eigenvec[ellipseIndex0].mulInside3(-1);
					eigenvec[ellipseIndex1].mulInside3(-1);
				}
			}
			eigenvecND[0].setValues(eigenvec[ellipseIndex0], 3);
			eigenvecND[1].setValues(eigenvec[ellipseIndex1], 3);

			// set halfAxes = radius
			halfAxes[0] = Math
					.sqrt(-eigenval[directionIndex] / eigenval[ellipseIndex0]);
			halfAxes[1] = Math
					.sqrt(-eigenval[directionIndex] / eigenval[ellipseIndex1]);
			halfAxes[2] = 1;

			// set the diagonal values
			diagonal[0] = eigenval[ellipseIndex0];
			diagonal[1] = eigenval[ellipseIndex1];
			diagonal[2] = eigenval[directionIndex];
			diagonal[3] = 0;

			// eigen matrix
			setEigenMatrix(halfAxes[0], halfAxes[1], 1);

			// set type
			type = QUADRIC_CONE;
		}
	}

	private void cylinder(double x, double y, double m) {

		// set midpoint
		midpoint.set(Coords.O);
		midpoint.addInside(tmpCoords.setMul(eigenvecND[0], x));
		midpoint.addInside(tmpCoords.setMul(eigenvecND[1], y));

		// set halfAxes = radius
		halfAxes[0] = Math.sqrt(m / eigenval[0]);
		halfAxes[1] = Math.sqrt(m / eigenval[1]);
		halfAxes[2] = 1;

		// set the diagonal values
		diagonal[0] = eigenval[0];
		diagonal[1] = eigenval[1];
		diagonal[2] = eigenval[2];
		diagonal[3] = 0;

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], 1);

		// set type
		type = QUADRIC_CYLINDER;
	}

	private void ellipsoid(double beta) {
		// sphere
		if (DoubleUtil.isEqual(eigenval[0] / eigenval[1], 1.0)
				&& DoubleUtil.isEqual(eigenval[0] / eigenval[2], 1.0)) {

			mu[0] = -eigenval[0] / beta;
			mu[1] = -eigenval[1] / beta;
			mu[2] = -eigenval[2] / beta;

			double r = Math.sqrt(1.0d / mu[0]);

			// set halfAxes = radius
			for (int i = 0; i < 3; i++) {
				halfAxes[i] = r;
			}

			// set type
			type = QUADRIC_SPHERE;
			linearEccentricity = 0.0d;
			eccentricity = 0.0d;

			volume = 4 * Math.PI * getHalfAxis(0) * getHalfAxis(1)
					* getHalfAxis(2) / 3;

			// set the diagonal values
			diagonal[0] = 1;
			diagonal[1] = 1;
			diagonal[2] = 1;
			diagonal[3] = -r * r;

			// eigen matrix
			setEigenMatrix(halfAxes[0], halfAxes[1], halfAxes[2]);

		} else { // ellipsoid

			findEigenvectors();

			// set eigen vecs
			boolean reverse = false;
			double dot0 = eigenvecND[0].dotproduct3(eigenvec[0]);
			double dot1 = eigenvecND[0].dotproduct3(eigenvec[1]);
			double dot2 = eigenvecND[0].dotproduct3(eigenvec[2]);
			if (Math.abs(dot1) > Math.abs(dot0)) {
				if (Math.abs(dot2) > Math.abs(dot1)) {
					// |dot2| > |dot1| & |dot0|: set ND0 to 2
					eigenvecND[0].setValues(eigenvec[2], 3);
					double tmp = eigenval[0];
					eigenval[0] = eigenval[2];
					if (dot2 < 0) { // reverse
						eigenvecND[0].mulInside3(-1);
						reverse = true;
					}
					dot0 = eigenvecND[1].dotproduct3(eigenvec[0]);
					dot1 = eigenvecND[1].dotproduct3(eigenvec[1]);
					if (Math.abs(dot1) > Math.abs(dot0)) { // set ND1 to 1
						eigenvecND[1].setValues(eigenvec[1], 3);
						// set ND2 to 0 (last one)
						eigenvecND[2].setValues(eigenvec[0], 3);
						eigenval[2] = tmp;
						dot0 = dot1;
						reverse = !reverse;
					} else { // set ND1 to 0
						eigenvecND[1].setValues(eigenvec[0], 3);
						// set ND2 to 1 (last one)
						eigenvecND[2].setValues(eigenvec[1], 3);
						eigenval[2] = eigenval[1];
						eigenval[1] = tmp;
					}
					if (dot0 < 0) { // reverse
						eigenvecND[1].mulInside3(-1);
						reverse = !reverse;
					}
					if (reverse) { // set direct coord sys
						eigenvecND[2].mulInside3(-1);
					}
				} else {
					// |dot1| > |dot2| & |dot0|: set ND0 to 1
					eigenvecND[0].setValues(eigenvec[1], 3);
					double tmp = eigenval[0];
					eigenval[0] = eigenval[1];
					if (dot1 < 0) { // reverse
						eigenvecND[0].mulInside3(-1);
						reverse = true;
					}
					dot0 = eigenvecND[1].dotproduct3(eigenvec[0]);
					dot2 = eigenvecND[1].dotproduct3(eigenvec[2]);
					if (Math.abs(dot2) > Math.abs(dot0)) { // set ND1 to 2
						eigenvecND[1].setValues(eigenvec[2], 3);
						// set ND2 to 0 (last one)
						eigenvecND[2].setValues(eigenvec[0], 3);
						eigenval[1] = eigenval[2];
						eigenval[2] = tmp;
						dot0 = dot2;
					} else { // set ND1 to 0
						eigenvecND[1].setValues(eigenvec[0], 3);
						eigenval[1] = tmp;
						// set ND2 to 2 (last one)
						eigenvecND[2].setValues(eigenvec[2], 3);
						reverse = !reverse;
					}
					if (dot0 < 0) { // reverse
						eigenvecND[1].mulInside3(-1);
						reverse = !reverse;
					}
					if (reverse) { // set direct coord sys
						eigenvecND[2].mulInside3(-1);
					}
				}
			} else {
				if (Math.abs(dot2) > Math.abs(dot0)) {
					// |dot2| > |dot1| & |dot0|: set ND0 to 2
					eigenvecND[0].setValues(eigenvec[2], 3);
					double tmp = eigenval[0];
					eigenval[0] = eigenval[2];
					if (dot2 < 0) { // reverse
						eigenvecND[0].mulInside3(-1);
						reverse = true;
					}
					dot0 = eigenvecND[1].dotproduct3(eigenvec[0]);
					dot1 = eigenvecND[1].dotproduct3(eigenvec[1]);
					if (Math.abs(dot1) > Math.abs(dot0)) { // set ND1 to 1
						eigenvecND[1].setValues(eigenvec[1], 3);
						// set ND2 to 0 (last one)
						eigenvecND[2].setValues(eigenvec[0], 3);
						eigenval[2] = tmp;
						dot0 = dot1;
						reverse = !reverse;
					} else { // set ND1 to 0
						eigenvecND[1].setValues(eigenvec[0], 3);
						// set ND2 to 1 (last one)
						eigenvecND[2].setValues(eigenvec[1], 3);
						eigenval[2] = eigenval[1];
						eigenval[1] = tmp;
					}
					if (dot0 < 0) { // reverse
						eigenvecND[1].mulInside3(-1);
						reverse = !reverse;
					}
					if (reverse) { // set direct coord sys
						eigenvecND[2].mulInside3(-1);
					}
				} else {
					// |dot0| > |dot2| & |dot1|: set ND0 to 0
					eigenvecND[0].setValues(eigenvec[0], 3);
					if (dot0 < 0) { // reverse
						eigenvecND[0].mulInside3(-1);
						reverse = true;
					}
					dot1 = eigenvecND[1].dotproduct3(eigenvec[1]);
					dot2 = eigenvecND[1].dotproduct3(eigenvec[2]);
					if (Math.abs(dot2) > Math.abs(dot1)) { // set ND1 to 2
						eigenvecND[1].setValues(eigenvec[2], 3);
						// set ND2 to 1 (last one)
						eigenvecND[2].setValues(eigenvec[1], 3);
						double tmp = eigenval[1];
						eigenval[1] = eigenval[2];
						eigenval[2] = tmp;
						reverse = !reverse;
						dot1 = dot2;
					} else { // set ND1 to 1
						eigenvecND[1].setValues(eigenvec[1], 3);
						// set ND2 to 2 (last one)
						eigenvecND[2].setValues(eigenvec[2], 3);
					}
					if (dot1 < 0) { // reverse
						eigenvecND[1].mulInside3(-1);
						reverse = !reverse;
					}
					if (reverse) { // set direct coord sys
						eigenvecND[2].mulInside3(-1);
					}
				}
			}
			// for (int i = 0; i < 3; i++) {
			// eigenvecND[i].setValues(eigenvec[i], 3);
			// }

			// mu
			mu[0] = -eigenval[0] / beta;
			mu[1] = -eigenval[1] / beta;
			mu[2] = -eigenval[2] / beta;

			// set halfAxes = radius
			for (int i = 0; i < 3; i++) {
				halfAxes[i] = Math.sqrt(1.0d / mu[i]);
			}

			// set the diagonal values
			for (int i = 0; i < 3; i++) {
				diagonal[i] = mu[i];
			}
			diagonal[3] = -1;

			// eigen matrix
			setEigenMatrix(halfAxes[0], halfAxes[1], halfAxes[2]);

			// set type
			type = QUADRIC_ELLIPSOID;

		}

	}

	@Override
	protected void findEigenvectors() {
		// Log.debug("\neigen values: " + eigenval[0] + ","
		// + eigenval[1] + ","
		// + eigenval[2]);

		if (eigenvec == null) {
			eigenvec = new Coords[3];
			for (int i = 0; i < 3; i++) {
				eigenvec[i] = new Coords(3);
			}
		}

		if (DoubleUtil.isRatioEqualTo1(eigenval[0], eigenval[1])) {
			// eigenval[2] of multiplicity 1
			findEigenvectorsMultiplicity1(eigenval[2], eigenvec[2], eigenvec[0], eigenvec[1]);
		} else if (DoubleUtil.isRatioEqualTo1(eigenval[0], eigenval[2])) {
			// eigenval[1] of multiplicity 1
			findEigenvectorsMultiplicity1(eigenval[1], eigenvec[1], eigenvec[2], eigenvec[0]);
		} else if (DoubleUtil.isRatioEqualTo1(eigenval[1], eigenval[2])) {
			// eigenval[0] of multiplicity 1
			findEigenvectorsMultiplicity1(eigenval[0], eigenvec[0], eigenvec[1], eigenvec[2]);
		} else {
			// all eigenvalues of multiplicity 1
			for (int i = 0; i < 2; i++) {
				computeEigenVectorMultiplicity1(matrix, eigenval[i],
						eigenvec[i]);
			}
			eigenvec[2].setCrossProduct3(eigenvec[0], eigenvec[1]); // ensure
																	// orientation

			if (eigenvec[2].isZero()) {
				// eigenval[0] / eigenval[1] of multiplicity 2
				// eigenval[2] of multiplicity 1
				findEigenvectorsMultiplicity1(eigenval[2], eigenvec[2], eigenvec[0], eigenvec[1]);
			} else {
				for (int i = 0; i < 3; i++) {
					eigenvec[i].normalize();
				}
			}
		}
	}

	private void findEigenvectorsMultiplicity1(double val, Coords v, Coords v0, Coords v1) {
		computeEigenVectorMultiplicity1(matrix, val, v);
		v.normalize();
		completeOrthonormalRatioEqualTo1(v0, v1, v);
	}

	private static final void computeEigenVectorMultiplicity1(double[] m,
			double mu, Coords v) {

		// lines are dependents
		// eigen value mu is not zero

		// first try, result maybe 0 if lines 1 & 2 are dependent
		v.set(m[5] / mu * (m[1] / mu - 1) - m[4] / mu * m[6] / mu,
				m[6] / mu * (m[0] / mu - 1) - m[4] / mu * m[5] / mu,
				m[4] / mu * m[4] / mu - (m[0] / mu - 1) * (m[1] / mu - 1));

		if (v.isZero()) {
			// second try, result maybe 0 if lines 1 & 3 are dependent
			v.set(m[5] / mu * m[6] / mu - m[4] / mu * (m[2] / mu - 1),
					(m[0] / mu - 1) * (m[2] / mu - 1) - m[5] / mu * m[5] / mu,
					m[4] / mu * m[5] / mu - m[6] / mu * (m[0] / mu - 1));

			if (v.isZero()) {
				// third try: lines 2 & 3 are not dependent, so line 1 equals 0
				// (multiplicity 1)
				v.set(1, 0, 0);
			}
		}
	}

	/**
	 * returns false if quadric's matrix contains NaNs
	 */
	private boolean checkDefined() {
		for (double value : matrix) {
			if (Double.isNaN(value)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Copy constructor
	 * 
	 * @param quadric
	 *            original quadric
	 */
	public GeoQuadric3D(GeoQuadric3D quadric) {
		this(quadric.getConstruction());
		set(quadric);
	}

	/**
	 * @return midpoint
	 */
	public Coords getMidpointND() {
		return getMidpoint3D();
	}

	// //////////////////////////////
	// SPHERE

	@Override
	protected void setSphereNDMatrix(Coords M, double r) {
		super.setSphereNDMatrix(M, r);

		volume = 4 * Math.PI * getHalfAxis(0) * getHalfAxis(1) * getHalfAxis(2)
				/ 3;

		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = 1;
		diagonal[3] = -r * r;

		// eigen matrix
		eigenMatrix.setOrigin(getMidpoint3D());
		for (int i = 1; i <= 3; i++) {
			eigenMatrix.set(i, i, getHalfAxis(i - 1));
		}
	}

	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		// TODO
	}

	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		// TODO do this in GeoQuadricND, implement degenerate cases
		setSphereNDMatrix(M.getInhomCoordsInD3(), M.distance(P));
	}

	// //////////////////////////////
	// CONE

	/**
	 * @param origin
	 *            vertex
	 * @param direction
	 *            axis direction
	 * @param angle
	 *            angle between axis and surface
	 */
	public void setCone(GeoPointND origin, GeoVectorND direction,
			double angle) {

		// check midpoint
		defined = origin.isDefined() && !origin.isInfinite();

		// check direction

		// check angle
		double r;
		double c = Math.cos(angle);
		double s = Math.sin(angle);

		if (c < 0 || s < 0) {
			defined = false;
		} else if (DoubleUtil.isZero(c)) {
			defined = false; // TODO if c=0 then draws a plane
		} else if (DoubleUtil.isZero(s)) {
			defined = false; // TODO if s=0 then draws a line
		} else {
			r = s / c;
			setCone(origin.getInhomCoordsInD3(), direction.getCoordsInD3(),
					null, r, r);
		}

	}

	/**
	 * Cone
	 * 
	 * @param origin
	 *            base origin
	 * @param direction
	 *            axis direction
	 * @param eigen
	 *            base eigenvector
	 * @param r
	 *            major semiaxis
	 * @param r2
	 *            minor semiaxis
	 */
	public void setCone(Coords origin, Coords direction, Coords eigen, double r,
			double r2) {

		// set center
		setMidpoint(origin.get());

		updateEigenvectors(direction, eigen);

		// set halfAxes = radius

		halfAxes[0] = r;
		halfAxes[1] = r2;

		halfAxes[2] = 1;

		// set the diagonal values
		diagonal[0] = r2 / r;
		diagonal[1] = r / r2;
		diagonal[2] = -r * r2;
		diagonal[3] = 0;

		// set matrix
		setMatrixFromEigen();

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], 1);

		// set type
		type = QUADRIC_CONE;

	}

	// //////////////////////////////
	// CONE

	private void updateEigenvectors(Coords direction, Coords eigen) {
		// set direction
		eigenvecND[2].setValues(direction, 3);

		// set others eigen vecs
		if (eigen != null) {
			eigenvecND[0] = eigen.normalize();
			eigenvecND[1] = eigenvecND[2].crossProduct(eigen).normalize();
		} else {
			eigenvecND[2].completeOrthonormal(eigenvecND[0], eigenvecND[1]);
		}

	}

	/**
	 * @param origin
	 *            base origin
	 * @param direction
	 *            axis direction
	 * @param r0
	 *            radius
	 */
	public void setCylinder(GeoPointND origin, Coords direction, double r0) {
		double r = r0;
		// check midpoint
		defined = origin.isDefined() && !origin.isInfinite();

		// check direction

		// check radius
		if (DoubleUtil.isZero(r)) {
			r = 0;
		} else if (r < 0) {
			defined = false;
		}

		if (defined) {
			setCylinder(origin.getInhomCoordsInD3(), direction, null, r, r);
		}

	}

	/**
	 * Elliptical cylinder
	 * 
	 * @param origin
	 *            base origin
	 * @param direction
	 *            axis direction
	 * @param eigen
	 *            base eigenvector
	 * @param r
	 *            major semiaxis
	 * @param r2
	 *            minor semiaxis
	 */
	public void setCylinder(Coords origin, Coords direction, Coords eigen,
			double r, double r2) {
		setCylinder(origin, direction, eigen, r, r2, 1);
	}

	/**
	 * Hyperbolic or elliptic cylinder
	 * 
	 * @param origin
	 *            base origin
	 * @param direction
	 *            axis direction
	 * @param eigen
	 *            base eigenvector
	 * @param r
	 *            major semiaxis
	 * @param r2
	 *            minor semiaxis
	 * @param sgn
	 *            -1 for hyp, 1 for elliptic
	 */
	public void setCylinder(Coords origin, Coords direction, Coords eigen,
			double r, double r2, double sgn) {

		// set center
		setMidpoint(origin.get());

		// set direction
		updateEigenvectors(direction, eigen);

		// set halfAxes = radius
		halfAxes[0] = r;
		halfAxes[1] = r2;

		halfAxes[2] = 1;

		// set the diagonal values
		diagonal[0] = r2 / r;
		diagonal[1] = sgn * r / r2;
		diagonal[2] = 0;
		diagonal[3] = -r * r2;

		// set matrix
		setMatrixFromEigen();

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], 1);

		// set type
		this.type = sgn > 0 ? QUADRIC_CYLINDER : QUADRIC_HYPERBOLIC_CYLINDER;
	}

	/**
	 * Hyperbolic cylinder
	 * 
	 * @param origin
	 *            base origin
	 * @param direction
	 *            axis direction
	 * @param eigen
	 *            base eigenvector
	 * @param r
	 *            major semiaxis
	 * @param r2
	 *            minor semiaxis
	 */
	public void setHyperbolicCylinder(Coords origin, Coords direction,
			Coords eigen, double r, double r2) {

		setCylinder(origin, direction, eigen, r, r2, -1);
	}

	/**
	 * Parabolic cylinder
	 * 
	 * @param origin
	 *            base origin
	 * @param direction
	 *            axis direction
	 * @param eigen
	 *            base eigenvector
	 * @param r2
	 *            parameter
	 */
	public void setParabolicCylinder(Coords origin, Coords direction,
			Coords eigen, double r2) {

		// set center
		setMidpoint(origin.get());

		// set direction
		updateEigenvectors(eigen.crossProduct(direction).normalize(),
				eigen.normalize());

		// set halfAxes = radius
		halfAxes[0] = 1;
		halfAxes[1] = 1;

		halfAxes[2] = Math.sqrt(r2 * 2);

		// set the diagonal values
		diagonal[0] = 0;
		diagonal[1] = 0;
		diagonal[2] = 1; // TODO still wrong
		diagonal[3] = 0;

		// set matrix
		setMatrixFromEigen(-r2);

		// eigen matrix
		// setEigenMatrix(1, 1, halfAxes[2]);
		eigenvecND[1] = new Coords(eigenvecND[1].getX(), eigenvecND[1].getY(),
				eigenvecND[1].getZ(), 0);
		this.setSemiDiagonalizedMatrix();
		// parabolicCylinder(-r2 * 2);
		this.setEigenMatrix(1, 1, halfAxes[2]);
		// set type
		this.type = QUADRIC_PARABOLIC_CYLINDER;
	}

	/**
	 * set the eigen matrix
	 * 
	 * @param x
	 *            x half-axis
	 * @param y
	 *            y half-axis
	 * @param z
	 *            z half-axis
	 */
	private void setEigenMatrix(double x, double y, double z) {

		eigenMatrix.setOrigin(getMidpoint3D());

		eigenMatrix.setVx(eigenvecND[0].mul(x));
		eigenMatrix.setVy(eigenvecND[1].mul(y));
		eigenMatrix.setVz(eigenvecND[2].mul(z));

	}

	// /////////////////////////////
	// GeoElement

	@Override
	public GeoElement copy() {
		return new GeoQuadric3D(this);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.QUADRIC;
	}

	@Override
	public String getTypeString() {
		switch (type) {
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
			return "Sphere";
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			return "Cylinder";
		case GeoQuadricNDConstants.QUADRIC_CONE:
			return "Cone";
		case GeoQuadricNDConstants.QUADRIC_ELLIPSOID:
			return "Ellipsoid";
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_ONE_SHEET:
			return "HyperboloidOneSheet";
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_TWO_SHEETS:
			return "HyperboloidTwoSheets";
		case GeoQuadricNDConstants.QUADRIC_PARABOLOID:
			return "Paraboloid";
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_PARABOLOID:
			return "HyperbolicParaboloid";
		case GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER:
			return "ParabolicCylinder";
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER:
			return "HyperbolicCylinder";
		case GeoQuadricNDConstants.QUADRIC_EMPTY:
			return "EmptySet";
		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
			return "Point";
		case GeoQuadricNDConstants.QUADRIC_PLANE:
			return "Plane";
		case GeoQuadricNDConstants.QUADRIC_LINE:
			return "Line";
		case GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED:
		default:
			return "Quadric";
		}
	}

	@Override
	public String getTypeStringForAlgebraView() {
		if (getParentAlgorithm() instanceof AlgoDependentQuadric3D) {
			return "Quadric";
		}

		return getTypeString();
	}

	@Override
	public boolean isEqual(GeoElementND Geo) {
		return false;
	}

	@Override
	public void set(GeoElementND geo) {
		GeoQuadric3D quadric = (GeoQuadric3D) geo;

		// copy everything
		toStringMode = quadric.toStringMode;
		final boolean typeChanged = type != quadric.type;
		type = quadric.type;

		for (int i = 0; i < 10; i++) {
			matrix[i] = quadric.matrix[i]; // flat matrix A
		}

		for (int i = 0; i < 3; i++) {
			eigenvecND[i].set(quadric.getEigenvec3D(i));
			halfAxes[i] = quadric.halfAxes[i];
		}

		for (int i = 0; i < 4; i++) {
			diagonal[i] = quadric.diagonal[i];
		}

		setMidpoint(quadric.getMidpoint().get());

		eigenMatrix.set(quadric.eigenMatrix);

		defined = quadric.defined;
		volume = quadric.volume;

		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			getPlanes();
			planes[0].set(quadric.planes[0]);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].set(quadric.planes[1]);
			}
		}

		if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			getLine().set(quadric.line);
		}
		// GGB-1629 we may need to classify quadric from CAS
		if (kernel.getConstruction().isFileLoading() && typeChanged
				&& type == QUADRIC_NOT_CLASSIFIED) {
			classifyQuadric();
		}
		super.set(geo);
		if (typeChanged) {
			kernel.notifyTypeChanged(this);
		}
	}

	@Override
	final public void setToUser() {
		toStringMode = GeoConicND.EQUATION_USER;
	}

	/**
	 * Set whether this line should be visible in AV when undefined
	 * 
	 * @param flag
	 *            true to show undefined
	 */
	public void showUndefinedInAlgebraView(boolean flag) {
		showUndefinedInAlgebraView = flag;
	}

	@Override
	public boolean showInAlgebraView() {
		return isDefined() || showUndefinedInAlgebraView;
	}

	@Override
	protected boolean showInEuclidianView() {
		return type != GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED
				&& type != GeoQuadricNDConstants.QUADRIC_EMPTY;
	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {
		if (!isDefined()) {
			return new StringBuilder("?");
		}
		StringBuilder sbToValueString = new StringBuilder();
		if (getDefinition() != null
				&& (getToStringMode() == GeoConicND.EQUATION_USER)) {
			return sbToValueString.append(getDefinition().toString(tpl));
		}
		switch (type) {
		case QUADRIC_SPHERE:
			if (getToStringMode() == GeoConicND.EQUATION_IMPLICIT) {
				return buildImplicitEquation(tpl);
			}
			buildSphereNDString(sbToValueString, tpl);
			break;
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
		default:
			return buildImplicitEquation(tpl);
		}

		return sbToValueString;
	}

	private StringBuilder buildImplicitEquation(StringTemplate tpl) {
		double[] coeffs = new double[10];
		coeffs[0] = matrix[0]; // x^2
		coeffs[1] = matrix[1]; // y^2
		coeffs[2] = matrix[2]; // z^2
		coeffs[9] = matrix[3]; // constant

		coeffs[3] = 2 * matrix[4]; // xy
		coeffs[4] = 2 * matrix[5]; // xz
		coeffs[5] = 2 * matrix[6]; // yz
		coeffs[6] = 2 * matrix[7]; // x
		coeffs[7] = 2 * matrix[8]; // y
		coeffs[8] = 2 * matrix[9]; // z

		String[] vars = tpl.getStringType().isGiac() ? vars3DCAS : vars3D;

		return kernel.buildImplicitEquation(coeffs, vars, true, true,
				tpl, true);
	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	// ///////////////////////////////////////
	// SURFACE (u,v)->(x,y,z) INTERFACE
	// ///////////////////////////////////////

	@Override
	public void evaluatePoint(double u, double v, Coords point) {
		switch (type) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID:
			point.setMulPoint(eigenMatrix, Math.cos(u) * Math.cos(v),
					Math.sin(u) * Math.cos(v), Math.sin(v));
			break;
		case QUADRIC_HYPERBOLOID_ONE_SHEET:
			if (getHalfAxis(2) == Double.POSITIVE_INFINITY) {
				point.setMulPoint(eigenMatrix, Math.cos(u), Math.sin(u), v);
			} else {
				double ch = Math.cosh(DrawConic3D.asinh(v));
				point.setMulPoint(eigenMatrix, Math.cos(u) * ch, Math.sin(u) * ch, v);
			}
			break;
		case QUADRIC_HYPERBOLOID_TWO_SHEETS:
			double t, ch;
			if (v < -1) {
				t = -DrawConic3D.acosh(-v);
				ch = v;
			} else if (v < 0) {
				t = 0;
				ch = -1;
			} else if (v < 1) {
				t = 0;
				ch = 1;
			} else {
				t = DrawConic3D.acosh(v);
				ch = v;
			}
			double sh = Math.sinh(Math.abs(t));
			point.setMulPoint(eigenMatrix, Math.cos(u) * sh, Math.sin(u) * sh,
					ch);
			break;

		case QUADRIC_PARABOLOID:
			point.setMulPoint(eigenMatrix, Math.cos(u) * v, Math.sin(u) * v,
					v * v);
			break;

		case QUADRIC_HYPERBOLIC_PARABOLOID:
			point.setMulPoint(eigenMatrix, u, v,
					getHalfAxis(0) * u * u + getHalfAxis(1) * v * v);
			break;

		case QUADRIC_PARABOLIC_CYLINDER:
			point.setMulPoint(eigenMatrix, v * v, u, v);
			break;

		case QUADRIC_HYPERBOLIC_CYLINDER:
			double s;
			double c;
			if (u < 1) {
				s = PathNormalizer.infFunction(u);
				c = -Math.cosh(DrawConic3D.asinh(s));
			} else {
				s = PathNormalizer.infFunction(u - 2);
				c = Math.cosh(DrawConic3D.asinh(s));
			}
			point.setMulPoint(eigenMatrix, c, s, v);
			break;

		case QUADRIC_CONE:
			double v2 = Math.abs(v);
			point.setMulPoint(eigenMatrix, Math.cos(u) * v2, Math.sin(u) * v2,
					v);
			break;
		case QUADRIC_CYLINDER:
			point.setMulPoint(eigenMatrix, Math.cos(u), Math.sin(u), v);
			break;
		case QUADRIC_SINGLE_POINT:
			point.set(getMidpoint3D());
			break;

		case QUADRIC_PARALLEL_PLANES:
		case QUADRIC_INTERSECTING_PLANES:
			if (u < 1) { // -1 < u < 1: first plane
				point.set(planes[0].getCoordSys()
						.getPoint(PathNormalizer.infFunction(u), v));
			} else { // 1 < u < 3: second plane
				point.set(planes[1].getCoordSys()
						.getPoint(PathNormalizer.infFunction(u - 2), v));
			}
			break;

		case QUADRIC_PLANE:
			point.set(planes[0].getCoordSys().getPoint(u, v));
			break;

		case QUADRIC_LINE:
			point.set(line.getPoint(u));
			break;

		default:
			Log.error(this + " has wrong type : " + type);
			break;
		}

	}

	@Override
	public Coords evaluateNormal(double u, double v) {

		Coords n;

		switch (type) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID:
			double r0 = getHalfAxis(0);
			double r1 = getHalfAxis(1);
			double r2 = getHalfAxis(2);
			n = new Coords(4);
			n.setMul(getEigenvec3D(0), r1 * r2 * Math.cos(u) * Math.cos(v));
			tmpCoords.setMul(getEigenvec3D(1),
					r0 * r2 * Math.sin(u) * Math.cos(v));
			n.addInside(tmpCoords);
			tmpCoords.setMul(getEigenvec3D(2), r0 * r1 * Math.sin(v));
			n.addInside(tmpCoords);
			n.normalize();
			return n;

		case QUADRIC_HYPERBOLOID_ONE_SHEET:
			r0 = getHalfAxis(0);
			r1 = getHalfAxis(1);
			r2 = getHalfAxis(2);
			n = new Coords(4);
			double ch = Math.cosh(DrawConic3D.asinh(v));
			n.setMul(getEigenvec3D(0), r1 * r2 * Math.cos(u) * ch);
			tmpCoords.setMul(getEigenvec3D(1), r0 * r2 * Math.sin(u) * ch);
			n.addInside(tmpCoords);
			tmpCoords.setMul(getEigenvec3D(2), -r0 * r1 * v);
			n.addInside(tmpCoords);
			n.normalize();
			return n;

		case QUADRIC_HYPERBOLOID_TWO_SHEETS:
			r0 = getHalfAxis(0);
			r1 = getHalfAxis(1);
			r2 = getHalfAxis(2);
			n = new Coords(4);

			double t;
			if (v < -1) {
				t = -DrawConic3D.acosh(-v);
				ch = v;
			} else if (v < 0) {
				t = 0;
				ch = -1;
			} else if (v < 1) {
				t = 0;
				ch = 1;
			} else {
				t = DrawConic3D.acosh(v);
				ch = v;
			}
			double sh = Math.sinh(Math.abs(t));

			n.setMul(getEigenvec3D(0), r1 * r2 * Math.cos(u) * sh);
			tmpCoords.setMul(getEigenvec3D(1), r0 * r2 * Math.sin(u) * sh);
			n.addInside(tmpCoords);
			tmpCoords.setMul(getEigenvec3D(2), -r0 * r1 * ch);
			n.addInside(tmpCoords);
			n.normalize();
			return n;

		case QUADRIC_PARABOLOID:
			r0 = getHalfAxis(0);
			r1 = getHalfAxis(1);
			r2 = getHalfAxis(2);
			n = new Coords(4);

			n.setMul(getEigenvec3D(0), 2 * r1 * r2 * Math.cos(u) * v);
			tmpCoords.setMul(getEigenvec3D(1), 2 * r0 * r2 * Math.sin(u) * v);
			n.addInside(tmpCoords);
			tmpCoords.setMul(getEigenvec3D(2), -r0 * r1);
			n.addInside(tmpCoords);
			n.normalize();
			return n;

		case QUADRIC_HYPERBOLIC_PARABOLOID:
			r0 = getHalfAxis(0);
			r1 = getHalfAxis(1);
			r2 = getHalfAxis(2);
			n = new Coords(4);

			n.setMul(getEigenvec3D(0), 2 * r0 * u);
			tmpCoords.setMul(getEigenvec3D(1), 2 * r1 * v);
			n.addInside(tmpCoords);
			tmpCoords.setMul(getEigenvec3D(2), -1);
			n.addInside(tmpCoords);
			n.normalize();
			return n;

		case QUADRIC_PARABOLIC_CYLINDER:
			r2 = getHalfAxis(2);
			n = new Coords(4);

			n.setMul(getEigenvec3D(0), -r2);
			tmpCoords.setMul(getEigenvec3D(2), 2 * v);
			n.addInside(tmpCoords);
			n.normalize();
			return n;

		case QUADRIC_HYPERBOLIC_CYLINDER:
			r0 = getHalfAxis(0);
			r1 = getHalfAxis(1);
			n = new Coords(4);

			double s;
			if (u < 1) {
				s = PathNormalizer.infFunction(u);
				n.setMul(getEigenvec3D(0),
						-r1 * Math.cosh(DrawConic3D.asinh(s)));
			} else {
				s = PathNormalizer.infFunction(u - 2);
				n.setMul(getEigenvec3D(0),
						r1 * Math.cosh(DrawConic3D.asinh(s)));
			}
			tmpCoords.setMul(getEigenvec3D(1), -r0 * s);
			n.addInside(tmpCoords);
			n.normalize();
			return n;

		case QUADRIC_CONE:
			r0 = getHalfAxis(0);
			r1 = getHalfAxis(1);
			double rr;
			if (v < 0) {
				rr = r0 * r1;
			} else {
				rr = -r0 * r1;
			}

			n = new Coords(4);
			n.setMul(getEigenvec3D(0), r1 * Math.cos(u));
			tmpCoords.setMul(getEigenvec3D(1), r0 * Math.sin(u));
			n.addInside(tmpCoords);
			tmpCoords.setMul(getEigenvec3D(2), rr);
			n.addInside(tmpCoords);
			n.normalize();

			return n;

		case QUADRIC_CYLINDER:

			r0 = getHalfAxis(0);
			r1 = getHalfAxis(1);

			n = new Coords(4);
			n.setMul(getEigenvec3D(0), r1 * Math.cos(u));
			tmpCoords.setMul(getEigenvec3D(1), r0 * Math.sin(u));
			n.addInside(tmpCoords);
			n.normalize();

			return n;

		case QUADRIC_PARALLEL_PLANES:
			return planes[0].getDirectionInD3();

		case QUADRIC_INTERSECTING_PLANES:
			if (u > 1) {
				return planes[1].getDirectionInD3();
			}
			return planes[0].getDirectionInD3();

		default:
			return null;
		}

	}

	@Override
	public double getMinParameter(int index) {
		switch (type) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID:
			switch (index) {
			case 0: // u
			default:
				return 0;
			case 1: // v
				return -Math.PI / 2;
			}
		case QUADRIC_HYPERBOLOID_ONE_SHEET:
		case QUADRIC_HYPERBOLOID_TWO_SHEETS:
			switch (index) {
			case 0: // u
			default:
				return 0;
			case 1: // v
				return Double.NEGATIVE_INFINITY;
			}
		case QUADRIC_PARABOLOID:
			switch (index) {
			case 0: // u
			default:
				return 0;
			case 1: // v
				return 0;
			}
		case QUADRIC_PARABOLIC_CYLINDER:
			switch (index) {
			case 0: // u
			default:
				return Double.NEGATIVE_INFINITY;
			case 1: // v
				return 0;
			}
		case QUADRIC_HYPERBOLIC_CYLINDER:
			switch (index) {
			case 0: // u
			default:
				return -1;
			case 1: // v
				return Double.NEGATIVE_INFINITY;
			}
		case QUADRIC_HYPERBOLIC_PARABOLOID:
			switch (index) {
			case 0: // u
			default:
				return Double.NEGATIVE_INFINITY;
			case 1: // v
				return Double.NEGATIVE_INFINITY;
			}
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			switch (index) {
			case 0: // u
			default:
				return 0;
			case 1: // v
				return Double.NEGATIVE_INFINITY;
			}

		default:
			return 0;
		}

	}

	@Override
	public double getMaxParameter(int index) {
		switch (type) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID:
			switch (index) {
			case 0: // u
			default:
				return 2 * Math.PI;
			case 1: // v
				return Math.PI / 2;
			}

		case QUADRIC_HYPERBOLOID_ONE_SHEET:
		case QUADRIC_HYPERBOLOID_TWO_SHEETS:
			switch (index) {
			case 0: // u
			default:
				return 2 * Math.PI;
			case 1: // v
				return Double.POSITIVE_INFINITY;
			}

		case QUADRIC_PARABOLOID:
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			switch (index) {
			case 0: // u
			default:
				return 2 * Math.PI;
			case 1: // v
				return Double.POSITIVE_INFINITY;
			}
		case QUADRIC_PARABOLIC_CYLINDER:
		case QUADRIC_HYPERBOLIC_PARABOLOID:
			switch (index) {
			case 0: // u
			default:
				return Double.POSITIVE_INFINITY;
			case 1: // v
				return Double.POSITIVE_INFINITY;
			}
		case QUADRIC_HYPERBOLIC_CYLINDER:
			switch (index) {
			case 0: // u
			default:
				return 3;
			case 1: // v
				return Double.POSITIVE_INFINITY;
			}
		default:
			return 0;
		}
	}

	// /////////////////////////////////////////
	// GEOELEMENT3D INTERFACE
	// /////////////////////////////////////////

	@Override
	public Coords getLabelPosition() {
		return Coords.O; // TODO
	}

	@Override
	public Coords getMainDirection() {
		// TODO create with parameter coord where is looked at
		return Coords.VZ;
	}

	// /////////////////////////////////////////////////
	// REGION 3D INTERFACE
	// /////////////////////////////////////////////////

	@Override
	public boolean isRegion() {
		return true;
	}

	@Override
	public boolean isRegion3D() {
		return true;
	}

	/**
	 * @param coords
	 *            coordinates in eigenvector system
	 * @param parameters
	 *            path parameters
	 */
	protected void getNormalProjectionParameters(Coords coords,
			double[] parameters) {

		Coords eigenCoords = eigenMatrix.solve(coords);
		double x = eigenCoords.getX();
		double y = eigenCoords.getY();
		double z = eigenCoords.getZ();

		switch (getType()) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID: // eigenMatrix is dilated with half axes
			parameters[0] = Math.atan2(y, x);
			double r = Math.sqrt(x * x + y * y);
			parameters[1] = Math.atan2(z, r);
			break;

		case QUADRIC_HYPERBOLOID_ONE_SHEET: // eigenMatrix is dilated with half
											// axes
			parameters[0] = Math.atan2(y, x);
			parameters[1] = z;
			break;

		case QUADRIC_HYPERBOLOID_TWO_SHEETS: // eigenMatrix is dilated with half
			// axes
			parameters[0] = Math.atan2(y, x);
			parameters[1] = z;
			break;

		case QUADRIC_PARABOLOID: // eigenMatrix is dilated with half axes
			double a = Math.atan2(y, x);
			if (a < 0) {
				a += 2 * Math.PI;
			}
			parameters[0] = a;
			if (z < 0) {
				parameters[1] = 0;
			} else {
				parameters[1] = Math.sqrt(z);
			}
			break;
		case QUADRIC_HYPERBOLIC_PARABOLOID:
			parameters[0] = x;
			parameters[1] = y;
			break;
		case QUADRIC_PARABOLIC_CYLINDER: // eigenMatrix is dilated with half
											// axes
			parameters[0] = y;
			if (x < 0) {
				parameters[1] = 0;
			} else {
				parameters[1] = Math.sqrt(x);
				if (z < 0) {
					parameters[1] *= -1;
				}
			}
			break;

		case QUADRIC_HYPERBOLIC_CYLINDER: // eigenMatrix is dilated with half
			// axes
			parameters[0] = PathNormalizer.inverseInfFunction(y);
			if (x > 0) {
				parameters[0] += 2;
			}
			parameters[1] = z;
			break;

		case QUADRIC_CONE:
		case QUADRIC_CYLINDER: // eigenMatrix is dilated with half axes
			parameters[0] = Math.atan2(y, x);
			parameters[1] = z;
			break;

		case QUADRIC_PARALLEL_PLANES:
		case QUADRIC_INTERSECTING_PLANES:
			coords.projectPlaneInPlaneCoords(
					planes[0].getCoordSys().getMatrixOrthonormal(), tmpCoords);
			parameters[0] = PathNormalizer.inverseInfFunction(tmpCoords.getX());
			parameters[1] = tmpCoords.getY();
			break;

		case QUADRIC_PLANE:
			coords.projectPlaneInPlaneCoords(
					planes[0].getCoordSys().getMatrixOrthonormal(), tmpCoords);
			parameters[0] = PathNormalizer.inverseInfFunction(tmpCoords.getX());
			parameters[1] = tmpCoords.getY();
			break;

		case QUADRIC_LINE:
			coords.projectLine(line.getStartInhomCoords(),
					line.getDirectionInD3(), tmpCoords, parameters);
			parameters[1] = 0;
			break;

		default:
			Log.error("Missing type -- type: " + getType());
			break;
		}
	}

	@Override
	public Coords[] getNormalProjection(Coords coords) {
		getNormalProjectionParameters(coords, tmpDouble2);

		return new Coords[] { getPoint(tmpDouble2[0], tmpDouble2[1]),
				new Coords(tmpDouble2) };
	}

	/**
	 * get normal projection of coords, set the projection in ret and parameters
	 * 
	 * @param coords
	 *            coords
	 * @param ret
	 *            projection
	 * @param parameters
	 *            parameters
	 */
	public void getNormalProjection(Coords coords, Coords ret,
			double[] parameters) {
		getNormalProjectionParameters(coords, parameters);
		evaluatePoint(parameters[0], parameters[1], ret);
	}

	/**
	 * try with t1, then with t2
	 * 
	 * @param willingCoords
	 *            willing coords
	 * @param willingDirection
	 *            willing direction
	 * @param t1
	 *            first possible parameter
	 * @param t2
	 *            second possible parameter
	 * @return closest point
	 */
	protected Coords[] getProjection(Coords willingCoords,
			Coords willingDirection, double t1, double t2) {

		if (tmpCoords6 == null) {
			tmpCoords6 = Coords.createInhomCoorsInD3();
		}

		tmpCoords6.setMul3(willingDirection, t1);
		tmpCoords6.setAdd3(tmpCoords6, willingCoords);
		tmpCoords6.setW(willingCoords.getW());
		return getNormalProjection(tmpCoords6);
	}

	@Override
	public Coords getPoint(double u, double v, Coords coords) {
		evaluatePoint(u, v, coords);
		return coords;
	}

	/**
	 * 
	 * @param u
	 *            u-param
	 * @param v
	 *            v-param
	 * @return deprecated use getPoint(double u, double v, Coords coords)
	 *         instead
	 */
	public Coords getPoint(double u, double v) {
		return getPoint(u, v, new Coords(4));
	}

	/**
	 * checks if u,v are region-compatible parameters
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @return point in region
	 */
	protected Coords getPointInRegion(double u, double v) {
		return getPoint(u, v);
	}

	@Override
	public Coords[] getProjection(Coords oldCoords, Coords willingCoords,
			Coords willingDirection) {

		// compute intersection
		CoordMatrix qm = getSymetricMatrix();
		// Log.debug("qm=\n"+qm);
		if (tmpMatrix4x2 == null) {
			tmpMatrix4x2 = new CoordMatrix(4, 2);
		}
		tmpMatrix4x2.setVx(willingDirection);
		tmpMatrix4x2.setOrigin(willingCoords);
		if (tmpMatrix2x4 == null) {
			tmpMatrix2x4 = new CoordMatrix(2, 4);
		}
		tmpMatrix4x2.transposeCopy(tmpMatrix2x4);

		// sets the solution matrix from line and quadric matrix
		CoordMatrix sm = tmpMatrix2x4.mul(qm).mul(tmpMatrix4x2);

		// Log.debug("sm=\n"+sm);
		double a = sm.get(1, 1);
		double b = sm.get(1, 2);
		double c = sm.get(2, 2);

		if (DoubleUtil.isEpsilon(a, b, c)) { // this can happen with degenerate
											// cases
			double t = c / -b;
			return getProjection(willingCoords, willingDirection, t, t);
		}

		double Delta = b * b - a * c;

		if (Delta >= 0) {
			double t1 = (-b - Math.sqrt(Delta)) / a;
			double t2 = (-b + Math.sqrt(Delta)) / a;
			if (a > 0) {
				return getProjection(willingCoords, willingDirection, t1, t2);
			}
			return getProjection(willingCoords, willingDirection, t2, t1);
		}

		// get closer point (in some "eigen coord sys")
		tmpCoords.setMul3(willingDirection, -b / a);
		tmpCoords.setAdd3(tmpCoords, willingCoords);
		tmpCoords.setW(1);
		return getNormalProjection(tmpCoords);

	}

	/**
	 * @param willingCoords
	 *            willing coords of the point
	 * @param willingDirection
	 *            willing direction
	 * @param p1
	 *            1st intersection of line with quadric
	 * @param parameters1
	 *            p1 parameters
	 * @param p2
	 *            2nd intersection of line with quadric
	 * @param parameters2
	 *            p2 parameters
	 */
	public void getProjections(Coords willingCoords,
			Coords willingDirection, Coords p1, double[] parameters1, Coords p2,
			double[] parameters2) {

		// compute intersection
		CoordMatrix qm = getSymetricMatrix();
		// Log.debug("qm=\n"+qm);
		if (tmpMatrix4x2 == null) {
			tmpMatrix4x2 = new CoordMatrix(4, 2);
		}
		tmpMatrix4x2.setVx(willingDirection);
		tmpMatrix4x2.setOrigin(willingCoords);
		if (tmpMatrix2x4 == null) {
			tmpMatrix2x4 = new CoordMatrix(2, 4);
		}
		tmpMatrix4x2.transposeCopy(tmpMatrix2x4);

		// sets the solution matrix from line and quadric matrix
		CoordMatrix sm = tmpMatrix2x4.mul(qm).mul(tmpMatrix4x2);

		// Log.debug("sm=\n"+sm);
		double a = sm.get(1, 1);
		double b = sm.get(1, 2);
		double c = sm.get(2, 2);
		
		if (tmpEqn == null) {
			tmpEqn = new double[3];
		}
		tmpEqn[0] = c;
		tmpEqn[1] = 2 * b;
		tmpEqn[2] = a;

		int nRoots = EquationSolver.solveQuadratic(tmpEqn);

		if (nRoots > 0) {
			double t1, t2;
			if (tmpEqn[0] < tmpEqn[1]) {
				t1 = tmpEqn[0];
				t2 = tmpEqn[1];
			} else {
				t1 = tmpEqn[1];
				t2 = tmpEqn[0];
			}
			tmpCoords.setAdd(willingCoords,
					tmpCoords.setMul(willingDirection, t1));
			getNormalProjectionParameters(tmpCoords, parameters1);
			checkParameters(parameters1);
			evaluatePoint(parameters1[0], parameters1[1], p1);

			tmpCoords.setAdd(willingCoords,
					tmpCoords.setMul(willingDirection, t2));
			getNormalProjectionParameters(tmpCoords, parameters2);
			checkParameters(parameters2);
			evaluatePoint(parameters2[0], parameters2[1], p2);

		} else {
			// get closest point (in some "eigen coord sys")
			getNormalProjection(
					tmpCoords.setAdd(willingCoords,
							tmpCoords.setMul(willingDirection, -b / a)),
					p1, parameters1);
			p2.setUndefined();
		}

	}

	/**
	 * reset last hit parameters
	 */
	public void resetLastHitParameters() {
		lastHitParameters = null;
	}

	/**
	 * set last hit parameters
	 * 
	 * @param parameters
	 *            parameters
	 */
	public void setLastHitParameters(double[] parameters) {
		lastHitParameters = parameters;
	}

	private boolean hasLastHitParameters() {
		return lastHitParameters != null;
	}

	/**
	 * check parameters are possible parameters; modify it if not
	 * 
	 * @param parameters
	 *            parameters
	 * @return true if possible parameters
	 */
	protected boolean checkParameters(double[] parameters) {
		return true;
	}

	@Override
	public boolean isInRegion(GeoPointND P) {
		return isInRegion(P.getCoordsInD3());
	}

	/**
	 * 
	 * @param coords
	 *            coords
	 * @return true if these coords lies on region
	 */
	public boolean isInRegion(Coords coords) {
		// calc tP.S.P
		return DoubleUtil
				.isZero(coords.dotproduct(getSymetricMatrix().mul(coords)));
	}

	@Override
	public boolean isInRegion(double x0, double y0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param source
	 *            source point
	 * @return direction from source to center (midpoint, or axis for cone,
	 *         cylinder...)
	 */
	private Coords getDirectionToCenter(Coords source) {

		switch (getType()) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID:
			tmpCoords.setSub(getMidpoint3D(), source);
			if (tmpCoords.isZero()) {
				return getEigenvec3D(0).copyVector();
			}
			return tmpCoords;
		case QUADRIC_HYPERBOLOID_ONE_SHEET:
		case QUADRIC_HYPERBOLOID_TWO_SHEETS:
		case QUADRIC_PARABOLOID:
			source.projectLine(getMidpoint3D(), getEigenvec3D(2), tmpCoords);
			tmpCoords.setSub(tmpCoords, source);
			if (tmpCoords.isZero()) {
				return getEigenvec3D(0).copyVector();
			}
			return tmpCoords;
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			eigenMatrix.pivotDegenerate(tmpCoords, source);
			// project on eigen xOy plane
			// when we are already on axis, pick a direction "at random"
			if (DoubleUtil.isZero(tmpCoords.getX())
					&& DoubleUtil.isZero(tmpCoords.getY())) {
				return getEigenvec3D(0).copyVector();
			}
			tmpCoords.setZ(0);
			tmpCoords.setW(0);
			if (tmpCoords2 == null) {
				tmpCoords2 = new Coords(4);
			}
			tmpCoords2.setMul(eigenMatrix, tmpCoords);
			tmpCoords2.normalize();
			tmpCoords2.mulInside(-1);
			return tmpCoords2;
		case QUADRIC_PARABOLIC_CYLINDER:
			tmpCoords.setSub(getMidpoint3D(), source);
			if (tmpCoords.dotproduct(getEigenvec3D(2)) > 0) {
				return getEigenvec3D(2).copyVector(); // back to "plane axis"
			}
			return getEigenvec3D(2).mul(-1); // back to "plane axis"
		case QUADRIC_HYPERBOLIC_CYLINDER:
			tmpCoords.setSub(getMidpoint3D(), source);
			if (tmpCoords.dotproduct(getEigenvec3D(0)) > 0) {
				return getEigenvec3D(0).copyVector(); // back to "plane axis"
			}
			return getEigenvec3D(0).mul(-1); // back to "plane axis"
		case QUADRIC_HYPERBOLIC_PARABOLOID:
			return getEigenvec3D(2).copyVector(); // back to "base plane"
		default:
			return null;
		}
	}

	@Override
	public void pointChangedForRegion(GeoPointND P) {
		GeoPoint3D p1 = (GeoPoint3D) P;

		RegionParameters rp = P.getRegionParameters();
		rp.setRegionType(type);

		if (type == QUADRIC_SINGLE_POINT) {
			p1.setCoords(getMidpoint3D(), false);
			p1.updateCoords();
			return;
		}

		if (type == QUADRIC_PLANE) {
			p1.updateCoords2D(planes[0], true);
			P.updateCoordsFrom2D(false, planes[0].getCoordSys());
			rp.setT1(PathNormalizer.inverseInfFunction(rp.getT1()));
			return;
		}

		if (type == QUADRIC_LINE) {
			double t = line.getParamOnLine(P);
			P.getRegionParameters().setT1(t);
			if (Double.isNaN(P.getRegionParameters().getT2())) {
				P.getRegionParameters().setT2(0);
			}
			// update point using pathChanged
			P.setCoords(line.getPoint(t), false);
			return;
		}

		if (hasLastHitParameters()) {
			// use last hitted parameters
			rp.setT1(lastHitParameters[0]);
			rp.setT2(lastHitParameters[1]);
			rp.setNormal(evaluateNormal(rp.getT1(), rp.getT2()));
			evaluatePoint(rp.getT1(), rp.getT2(), p1.getCoords());
			p1.updateCoords();
			p1.setWillingCoordsUndefined();
			p1.setWillingDirectionUndefined();
			resetLastHitParameters();
		} else {

			if (type == QUADRIC_PARALLEL_PLANES
					|| type == QUADRIC_INTERSECTING_PLANES) {

				Coords coords, direction;

				if (p1.hasWillingCoords()) { // use willing coords
					coords = p1.getWillingCoords();
				} else {
					// use real coords
					coords = p1.getCoords();
				}

				GeoPlane3D plane = planes[0];
				CoordMatrix planeMatrix = plane.getCoordSys()
						.getMatrixOrthonormal();
				if (!p1.hasWillingDirection()) { // use normal direction for
					// projection
					direction = planeMatrix.getVz();
				} else { // use willing direction for projection
					direction = p1.getWillingDirection();
				}

				coords.projectPlaneInPlaneCoords(planeMatrix.getVx(),
						planeMatrix.getVy(), direction, planeMatrix.getOrigin(),
						tmpCoords);

				double t1Shift = 0;

				if (!DoubleUtil.isZero(tmpCoords.getZ())) {
					plane = planes[1];
					planeMatrix = plane.getCoordSys().getMatrixOrthonormal();
					if (!p1.hasWillingDirection()) { // use normal direction for
						// projection
						direction = planeMatrix.getVz();
					}

					coords.projectPlaneInPlaneCoords(planeMatrix.getVx(),
							planeMatrix.getVy(), direction,
							planeMatrix.getOrigin(), tmpCoords);

					t1Shift = 2;
				}

				p1.setCoords(plane.getPoint(tmpCoords.getX(), tmpCoords.getY(),
						new Coords(4)), false);
				rp.setT1(PathNormalizer.inverseInfFunction(tmpCoords.getX())
						+ t1Shift);
				rp.setT2(tmpCoords.getY());
				rp.setNormal(plane.getDirectionInD3());

				p1.setWillingCoordsUndefined();
				p1.setWillingDirectionUndefined();

				return;
			}

			Coords willingCoords;
			if (p1.hasWillingCoords()) {
				willingCoords = p1.getWillingCoords().copyVector();
				p1.setWillingCoordsUndefined();
			} else {
				willingCoords = P.getCoordsInD3();
			}

			Coords willingDirection;
			if (p1.hasWillingDirection()) {
				willingDirection = p1.getWillingDirection().copyVector();
				p1.setWillingDirectionUndefined();
			} else {
				willingDirection = getDirectionToCenter(willingCoords);
			}

			Coords[] coords = getProjection(null, willingCoords,
					willingDirection);

			rp.setT1(coords[1].get(1));
			rp.setT2(coords[1].get(2));
			rp.setNormal(evaluateNormal(coords[1].get(1), coords[1].get(2)));
			p1.setCoords(coords[0], false);
			p1.updateCoords();
		}

	}

	@Override
	public void regionChanged(GeoPointND P) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)
				|| P.getRegionParameters().isNaN()) {
			pointChangedForRegion(P);
			return;
		}

		RegionParameters rp = P.getRegionParameters();

		// if type of region changed (other quadric) then we
		// have to recalc the parameter with pointChangedForRegion()
		if (P.isDefined() && !compatibleType(rp.getRegionType())) {
			pointChangedForRegion(P);
			return;
		}

		GeoPoint3D p1 = (GeoPoint3D) P;

		if (type == QUADRIC_SINGLE_POINT) {
			p1.setCoords(getMidpoint3D(), false);
			p1.updateCoords();
			return;
		}

		Coords coords = getPointInRegion(rp.getT1(), rp.getT2());
		p1.setCoords(coords, false);
		p1.updateCoords();

	}

	private boolean compatibleType(int regionType) {
		return type == GeoQuadricNDConstants.QUADRIC_EMPTY
				|| regionType == GeoQuadricNDConstants.QUADRIC_EMPTY
				|| type == regionType;
	}

	// ///////////////////////////////////
	// TRANSFORMATIONS
	// ///////////////////////////////////

	@Override
	public void translate(Coords v) {
		Coords m = getMidpoint3D();
		m.addInside(v);
		setMidpoint(m.get());

		// current symetric matrix
		CoordMatrix sm = getSymetricMatrix();
		// transformation matrix
		CoordMatrix tm = CoordMatrix.identity(4);
		tm.subToOrigin(v);
		// set new symetric matrix
		setMatrix((tm.transposeCopy()).mul(sm).mul(tm));

		// eigen matrix
		eigenMatrix.setOrigin(m);

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].translate(v);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].translate(v);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			line.translate(v);
		}

	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}

		CoordMatrix4x4.rotation4x4(r.getDouble(), S.getInhomCoordsInD3(),
				tmpMatrix4x4);
		rotate(tmpMatrix4x4);

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].rotate(r, S);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].rotate(r, S);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			line.rotate(r, S);
		}
	}

	@Override
	public void rotate(NumberValue r) {
		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}

		CoordMatrix4x4.rotation4x4(r.getDouble(), tmpMatrix4x4);
		rotate(tmpMatrix4x4);

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].rotate(r);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].rotate(r);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			line.rotate(r);
		}
	}

	private void rotate(CoordMatrix4x4 tm) {

		// eigen matrix
		eigenMatrix = tm.mul(eigenMatrix);

		// midpoint
		setMidpoint(eigenMatrix.getOrigin().get());

		// eigen vectors
		for (int i = 0; i < 3; i++) {
			eigenvecND[i] = tm.mul(eigenvecND[i]);
		}

		// symetric matrix
		CoordMatrix tmInv = tm.inverse();
		setMatrix((tmInv.transposeCopy()).mul(getSymetricMatrix()).mul(tmInv));

	}

	@Override
	public void rotate(NumberValue r, GeoPointND S,
			GeoDirectionND orientation) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		CoordMatrix4x4.rotation4x4(orientation.getDirectionInD3().normalized(),
				r.getDouble(), S.getInhomCoordsInD3(), tmpMatrix4x4);
		rotate(tmpMatrix4x4);

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].rotate(r, S, orientation);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].rotate(r, S, orientation);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			line.rotate(r, S, orientation);
		}
	}

	@Override
	public void rotate(NumberValue r, GeoLineND axis) {
		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		CoordMatrix4x4.rotation4x4(axis.getDirectionInD3().normalized(),
				r.getDouble(), axis.getStartInhomCoords(), tmpMatrix4x4);
		rotate(tmpMatrix4x4);

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].rotate(r, axis);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].rotate(r, axis);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			this.line.rotate(r, axis);
		}

	}

	// //////////////////////
	// MIRROR
	// //////////////////////

	@Override
	public void mirror(Coords point) {
		// eigen matrix
		eigenMatrix.mulInside(-1);
		eigenMatrix.addToOrigin(point.mul(2));

		// midpoint
		setMidpoint(eigenMatrix.getOrigin().get());

		// eigen vectors
		for (int i = 0; i < 3; i++) {
			eigenvecND[i].mulInside(-1);
		}

		// symetric matrix
		setMatrixFromEigen();

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].mirror(point);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].mirror(point);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			line.mirror(point);
		}
	}

	@Override
	public void mirror(GeoLineND mirrorLine) {
		Coords point = mirrorLine.getStartInhomCoords();
		Coords direction = mirrorLine.getDirectionInD3().normalized();

		// midpoint
		Coords mp = getMidpoint3D();
		mp.projectLine(point, direction, tmpCoords, null);
		mp.mulInside(-1);
		mp.addInsideMul(tmpCoords, 2);
		setMidpoint(mp.get());

		// eigen vectors
		for (int i = 0; i < 3; i++) {
			Coords v = eigenvecND[i];
			double a = 2 * v.dotproduct(direction);
			v.mulInside(-1);
			v.addInsideMul(direction, a);
		}

		// symetric matrix
		setMatrixFromEigen();

		// set eigen matrix
		setEigenMatrix(getHalfAxis(0), getHalfAxis(1), getHalfAxis(2));

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].mirror(mirrorLine);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].mirror(mirrorLine);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			this.line.mirror(mirrorLine);
		}
	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		Coords vn = plane.getDirectionInD3().normalized();

		// midpoint
		Coords mp = getMidpoint3D();
		mp.projectPlane(plane.getCoordSys().getMatrixOrthonormal(), tmpCoords);
		mp.mulInside(-1);
		mp.addInside(tmpCoords.mul(2));
		setMidpoint(mp.get());

		// eigen vectors
		for (int i = 0; i < 3; i++) {
			Coords v = eigenvecND[i];
			double a = -2 * v.dotproduct(vn);
			v.addInside(vn.mul(a));
		}

		// symetric matrix
		setMatrixFromEigen();

		// set eigen matrix
		setEigenMatrix(getHalfAxis(0), getHalfAxis(1), getHalfAxis(2));

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].mirror(plane);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].mirror(plane);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			line.mirror(plane);
		}

	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {
		double r = rval.getDouble();

		// midpoint
		Coords mp = getMidpoint3D();
		mp.mulInside(r);
		mp.addInside(S.mul(1 - r));
		setMidpoint(mp.get());

		if (r < 0) {
			// eigen vectors
			for (int i = 0; i < 3; i++) {
				eigenvecND[i].mulInside(-1);
			}

			r = -r;
		}

		// half axis and diagonals
		for (int i = 0; i < 3; i++) {
			halfAxes[i] *= r;
			diagonal[i] *= r;
		}

		// symetric matrix
		setMatrixFromEigen();

		// set eigen matrix
		setEigenMatrix(getHalfAxis(0), getHalfAxis(1), getHalfAxis(2));

		// volume
		volume *= r * r * r;

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].dilate(rval, S);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].dilate(rval, S);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			line.dilate(rval, S);
		}
	}

	// ///////////////////////////////////
	// VOLUME
	// ///////////////////////////////////

	@Override
	public double getVolume() {
		switch (getType()) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID:
			return volume;
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			// return Double.POSITIVE_INFINITY; //TODO ? (0 or infinity)
		default:
			return Double.NaN;
		}
	}

	@Override
	public boolean hasFiniteVolume() {
		switch (getType()) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID:
			return isDefined();
		default:
			return false;
		}
	}

	@Override
	public void setUndefined() {
		super.setUndefined();
		volume = Double.NaN;
	}

	@Override
	final protected void singlePoint() {
		type = GeoQuadricNDConstants.QUADRIC_SINGLE_POINT;
	}

	@Override
	public boolean isGeoQuadric() {
		return true;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		getLineStyleXML(sb);

		sb.append("\t<eigenvectors ");
		sb.append(" x0=\"" + eigenvecND[0].getX() + "\"");
		sb.append(" y0=\"" + eigenvecND[0].getY() + "\"");
		sb.append(" z0=\"" + eigenvecND[0].getZ() + "\"");
		sb.append(" x1=\"" + eigenvecND[1].getX() + "\"");
		sb.append(" y1=\"" + eigenvecND[1].getY() + "\"");
		sb.append(" z1=\"" + eigenvecND[1].getZ() + "\"");
		sb.append(" x2=\"" + eigenvecND[2].getX() + "\"");
		sb.append(" y2=\"" + eigenvecND[2].getY() + "\"");
		sb.append(" z2=\"" + eigenvecND[2].getZ() + "\"");
		sb.append("/>\n");

		// matrix must be saved after eigenvectors
		// as only <matrix> will cause a call to classifyConic()
		// see geogebra.io.MyXMLHandler: handleMatrix() and handleEigenvectors()
		getXMLtagsMatrix(sb);

		XMLBuilder.appendEquationTypeConic(sb, getToStringMode(), null);

	}

	@Override
	final public void setEigenvectors(double x0, double y0, double z0,
			double x1, double y1, double z1, double x2, double y2, double z2) {
		eigenvecND[0].set(x0, y0, z0);
		eigenvecND[1].set(x1, y1, z1);
		eigenvecND[2].set(x2, y2, z2);
	}

	/**
	 * put XML tags for matrix in sb
	 * 
	 * @param sb
	 *            string builder
	 */
	protected void getXMLtagsMatrix(StringBuilder sb) {
		sb.append("\t<matrix");
		for (int i = 0; i < 10; i++) {
			sb.append(" A" + i + "=\"" + matrix[i] + "\"");
		}
		sb.append("/>\n");
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.EQUATION;
	}

	@Override
	public boolean showLineProperties() {
		return type == GeoQuadricNDConstants.QUADRIC_LINE
				|| type == GeoQuadricNDConstants.QUADRIC_SINGLE_POINT;
	}

	@Override
	protected void getXMLanimationTags(final StringBuilder sb) {
		// no need for quadrics
	}

	@Override
	public Equation getEquation() {
		return kernel.getAlgebraProcessor().parseEquation(this);
	}

	/**
	 * 
	 * @param origin
	 *            origin
	 * @param direction
	 *            direction
	 * @param eigen
	 *            first eigen vector
	 * @param r
	 *            first radius
	 * @param r2
	 *            second radius
	 */
	public void set(Coords origin, Coords direction, Coords eigen, double r,
			double r2) {
		// implemented in GeoQuadric3DPart
	}

	/**
	 * sets the min and max values for limits
	 * 
	 * @param min
	 *            minimum
	 * @param max
	 *            maximum
	 */
	public void setLimits(double min, double max) {
		// implemented in GeoQuadric3DPart
	}

	@Override
	public String[] getEquationVariables() {
		ArrayList<String> vars = new ArrayList<>();
		if (!MyDouble.exactEqual(matrix[0], 0)
				|| !MyDouble.exactEqual(matrix[4], 0)
				|| !MyDouble.exactEqual(matrix[5], 0)
				|| !MyDouble.exactEqual(matrix[7], 0)) {
			vars.add("x");
		}
		if (!MyDouble.exactEqual(matrix[1], 0)
				|| !MyDouble.exactEqual(matrix[4], 0)
				|| !MyDouble.exactEqual(matrix[6], 0)
				|| !MyDouble.exactEqual(matrix[8], 0)) {
			vars.add("y");
		}
		if (!MyDouble.exactEqual(matrix[2], 0)
				|| !MyDouble.exactEqual(matrix[5], 0)
				|| !MyDouble.exactEqual(matrix[6], 0)
				|| !MyDouble.exactEqual(matrix[9], 0)) {
			vars.add("z");
		}
		return vars.toArray(new String[0]);
	}

	@Override
	public boolean setTypeFromXML(String style, String parameter, boolean force) {
		if ("implicit".equals(style)) {
			setToImplicit();
		} else if ("specific".equals(style)) {
			toStringMode = GeoConicND.EQUATION_SPECIFIC;
		} else if ("user".equals(style)) {
			setToUser();
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void setToImplicit() {
		toStringMode = GeoConicND.EQUATION_IMPLICIT;
	}

	@Override
	public boolean isSpecificPossible() {
		return type == QUADRIC_SPHERE;
	}

	@Override
	public String getSpecificEquation() {
		if (type == GeoQuadricNDConstants.QUADRIC_SPHERE) {
			return getLoc().getMenu("SphereEquation");
		}
		return null;
	}
}
