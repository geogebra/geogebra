package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawConic3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDependentQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
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
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;

/**
 * class describing quadric for 3D space
 * 
 * @author matthieu
 * 
 *         ( A[0] A[4] A[5] A[7]) matrix = ( A[4] A[1] A[6] A[8]) ( A[5] A[6]
 *         A[2] A[9]) ( A[7] A[8] A[9] A[3])
 * 
 */
public class GeoQuadric3D extends GeoQuadricND implements Functional2Var,
		Region3D, Translateable, RotateableND, MirrorableAtPlane,
		Transformable, Dilateable, HasVolume, GeoQuadric3DInterface {

	private static String[] vars3D = { "x\u00b2", "y\u00b2", "z\u00b2", "x y",
			"x z", "y z", "x", "y", "z" };

	private CoordMatrix4x4 eigenMatrix = CoordMatrix4x4.Identity();

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
	 * @param label
	 *            label
	 * @param coeffs
	 *            coefficients
	 */
	public GeoQuadric3D(Construction c, String label, double[] coeffs) {

		this(c);
		setMatrix(coeffs);
		setLabel(label);
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

		if (kernel.getConstruction() != null
				&& !kernel.getConstruction().isFileLoading()) {
			// will be done at XML end, by setMatrixFromXML()
			classifyQuadric();
		}
	}

	/**
	 * sets quadric's matrix from coefficients of equation from array
	 * 
	 * @param coeffs
	 *            Array of coefficients
	 */
	final public void setMatrixFromXML(double[] coeffs) {

		for (int i = 0; i < 10; i++) {
			matrix[i] = coeffs[i];
		}

		if (type == GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED) {
			classifyQuadric();
		}
	}

	private double detS;

	/**
	 * Update quadric type and properties
	 */
	protected void classifyQuadric() {

		defined = checkDefined();
		if (!defined)
			return;

		// det of S
		detS = matrix[0] * matrix[1] * matrix[2] - matrix[0] * matrix[6]
				* matrix[6] - matrix[1] * matrix[5] * matrix[5] - matrix[2]
				* matrix[4] * matrix[4] + 2 * matrix[4] * matrix[5] * matrix[6];
		if (Kernel.isZero(detS)) {
			classifyNoMidpointQuadric();
		} else {
			classifyMidpointQuadric();
		}

	}

	private void classifyNoMidpointQuadric() {

		// no midpoint, detS == 0

		// set eigenvalues
		eigenval[0] = -matrix[0] * matrix[1] - matrix[1] * matrix[2]
				- matrix[2] * matrix[0] + matrix[4] * matrix[4] + matrix[5]
				* matrix[5] + matrix[6] * matrix[6];
		eigenval[1] = matrix[0] + matrix[1] + matrix[2];
		eigenval[2] = -1;

		int nRoots = cons.getKernel().getEquationSolver()
				.solveQuadratic(eigenval, eigenval, Kernel.STANDARD_PRECISION);

		if (nRoots == 1) {
			eigenval[1] = eigenval[0];
			nRoots++;
		}

		eigenval[2] = 0;

		// App.debug("eigenvals = " + eigenval[0] + "," + eigenval[1] + ","
		// + eigenval[2]);

		// check if others eigenvalues are 0
		if (Kernel.isZero(eigenval[0])) {
			if (Kernel.isZero(eigenval[1])) {
				// three eigenvalues = 0: one plane
				getPlanes();
				planes[0].setEquation(matrix[7], matrix[8], matrix[9],
						matrix[3] / 2);
				type = GeoQuadricNDConstants.QUADRIC_PLANE;
			} else {
				// two eigenvalues = 0
				twoZeroEigenvalues(eigenval[1]);
			}
		} else if (Kernel.isZero(eigenval[1])) {
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

			if (Kernel.isRatioEqualTo1(eigenval[0], eigenval[1])) {
				// find from eigenvalue = 0, since both others are equal
				findEigenvector(eigenval[2], eigenvecND[2]);
				eigenvecND[2].normalize();
				eigenvecND[2].completeOrthonormal(eigenvecND[0], eigenvecND[1]);
			} else {
				findEigenvector(eigenval[0], eigenvecND[0]);
				eigenvecND[0].normalize();
				findEigenvector(eigenval[1], eigenvecND[1]);
				eigenvecND[1].normalize();
				eigenvecND[2].setCrossProduct(eigenvecND[0], eigenvecND[1]);
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
				if (Kernel.isZero(z)) {
					// cylinder
					if (Kernel.isZero(m)) {
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
				if (Kernel.isZero(z)) {
					// cylinder
					if (Kernel.isZero(m)) {
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

	private CoordMatrix tmpMatrix3x3;

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

		// App.debug("\n=================================\nvalue = " + value);

		ret.setX(0);
		ret.setY(0);
		ret.setZ(0);
		ret.setW(0);
		tmpMatrix3x3.pivotDegenerate(ret, Coords.ZERO);

		// App.debug("\nvalue = " + value + "\nmatrix = \n" + tmpMatrix3x3
		// + "\nsol = \n" + ret);
	}

	private GeoPlane3D[] planes;

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

	private GeoLine3D line;

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

	private CoordMatrix eigenvecNDMatrix, semiDiagMatrix;

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
		if (!Kernel.isZero(semiDiagMatrix.get(1, 4))
				|| !Kernel.isZero(semiDiagMatrix.get(2, 4))) {
			// parabolic cylinder
			parabolicCylinder(value);
		} else {
			// parallel planes or empty
			double a = semiDiagMatrix.get(3, 4);
			double b = semiDiagMatrix.get(4, 4);

			// get case
			double c = a / value;
			double m = c * c - b / value;
			if (Kernel.isZero(m)) {
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

		type = GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES;
	}

	private void parabolicCylinder(double value) {

		double a = semiDiagMatrix.get(1, 4);
		double b = semiDiagMatrix.get(2, 4);
		double c = semiDiagMatrix.get(3, 4);
		double d = semiDiagMatrix.get(4, 4);

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
		eigenvecND[1].setCrossProduct(eigenvecND[2], eigenvecND[0]);

		// for (int i = 0; i < 3; i++) {
		// App.debug("eigenvecND[" + i + "]=\n" + eigenvecND[i]);
		// }

		// set eigenvalues
		eigenval[0] = 0;
		eigenval[1] = 0;
		eigenval[2] = value;

		// set midpoint
		midpoint.set(Coords.O);
		midpoint.addInside(tmpCoords.setMul(eigenvecND[0], valSgn
				* (d - c * c / value)
				/ (2 * norm)));
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

	private Coords tmpCoords2, tmpCoords3, tmpCoords4, tmpCoords5;

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

		cs = planes[1].getCoordSys();
		cs.resetCoordSys();
		cs.addPoint(tmpCoords4);
		cs.addVectorWithoutCheckMadeCoordSys(eigenvecND[2]);
		tmpCoords3.setSub(tmpCoords, tmpCoords2);
		cs.addVectorWithoutCheckMadeCoordSys(tmpCoords3);
		cs.makeOrthoMatrix(false, false);

		type = GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES;

	}

	private void classifyMidpointQuadric() {

		// set midpoint
		double x = (-matrix[1] * matrix[2] * matrix[7] + matrix[1] * matrix[5]
				* matrix[9] + matrix[2] * matrix[4] * matrix[8] - matrix[4]
				* matrix[6] * matrix[9] - matrix[5] * matrix[6] * matrix[8] + matrix[6]
				* matrix[6] * matrix[7])
				/ detS;
		double y = (-matrix[0] * matrix[2] * matrix[8] + matrix[0] * matrix[6]
				* matrix[9] + matrix[2] * matrix[4] * matrix[7] - matrix[4]
				* matrix[5] * matrix[9] + matrix[5] * matrix[5] * matrix[8] - matrix[5]
				* matrix[6] * matrix[7])
				/ detS;
		double z = (-matrix[0] * matrix[1] * matrix[9] + matrix[0] * matrix[6]
				* matrix[8] + matrix[1] * matrix[5] * matrix[7] + matrix[4]
				* matrix[4] * matrix[9] - matrix[4] * matrix[5] * matrix[8] - matrix[4]
				* matrix[6] * matrix[7])
				/ detS;
		double[] coords = { x, y, z, 1 };
		setMidpoint(coords);

		// App.debug("\nmidpoint = " + x + "," + y + "," + z);

		// set eigenvalues
		eigenval[0] = detS;
		eigenval[1] = -matrix[0] * matrix[1] - matrix[1] * matrix[2]
				- matrix[2] * matrix[0] + matrix[4] * matrix[4] + matrix[5]
				* matrix[5] + matrix[6] * matrix[6];
		eigenval[2] = matrix[0] + matrix[1] + matrix[2];
		eigenval[3] = -1;

		int nRoots = cons.getKernel().getEquationSolver()
				.solveCubic(eigenval, eigenval, Kernel.STANDARD_PRECISION);

		if (nRoots == 1) {
			eigenval[1] = eigenval[0];
			nRoots++;
		}

		if (nRoots == 2) {
			eigenval[2] = eigenval[1];
		}

		// degenerate ? (beta is det of 4x4 matrix)
		double beta = matrix[7] * x + matrix[8] * y + matrix[9] * z + matrix[3];

		if (Kernel.isZero(beta)) {
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
					} else {// xx-yy+zz=-beta
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
		// App.debug("hyperboloidOneSheet : " + val0 + "," + val1 + "," + val2);

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
		halfAxes[2] = Math.sqrt(-1.0d / mu[2]);

		// set the diagonal values
		for (int i = 0; i < 3; i++) {
			diagonal[i] = mu[i];
		}
		diagonal[3] = -1;

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], halfAxes[2]);

		// set type
		type = QUADRIC_HYPERBOLOID_ONE_SHEET;

	}

	private void hyperboloidTwoSheets(double val0, double val1, double val2,
			double beta) {
		// App.debug("hyperboloidTwoSheets : " + val0 + "," + val1 + "," +
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

	private void completeOrthonormalRatioEqualTo1(Coords ev0, Coords ev1,
			Coords ev2) {
		// try to keep ev0
		tmpCoords.setCrossProduct(ev2, ev0);
		if (!tmpCoords.isZero()) {
			ev1.setValues(tmpCoords, 3);
			ev1.normalize();
		} // else ev1 and ev2 are already orthogonal
		ev0.setCrossProduct(ev1, ev2);
		ev0.normalize();
	}

	private void setHyperboloidEigenvectors() {
		// mu[2] can't be equal to mu[0] and mu[1] -- not same sign
		tmpCoords.setValues(eigenvecND[2], 3);
		findEigenvector(eigenval[2], eigenvecND[2]);
		eigenvecND[2].normalize();
		if (tmpCoords.dotproduct3(eigenvecND[2]) < 0) {
			eigenvecND[2].mulInside3(-1);
		}

		if (Kernel.isRatioEqualTo1(eigenval[0], eigenval[1])) {
			// eigenval[0] == eigenval[1]
			completeOrthonormalRatioEqualTo1(eigenvecND[0], eigenvecND[1],
					eigenvecND[2]);
		} else {
			tmpCoords.setValues(eigenvecND[0], 3);
			findEigenvector(eigenval[0], eigenvecND[0]);
			eigenvecND[0].normalize();
			eigenvecND[1].setCrossProduct(eigenvecND[2], eigenvecND[0]);
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
			halfAxes[0] = Math.sqrt(-eigenval[directionIndex]
					/ eigenval[ellipseIndex0]);
			halfAxes[1] = Math.sqrt(-eigenval[directionIndex]
					/ eigenval[ellipseIndex1]);
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
		if (Kernel.isEqual(eigenval[0] / eigenval[1], 1.0)
				&& Kernel.isEqual(eigenval[0] / eigenval[2], 1.0)) {

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
					if (Math.abs(dot1) > Math.abs(dot0)) {// set ND1 to 1
						eigenvecND[1].setValues(eigenvec[1], 3);
						// set ND2 to 0 (last one)
						eigenvecND[2].setValues(eigenvec[0], 3);
						eigenval[2] = tmp;
						dot0 = dot1;
						reverse = !reverse;
					} else {// set ND1 to 0
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
					if (Math.abs(dot2) > Math.abs(dot0)) {// set ND1 to 2
						eigenvecND[1].setValues(eigenvec[2], 3);
						// set ND2 to 0 (last one)
						eigenvecND[2].setValues(eigenvec[0], 3);
						eigenval[1] = eigenval[2];
						eigenval[2] = tmp;
						dot0 = dot2;
					} else {// set ND1 to 0
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
					if (Math.abs(dot1) > Math.abs(dot0)) {// set ND1 to 1
						eigenvecND[1].setValues(eigenvec[1], 3);
						// set ND2 to 0 (last one)
						eigenvecND[2].setValues(eigenvec[0], 3);
						eigenval[2] = tmp;
						dot0 = dot1;
						reverse = !reverse;
					} else {// set ND1 to 0
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
					if (Math.abs(dot2) > Math.abs(dot1)) {// set ND1 to 2
						eigenvecND[1].setValues(eigenvec[2], 3);
						// set ND2 to 1 (last one)
						eigenvecND[2].setValues(eigenvec[1], 3);
						double tmp = eigenval[1];
						eigenval[1] = eigenval[2];
						eigenval[2] = tmp;
						reverse = !reverse;
						dot1 = dot2;
					} else {// set ND1 to 1
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
		// App.debug("\neigen values: " + eigenval[0] + ","
		// + eigenval[1] + ","
		// + eigenval[2]);

		if (eigenvec == null) {
			eigenvec = new Coords[3];
			for (int i = 0; i < 3; i++) {
				eigenvec[i] = new Coords(3);
			}
		}

		if (Kernel.isRatioEqualTo1(eigenval[0], eigenval[1])) {
			// eigenval[2] of multiplicity 1
			computeEigenVectorMultiplicity1(matrix, eigenval[2], eigenvec[2]);
			eigenvec[2].normalize();
			completeOrthonormalRatioEqualTo1(eigenvec[0], eigenvec[1],
					eigenvec[2]);
			// eigenvec[2].completeOrthonormal3(eigenvec[0], eigenvec[1]);
		} else if (Kernel.isRatioEqualTo1(eigenval[0], eigenval[2])) {
			// eigenval[1] of multiplicity 1
			computeEigenVectorMultiplicity1(matrix, eigenval[1], eigenvec[1]);
			eigenvec[1].normalize();
			completeOrthonormalRatioEqualTo1(eigenvec[2], eigenvec[0],
					eigenvec[1]);
			// eigenvec[1].completeOrthonormal3(eigenvec[2], eigenvec[0]);
		} else if (Kernel.isRatioEqualTo1(eigenval[1], eigenval[2])) {
			// eigenval[0] of multiplicity 1
			computeEigenVectorMultiplicity1(matrix, eigenval[0], eigenvec[0]);
			eigenvec[0].normalize();
			completeOrthonormalRatioEqualTo1(eigenvec[1], eigenvec[2],
					eigenvec[0]);
			// eigenvec[0].completeOrthonormal3(eigenvec[1], eigenvec[2]);
		} else {
			// all eigenvalues of multiplicity 1
			for (int i = 0; i < 2; i++) {
				computeEigenVectorMultiplicity1(matrix, eigenval[i],
						eigenvec[i]);
			}
			eigenvec[2].setCrossProduct(eigenvec[0], eigenvec[1]); // ensure
																	// orientation

			for (int i = 0; i < 3; i++) {
				eigenvec[i].normalize();
			}

			// for (int i = 0; i < 3; i++) {
			// for (int j = i + 1; j < 3; j++) {
			// App.debug("dotproduct = "
			// + eigenvec[i].dotproduct(eigenvec[j]));
			// }
			// }
			//
			// App.debug("orientation : "
			// + eigenvec[0].crossProduct(eigenvec[1]).dotproduct(
			// eigenvec[2]));
		}

		// String s = "";
		// for (int i = 0; i < 3; i++) {
		// Coords v = eigenvec[i];
		// s += "\neigen vector #" + i + ": " + "{" + v.getX() + ","
		// + v.getY() + "," + v.getZ() + "}";
		// }
		// App.debug(s);

	}

	// private static final String format(double v) {
	// return "" + v;
	// }
	//
	// private static final String subEFormat(double v, double e) {
	// return "" + (v - e);
	// }

	private Coords[] eigenvec;

	private static final void computeEigenVectorMultiplicity1(double[] m,
			double mu, Coords v) {

		// lines are dependents

		// first try, result maybe 0 if lines 1 & 2 are dependent
		v.set(m[5] * (m[1] - mu) - m[4] * m[6], m[6] * (m[0] - mu) - m[4]
				* m[5], m[4] * m[4] - (m[0] - mu) * (m[1] - mu));

		if (v.isZero()) {
			// second try, result maybe 0 if lines 1 & 3 are dependent
			v.set(m[5] * m[6] - m[4] * (m[2] - mu), (m[0] - mu) * (m[2] - mu)
					- m[5] * m[5], m[4] * m[5] - m[6] * (m[0] - mu));

			if (v.isZero()) {
				// third try: lines 2 & 3 are not dependent, so line 1 equals 0
				// (multiplicity 1)
				v.set(1, 0, 0);
			}
		}

		// App.debug("\neigen value: " + mu + "\nmatrix - mu * Id:\n"
		// + subEFormat(m[0], mu) + " " + format(m[4]) + " "
		// + format(m[5]) + "\n" + format(m[4]) + " "
		// + subEFormat(m[1], mu) + " " + format(m[6]) + "\n"
		// + format(m[5]) + " " + format(m[6]) + " "
		// + subEFormat(m[2], mu) + "\neigen vector:\n" + "{" + v.getX()
		// + "," + v.getY() + "," + v.getZ() + "}");

	}

	/**
	 * returns false if quadric's matrix is the zero matrix or has infinite or
	 * NaN values
	 */
	final private boolean checkDefined() {

		/*
		 * boolean allZero = true; double maxCoeffAbs = 0;
		 * 
		 * for (int i = 0; i < 6; i++) { if (Double.isNaN(matrix[i]) ||
		 * Double.isInfinite(matrix[i])) { return false; }
		 * 
		 * double abs = Math.abs(matrix[i]); if (abs >
		 * Kernel.STANDARD_PRECISION) allZero = false; if ((i == 0 || i == 1 ||
		 * i == 3) && maxCoeffAbs < abs) { // check max only on coeffs x*x, y*y,
		 * x*y maxCoeffAbs = abs; } } if (allZero) { return false; }
		 * 
		 * // huge or tiny coefficients? double factor = 1.0; if (maxCoeffAbs <
		 * MIN_COEFFICIENT_SIZE) { factor = 2; while (maxCoeffAbs * factor <
		 * MIN_COEFFICIENT_SIZE) factor *= 2; } else if (maxCoeffAbs >
		 * MAX_COEFFICIENT_SIZE) { factor = 0.5; while (maxCoeffAbs * factor >
		 * MAX_COEFFICIENT_SIZE) factor *= 0.5; }
		 * 
		 * // multiply matrix with factor to avoid huge and tiny coefficients if
		 * (factor != 1.0) { maxCoeffAbs *= factor; for (int i=0; i < 6; i++) {
		 * matrix[i] *= factor; } }
		 */
		return true;
	}

	public GeoQuadric3D(GeoQuadric3D quadric) {
		this(quadric.getConstruction());
		set(quadric);
	}

	public Coords getMidpointND() {
		return getMidpoint3D();
	}

	// //////////////////////////////
	// SPHERE

	private double volume = Double.NaN;

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

	public void setCone(GeoPointND origin, GeoVectorND direction, double angle) {

		// check midpoint
		defined = ((GeoElement) origin).isDefined() && !origin.isInfinite();

		// check direction

		// check angle
		double r;
		double c = Math.cos(angle);
		double s = Math.sin(angle);

		if (c < 0 || s < 0)
			defined = false;
		else if (Kernel.isZero(c))
			defined = false;// TODO if c=0 then draws a plane
		else if (Kernel.isZero(s))
			defined = false;// TODO if s=0 then draws a line
		else {
			r = s / c;
			setCone(origin.getInhomCoordsInD3(), direction.getCoordsInD3(), r);
		}

	}

	public void setCone(Coords origin, Coords direction, double r) {

		// set center
		setMidpoint(origin.get());

		// set direction
		eigenvecND[2].setValues(direction, 3);

		// set others eigen vecs
		eigenvecND[2].completeOrthonormal(eigenvecND[0], eigenvecND[1]);

		// set halfAxes = radius
		for (int i = 0; i < 2; i++)
			halfAxes[i] = r;

		halfAxes[2] = 1;

		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = -r * r;
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

	public void setCylinder(GeoPointND origin, Coords direction, double r) {

		// check midpoint
		defined = ((GeoElement) origin).isDefined() && !origin.isInfinite();

		// check direction

		// check radius
		if (Kernel.isZero(r)) {
			r = 0;
		} else if (r < 0) {
			defined = false;
		}

		if (defined) {
			setCylinder(origin.getInhomCoordsInD3(), direction, r);
		}

	}

	public void setCylinder(Coords origin, Coords direction, double r) {

		// set center
		setMidpoint(origin.get());

		// set direction
		eigenvecND[2].setValues(direction, 3);

		// set others eigen vecs
		eigenvecND[2].completeOrthonormal(eigenvecND[0], eigenvecND[1]);

		// set halfAxes = radius
		for (int i = 0; i < 2; i++)
			halfAxes[i] = r;

		halfAxes[2] = 1;

		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = 0;
		diagonal[3] = -r * r;

		// set matrix
		setMatrixFromEigen();

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], 1);

		// set type
		type = QUADRIC_CYLINDER;
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

		if (getParentAlgorithm() != null
				&& getParentAlgorithm() instanceof AlgoDependentQuadric3D) {
			return "Quadric";
		}

		return getTypeString();
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		return false;
	}

	@Override
	public void set(GeoElementND geo) {

		GeoQuadric3D quadric = (GeoQuadric3D) geo;

		// copy everything
		toStringMode = quadric.toStringMode;
		type = quadric.type;
		for (int i = 0; i < 10; i++)
			matrix[i] = quadric.matrix[i]; // flat matrix A

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

		super.set(geo);

	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return type != GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED
				&& type != GeoQuadricNDConstants.QUADRIC_EMPTY;
	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {

		StringBuilder sbToValueString = new StringBuilder();

		switch (type) {
		case QUADRIC_SPHERE:
			buildSphereNDString(sbToValueString, tpl);
			break;
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
		default:
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

			return kernel.buildImplicitEquation(coeffs, vars3D, false, true,
					true, '=', tpl);
		}

		return sbToValueString;
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

	// ///////////////////////////////////////
	// SURFACE (u,v)->(x,y,z) INTERFACE
	// ///////////////////////////////////////

	public Coords evaluatePoint(double u, double v) {
		Coords ret = Coords.createInhomCoorsInD3();
		evaluatePoint(u, v, ret);
		return ret;
	}

	public void evaluatePoint(double u, double v, Coords point) {

		switch (type) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID:
			point.setMulPoint(eigenMatrix, Math.cos(u) * Math.cos(v),
					Math.sin(u) * Math.cos(v), Math.sin(v));
			break;
		case QUADRIC_HYPERBOLOID_ONE_SHEET:
			double ch = Math.cosh(DrawConic3D.asinh(v));
			point.setMulPoint(eigenMatrix, Math.cos(u) * ch, Math.sin(u) * ch,
					v);
			break;

		case QUADRIC_HYPERBOLOID_TWO_SHEETS:
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
			point.setMulPoint(eigenMatrix, Math.cos(u) * sh, Math.sin(u) * sh,
					ch);
			break;

		case QUADRIC_PARABOLOID:
			point.setMulPoint(eigenMatrix, Math.cos(u) * v, Math.sin(u) * v, v
					* v);
			break;

		case QUADRIC_HYPERBOLIC_PARABOLOID:
			point.setMulPoint(eigenMatrix, u, v, getHalfAxis(0) * u * u
					+ getHalfAxis(1) * v * v);
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
				point.set(planes[0].getCoordSys().getPoint(
						PathNormalizer.infFunction(u), v));
			} else { // 1 < u < 3: second plane
				point.set(planes[1].getCoordSys().getPoint(
						PathNormalizer.infFunction(u - 2), v));
			}
			break;

		case QUADRIC_PLANE:
			point.set(planes[0].getCoordSys().getPoint(u, v));
			break;

		case QUADRIC_LINE:
			point.set(line.getPoint(u));
			break;

		default:
			App.error(this + " has wrong type : " + type);
			break;
		}

	}

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
				n.setMul(getEigenvec3D(0), r1 * Math.cosh(DrawConic3D.asinh(s)));
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
		return new Coords(4); // TODO
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
			coords.projectPlaneInPlaneCoords(planes[0].getCoordSys()
					.getMatrixOrthonormal(), tmpCoords);
			parameters[0] = PathNormalizer.inverseInfFunction(tmpCoords.getX());
			parameters[1] = tmpCoords.getY();
			break;

		case QUADRIC_PLANE:
			coords.projectPlaneInPlaneCoords(planes[0].getCoordSys()
					.getMatrixOrthonormal(), tmpCoords);
			parameters[0] = PathNormalizer.inverseInfFunction(tmpCoords.getX());
			parameters[1] = tmpCoords.getY();
			break;

		case QUADRIC_LINE:
			coords.projectLine(line.getStartInhomCoords(),
					line.getDirectionInD3(), tmpCoords, parameters);
			parameters[1] = 0;
			break;

		default:
			App.printStacktrace("TODO -- type: " + getType());
			break;
		}
	}

	protected double[] tmpDouble2 = new double[2],
			tmpDouble2bis = new double[2];

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

		return getNormalProjection(willingCoords.add(willingDirection.mul(t1)));
	}

	public Coords getPoint(double u, double v) {
		return evaluatePoint(u, v);
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

	private CoordMatrix tmpMatrix4x2, tmpMatrix2x4;

	public Coords[] getProjection(Coords oldCoords, Coords willingCoords,
			Coords willingDirection) {

		// compute intersection
		CoordMatrix qm = getSymetricMatrix();
		// App.debug("qm=\n"+qm);
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

		// App.debug("sm=\n"+sm);
		double a = sm.get(1, 1);
		double b = sm.get(1, 2);
		double c = sm.get(2, 2);

		if (Kernel.isEpsilon(a, b, c)) { // this can happen with degenerate
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
		return getNormalProjection(willingCoords.add(willingDirection.mul(-b
				/ a)));

	}

	public void getProjections(Coords oldCoords, Coords willingCoords,
			Coords willingDirection, Coords p1, double[] parameters1,
			Coords p2, double[] parameters2) {

		// compute intersection
		CoordMatrix qm = getSymetricMatrix();
		// App.debug("qm=\n"+qm);
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

		// App.debug("sm=\n"+sm);
		double a = sm.get(1, 1);
		double b = sm.get(1, 2);
		double c = sm.get(2, 2);
		double Delta = b * b - a * c;

		if (Delta >= 0) {
			double t1 = (-b - Math.sqrt(Delta)) / a;
			double t2 = (-b + Math.sqrt(Delta)) / a;

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
							tmpCoords.setMul(willingDirection, -b / a)), p1,
					parameters1);
			p2.setUndefined();
		}

	}

	private double[] lastHitParameters = null;

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
		return Kernel
				.isZero(coords.dotproduct(getSymetricMatrix().mul(coords)));
	}

	public boolean isInRegion(double x0, double y0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 * @param p
	 * @return direction from p to center (midpoint, or axis for cone,
	 *         cylinder...)
	 */
	private Coords getDirectionToCenter(Coords p) {

		switch (getType()) {
		case QUADRIC_SPHERE:
		case QUADRIC_ELLIPSOID:
			tmpCoords.setSub(getMidpoint3D(), p);
			return tmpCoords;
		case QUADRIC_HYPERBOLOID_ONE_SHEET:
		case QUADRIC_HYPERBOLOID_TWO_SHEETS:
		case QUADRIC_PARABOLOID:
			p.projectLine(getMidpoint3D(), getEigenvec3D(2), tmpCoords);
			tmpCoords.setSub(tmpCoords, p);
			return tmpCoords;
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			eigenMatrix.pivotDegenerate(tmpCoords, p);
			// project on eigen xOy plane
			// when we are already on axis, pick a direction "at random"
			if (Kernel.isZero(tmpCoords.getX())
					&& Kernel.isZero(tmpCoords.getY())) {
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
			tmpCoords.setSub(getMidpoint3D(), p);
			if (tmpCoords.dotproduct(getEigenvec3D(2)) > 0) {
				return getEigenvec3D(2).copyVector(); // back to "plane axis"
			}
			return getEigenvec3D(2).mul(-1); // back to "plane axis"
		case QUADRIC_HYPERBOLIC_CYLINDER:
			tmpCoords.setSub(getMidpoint3D(), p);
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

	public void pointChangedForRegion(GeoPointND P) {

		GeoPoint3D p = (GeoPoint3D) P;

		RegionParameters rp = P.getRegionParameters();
		rp.setRegionType(type);

		if (type == QUADRIC_SINGLE_POINT) {
			p.setCoords(getMidpoint3D(), false);
			p.updateCoords();
			return;
		}

		if (type == QUADRIC_PLANE) {
			p.updateCoords2D(planes[0], true);
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
			// udpate point using pathChanged
			P.setCoords(line.getPoint(t), false);
			return;
		}

		if (hasLastHitParameters()) {
			// use last hitted parameters
			rp.setT1(lastHitParameters[0]);
			rp.setT2(lastHitParameters[1]);
			rp.setNormal(evaluateNormal(rp.getT1(), rp.getT2()));
			evaluatePoint(rp.getT1(), rp.getT2(), p.getCoords());
			p.updateCoords();
			p.setWillingCoordsUndefined();
			p.setWillingDirectionUndefined();
			resetLastHitParameters();
		} else {

			if (type == QUADRIC_PARALLEL_PLANES
					|| type == QUADRIC_INTERSECTING_PLANES) {

				Coords coords, direction;

				if (p.hasWillingCoords()) { // use willing coords
					coords = p.getWillingCoords();
				} else {
					// use real coords
					coords = p.getCoords();
				}

				GeoPlane3D plane = planes[0];
				CoordMatrix planeMatrix = plane.getCoordSys()
						.getMatrixOrthonormal();
				if (!p.hasWillingDirection()) { // use normal direction for
					// projection
					direction = planeMatrix.getVz();
				} else { // use willing direction for projection
					direction = p.getWillingDirection();
				}

				coords.projectPlaneInPlaneCoords(planeMatrix.getVx(),
						planeMatrix.getVy(), direction,
						planeMatrix.getOrigin(), tmpCoords);

				double t1Shift = 0;

				if (!Kernel.isZero(tmpCoords.getZ())) {
					plane = planes[1];
					planeMatrix = plane.getCoordSys().getMatrixOrthonormal();
					if (!p.hasWillingDirection()) { // use normal direction for
						// projection
						direction = planeMatrix.getVz();
					}

					coords.projectPlaneInPlaneCoords(planeMatrix.getVx(),
							planeMatrix.getVy(), direction,
							planeMatrix.getOrigin(), tmpCoords);

					t1Shift = 2;
				}

				p.setCoords(plane.getPoint(tmpCoords.getX(), tmpCoords.getY()),
						false);
				rp.setT1(PathNormalizer.inverseInfFunction(tmpCoords.getX())
						+ t1Shift);
				rp.setT2(tmpCoords.getY());
				rp.setNormal(plane.getDirectionInD3());

				p.setWillingCoordsUndefined();
				p.setWillingDirectionUndefined();

				return;
			}

			Coords willingCoords;
			if (p.hasWillingCoords()) {
				willingCoords = p.getWillingCoords().copyVector();
				p.setWillingCoordsUndefined();
			} else {
				willingCoords = P.getCoordsInD3();
			}

			Coords willingDirection;
			if (p.hasWillingDirection()) {
				willingDirection = p.getWillingDirection().copyVector();
				p.setWillingDirectionUndefined();
			} else {
				willingDirection = getDirectionToCenter(willingCoords);
			}

			Coords[] coords = getProjection(null, willingCoords,
					willingDirection);

			rp.setT1(coords[1].get(1));
			rp.setT2(coords[1].get(2));
			rp.setNormal(evaluateNormal(coords[1].get(1), coords[1].get(2)));
			p.setCoords(coords[0], false);
			p.updateCoords();
		}

	}

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

		GeoPoint3D p = (GeoPoint3D) P;

		if (type == QUADRIC_SINGLE_POINT) {
			p.setCoords(getMidpoint3D(), false);
			p.updateCoords();
			return;
		}

		Coords coords = getPointInRegion(rp.getT1(), rp.getT2());
		p.setCoords(coords, false);
		p.updateCoords();


	}


	private boolean compatibleType(int regionType) {
		return type == GeoQuadricNDConstants.QUADRIC_EMPTY
				|| regionType == GeoQuadricNDConstants.QUADRIC_EMPTY
				|| type == regionType;
	}

	// ///////////////////////////////////
	// TRANSFORMATIONS
	// ///////////////////////////////////

	public void translate(Coords v) {
		Coords m = getMidpoint3D();
		m.addInside(v);
		setMidpoint(m.get());

		// current symetric matrix
		CoordMatrix sm = getSymetricMatrix();
		// transformation matrix
		CoordMatrix tm = CoordMatrix.Identity(4);
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

	public void rotate(NumberValue r, GeoPointND S) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}

		CoordMatrix4x4.Rotation4x4(r.getDouble(), S.getInhomCoordsInD3(),
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

	public void rotate(NumberValue r) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}

		CoordMatrix4x4.Rotation4x4(r.getDouble(), tmpMatrix4x4);
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

	private CoordMatrix4x4 tmpMatrix4x4;

	public void rotate(NumberValue r, GeoPointND S, GeoDirectionND orientation) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		CoordMatrix4x4.Rotation4x4(orientation.getDirectionInD3().normalized(),
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

	public void rotate(NumberValue r, GeoLineND line) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		CoordMatrix4x4.Rotation4x4(line.getDirectionInD3().normalized(),
				r.getDouble(), line.getStartInhomCoords(), tmpMatrix4x4);
		rotate(tmpMatrix4x4);

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].rotate(r, line);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].rotate(r, line);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			this.line.rotate(r, line);
		}

	}

	// //////////////////////
	// MIRROR
	// //////////////////////

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

	private Coords tmpCoords = new Coords(4);

	public void mirror(GeoLineND line) {

		Coords point = line.getStartInhomCoords();
		Coords direction = line.getDirectionInD3().normalized();

		// midpoint
		Coords mp = getMidpoint3D();
		mp.projectLine(point, direction, tmpCoords, null);
		mp.mulInside(-1);
		mp.addInside(tmpCoords.mul(2));
		setMidpoint(mp.get());

		// eigen vectors
		for (int i = 0; i < 3; i++) {
			Coords v = eigenvecND[i];
			double a = 2 * v.dotproduct(direction);
			v.mulInside(-1);
			v.addInside(direction.mul(a));
		}

		// symetric matrix
		setMatrixFromEigen();

		// set eigen matrix
		setEigenMatrix(getHalfAxis(0), getHalfAxis(1), getHalfAxis(2));

		// planes
		if (type == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| type == GeoQuadricNDConstants.QUADRIC_PLANE) {
			planes[0].mirror(line);
			if (type != GeoQuadricNDConstants.QUADRIC_PLANE) {
				planes[1].mirror(line);
			}
		}
		// line
		else if (type == GeoQuadricNDConstants.QUADRIC_LINE) {
			this.line.mirror(line);
		}

	}

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

		// implicit or specific mode
		/*
		 * switch (toStringMode) { case GeoConicND.EQUATION_SPECIFIC :
		 * sb.append("\t<eqnStyle style=\"specific\"/>\n"); break;
		 * 
		 * case GeoConicND.EQUATION_EXPLICIT :
		 * sb.append("\t<eqnStyle style=\"explicit\"/>\n"); break;
		 * 
		 * default : sb.append("\t<eqnStyle style=\"implicit\"/>\n"); }
		 */

	}

	final public void setEigenvectors(double x0, double y0, double z0,
			double x1, double y1, double z1, double x2, double y2, double z2) {

		eigenvecND[0].set(x0, y0, z0);
		eigenvecND[1].set(x1, y1, z1);
		eigenvecND[2].set(x2, y2, z2);

		// compute dependent quadric again to ensure eigenvalues are correct
		if (algoParent != null && algoParent instanceof AlgoDependentQuadric3D) {
			algoParent.compute();
		}
	}

	/**
	 * put XML tags for matrix in sb
	 * 
	 * @param sb
	 *            string builder
	 */
	protected void getXMLtagsMatrix(StringBuilder sb) {
		sb.append("\t<matrix");
		for (int i = 0; i < 10; i++)
			sb.append(" A" + i + "=\"" + matrix[i] + "\"");
		sb.append("/>\n");
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	public ValueType getValueType() {
		return ValueType.EQUATION;
	}

	@Override
	public boolean showLineProperties() {
		return type == GeoQuadricNDConstants.QUADRIC_LINE
				|| type == GeoQuadricNDConstants.QUADRIC_SINGLE_POINT;
	}

}
