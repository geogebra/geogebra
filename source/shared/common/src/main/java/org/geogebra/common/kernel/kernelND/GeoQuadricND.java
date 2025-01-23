/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.kernelND;

import java.util.Arrays;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.CoordConverter;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Abstract class describing quadrics in n-dimension space. Extended by
 * GeoConic, GeoQuadric3D
 * 
 * @author Mathieu
 *
 */
public abstract class GeoQuadricND extends GeoElement
		implements GeoQuadricNDConstants, QuadraticEquationRepresentable, Traceable {

	private int dimension;
	/** matrix dimension */
	protected int matrixDim;

	// types

	/** quadric type */
	public int type = GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED;

	/**
	 * flat matrix
	 * 
	 * @see org.geogebra.common.kernel.geos.GeoConic Also see GeoQuadric3D in
	 *      Desktop
	 */
	protected double[] matrix;

	private CoordMatrix symmetricMatrix;

	/**
	 * half axes
	 */
	public double[] halfAxes;

	/** linear eccentricity */
	public double linearEccentricity;
	/** (relative) eccentricity */
	public double eccentricity;
	/** parabola parameter */
	public double p;

	protected String parameter = "t";

	/** flag for isDefined() */
	protected boolean defined = true;

	/** midpoint */
	protected Coords midpoint;
	/** TODO merge with 2D eigenvec */
	protected Coords[] eigenvecND;

	/** variable string */
	private static final char[] VAR_STRING = { 'x', 'y', 'z' };

	/** eigenvalues */
	protected double[] eigenval;

	/** mu TODO better javadoc */
	protected double[] mu = new double[2];
	/** flag for intersect(quadric, quadric) */
	protected boolean isIntersection;

	private ChangeableParent changeableParent = null;

	private boolean trace;

	/**
	 * default constructor
	 * 
	 * @param c
	 *            construction
	 * @param dimension
	 *            dimension of the space (2D or 3D)
	 */
	public GeoQuadricND(Construction c, int dimension) {
		this(c, dimension, false);
	}

	/**
	 * @param c
	 *            construction
	 */
	public GeoQuadricND(Construction c) {
		super(c);
		toStringMode = Form.IMPLICIT.rawValue;
	}

	/**
	 * default constructor
	 * 
	 * @param c
	 *            construction
	 * @param dimension
	 *            dimension of the space (2D or 3D)
	 * @param isIntersection
	 *            if this is an intersection curve
	 */
	public GeoQuadricND(Construction c, int dimension, boolean isIntersection) {
		this(c);
		this.toStringMode = Form.IMPLICIT.rawValue;
		this.isIntersection = isIntersection;
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		createFields(dimension);
	}

	/**
	 * @param dim
	 *            conic dimension
	 */
	protected void createFields(int dim) {
		this.dimension = dim;
		matrixDim = (dim + 1) * (dim + 2) / 2;
		matrix = new double[matrixDim];
		halfAxes = new double[dim];
		midpoint = new Coords(dim + 1);
		midpoint.set(dim + 1, 1);

		eigenval = new double[dim + 1];
		mu = new double[dim];
	}

	/**
	 * 
	 * @return flat matrix
	 */
	public double[] getFlatMatrix() {
		return matrix;
	}

	@Override
	public void set(GeoElementND geo) {
		GeoQuadricND quadric = (GeoQuadricND) geo;
		if (quadric.hasChangeableParent3D()) {
			setChangeableParent(quadric.changeableParent.getNumber(),
					quadric.changeableParent.getDirector().toGeoElement(),
					quadric.changeableParent.getConverter());
		}
		reuseDefinition(geo);
	}

	////////////////////////////////
	// EIGENVECTORS
	/**
	 * 
	 * @param i
	 *            index
	 * @return i-th eigenvector
	 */
	public Coords getEigenvec3D(int i) {
		return eigenvecND[i];
	}

	/////////////////////////////////
	// MATRIX REPRESENTATION
	/////////////////////////////////

	/**
	 * @param vals
	 *            flat matrix
	 * @return the matrix representation of the quadric in its dimension
	 *         regarding vals
	 */
	protected CoordMatrix getSymmetricMatrix(double[] vals) {

		if (symmetricMatrix == null) {
			symmetricMatrix = new CoordMatrix(4, 4);
		}

		symmetricMatrix.set(1, 1, vals[0]);
		symmetricMatrix.set(2, 2, vals[1]);
		symmetricMatrix.set(3, 3, vals[2]);
		symmetricMatrix.set(4, 4, vals[3]);

		symmetricMatrix.set(1, 2, vals[4]);
		symmetricMatrix.set(2, 1, vals[4]);
		symmetricMatrix.set(1, 3, vals[5]);
		symmetricMatrix.set(3, 1, vals[5]);
		symmetricMatrix.set(2, 3, vals[6]);
		symmetricMatrix.set(3, 2, vals[6]);

		symmetricMatrix.set(1, 4, vals[7]);
		symmetricMatrix.set(4, 1, vals[7]);
		symmetricMatrix.set(2, 4, vals[8]);
		symmetricMatrix.set(4, 2, vals[8]);
		symmetricMatrix.set(3, 4, vals[9]);
		symmetricMatrix.set(4, 3, vals[9]);

		return symmetricMatrix;
	}

	/**
	 * @return the matrix representation of the quadric in its dimension
	 */
	public CoordMatrix getSymmetricMatrix() {
		return getSymmetricMatrix(matrix);
	}

	/**
	 * sets the matrix values from the symmetric matrix m
	 * 
	 * @param m
	 *            matrix
	 */
	protected void setMatrix(CoordMatrix m) {

		matrix[0] = m.get(1, 1);
		matrix[1] = m.get(2, 2);
		matrix[2] = m.get(3, 3);
		matrix[3] = m.get(4, 4);

		matrix[4] = m.get(1, 2);
		matrix[5] = m.get(1, 3);
		matrix[6] = m.get(2, 3);

		matrix[7] = m.get(1, 4);
		matrix[8] = m.get(2, 4);
		matrix[9] = m.get(3, 4);
	}

	/////////////////////////////////
	// SPECIAL CASES SETTERS
	/////////////////////////////////

	/**
	 * set the center and radius (as segment) of the N-sphere
	 * 
	 * @param M
	 *            center
	 * @param segment
	 *            radius
	 */
	abstract public void setSphereND(GeoPointND M, GeoSegmentND segment);

	/**
	 * makes this quadric a sphere with midpoint M and radius r
	 * 
	 * @param M
	 *            center
	 * @param r
	 *            radius
	 */
	public void setSphereND(GeoPointND M, double r) {
		defined = M.isDefined() && !M.isInfinite(); // check midpoint
		setSphereND(M.getInhomCoordsInD3(), r);
	}

	/**
	 * makes this quadric a sphere with midpoint M and radius r
	 * 
	 * @param M
	 *            center
	 * @param rad
	 *            radius
	 */
	public void setSphereND(Coords M, double rad) {
		double r = rad;
		// check radius
		if (DoubleUtil.isZero(r)) {
			r = 0;
		} else if (r < 0) {
			defined = false;
		}

		if (defined) {
			setSphereNDMatrix(M, r);
			setAffineTransform();
		}
	}

	/**
	 * @param M
	 *            center
	 * @param P
	 *            point on sphere
	 */
	abstract public void setSphereND(GeoPointND M, GeoPointND P);

	/**
	 * @param M
	 *            matrix
	 * @param r
	 *            radius
	 */
	protected void setSphereNDMatrix(Coords M, double r) {

		double[] coords = M.get();

		// set midpoint
		setMidpoint(coords);

		// set halfAxes = radius
		for (int i = 0; i < dimension; i++) {
			halfAxes[i] = r;
		}

		// set quadric's matrix with M(mx, my, mz) and r
		// [ 1 0 -m ]
		// [ 0 1 -n ]
		// [ -m -n m\u00b2+n\u00b2-r\u00b2 ]

		for (int i = 0; i < dimension; i++) {
			matrix[i] = 1.0d;
		}

		matrix[dimension] = -r * r;
		for (int i = 0; i < dimension; i++) {
			matrix[dimension] += coords[i] * coords[i];
		}

		for (int i = dimension + 1; i < matrixDim - dimension; i++) {
			matrix[i] = 0.0;
		}

		for (int i = matrixDim - dimension; i < matrixDim; i++) {
			matrix[i] = -coords[i - (matrixDim - dimension)];
		}

		if (r >= Kernel.STANDARD_PRECISION) { // radius not zero
			if (type != QUADRIC_SPHERE) {
				type = QUADRIC_SPHERE;
				linearEccentricity = 0.0d;
				eccentricity = 0.0d;
				// set first eigenvector and eigenvectors
				setFirstEigenvector(new double[] { 1, 0 });
				findEigenvectors();
			}
		} else if (DoubleUtil.isZero(r)) { // radius == 0
			singlePoint();
		} else { // radius < 0 or radius = infinite
			empty();
		}
	}

	/**
	 * turn type of quadric to empty
	 */
	public void empty() {
		type = QUADRIC_EMPTY;
	}

	@Override
	public void setUndefined() {
		defined = false;
		empty();
		resetDefinition();
		if (matrix != null) {
			Arrays.fill(matrix, Double.NaN);
		}
	}

	/**
	 * mark as defined
	 */
	final public void setDefined() {
		defined = true;
	}

	/**
	 * @param coord1
	 *            x-coord of midpoint
	 * @param coord2
	 *            y-coord of midpoint
	 */
	protected void setMidpoint(double coord1, double coord2) {
		midpoint.set(1, coord1);
		midpoint.set(2, coord2);
	}

	/**
	 * @param coords
	 *            coords of midpoint
	 */
	protected void setMidpoint(double[] coords) {
		midpoint.set(coords);
	}

	/**
	 * @return midpoint 2D
	 */
	public Coords getMidpoint2D() {
		return midpoint;
	}

	/**
	 * @return midpoint 2D
	 */
	public Coords getMidpoint() {
		return getMidpoint2D();
	}

	/**
	 * @return midpoint 3D
	 */
	public Coords getMidpoint3D() {
		Coords ret = new Coords(4);
		for (int i = 1; i < midpoint.getLength(); i++) {
			ret.set(i, midpoint.get(i));
		}
		ret.setW(midpoint.getLast());
		return ret;
	}

	/**
	 * @param i
	 *            index
	 * @return i-th half axis
	 */
	public double getHalfAxis(int i) {
		return halfAxes[i];
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	/**
	 * @return quadric type
	 */
	final public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            quadric type
	 */
	final public void setType(int type) {
		this.type = type;
	}

	////////////////////////////////////// :
	// STRING
	////////////////////////////////////// :

	/**
	 * returns equation of conic. in implicit mode: a x\u00b2 + b xy + c y\u00b2
	 * + d x + e y + f = 0. in specific mode: y\u00b2 = ... , (x - m)\u00b2 + (y
	 * - n)\u00b2 = r\u00b2, ...
	 */
	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder sbToString = new StringBuilder();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": ");
		sbToString.append(buildValueString(tpl).toString());
		return sbToString.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return buildValueString(tpl).toString();
	}

	/**
	 * @param tpl
	 *            string template
	 * @return value string as string builder
	 */
	abstract protected StringBuilder buildValueString(StringTemplate tpl);

	/**
	 * Appends value string of this (if this isa sphere) to given builder
	 * 
	 * @param sbToValueString
	 *            string builder
	 * @param tpl
	 *            string template
	 */
	protected void buildSphereNDString(StringBuilder sbToValueString,
			StringTemplate tpl) {
		String squared = tpl.squared();

		String rsquared = kernel.format(getHalfAxis(0) * getHalfAxis(0), tpl);

		for (int i = 0; i < dimension; i++) {
			if (DoubleUtil.isZero(getMidpoint().get(i + 1))) {
				sbToValueString.append(VAR_STRING[i]);
				sbToValueString.append(squared);
			} else {
				sbToValueString.append("(");
				sbToValueString.append(VAR_STRING[i]);
				sbToValueString.append(" ");
				kernel.formatSigned(-getMidpoint().get(i + 1), sbToValueString,
						tpl);
				sbToValueString.append(")");
				sbToValueString.append(squared);
			}
			if (i < dimension - 1) {
				sbToValueString.append(" + ");
			} else {
				sbToValueString.append(" = ");
			}
		}

		sbToValueString
				.append(rsquared);
	}

	/**
	 * @param coords
	 *            coords of first eigenvector
	 */
	// TODO turn methods below to abstract, implement it in GeoQuadric3D
	protected void setFirstEigenvector(double[] coords) {
		// do nothing, overridden in some classes
	}

	/**
	 * Updates eigenvectors
	 */
	protected void findEigenvectors() {
		// do nothing, overridden in some classes
	}

	/**
	 * Update to become single point
	 */
	abstract protected void singlePoint();

	/**
	 * Update affine transform
	 */
	protected void setAffineTransform() {
		// AffineTransform transform = getAffineTransform();

		/*
		 * ( v1x v2x bx ) ( v1y v2y by ) ( 0 0 1 )
		 */
		/*
		 * transform.setTransform( eigenvec[0].x, eigenvec[0].y, eigenvec[1].x,
		 * eigenvec[1].y, b.x, b.y);
		 */
	}

	// ////////////////////////////////////////////////////
	// PARENT NUMBER (HEIGHT OF A PRISM, ...)
	// ////////////////////////////////////////////////////

	/**
	 * sets the changeable parent
	 * 
	 * @param number
	 *            number
	 * @param direction
	 *            direction
	 * 
	 */
	final public void setChangeableParent(GeoNumeric number,
			GeoElement direction, CoordConverter converter) {
		changeableParent = new ChangeableParent(number, direction, converter);
	}

	@Override
	public boolean hasChangeableParent3D() {
		return changeableParent != null;
	}

	@Override
	public ChangeableParent getChangeableParent3D() {
		return changeableParent;
	}

	/**
	 * 
	 * @return dimension (2 for conic, 3 for quadric)
	 */
	public int getDimension() {
		return dimension;
	}

	//////////////////
	// TRACE
	//////////////////
	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	@Override
	public final char getLabelDelimiter() {
		return ':';
	}

	@Override
	public DescriptionMode getDescriptionMode() {
		if (toStringMode == Form.USER.rawValue
				&& (isIndependent() || getParentAlgorithm().getClassName() == Algos.Expression)) {
			return DescriptionMode.VALUE;
		}
		return super.getDescriptionMode();
	}

	@Override
	public boolean isParametricFormPossible() {
		return false;
	}

	/**
	 * Returns whether specific equation representation is possible.
	 * 
	 * @return true iff specific equation representation is possible.
	 */
	@Override
	public boolean isSpecificFormPossible() {
		return false;
	}

	/**
	 * @return the localized label for the specific equation form.
	 */
	public String getSpecificEquationLabel() {
		return null;
	}

	@Override
	public String getSpecificEquationLabelKey() {
		return null;
	}

	@Override
	public String getImplicitEquationLabelKey() {
		return "ImplicitConicEquation";
	}

	/**
	 * Returns whether explicit parabola equation representation (y = a x\u00b2 +
	 * b x + c) is possible.
	 * 
	 * @return true iff explicit equation is possible
	 */
	@Override
	public boolean isExplicitFormPossible() {
		return false;
	}

	/**
	 * Returns whether vertex form of parabola equation representation (y = a
	 * (x-h)\u00b2 + k) is possible.
	 * 
	 * @return true if vertex form equation is possible
	 */
	@Override
	public boolean isVertexFormPossible() {
		return false;
	}

	/**
	 * Returns whether conic form of parabola equation representation ( 4p(y - k)
	 * = (x - h)^2 is possible.
	 * 
	 * @return true if conic form equation is possible
	 */
	@Override
	public boolean isConicFormPossible() {
		return false;
	}

	@Override // EquationQuadric
	@CheckForNull
	public Form getEquationForm() {
		return Form.valueOf(toStringMode);
	}

	@Override // EquationQuadric
	public void setEquationForm(int toStringMode) {
		Form equationForm = Form.valueOf(toStringMode);
		if (equationForm != null) {
			this.toStringMode = equationForm.rawValue;
		}
	}

	@Override
	public void setToParametricForm(String parameter) {
		setEquationForm(Form.PARAMETRIC);
		if (parameter != null) {
			this.parameter = parameter;
		}
	}

	protected boolean hasEqualMatrix(GeoQuadricND conic) {
		double[] B = conic.matrix;
		double lambda1 = 0.0;
		boolean aZero, bZero, equal = true;
		for (int i = 0; i < 6; i++) {
			aZero = DoubleUtil.isZero(matrix[i]);
			bZero = DoubleUtil.isZero(B[i]);

			// A[i] == 0 and B[i] != 0 => not equal
			if (aZero && !bZero) {
				equal = false;
			} else if (bZero && !aZero) {
				equal = false;
			} else if (!aZero && !bZero) {
				// init lambda?
				if (lambda1 == 0.0) {
					lambda1 = matrix[i] / B[i];
					// check equality
				} else {
					equal = DoubleUtil.isEqual(matrix[i], lambda1 * B[i]);
				}
			}
			// leaf loop
			if (!equal) {
				break;
			}
		}
		return equal;
	}
}
