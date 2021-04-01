/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.kernelND;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolver;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.algos.AlgoEllipseFociLength;
import org.geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPoint;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.Mirrorable;
import org.geogebra.common.kernel.geos.PointRotateable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.geos.XMLBuilder;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.integration.EllipticArcLength;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.GgbMat;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class for conic in any dimension.
 * 
 * @author Mathieu
 *
 */
public abstract class GeoConicND extends GeoQuadricND
		implements Translateable, GeoConicNDConstants,
		MatrixTransformable, PointRotateable, Transformable, Mirrorable,
		Dilateable, GeoCoordSys2D, FromMeta, Parametrizable {
	/** avoid very large and small coefficients for numerical stability */
	protected static final double MAX_COEFFICIENT_SIZE = 100000;
	/** avoid very large and small coefficients for numerical stability */
	protected static final double MIN_COEFFICIENT_SIZE = 1;

	/** mode for equations like ax^2+bxy+cy^2+dx+ey+f=0 */
	public static final int EQUATION_IMPLICIT = 0;
	/** mode for equations like y=ax^2+bx+c */
	public static final int EQUATION_EXPLICIT = 1;
	/** mode for equations like (x-m)^2/a^2+(y-n)^2/b^2=1 */
	public static final int EQUATION_SPECIFIC = 2;
	/** X=(1,1)+(sin(t),cos(t)) */
	public static final int EQUATION_PARAMETRIC = 3;
	/** user defined equation form */
	public static final int EQUATION_USER = 4;
	/** vertex form **/
	public static final int EQUATION_VERTEX = 5;
	/** conic form **/
	public static final int EQUATION_CONICFORM = 6;

	/** variable strings for default output */
	final private static String[] vars = { "x\u00b2", "x y", "y\u00b2", "x",
			"y" };
	/** variable strings for LaTeX output */
	final private static String[] varsLateX = { "x^{2}", "x y", "y^{2}", "x",
			"y" };
	/** variable strings for CAS output */
	final private static String[] varsCAS = { "x^2", "x*y", "y^2", "x", "y" };

	/** point in case of single point degenerate conic */
	protected GeoPoint singlePoint;

	/** lines of which this conic consists in case it's degenerate */
	public GeoLine[] lines;

	/** two Eigenvectors (unit vectors), set by setEigenvectors() */
	public final GeoVec2D[] eigenvec = { new GeoVec2D(kernel, 1, 0),
			new GeoVec2D(kernel, 0, 1) };

	/**
	 * (eigenvecX, eigenvecY) are coords of currently calculated first
	 * eigenvector (eigenvecX, eigenvecY) is not a unit vector
	 */
	protected double eigenvecX;
	/** @see #eigenvecX */
	protected double eigenvecY;

	/** eigenvector-real world transformation */
	protected GAffineTransform transform;
	/** old value of transform */
	protected GAffineTransform oldTransform;

	/** translation vector (midpoint, vertex) */
	public GeoVec2D b = new GeoVec2D(kernel);
	/** start points for lines in degenerate cases */
	protected GeoPoint[] startPoints;
	/** points on this conic */
	protected ArrayList<GeoPointND> pointsOnConic;

	// for classification
	private double detS;
	private double length;
	private double temp;
	private double temp1;
	private double temp2;
	private double nx;
	private double ny;
	private double lambda;
	private int index = 0;
	private GeoVec2D c = new GeoVec2D(kernel);
	/** error DetS */
	public double errDetS = Kernel.STANDARD_PRECISION;

	private boolean isShape = false;
	private double[] tmpDouble4;

	private double[] coeffs = new double[6];

	private boolean eigenvectorsSetOnLoad = false;
	private GgbMat polarMatrix;
	private boolean isEndOfQuadric = false;
	private HitType lastHitType = HitType.NONE;
	private Coords labelPosition;
	private Coords labelPosition3D;
	private PathParameter labelParameter;
	private TreeSet<GeoElement> metas;
	private String parameter = "t";
	private Coords tmpCoords1;
	private Coords tmpCoords2;

	/**
	 * default constructor
	 *
	 * @param c
	 *            construction
	 * @param dimension
	 *            dimension
	 */
	public GeoConicND(Construction c, int dimension) {
		this(c, dimension, false, EQUATION_IMPLICIT);
	}

	/**
	 * default constructor
	 *
	 * @param c
	 *            construction
	 * @param dimension
	 *            dimension
	 * @param isIntersection
	 *            if this is an intersection curve
	 * @param stringMode
	 *            toStroingMode, one of EQUATION_* constants
	 */
	public GeoConicND(Construction c, int dimension, boolean isIntersection,
					  int stringMode) {
		super(c, dimension, isIntersection);
		setToStringMode(stringMode);
	}

	/**
	 * 
	 * @param i
	 *            index of eigenvector
	 * @return eigen vector in native dimension of the conic
	 */
	public Coords getEigenvec(int i) {
		return new Coords(eigenvec[i].getCoords());
	}

	/**
	 * 
	 * @param i
	 *            index of eigenvector
	 * @return eigen vector in dimension 3
	 */
	@Override
	abstract public Coords getEigenvec3D(int i);

	/**
	 * If 2D conic, return identity (xOy plane)
	 * 
	 * @return coord sys where the conic lies
	 */
	@Override
	abstract public CoordSys getCoordSys();

	/**
	 * 
	 * @param i
	 *            index of line
	 * @return the direction in case of line(s)
	 */
	abstract public Coords getDirection3D(int i);

	/**
	 * 
	 * @param i
	 *            index of line
	 * @return the origin of lines in case of parallel lines
	 */
	abstract public Coords getOrigin3D(int i);

	/**
	 * @return the matrix representation of the conic in its 2D sub space
	 */
	@Override
	protected CoordMatrix getSymetricMatrix(double[] vals) {
		CoordMatrix ret = new CoordMatrix(3, 3);

		ret.set(1, 1, vals[0]);
		ret.set(2, 2, vals[1]);
		ret.set(3, 3, vals[2]);

		ret.set(1, 2, vals[3]);
		ret.set(2, 1, vals[3]);
		ret.set(1, 3, vals[4]);
		ret.set(3, 1, vals[4]);
		ret.set(2, 3, vals[5]);
		ret.set(3, 2, vals[5]);

		return ret;
	}

	/**
	 * makes this conic a circle with midpoint M and radius r
	 * 
	 * @param M
	 *            center
	 * @param r
	 *            radius
	 */
	final public void setCircle(GeoPoint M, double r) {
		setSphereND(M, r);

	}

	/**
	 * makes this conic a circle with midpoint M through Point P
	 * 
	 * @param M
	 *            center
	 * @param P
	 *            point
	 */
	final public void setCircle(GeoPoint M, GeoPoint P) {
		defined = M.isDefined() && P.isDefined() && !P.isInfinite();
		if (!defined) {
			return;
		}

		if (M.isInfinite()) {
			// midpoint at infinity -> parallelLines
			// one through P, the other through infinite point M
			/*
			 * b.x = P.inhomX; b.y = P.inhomY;
			 */
			double[] coords = new double[3];
			P.getCoords(coords);
			setMidpoint(coords);
			// M is normalvector of double line
			eigenvecX = -M.y;
			eigenvecY = M.x;
			findEigenvectors();
			halfAxes[0] = Double.POSITIVE_INFINITY;
			halfAxes[1] = Double.POSITIVE_INFINITY;
			mu[0] = 0.0; // line at infinity is not drawn
			parallelLines(mu);
			// set line at infinity 0 = 1
			lines[1].x = Double.NaN;
			lines[1].y = Double.NaN;
			lines[1].z = Double.NaN;
			// set degenerate matrix
			matrix[0] = 0.0d;
			matrix[1] = 0.0d;
			matrix[2] = lines[0].z;
			matrix[3] = 0.0d;
			matrix[4] = lines[0].x / 2.0;
			matrix[5] = lines[0].y / 2.0;
		} else {
			setCircleMatrix(M, M.distance(P));
		}
		setAffineTransform();
	}

	@Override
	public void setSphereND(GeoPointND M, double r) {
		defined = M.isDefined() && !M.isInfinite(); // check midpoint
		setSphereND(M.getInhomCoordsInD2(), r);
	}

	//////////////////////////////////////
	// PATH INTERFACE
	//////////////////////////////////////

	@Override
	public boolean isPath() {
		return true;
	}

	/**
	 * compute closest t parameter to point P
	 * 
	 * @param P
	 *            point
	 * @return t parameter
	 */
	public double getClosestParameterForParabola(GeoPointND P) {
		Coords coords = P.getCoordsInD2(getCoordSys());
		coordsRWtoEV(coords);
		double x = coords.getX();
		double y = coords.getY();
		if (tmpDouble4 == null) {
			tmpDouble4 = new double[4];
		}

		// solve PM.dM=0
		tmpDouble4[3] = p / 2;
		tmpDouble4[2] = 0;
		tmpDouble4[1] = p - x;
		tmpDouble4[0] = -y;

		int nRoots = EquationSolver.solveCubicS(tmpDouble4,
				tmpDouble4, Kernel.STANDARD_PRECISION);

		// find closest root
		double dist = Double.POSITIVE_INFINITY;
		double param = 0;
		for (int i = 0; i < nRoots; i++) {
			double t = tmpDouble4[i];
			double yt = p * t;
			double xt = yt * t / 2.0;
			double dx = xt - x;
			double dy = yt - y;
			double d = dx * dx + dy * dy;
			if (d < dist) {
				dist = d;
				param = t;
			}

			// //debug
			// coords.setX(xt);
			// coords.setY(yt);
			// coords.setZ(1);
			// coordsEVtoRW(coords);
			// Log.debug("root #" + i + ": (" + coords.getX() + ","
			// + coords.getY() + ") , d=" + d);
		}
		return param;
	}

	/**
	 * 
	 * @param t
	 *            parameter on parabola
	 * @return curvature value
	 */
	public double evaluateCurvatureForParabola(double t) {
		double s = Math.sqrt(1 + t * t);
		return 1 / (p * s * s * s);
	}

	/**
	 * evaluate first derivative for parameter t
	 * 
	 * @param t
	 *            parameter
	 * @param result
	 *            (x,y) first derivative
	 */
	public void evaluateFirstDerivativeForParabola(double t, double[] result) {
		Coords eigenvec0 = getEigenvec(0);
		Coords eigenvec1 = getEigenvec(1);
		result[0] = p * (t * eigenvec0.getX() + eigenvec1.getX());
		result[1] = p * (t * eigenvec0.getY() + eigenvec1.getY());
	}

	/**
	 * evaluate second derivative for parameter t
	 * 
	 * @param t
	 *            parameter
	 * @param result
	 *            (x,y) second derivative
	 */
	public void evaluateSecondDerivativeForParabola(double t, double[] result) {
		Coords eigenvec0 = getEigenvec(0);
		result[0] = p * eigenvec0.getX();
		result[1] = p * eigenvec0.getY();
	}

	@Override
	public void pointChanged(GeoPointND P) {
		Coords coords = P.getCoordsInD2(getCoordSys());

		PathParameter pp = P.getPathParameter();

		pointChanged(coords, pp);

		P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		P.updateCoordsFrom2D(false, getCoordSys());
		P.updateCoords();

	}

	/**
	 * Edited by: Kai Chung Tam Date: 4/6/2011 Fixed case CONIC_ELLIPSE,
	 * CONIC_HYPERBOLA and CONIC_PARABOLA
	 * 
	 * @param P
	 *            a point
	 * @param pp
	 *            path parameter of the point
	 */
	public void pointChanged(Coords P, PathParameter pp) {
		pointChanged(P, pp, true);
	}

	/**
	 * Edited by: Kai Chung Tam Date: 4/6/2011 Fixed case CONIC_ELLIPSE,
	 * CONIC_HYPERBOLA and CONIC_PARABOLA
	 * 
	 * @param P
	 *            a point
	 * @param pp
	 *            path parameter of the point
	 * @param checkSection
	 *            check the section (if exists)
	 */
	public void pointChanged(Coords P, PathParameter pp, boolean checkSection) {
		double px, py, ha, hb, hc_2;
		double abspx, abspy; // for parabola and hyperbola
		double tolerance = Kernel.STANDARD_PRECISION; // required precision
														// (robustness not
														// proven)

		pp.setPathType(type);

		switch (type) {
		default:
		case CONIC_EMPTY:
			P.setX(Double.NaN);
			P.setY(Double.NaN);
			P.setZ(Double.NaN);
			break;

		case CONIC_SINGLE_POINT:
			P.setX(singlePoint.x);
			P.setY(singlePoint.y);
			P.setZ(singlePoint.z);
			break;

		case CONIC_INTERSECTING_LINES:
		case CONIC_PARALLEL_LINES:
			/*
			 * For line conics, we use the parameter ranges first line: t = (-1,
			 * 1) second line: t = (1, 3) and convert this to s = (-inf, inf)
			 * using first line: s = t /(1 - abs(t)) second line: s = (t-2) /(1
			 * - abs(t-2)) which allows us to use the line's path parameter s
			 */

			// choose closest line
			boolean firstLine = lines[0].distanceHom(P) <= lines[1]
					.distanceHom(P);
			GeoLine line = firstLine ? lines[0] : lines[1];

			// compute line path parameter
			line.doPointChanged(P, pp);

			// convert line parameter to (-1,1)
			pp.setT(PathNormalizer.inverseInfFunction(pp.getT()));
			if (!firstLine) {
				pp.setT(pp.getT() + 2); // convert from (-1,1) to (1,3)
			}
			break;

		case CONIC_LINE:
		case CONIC_DOUBLE_LINE:
			getLines();
			lines[0].doPointChanged(P, pp);
			break;

		case CONIC_CIRCLE:
			// transform to eigenvector coord-system
			coordsRWtoEV(P);
			// calc parameter
			px = P.getX() / P.getZ();
			py = P.getY() / P.getZ();

			// Application.debug("px,py="+px+","+py);

			// relation between the internal parameter t and the angle theta:
			// t = atan(a/b tan(theta)) where tan(theta) = py / px
			// avoid cos(atan(x)) for the vertices
			pp.setT(Math.atan2(halfAxes[0] * py, halfAxes[1] * px));

			// calc Point on conic using this parameter
			P.setX(halfAxes[0] * Math.cos(pp.getT()));
			P.setY(halfAxes[1] * Math.sin(pp.getT()));

			P.setZ(1.0);
			// transform back to real world coord system
			coordsEVtoRW(P);
			break;
		case CONIC_ELLIPSE:
			// transform to eigenvector coord-system
			coordsRWtoEV(P);

			// calc parameter
			px = P.getX() / P.getZ();
			py = P.getY() / P.getZ();
			abspx = Math.abs(px);
			abspy = Math.abs(py);
			ha = halfAxes[0];
			hb = halfAxes[1];
			hc_2 = ha * ha - hb * hb;

			// special case handling from:
			// http://cdserv1.wbut.ac.in/81-8147-617-4/Linux/MagicSoftware/WildMagic2/Documentation/DistancePointToEllipse2.pdf
			if (abspx < Kernel.STANDARD_PRECISION) {
				// pp.setT(Math.asin(Math.max(-1,-hb*abspy/hc_2)));
				if (abspy < Kernel.STANDARD_PRECISION) {
					if (hb < ha) {
						pp.setT(Math.PI / 2);
					} else {
						pp.setT(0);
					}
				} else {
					if (hb < ha) {
						pp.setT(Math.PI / 2);
					} else {
						if (abspy * hb < hc_2) {
							pp.setT(Math.asin(hb * abspy / hc_2));
						} else {
							pp.setT(Math.PI / 2);
						}
					}
				}
			} else if (abspy < Kernel.STANDARD_PRECISION) {
				// pp.setT(Math.acos(Math.min(1,ha*abspx/hc_2)));
				if (ha < hb) {
					pp.setT(0);
				} else {
					if (abspx * ha < hc_2) {
						pp.setT(Math.acos(ha * abspx / hc_2));
					} else {
						pp.setT(0);
					}
				}
			} else {
				// To solve (1-u^2)*(b*py + (a^2-b^2)*u)^2-a^2*px^2*u^2 = 0,
				// where u = sin(theta)
				double[] roots = getPerpendicularParams(abspx, abspy);

				if (roots[0] > 0) {
					pp.setT(Math.asin(roots[0]));
				} else if (roots[1] > 0) {
					pp.setT(Math.asin(roots[1]));
				} else if (roots[2] > 0) {
					pp.setT(Math.asin(roots[2]));
				} else {
					pp.setT(Math.asin(roots[3]));
				}
			}

			// transform the parameter if (px,py) is not in the first quadrant.
			if (px < 0) {
				pp.setT(Math.PI - pp.getT());
			}
			if (py < 0) {
				pp.setT(-pp.getT());
			}

			P.setX(ha * Math.cos(pp.getT()));
			P.setY(hb * Math.sin(pp.getT()));
			P.setZ(1.0);
			// transform back to real world coord system
			coordsEVtoRW(P);
			break;
		case CONIC_HYPERBOLA:
			/*
			 * For hyperbolas, we use the parameter ranges right branch: t =
			 * (-1, 1) left branch: t = (1, 3) and get this from s = (-inf, inf)
			 * using right branch: s = t /(1 - abs(t)) where we use the
			 * parameter form (a*cosh(s), b*sinh(s)) for the right branch of the
			 * hyperbola.
			 */

			// transform to eigenvector coord-system
			coordsRWtoEV(P);

			// calc parameter
			px = P.getX() / P.getZ();
			py = P.getY() / P.getZ();
			abspx = Math.abs(px);
			abspy = Math.abs(py);
			ha = halfAxes[0];
			hb = halfAxes[1];
			hc_2 = ha * ha + hb * hb;
			double s;

			if (abspy < Kernel.STANDARD_PRECISION) {
				s = MyMath.acosh(Math.max(1, ha * abspx / hc_2));
			} else {
				// To solve (1+u^2)*(-(b^2+a^2)*u +b*py)^2 - a^2*px^2, where
				// u=sinh(t)

				double[] roots = getPerpendicularParams(abspx, abspy);

				if (roots[0] > 0) {
					s = MyMath.asinh(roots[0]);
				} else if (roots[1] > 0) {
					s = MyMath.asinh(roots[1]);
				} else if (roots[2] > 0) {
					s = MyMath.asinh(roots[2]);
				} else {
					s = MyMath.asinh(roots[3]);
				}
			}

			// transform the s-parameter if (px,py) is not in the first
			// quadrant.
			if (py < 0) { // lower-half plane
				s = -s;
			}
			// compute t in (-1,1) from s in (-inf, inf)
			pp.setT(PathNormalizer.inverseInfFunction(s));
			P.setX(ha * Math.cosh(s));
			P.setY(hb * Math.sinh(s));
			P.setZ(1.0);

			if (px < 0) { // left branch
				pp.setT(pp.getT() + 2); // convert (-1,1) to (1,3)
				P.setX(-P.getX());
			}

			// transform back to real world coord system
			coordsEVtoRW(P);
			break;

		case CONIC_PARABOLA:
			// transform to eigenvector coord-system
			coordsRWtoEV(P);

			// calculate parameters. consider only the upper-half plane.
			px = P.getX() / P.getZ();
			py = P.getY() / P.getZ();
			abspy = Math.abs(py);

			if (abspy < tolerance) { // Point is on x-axis
				pp.setT(Math.sqrt(Math.max(0, 2 * (px - p) / p)));
			} else { // binary search

				double[] eqn = { abspy, -p + px, 0, -p / 2 };
				double[] roots = { 0, 0, 0 };
				EquationSolver.solveCubicS(eqn, roots,
						Kernel.STANDARD_PRECISION);
				if (roots[0] > 0) {
					pp.setT(roots[0]);
				} else if (roots[1] > 0) {
					pp.setT(roots[1]);
				} else {
					pp.setT(roots[2]);
				}

				if (py < 0) {
					pp.setT(-pp.getT());
				}
			}

			P.setX(p * pp.getT() * pp.getT() / 2.0);
			P.setY(p * pp.getT());
			P.setZ(1.0);
			// transform back to real world coord system
			coordsEVtoRW(P);
			break;
		}
	}

	/**
	 * @param P
	 *            point
	 * @return array of parameters t such that Line[point[this,t],P] is
	 *         perpendicular to this
	 */
	public double[] getPerpendicularParams(Coords P) {
		coordsRWtoEV(P);

		// calc parameter
		double px = P.getX() / P.getZ();
		double py = P.getY() / P.getZ();
		double abspx = Math.abs(px);
		double abspy = Math.abs(py);
		return getPerpendicularParams(abspx, abspy);
	}

	private double[] getPerpendicularParams(double abspx, double abspy) {
		double ha = halfAxes[0];
		double hb = halfAxes[1];
		double bpy = hb * abspy;
		double[] roots = { 0, 0, 0, 0 };
		double[] eqn;
		if (type == CONIC_ELLIPSE) {
			double hc_2 = ha * ha - hb * hb;
			eqn = new double[] { bpy * bpy, 2 * bpy * hc_2,
					-bpy * bpy + hc_2 * hc_2 - ha * ha * abspx * abspx,
					-2 * bpy * hc_2, -hc_2 * hc_2 };
		} else {
			double hc_2 = ha * ha + hb * hb;
			eqn = new double[] { bpy * bpy, -2 * bpy * hc_2,
					bpy * bpy + hc_2 * hc_2 - ha * ha * abspx * abspx,
					-2 * bpy * hc_2, hc_2 * hc_2 };
		}
		cons.getKernel().getEquationSolver().solveQuartic(eqn, roots,
				Kernel.STANDARD_PRECISION);
		return roots;
	}

	/*
	 * Edited by Kai Chung Tam
	 */

	@Override
	public void pathChanged(GeoPointND P) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)) {
			pointChanged(P);
			return;
		}

		// Application.debug(getEigenvec(0));

		Coords coords = P.getCoordsInD2(getCoordSys());
		PathParameter pp = P.getPathParameter();

		// Application.debug(P.getCoordsInD3()+"\n2D:\n"+coords+"\npp="+pp.getT());

		pathChanged(coords, pp);

		P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		P.updateCoordsFrom2D(false, getCoordSys());

		// Application.debug("after:\n"+P.getCoordsInD3()+"\n2D:\n"+coords);

	}

	private boolean compatibleType(int t) {
		if (type == t) {
			return true;
		}
		// the conic type change temporarily to point or empty conic --
		// once the conic returns back, we want the old parameter to be used
		if (t == CONIC_EMPTY || t == CONIC_SINGLE_POINT) {
			return true;
		}
		return false;
	}

	/**
	 * check if compatible types
	 * 
	 * @param P
	 *            point
	 * @param pp
	 *            path parameter
	 */
	protected void pathChanged(Coords P, PathParameter pp) {

		// if type of path changed (other conic) then we
		// have to recalc the parameter with pointChanged()
		if (!compatibleType(pp.getPathType()) || Double.isNaN(pp.getT())) {
			pointChanged(P, pp);
			return;
		}

		pathChangedWithoutCheck(P, pp, true);
	}

	/**
	 * @param P
	 *            point
	 * @param pp
	 *            path parameter
	 * @param checkSection
	 *            check section parts (if exist)
	 */
	protected void pathChangedWithoutCheckEllipse(Coords P, PathParameter pp,
			boolean checkSection) {
		// calc Point on conic using this parameter (in eigenvector space)
		P.setX(halfAxes[0] * Math.cos(pp.getT()));
		P.setY(halfAxes[1] * Math.sin(pp.getT()));
		P.setZ(1.0);

		// transform back to real world coord system
		coordsEVtoRW(P);
	}

	/**
	 * @param P
	 *            point
	 * @param pp
	 *            path parameter
	 * @param checkSection
	 *            check section parts (if exist)
	 */
	protected void pathChangedWithoutCheckParabola(Coords P, PathParameter pp,
			boolean checkSection) {
		P.setY(p * pp.getT());
		P.setX(P.getY() * pp.getT() / 2.0);
		P.setZ(1.0);

		// transform back to real world coord system
		coordsEVtoRW(P);
	}

	/**
	 * @param P
	 *            point
	 * @param pp
	 *            path parameter
	 * @param checkSection
	 *            check section parts (if exist)
	 */
	protected void pathChangedWithoutCheckHyperbola(Coords P, PathParameter pp,
			boolean checkSection) {
		/*
		 * For hyperbolas, we use the parameter ranges right branch: t = (-1, 1)
		 * left branch: t = (1, 3) and convert this to s = (-inf, inf) using
		 * right branch: s = t /(1 - abs(t)) left branch: s = (t-2) /(1 -
		 * abs(t-2)) which allows us to use the parameter form (a*cosh(s),
		 * b*sinh(s)) for the right branch of the hyperbola.
		 */
		boolean leftBranch = pp.getT() > 1;
		double t = leftBranch ? pp.getT() - 2 : pp.getT();
		double s = t / (1 - Math.abs(t));

		P.setX(halfAxes[0] * MyMath.cosh(s));
		P.setY(halfAxes[1] * MyMath.sinh(s));
		P.setZ(1.0);
		if (leftBranch) {
			P.setX(-P.getX());
		}

		// transform back to real world coord system
		coordsEVtoRW(P);
	}

	/**
	 * @param P
	 *            point
	 * @param pp
	 *            path parameter
	 * @param checkSection
	 *            check section parts (if exist)
	 */
	public void pathChangedWithoutCheck(Coords P, PathParameter pp,
			boolean checkSection) {

		switch (type) {
		default:
		case CONIC_EMPTY:
			P.setX(Double.NaN);
			P.setY(Double.NaN);
			P.setZ(Double.NaN);
			break;

		case CONIC_SINGLE_POINT:
			P.setX(singlePoint.x);
			P.setY(singlePoint.y);
			P.setZ(singlePoint.z);
			break;

		case CONIC_INTERSECTING_LINES:
		case CONIC_PARALLEL_LINES:
			/*
			 * For line conics, we use the parameter ranges first line: t = (-1,
			 * 1) second line: t = (1, 3) and convert this to s = (-inf, inf)
			 * using first line: s = t /(1 - abs(t)) second line: s = (t-2) /(1
			 * - abs(t-2)) which allows us to use the line's path parameter s
			 */
			double pathParam = pp.getT();
			boolean leftBranch = pathParam > 1;
			pp.setT(leftBranch ? pathParam - 2 : pathParam);
			// convert from (-1,1) to (-inf, inf) line path parameter
			pp.setT(pp.getT() / (1 - Math.abs(pp.getT())));
			if (leftBranch) {
				lines[1].pathChanged(P, pp);
			} else {
				lines[0].pathChanged(P, pp);
			}

			// set our path parameter again
			pp.setT(pathParam);
			break;

		case CONIC_LINE:
		case CONIC_DOUBLE_LINE:
			lines[0].pathChanged(P, pp);
			break;

		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
			pathChangedWithoutCheckEllipse(P, pp, checkSection);
			break;

		case CONIC_HYPERBOLA:
			pathChangedWithoutCheckHyperbola(P, pp, checkSection);
			break;

		case CONIC_PARABOLA:
			pathChangedWithoutCheckParabola(P, pp, checkSection);
			break;
		}
	}

	/**
	 * Returns the largest possible parameter value for this path
	 * 
	 * @return the largest possible parameter value for this path (may be
	 *         Double.POSITIVE_INFINITY)
	 */
	@Override
	public double getMaxParameter() {
		switch (type) {
		case CONIC_DOUBLE_LINE:
		case CONIC_PARABOLA:
		case CONIC_LINE:
			return Double.POSITIVE_INFINITY;

		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
			return Math.PI;

		case CONIC_HYPERBOLA:
		case CONIC_INTERSECTING_LINES:
		case CONIC_PARALLEL_LINES:
			// For hyperbolas and line conics, we use the parameter ranges
			// right branch: t = (-1, 1)
			// left branch: t = (1, 3)
			return 3;

		case CONIC_EMPTY:
		case CONIC_SINGLE_POINT:
		default:
			return 0;
		}
	}

	/**
	 * Returns the smallest possible parameter value for this path
	 * 
	 * @return the smallest possible parameter value for this path (may be
	 *         Double.NEGATIVE_INFINITY)
	 */
	@Override
	public double getMinParameter() {
		switch (type) {
		case CONIC_PARABOLA:
		case CONIC_DOUBLE_LINE:
		case CONIC_LINE:
			return Double.NEGATIVE_INFINITY;

		case CONIC_HYPERBOLA:
		case CONIC_INTERSECTING_LINES:
		case CONIC_PARALLEL_LINES:
			// For hyperbolas and line conics, we use the parameter ranges
			// right branch: t = (-1, 1)
			// left branch: t = (1, 3)
			return -1;

		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
			return -Math.PI;

		case CONIC_EMPTY:
		case CONIC_SINGLE_POINT:
		default:
			return 0;
		}
	}

	@Override
	public boolean isClosedPath() {
		switch (type) {
		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
			return true;

		default:
			return false;
		}
	}

	@Override
	public boolean isOnPath(GeoPointND P, double eps) {
		if (P.getPath() == this) {
			return true;
		}

		return isOnFullConic(P, eps);
	}

	/**
	 * states wheter P lies on this conic or not. Note: this method is not
	 * overwritten by subclasses like isIntersectionPointIncident()
	 * 
	 * @return true P lies on this conic
	 * @param P
	 *            point
	 * @param eps
	 *            precision
	 */
	public final boolean isOnFullConic(GeoPointND P, double eps) {
		if (!P.isDefined()) {
			return false;
		}

		return isOnFullConic(P.getCoordsInD2(), eps);
	}

	/**
	 * @param P
	 *            point
	 * @param eps
	 *            precision
	 * @return true if point is on path with given precision
	 */
	public final boolean isOnFullConic(Coords P, double eps) {
		switch (type) {
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			Coords singlePointCoords = new Coords(singlePoint.x, singlePoint.y,
					singlePoint.z);
			return P.distance(singlePointCoords) < eps;

		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
			return lines[0].isOnFullLine(P, eps)
					|| lines[1].isOnFullLine(P, eps);

		case GeoConicNDConstants.CONIC_LINE:
			return lines[0].isOnFullLine(P, eps);

		case GeoConicNDConstants.CONIC_EMPTY:
			return false;
		}

		// if we get here let's handle the remaining cases

		// remember coords of P
		double Px = P.getX();
		double Py = P.getY();
		double Pz = P.getZ();

		// convert P to eigenvector coord system
		if (type == GeoConicNDConstants.CONIC_CIRCLE) {
			translateByCenter(P);
		} else {
			coordsRWtoEV(P);
		}
		double px = P.getX() / P.getZ();
		double py = P.getY() / P.getZ();

		boolean result = false;
		switch (type) {
		default:
			// do nothing
			break;
		case GeoConicNDConstants.CONIC_CIRCLE:
			// x^2 + y^2 = r^2
			double radius2 = halfAxes[0] * halfAxes[0];
			result = DoubleUtil.isEqual(px * px / radius2 + py * py / radius2, 1,
					eps);
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:
			// x^2/a^2 + y^2/b^2 = 1
			result = DoubleUtil.isEqual(px * px / (halfAxes[0] * halfAxes[0])
					+ py * py / (halfAxes[1] * halfAxes[1]), 1, eps);
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
			// x^2/a^2 - y^2/b^2 = 1
			result = DoubleUtil.isEqual(px * px / (halfAxes[0] * halfAxes[0]),
					1 + py * py / (halfAxes[1] * halfAxes[1]), eps);
			break;

		case GeoConicNDConstants.CONIC_PARABOLA:
			// y^2 = 2 p x
			result = DoubleUtil.isEqual(py * py, 2 * p * px, eps);
			break;
		}

		// restore coords of P
		P.setX(Px);
		P.setY(Py);
		P.setZ(Pz);
		return result;
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	/**
	 * Transforms coords of point P from Eigenvector space to real world space.
	 * 
	 * @param P
	 *            2D point in EV coords
	 */
	protected final void coordsEVtoRW(Coords P) {
		// rotate by alpha
		double px = P.getX();
		double py = P.getY();
		Coords eigenvec0 = getEigenvec(0);
		Coords eigenvec1 = getEigenvec(1);
		P.setX(px * eigenvec0.getX() + py * eigenvec1.getX());
		P.setY(px * eigenvec0.getY() + py * eigenvec1.getY());

		// translate by b
		Coords mid = getMidpoint();
		P.setX(P.getX() + P.getZ() * mid.getX());
		P.setY(P.getY() + P.getZ() * mid.getY());
	}

	/**
	 * Transforms coords of point P from real world space to Eigenvector space.
	 * 
	 * @param P
	 *            2D point in EV coords
	 */
	private void coordsRWtoEV(Coords P) {

		Coords mid = getMidpoint();

		// translate by -b
		P.setX(P.getX() - P.getZ() * mid.getX());
		P.setY(P.getY() - P.getZ() * mid.getY());

		// rotate by -alpha
		double px = P.getX();
		double py = P.getY();
		Coords eigenvec0 = getEigenvec(0);
		Coords eigenvec1 = getEigenvec(1);
		P.setX(px * eigenvec0.getX() + py * eigenvec0.getY());
		P.setY(px * eigenvec1.getX() + py * eigenvec1.getY());
	}

	private void translateByCenter(Coords P) {

		Coords mid = getMidpoint();

		// translate by -b
		P.setX(P.getX() - P.getZ() * mid.getX());
		P.setY(P.getY() - P.getZ() * mid.getY());
	}

	@Override
	public boolean isFillable() {
		return true;
	}

	////////////////////////////////////
	// MATRIX AND EQUATION
	////////////////////////////////////

	/**
	 * sets the matrix values from the symmetric matrix m
	 * 
	 * @param m
	 *            matrix
	 */
	@Override
	final public void setMatrix(CoordMatrix m) {
		setDefinition(null);
		matrix[0] = m.get(1, 1);
		matrix[1] = m.get(2, 2);
		matrix[2] = m.get(3, 3);
		matrix[3] = (m.get(1, 2) + m.get(2, 1)) / 2.0;
		matrix[4] = (m.get(1, 3) + m.get(3, 1)) / 2.0;
		matrix[5] = (m.get(2, 3) + m.get(3, 2)) / 2.0;

		classifyConic();
	}

	////////////////////////////////////
	// FROM GEOCONIC
	////////////////////////////////////

	/**
	 * Returns a list of points that this conic passes through. May return null.
	 * 
	 * @return list of points that this conic passes through.
	 */
	public final ArrayList<GeoPointND> getPointsOnConic() {
		return pointsOnConic;
	}

	/**
	 * Sets a list of points that this conic passes through. This method should
	 * only be used by AlgoMacro.
	 * 
	 * @param points
	 *            list of points that this conic passes through
	 */
	public final void setPointsOnConic(ArrayList<GeoPointND> points) {
		pointsOnConic = points;
	}

	/**
	 * Adds a point to the list of points that this conic passes through.
	 * 
	 * @param pt
	 *            point
	 */
	public final void addPointOnConic(GeoPointND pt) {
		if (pointsOnConic == null) {
			pointsOnConic = new ArrayList<>();
		}

		if (!pointsOnConic.contains(pt)) {
			pointsOnConic.add(pt);
		}
	}

	/**
	 * Removes a point from the list of points that this conic passes through.
	 * 
	 * @param pt
	 *            Point to be removed
	 */
	public final void removePointOnConic(GeoPointND pt) {
		if (pointsOnConic != null) {
			pointsOnConic.remove(pt);
		}
	}

	/**
	 * geo is expected to be a conic. make deep copy of member vars of geo.
	 */
	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoLine) {
			GeoLine line = (GeoLine) geo;
			this.setCoeffs(0, 0, 0, line.getX(), line.getY(), line.getZ());
			reuseDefinition(geo);
			return;
		}
		GeoConicND co = (GeoConicND) geo;

		// copy everything
		setModeIfEquationFormIsNotForced(co.toStringMode);
		type = co.type;
		for (int i = 0; i < 6; i++) {
			matrix[i] = co.matrix[i]; // flat matrix A
		}

		if (co.transform != null) {
			GAffineTransform at = getAffineTransform();
			at.setTransform(co.transform);
		}

		eigenvec[0].setCoords(co.eigenvec[0]);
		eigenvec[1].setCoords(co.eigenvec[1]);
		// b.setCoords(co.b);
		setMidpoint(co.getMidpoint().get());
		halfAxes[0] = co.halfAxes[0];
		halfAxes[1] = co.halfAxes[1];
		linearEccentricity = co.linearEccentricity;
		eccentricity = co.eccentricity;
		p = co.p;
		mu[0] = co.mu[0];
		mu[1] = co.mu[1];

		setLines(co);

		if (co.singlePoint != null) {
			if (singlePoint == null) {
				singlePoint = new GeoPoint(cons);
			}
			singlePoint.setCoords(co.singlePoint);
		}
		if (co.startPoints != null) {
			if (startPoints == null) {
				startPoints = new GeoPoint[2];
				for (int i = 0; i < 2; i++) {
					startPoints[i] = new GeoPoint(cons);
				}
			}
			for (int i = 0; i < 2; i++) {
				startPoints[i].set(co.startPoints[i]);
			}
		}
		defined = co.defined;

		super.set(geo);
	}

	private void setLines(GeoConicND co) {
		if (co.lines != null) {
			getLines();
			for (int i = 0; i < 2; i++) {
				lines[i].setCoords(co.lines[i]);
				if (co.lines[i].startPoint != null) {
					lines[i].setStartPoint(co.lines[i].startPoint.copy());
				}
				if (co.lines[i].endPoint != null) {
					lines[i].setEndPoint(co.lines[i].endPoint.copy());
				}
			}
		}
	}

	/**
	 * Updates this conic. If the transform has changed, we call
	 * makePathParametersInvalid() to force an update of all path parameters of
	 * all points on this conic.
	 */
	@Override
	public void update(boolean drag) {
		makePathParametersInvalid();
		super.update(drag);
	}

	/**
	 * Sets equation mode to specific, implicit or explicit
	 * 
	 * @param mode
	 *            equation mode (one of EQUATION_* constants)
	 */
	@Override
	public void setToStringMode(int mode) {
		if (isEquationFormEnforced()) {
			toStringMode = cons.getApplication().getConfig().getEnforcedConicEquationForm();
		} else {
			setModeWithImplicitEquationAsDefault(mode);
		}
	}

	/**
	 * returns true if this conic is a circle Michael Borcherds 2008-03-23
	 * 
	 * @return true iff this conic is circle
	 */
	public final boolean isCircle() {
		return (type == CONIC_CIRCLE);
	}

	/**
	 * returns true if this conic is a parabola
	 * 
	 * @return true iff this conic is parabola
	 */
	public boolean isParabola() {
		return (type == CONIC_PARABOLA);
	}

	/**
	 * returns true if this conic is an ellipse
	 * 
	 * @return true iff this conic is ellipse
	 */
	public boolean isEllipse() {
		return (type == CONIC_ELLIPSE);
	}

	/**
	 * returns true if this conic is a hyperbola
	 * 
	 * @return true iff this conic is hyperbola
	 */
	public boolean isHyperbola() {
		return (type == CONIC_HYPERBOLA);
	}

	/**
	 * returns true if this conic is a double line
	 * 
	 * @return true iff this conic is double line
	 */
	public boolean isDoubleLine() {
		return (type == CONIC_DOUBLE_LINE);
	}

	/** Changes equation mode to Implicit */
	final public void setToImplicit() {
		setToStringMode(EQUATION_IMPLICIT);
	}

	/** Changes equation mode to Explicit */
	final public void setToExplicit() {
		setToStringMode(EQUATION_EXPLICIT);
	}

	/**
	 * Changes equation mode to Explicit
	 * 
	 * @param parameter
	 *            new parameter name
	 */
	final public void setToParametric(String parameter) {
		setToStringMode(EQUATION_PARAMETRIC);
		if (parameter != null) {
			this.parameter = parameter;
		}
	}

	/** Changes equation mode to Explicit */
	final public void setToUser() {
		setToStringMode(EQUATION_USER);
	}

	/** Changes equation mode to Vertex form */
	final public void setToVertexform() {
		setToStringMode(EQUATION_VERTEX);
	}

	/** Changes equation mode to Vertex form */
	final public void setToConicform() {
		setToStringMode(EQUATION_CONICFORM);
	}

	@Override
	final public boolean isSpecificPossible() {
		switch (type) {
		case CONIC_CIRCLE:
		case CONIC_DOUBLE_LINE:
		case CONIC_INTERSECTING_LINES:
		case CONIC_PARALLEL_LINES:
			return true;

		case CONIC_ELLIPSE:
		case CONIC_HYPERBOLA:
			// xy vanished
			return (DoubleUtil.isZero(matrix[3]));

		case CONIC_PARABOLA:
			// x\u00b2 or y\u00b2 vanished
			return DoubleUtil.isZero(matrix[0]) || DoubleUtil.isZero(matrix[1]);

		default:
		case CONIC_LINE:
			return false;
		}
	}

	@Override
	final public boolean isExplicitPossible() {
		if (type == CONIC_LINE) {
			return false;
		}
		return !DoubleUtil.isZero(matrix[5]) && DoubleUtil.isZero(matrix[3])
				&& DoubleUtil.isZero(matrix[1]);
	}

	@Override
	final public boolean isVertexformPossible() {
		return !DoubleUtil.isZero(matrix[0]) && !DoubleUtil.isZero(matrix[5])
				&& DoubleUtil.isZero(matrix[1]) && DoubleUtil.isZero(matrix[3]);
	}

	@Override
	final public boolean isConicformPossible() {
		// directrix parallel with xAxis
		if (!DoubleUtil.isZero(matrix[0]) && !DoubleUtil.isZero(matrix[5])
				&& DoubleUtil.isZero(matrix[1]) && DoubleUtil.isZero(matrix[3])) {
			return true;
		}

		// directrix parallel with yAxis
		if (!DoubleUtil.isZero(matrix[1]) && !DoubleUtil.isZero(matrix[4])
				&& DoubleUtil.isZero(matrix[0]) && DoubleUtil.isZero(matrix[3])) {
			return true;
		}

		return false;
	}

	/**
	 * returns false if conic's matrix is the zero matrix or has infinite or NaN
	 * values
	 */
	final private boolean checkDefined() {
		boolean allZero = true;
		double maxCoeffAbs = 0;

		for (int i = 0; i < 6; i++) {
			if (Double.isNaN(matrix[i]) || Double.isInfinite(matrix[i])) {
				return false;
			}

			double abs = Math.abs(matrix[i]);
			if (abs > Kernel.STANDARD_PRECISION) {
				allZero = false;
			}
			// check max only on coeffs x*x, y*y, x*y
			if ((i == 0 || i == 1 || i == 3) && maxCoeffAbs < abs) {
				maxCoeffAbs = abs;
			}
		}
		if (allZero) {
			return false;
		}

		// eg y = a x^2
		if (maxCoeffAbs == 0) {
			return true;
		}

		// huge or tiny coefficients?
		double factor = 1.0;
		if (maxCoeffAbs < MIN_COEFFICIENT_SIZE) {
			factor = 2;
			while (maxCoeffAbs * factor < MIN_COEFFICIENT_SIZE) {
				factor *= 2;
			}
		} else if (maxCoeffAbs > MAX_COEFFICIENT_SIZE) {
			factor = 0.5;
			while (maxCoeffAbs * factor > MAX_COEFFICIENT_SIZE) {
				factor *= 0.5;
			}
		}

		// multiply matrix with factor to avoid huge and tiny coefficients
		if (factor != 1.0 && !Double.isInfinite(factor)
				&& !Double.isNaN(factor)) {
			for (int i = 0; i < 6; i++) {
				matrix[i] *= factor;
			}
		}
		return true;
	}

	@Override
	final protected boolean showInEuclidianView() {
		return isDefined() && (type != CONIC_EMPTY || isInverseFill());
	}

	/**
	 * Returns whether this conic consists of lines
	 * 
	 * @return true for line conics
	 */
	final public boolean isLineConic() {
		switch (type) {
		case CONIC_DOUBLE_LINE:
		case CONIC_PARALLEL_LINES:
		case CONIC_INTERSECTING_LINES:
		case CONIC_LINE:
			return true;

		default:
			return false;
		}
	}

	/**
	 * Returns whether this conic is degenerate
	 * 
	 * @return true iff degenerate
	 */
	final public boolean isDegenerate() {
		switch (type) {
		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
		case CONIC_HYPERBOLA:
		case CONIC_PARABOLA:
			return false;

		default:
			return true;
		}
	}

	/**
	 * sets conic's matrix from coefficients of equation from array
	 * 
	 * @param coeffs
	 *            Array of coefficients
	 */
	final public void setCoeffs(double[] coeffs) {
		setCoeffs(coeffs[0], coeffs[1], coeffs[2], coeffs[3], coeffs[4],
				coeffs[5]);
	}

	/**
	 * sets conic's matrix from coefficients of equation a x\u00b2 + b xy + c
	 * y\u00b2 + d x + e y + f = 0
	 * 
	 * @param a
	 *            coeff at x^2
	 * @param b
	 *            coeff at xy
	 * @param c
	 *            coeff at y^2
	 * @param d
	 *            coeff at x
	 * @param e
	 *            coeff at y
	 * @param f
	 *            constant coeff
	 */
	final public void setCoeffs(double a, double b, double c, double d,
			double e, double f) {
		setDefinition(null);
		matrix[ConicMatrix.XX] = a; // x\u00b2
		matrix[ConicMatrix.YY] = c; // y\u00b2
		matrix[ConicMatrix.CONST] = f; // constant
		matrix[ConicMatrix.XY] = b / 2.0; // xy
		matrix[ConicMatrix.X] = d / 2.0; // x
		matrix[ConicMatrix.Y] = e / 2.0; // y

		classifyConic();
	}

	@Override
	public String toValueStringMinimal(StringTemplate tpl) {
		return getXMLtagsMinimal();
	}

	@Override
	public String toStringMinimal(StringTemplate tpl) {
		return getXMLtagsMinimal();
	}

	/**
	 * returns some class-specific xml tags for getConstructionRegressionOut
	 * 
	 * @return some class-specific xml tags for getConstructionRegressionOut
	 */
	protected String getXMLtagsMinimal() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			sb.append(regrFormat(matrix[i]));
			sb.append(" ");
		}
		sb.append(regrFormat(matrix[5]));

		return sb.toString();
	}
	// I'm not sure if this is the right place for the *Minimal() methods.
	// In v3.2 they were put into kernel.GeoConic. It seems both are OK.
	// -- Zoltan, 2011-08-01

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {
		return buildValueString(tpl, matrix);
	}

	/**
	 * 
	 * @param tpl
	 *            string template
	 * @param matrix1
	 *            matrix
	 * @return the value string regarding the given matrix (used for views)
	 */
	protected StringBuilder buildValueString(StringTemplate tpl,
			double[] matrix1) {
		StringBuilder sbToValueString = new StringBuilder();
		if (!isDefined()) {
			sbToValueString.append("?");
			return sbToValueString;
		}
		coeffs[0] = matrix1[ConicMatrix.XX]; // x\u00b2
		coeffs[2] = matrix1[ConicMatrix.YY]; // y\u00b2
		coeffs[5] = matrix1[ConicMatrix.CONST]; // constant
		coeffs[1] = 2 * matrix1[ConicMatrix.XY]; // xy
		coeffs[3] = 2 * matrix1[ConicMatrix.X]; // x
		coeffs[4] = 2 * matrix1[ConicMatrix.Y]; // y
		if (tpl.hasCASType()) {
			StringBuilder sb = sbToValueString;
			sb.setLength(0);
			String x = tpl.printVariableName("x");
			String y = tpl.printVariableName("y");
			sb.append("(");
			sb.append(kernel.format(coeffs[0], tpl));
			sb.append(")*");
			sb.append(x);
			sb.append("^2+(");
			sb.append(kernel.format(coeffs[1], tpl));
			sb.append(")*");
			sb.append(x);
			sb.append("*");
			sb.append(y);
			sb.append("+(");
			sb.append(kernel.format(coeffs[2], tpl));
			sb.append(")*");
			sb.append(y);
			sb.append("^2+(");
			sb.append(kernel.format(coeffs[3], tpl));
			sb.append(")*");
			sb.append(x);
			sb.append("+(");
			sb.append(kernel.format(coeffs[4], tpl));
			sb.append(")*");
			sb.append(y);
			sb.append("+");
			sb.append(kernel.format(coeffs[5], tpl));
			// serialise to CAS as "...=0" so eg Coefficients(c) works
			sb.append("=0");
			return sb;
		}
		if (getToStringMode() == GeoConicND.EQUATION_PARAMETRIC) {
			return this.buildParametricValueString(tpl, 2);
		}
		if (getDefinition() != null
				&& getToStringMode() == GeoConicND.EQUATION_USER) {
			return sbToValueString.append(getDefinition().toValueString(tpl));
		}
		if (type == CONIC_LINE) {
			lines[0].toStringLHS(sbToValueString, tpl);
			sbToValueString.append(" = 0");
			return sbToValueString;
		}

		final String squared = tpl.squared();
		String[] myVars;
		switch (tpl.getStringType()) {
		case LATEX:
			myVars = varsLateX;
			break;

		case GIAC:
			myVars = varsCAS;
			break;

		default:
			myVars = vars;
		}

		switch (getToStringMode()) {
		case EQUATION_SPECIFIC:
			if (!isSpecificPossible()) {
				return kernel.buildImplicitEquation(coeffs, myVars,
						true, false, tpl, true);
			}

			switch (type) {
			case CONIC_CIRCLE:
				buildSphereNDString(sbToValueString, tpl);
				return sbToValueString;

			case CONIC_ELLIPSE:
				if (DoubleUtil.isZero(coeffs[1])) { // xy coeff = 0
					double coeff0, coeff1;
					// we have to check the first eigenvector: it could be (1,0)
					// or (0,1)
					// if it is (0,1) we have to swap the coefficients of x^2
					// and y^2
					if (DoubleUtil.isZero(eigenvec[0].getY())) {
						coeff0 = halfAxes[0];
						coeff1 = halfAxes[1];
					} else {
						coeff0 = halfAxes[1];
						coeff1 = halfAxes[0];
					}

					if (DoubleUtil.isZero(b.getX())) {
						sbToValueString.append("x");
						sbToValueString.append(squared);
					} else {
						sbToValueString.append("(x ");
						kernel.formatSigned(-b.getX(), sbToValueString, tpl);
						sbToValueString.append(")");
						sbToValueString.append(squared);
					}
					sbToValueString.append(" / ");
					sbToValueString.append(kernel.format(coeff0 * coeff0, tpl));
					sbToValueString.append(" + ");
					if (DoubleUtil.isZero(b.getY())) {
						sbToValueString.append("y");
						sbToValueString.append(squared);
					} else {
						sbToValueString.append("(y ");
						kernel.formatSigned(-b.getY(), sbToValueString, tpl);
						sbToValueString.append(")");
						sbToValueString.append(squared);
					}
					sbToValueString.append(" / ");
					sbToValueString.append(kernel.format(coeff1 * coeff1, tpl));
					sbToValueString.append(" = 1");

					return sbToValueString;
				}
				return kernel.buildImplicitEquation(coeffs, myVars,
						true, false, tpl, true);

			case CONIC_HYPERBOLA:
				if (DoubleUtil.isZero(coeffs[1])) { // xy coeff = 0
					char firstVar, secondVar;
					double b1, b2;
					// we have to check the first eigenvector: it could be (1,0)
					// or (0,1)
					// if it is (0,1) we have to swap the x and y, needs isZero
					// for #4248
					if (DoubleUtil.isZero(eigenvec[0].getY())) {
						firstVar = 'x';
						secondVar = 'y';
						b1 = b.getX();
						b2 = b.getY();
					} else {
						firstVar = 'y';
						secondVar = 'x';
						b1 = b.getY();
						b2 = b.getX();
					}

					if (DoubleUtil.isZero(b1)) {
						sbToValueString.append(firstVar);
						sbToValueString.append(squared);
					} else {
						sbToValueString.append('(');
						sbToValueString.append(firstVar);
						sbToValueString.append(' ');
						kernel.formatSigned(-b1, sbToValueString, tpl);
						sbToValueString.append(")");
						sbToValueString.append(squared);
					}
					sbToValueString.append(" / ");
					sbToValueString.append(
							kernel.format(halfAxes[0] * halfAxes[0], tpl));
					sbToValueString.append(" - ");
					if (DoubleUtil.isZero(b2)) {
						sbToValueString.append(secondVar);
						sbToValueString.append(squared);
					} else {
						sbToValueString.append('(');
						sbToValueString.append(secondVar);
						sbToValueString.append(' ');
						kernel.formatSigned(-b2, sbToValueString, tpl);
						sbToValueString.append(")");
						sbToValueString.append(squared);
					}
					sbToValueString.append(" / ");
					sbToValueString.append(
							kernel.format(halfAxes[1] * halfAxes[1], tpl));
					sbToValueString.append(" = 1");

					return sbToValueString;
				}
				return kernel.buildImplicitEquation(coeffs, myVars,
						true, false, tpl, true);

			case CONIC_PARABOLA:
				if (!DoubleUtil.isZero(coeffs[2])) {
					return kernel.buildExplicitConicEquation(coeffs, myVars, 2, tpl);
				} else if (!DoubleUtil.isZero(coeffs[0])) {
					return kernel.buildExplicitConicEquation(coeffs, myVars, 0, tpl);
				} else {
					return kernel.buildImplicitEquation(coeffs, myVars,
							true, false, tpl, true);
				}

			case CONIC_DOUBLE_LINE:
				sbToValueString.append('(');
				lines[0].toStringLHS(sbToValueString, tpl);
				sbToValueString.append(")");
				sbToValueString.append(squared);
				sbToValueString.append(" = 0");
				return sbToValueString;

			case CONIC_PARALLEL_LINES:
			case CONIC_INTERSECTING_LINES:
				sbToValueString.append('(');
				lines[0].toStringLHS(sbToValueString, tpl);
				sbToValueString.append(") (");
				lines[1].toStringLHS(sbToValueString, tpl);
				sbToValueString.append(") = 0");
				return sbToValueString;

			}

		case EQUATION_EXPLICIT:
			if (isExplicitPossible()) {
				return kernel.buildExplicitConicEquation(coeffs, myVars, 4, tpl);
			}

		case EQUATION_VERTEX:
			if (isVertexformPossible()) {
				return kernel.buildVertexformEquation(coeffs, myVars, tpl);
			}

		case EQUATION_CONICFORM:
			if (isConicformPossible()) {
				return kernel.buildConicformEquation(coeffs, myVars, tpl);
			}

		default: // implicit
			return kernel.buildImplicitEquation(coeffs, myVars,
					true, false, tpl, true);
		}
	}

	/**
	 * Returns the halfaxes
	 * 
	 * @return lengths of halfaxes
	 */
	final public double[] getHalfAxes() {
		return halfAxes;
	}

	/**
	 * for intersecting lines, parallel lines
	 * 
	 * @return lines the conic consists of
	 */
	final public GeoLine[] getLines() {
		if (lines == null) {
			lines = new GeoLine[2];
			lines[0] = new GeoLine(cons);
			lines[1] = new GeoLine(cons);
		}
		return lines;
	}

	/**
	 * Returns the point (in case this conic is a single point)
	 * 
	 * @return the single point
	 */
	final public GeoPoint getSinglePoint() {
		return singlePoint;
	}

	/**
	 * Returns the eigenvector-real worl transformation
	 * 
	 * @return eigenvector-real worl transformation
	 */
	final public GAffineTransform getAffineTransform() {
		if (transform == null) {
			transform = AwtFactory.getPrototype().newAffineTransform();
		}
		return transform;
	}

	@Override
	final protected void setAffineTransform() {
		GAffineTransform at = getAffineTransform();

		/*
		 * ( v1x v2x bx ) ( v1y v2y by ) ( 0 0 1 )
		 */
		at.setTransform(eigenvec[0].getX(), eigenvec[0].getY(),
				eigenvec[1].getX(), eigenvec[1].getY(), b.getX(), b.getY());
	}

	/**
	 * Returns midpoint or vertex
	 * 
	 * @return midpoint or vertex
	 */
	final public GeoVec2D getTranslationVector() {
		return b;
	}

	/**
	 * return the radius of the circle (if the conic is a circle)
	 * 
	 * @return the radius of the circle
	 */
	final public double getCircleRadius() {
		return getHalfAxis(0);
	}

	/**
	 * Transforms coords of point P from Eigenvector space to real world space.
	 * Note: P.setCoords() is not called here!
	 * 
	 * @param P
	 *            point in EV coords
	 */
	protected final void coordsEVtoRW(GeoPoint P) {
		// rotate by alpha
		double px = P.x;
		P.x = px * eigenvec[0].getX() + P.y * eigenvec[1].getX();
		P.y = px * eigenvec[0].getY() + P.y * eigenvec[1].getY();

		// translate by b
		P.x = P.x + P.z * b.getX();
		P.y = P.y + P.z * b.getY();
	}

	/**
	 * Transforms coords of point P from real world space to Eigenvector space.
	 * Note: P.setCoords() is not called here!
	 */
	/*
	 * private void coordsRWtoEV(GeoPoint P) { TODO delete if not needed //
	 * translate by -b P.x = P.x - P.z * b.getX(); P.y = P.y - P.z * b.getY();
	 * 
	 * // rotate by -alpha double px = P.x; P.x = px * eigenvec[0].getX() + P.y
	 * * eigenvec[0].getY(); P.y = px * eigenvec[1].getX() + P.y *
	 * eigenvec[1].getY(); }
	 */

	/** @return copy of flat matrix */
	final public double[] getMatrix() {
		double[] ret = { matrix[0], matrix[1], matrix[2], matrix[3], matrix[4],
				matrix[5] };
		return ret;
	}

	/**
	 * set out with flat matrix of this conic
	 * 
	 * @param out
	 *            array in which the flat matrix should be stored
	 */
	final public void getMatrix(double[] out) {
		for (int i = 0; i < 6; i++) {
			out[i] = matrix[i];
		}
	}

	/**
	 * set conic's matrix from flat matrix
	 * 
	 * @param matrix
	 *            array from which the flat matrix should be read
	 */
	final public void setMatrix(double[] matrix) {
		setDefinition(null);
		for (int i = 0; i < 6; i++) {
			this.matrix[i] = matrix[i];
		}
		classifyConic();
	}

	/**
	 * Set conic's matrix from flat matrix (array of length 6).
	 * 
	 * @param matrix
	 *            array from which the flat matrix should be read
	 */
	final public void setDegenerateMatrixFromArray(double[] matrix) {
		for (int i = 0; i < 6; i++) {
			this.matrix[i] = matrix[i];
		}
		classifyConic(true);
	}

	/**
	 * set conic's matrix from 3x3 matrix (not necessarily be symmetric).
	 * 
	 * @param C
	 *            matrix
	 */
	final public void setMatrix(double[][] C) {
		matrix[0] = C[0][0];
		matrix[1] = C[1][1];
		matrix[2] = C[2][2];
		matrix[3] = (C[0][1] + C[1][0]) / 2.0;
		matrix[4] = (C[0][2] + C[2][0]) / 2.0;
		matrix[5] = (C[1][2] + C[2][1]) / 2.0;
		classifyConic();
	}

	/**
	 * makes this conic a circle with midpoint M and radius BC Michael Borcherds
	 * 2008-03-13
	 * 
	 * @param M
	 *            midpoint
	 * @param B
	 *            first radius endpoint
	 * @param C
	 *            second radius endpoint
	 */
	final public void setCircle(GeoPoint M, GeoPoint B, GeoPoint C) {
		defined = M.isDefined() && !M.isInfinite() && B.isDefined()
				&& !B.isInfinite() && C.isDefined() && !C.isInfinite();

		double r = B.distance(C);

		// check radius
		if (DoubleUtil.isZero(r)) {
			r = 0;
		} else if (r < 0) {
			defined = false;
		}

		if (defined) {
			setCircleMatrix(M, r);
			setAffineTransform();
		}
	}

	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		setCircle((GeoPoint) M, (GeoSegment) segment);
	}

	/**
	 * makes this conic a circle with midpoint M and radius geoSegment Michael
	 * Borcherds 2008-03-13
	 * 
	 * @param M
	 *            center of circle
	 * @param geoSegment
	 *            length of geoSegment is radius of the circle
	 */
	final public void setCircle(GeoPoint M, GeoSegment geoSegment) {
		defined = M.isDefined() && !M.isInfinite() && geoSegment.isDefined();

		double r = geoSegment.getLength();

		// check radius
		if (DoubleUtil.isZero(r)) {
			r = 0;
		} else if (r < 0) {
			defined = false;
		}

		if (defined) {
			setCircleMatrix(M, r);
			setAffineTransform();
		}
	}

	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		setCircle((GeoPoint) M, (GeoPoint) P);
	}

	/**
	 * Sets matrix to matrix of a circle with given center and radius
	 * 
	 * @param M
	 *            center
	 * @param r
	 *            radius
	 */
	protected void setCircleMatrix(GeoPoint M, double r) {

		setSphereNDMatrix(M.getInhomCoordsInD2(), r);
	}

	/**
	 * set Parabola from focus and line
	 * 
	 * @param F
	 *            focus
	 * @param g
	 *            line
	 */
	final public void setParabola(GeoPointND F, GeoLine g) {
		defined = F.isDefined() && !F.isInfinite() && g.isDefined();

		if (!defined) {
			return;
		}

		// set parabola's matrix
		double fx = F.getInhomX();
		double fy = F.getInhomY();

		matrix[0] = g.y * g.y;
		matrix[1] = g.x * g.x;
		double lsq = matrix[0] + matrix[1];
		matrix[2] = lsq * (fx * fx + fy * fy) - g.z * g.z;
		matrix[3] = -g.x * g.y;
		matrix[4] = -(lsq * fx + g.x * g.z);
		matrix[5] = -(lsq * fy + g.y * g.z);
		normalizeMatrix();
		classifyConic();

		// avoid flip
		if (defined && eigenvecX * g.x + eigenvecY * g.y < 0) {
			eigenvecX *= -1;
			eigenvecY *= -1;

			c.setX(matrix[4] * eigenvecX + matrix[5] * eigenvecY);
			c.setY(matrix[5] * eigenvecX - matrix[4] * eigenvecY);

			parabola();
		}

	}

	private void normalizeMatrix() {
		double norm = 1;
		for (int i = 0; i < 6; i++) {
			double entry = Math.abs(matrix[i]);
			if (Math.abs(norm) < entry && 1000 < entry) {
				norm = entry;
			}
			if (Math.abs(norm) > entry && 0.001 > entry
					&& !DoubleUtil.isZero(entry)) {
				norm = entry;
			}
		}
		if (norm != 1) {
			for (int i = 0; i < 6; i++) {
				matrix[i] = matrix[i] / norm;
			}
		}

	}

	/**
	 * set Parabola from focus (0,0) and line y = y0
	 * 
	 * @param y0
	 *            y coord for line
	 */
	final public void setParabola(double y0) {
		// set parabola's matrix

		matrix[0] = 1;
		matrix[1] = 0;
		matrix[2] = -y0 * y0;
		matrix[3] = 0;
		matrix[4] = 0;
		matrix[5] = y0;

		classifyConic();
	}

	/**
	 * set the matrix of ellipse or hyperbola
	 * 
	 * @param B
	 *            first focus
	 * @param C
	 *            second focus
	 * @param a
	 *            first half axis
	 */
	final public void setEllipseHyperbola(GeoPoint B, GeoPoint C, double a) {
		if (B.isInfinite() || C.isInfinite()
				|| a < -Kernel.STANDARD_PRECISION) {
			defined = false;
			return;
		}
		defined = true;

		// set conics's matrix
		double b1 = B.inhomX;
		double b2 = B.inhomY;
		double c1 = C.inhomX;
		double c2 = C.inhomY;

		/*
		 * removed again, we want Ellipse[(-3,3), (3,-5), 5] to give a
		 * double-line (also see #1297) double halfLengthBC =
		 * Math.sqrt((b1-c1)*(b1-c1)+(b2-c2)*(b2-c2))/2;
		 * 
		 * if (AbstractKernel.isEqual(halfLengthBC, a)) { defined = false;
		 * return; }
		 */

		// precalculations
		double diff1 = b1 - c1;
		double diff2 = b2 - c2;
		double sqsumb = b1 * b1 + b2 * b2;
		double sqsumc = c1 * c1 + c2 * c2;
		double sqsumdiff = sqsumb - sqsumc;
		double a2 = 2.0 * a;
		double asq4 = a2 * a2;
		double asq = a * a;
		double afo = asq * asq;

		matrix[0] = 4.0 * (a2 - diff1) * (a2 + diff1);
		matrix[3] = -4.0 * diff1 * diff2;
		matrix[1] = 4.0 * (a2 - diff2) * (a2 + diff2);
		matrix[4] = -2.0 * (asq4 * (b1 + c1) - diff1 * sqsumdiff);
		matrix[5] = -2.0 * (asq4 * (b2 + c2) - diff2 * sqsumdiff);
		matrix[2] = -16.0 * afo - sqsumdiff * sqsumdiff
				+ 8.0 * asq * (sqsumb + sqsumc);

		// set eigenvectors' directions (B -> C and normalvector)
		// this is needed, so that setEigenvectors() (called by classifyConic)
		// will surely take the right direction
		// normalizing is not needed at this point
		eigenvec[0].setX(c1 - b1);
		eigenvec[0].setY(c2 - b2);
		eigenvec[1].setX(-eigenvec[0].getY());
		eigenvec[1].setY(eigenvec[0].getX());

		classifyConic();

		// check if we got an ellipse or hyperbola
		if (!(type == CONIC_HYPERBOLA || type == CONIC_ELLIPSE
				|| type == CONIC_CIRCLE || type == CONIC_DOUBLE_LINE)) {
			defined = false;
		}
	}

	/*************************************
	 * MOVEMENTS
	 *************************************/

	/**
	 * translate conic by vector v
	 * 
	 * @param v
	 *            translation vector
	 */
	@Override
	public void translate(Coords v) {
		doTranslate(v);

		// classifyConic();
		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	@Override
	final public boolean isTranslateable() {
		return true;
	}

	/**
	 * translate this conic by vector (vx, vy)
	 * 
	 * @param vx
	 *            x-coord of translation vector
	 * @param vy
	 *            y-coord of translation vector
	 */
	final public void translate(double vx, double vy) {
		doTranslate(vx, vy);

		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	/**
	 * @param v
	 *            translation vector
	 */
	protected void doTranslate(Coords v) {
		doTranslate(v.getX(), v.getY());
	}

	/**
	 * @param vx
	 *            translation in horizontal direction
	 * @param vy
	 *            translation in vertical dyrection
	 */
	protected final void doTranslate(double vx, double vy) {
		// calc translated matrix
		translateMatrix(matrix, vx, vy);

		// avoid classification and set changes by hand:
		setMidpoint(new Coords(new double[] { vx, vy, 0 })
				.addInside(getMidpoint()).get());
		/*
		 * b.x += vx; b.y += vy;
		 */
	}

	/**
	 * @param mat
	 *            matrix
	 * @param vx
	 *            translation in horizontal direction
	 * @param vy
	 *            translation in vertical direction
	 */
	protected void translateMatrix(double[] mat, double vx, double vy) {
		setDefinition(null);
		mat[2] = mat[2] + vx * (mat[0] * vx - 2.0 * mat[4])
				+ vy * (mat[1] * vy - 2.0 * mat[5] + 2.0 * mat[3] * vx);
		mat[4] = mat[4] - mat[0] * vx - mat[3] * vy;
		mat[5] = mat[5] - mat[3] * vx - mat[1] * vy;
	}

	/**
	 * rotate this conic by angle phi around (0,0)
	 * 
	 * @param phiVal
	 *            angle
	 */
	@Override
	public void rotate(NumberValue phiVal) {
		double phi = phiVal.getDouble();
		rotate(phi);

		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	/**
	 * rotate this conic by angle phi around Q
	 * 
	 * @param phiVal
	 *            angle
	 * @param point
	 *            rotation center
	 */
	@Override
	public void rotate(NumberValue phiVal, GeoPointND point) {
		Coords Q = point.getInhomCoords();
		double phi = phiVal.getDouble();
		double qx = Q.getX();
		double qy = Q.getY();

		// first translate to new origin Q
		doTranslate(-qx, -qy);
		// rotate around new origin Q
		rotate(phi);
		// translate back to old origin (0,0)
		doTranslate(qx, qy);

		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {
		double det = a00 * a11 - a01 * a10;
		double det2 = det * det;

		double A0 = a11 * (a11 * matrix[0] - a10 * matrix[3])
				- a10 * (a11 * matrix[3] - a10 * matrix[1]);
		double A3 = a00 * (a11 * matrix[3] - a10 * matrix[1])
				- a01 * (a11 * matrix[0] - a10 * matrix[3]);
		double A1 = a00 * (a00 * matrix[1] - a01 * matrix[3])
				- a01 * (a00 * matrix[3] - a01 * matrix[0]);
		double A4 = a11 * matrix[4] - a10 * matrix[5];
		matrix[5] = (a00 * matrix[5] - a01 * matrix[4]) / det;
		matrix[0] = A0 / det2;
		matrix[1] = A1 / det2;
		matrix[3] = A3 / det2;
		matrix[4] = A4 / det;

		classifyConic();
	}

	/**
	 * rotate this conic by angle phi around (0,0) [ cos -sin 0 ] [ sin cos 0 ]
	 * [ 0 0 1 ]
	 */
	final private void rotate(double phi) {
		// set rotated matrix
		rotateMatrix(matrix, phi);

		// avoid classification: make changes by hand
		eigenvec[0].rotate(phi);
		eigenvec[1].rotate(phi);
		b.rotate(phi);
		setMidpoint(new double[] { b.getX(), b.getY() });
	}

	/**
	 * @return rotation of the conic
	 */
	public double getRotation() {
		return Math.abs(Math.asin(eigenvec[1].getX()))
				* -Math.signum(eigenvec[1].getX() * eigenvec[1].getY());
	}

	/**
	 * rotate the matrix
	 * 
	 * @param matrix
	 *            matrix
	 * @param phi
	 *            angle
	 */
	final protected static void rotateMatrix(double[] matrix, double phi) {
		double sum = matrix[0] + matrix[1];
		double diff = matrix[0] - matrix[1];
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		// cos(2 phi) = cos(phi)\u00b2 - sin(phi)\u00b2 = (cos + sin)*(cos -
		// sin)
		double cos2 = (cos + sin) * (cos - sin);
		// cos(2 phi) = 2 cos sin
		double sin2 = 2.0 * cos * sin;

		double temp = diff * cos2 - 2.0 * matrix[3] * sin2;
		double A0 = (sum + temp) / 2.0;
		double A1 = (sum - temp) / 2.0;
		double A3 = matrix[3] * cos2 + diff * cos * sin;
		double A4 = matrix[4] * cos - matrix[5] * sin;
		matrix[5] = matrix[5] * cos + matrix[4] * sin;
		matrix[0] = A0;
		matrix[1] = A1;
		matrix[3] = A3;
		matrix[4] = A4;
	}

	/**
	 * dilate this conic from point (0,0) by factor r
	 * 
	 * @param r
	 *            ratio
	 */
	final protected void dilate(double r) {

		// remember Eigenvector orientation
		boolean oldOrientation = hasPositiveEigenvectorOrientation();

		// do dilation
		doDilate(r);

		// classify as type may have change
		classifyConic();

		// make sure we preserve old Eigenvector orientation
		setPositiveEigenvectorOrientation(oldOrientation);
	}

	/**
	 * Dilate the conic wrt point (0,0)
	 * 
	 * @param factor
	 *            dilation factor
	 */
	protected final void doDilate(double factor) {
		// calc dilated matrix
		double r = 1d / factor;
		double r2 = r * r;
		matrix[0] *= r2;
		matrix[1] *= r2;
		matrix[3] *= r2;
		matrix[4] *= r;
		matrix[5] *= r;
	}

	/**
	 * to avoid classification in movements this method is called to update the
	 * lines (point) of degenerate conics
	 */
	protected final void updateDegenerates() {
		// update lines of degenerate conic
		switch (type) {
		default:
			// do nothing
			break;
		case CONIC_SINGLE_POINT:
			singlePoint();
			break;

		case CONIC_INTERSECTING_LINES:
			intersectingLines(mu); // coefficient mu unchanged
			break;

		case CONIC_DOUBLE_LINE:
			doubleLine();
			break;

		case CONIC_PARALLEL_LINES:
			parallelLines(mu); // coefficient mu unchanged
			break;
		case CONIC_LINE:
			singleLine(mu); // coefficient mu unchanged
			break;
		}
	}

	/*************************************
	 * CONIC CLASSIFICATION
	 *************************************/

	/**
	 * Sets both eigenvectors to e0, e1 on file load. (note: needed for
	 * "near-to-relationship" after loading a file)
	 * 
	 * @param x0
	 *            homogenous x-coord of e0
	 * @param y0
	 *            homogenous y-coord of e0
	 * @param z0
	 *            homogenous z-coord of e0
	 * @param x1
	 *            homogenous x-coord of e1
	 * @param y1
	 *            homogenous y-coord of e1
	 * @param z1
	 *            homogenous z-coord of e1
	 * 
	 */
	final public void setEigenvectors(double x0, double y0, double z0,
			double x1, double y1, double z1) {

		eigenvec[0].setX(x0 / z0);
		eigenvec[0].setY(y0 / z0);
		eigenvec[1].setX(x1 / z1);
		eigenvec[1].setY(y1 / z1);
		eigenvectorsSetOnLoad = true;
	}

	@Override
	protected void setFirstEigenvector(double[] coords) {
		eigenvecX = coords[0];
		eigenvecY = coords[1];
	}

	@Override
	final protected void findEigenvectors() {
		// newly calculated first eigenvector = (eigenvecX, eigenvecY)
		// old eigenvectors: eigenvec[0], eigenvec[1]
		// set direction of eigenvectors with respect to old values:
		// take inner product >= 0 (small angle change)

		// make (eigenvecX, eigenvecY) a unit vector
		length = MyMath.length(eigenvecX, eigenvecY);
		if (length != 1.0d) {
			eigenvecX = eigenvecX / length;
			eigenvecY = eigenvecY / length;
		}

		if (kernel.isContinuous()) {
			// first eigenvector
			if (eigenvec[0].getX() * eigenvecX < -eigenvec[0].getY()
					* eigenvecY) {
				eigenvec[0].setX(-eigenvecX);
				eigenvec[0].setY(-eigenvecY);
			} else {
				eigenvec[0].setX(eigenvecX);
				eigenvec[0].setY(eigenvecY);
			}

			// second eigenvector (compared to normalvector (-eigenvecY,
			// eigenvecX)
			if (eigenvec[1].getY() * eigenvecX < eigenvec[1].getX()
					* eigenvecY) {
				eigenvec[1].setX(eigenvecY);
				eigenvec[1].setY(-eigenvecX);
			} else {
				eigenvec[1].setX(-eigenvecY);
				eigenvec[1].setY(eigenvecX);
			}
		}
		// non-continous
		else if (!eigenvectorsSetOnLoad) {
			eigenvec[0].setX(eigenvecX);
			eigenvec[0].setY(eigenvecY);
			eigenvec[1].setX(-eigenvecY);
			eigenvec[1].setY(eigenvecX);
		}

		eigenvectorsSetOnLoad = false;
	}

	final private void setParabolicEigenvectors() {
		// fist eigenvector of parabola must not be changed

		// newly calculated first eigenvector = (eigenvecX, eigenvecY)
		// old eigenvectors: eigenvec[0], eigenvec[1]
		// set direction of eigenvectors with respect to old values:
		// take inner product >= 0 (small angle change)

		// make (eigenvecX, eigenvecY) a unit vector
		length = MyMath.length(eigenvecX, eigenvecY);
		if (length != 1.0d) {
			eigenvecX = eigenvecX / length;
			eigenvecY = eigenvecY / length;
		}

		// first eigenvector
		eigenvec[0].setX(eigenvecX);
		eigenvec[0].setY(eigenvecY);

		if (kernel.isContinuous()) {
			// second eigenvector (compared to normalvector (-eigenvecY,
			// eigenvecX)
			if (eigenvec[1].getY() * eigenvecX < eigenvec[1].getX()
					* eigenvecY) {
				eigenvec[1].setX(eigenvecY);
				eigenvec[1].setY(-eigenvecX);
			} else {
				eigenvec[1].setX(-eigenvecY);
				eigenvec[1].setY(eigenvecX);
			}
		} else if (!eigenvectorsSetOnLoad) {
			// non-continous
			eigenvec[1].setX(-eigenvecY);
			eigenvec[1].setY(eigenvecX);
		}

		eigenvectorsSetOnLoad = false;
	}

	/**
	 * Makes all path parameters of points on this conic invalid if the
	 * Eigenvectors have changed. This will force recalculation of the path
	 * parameters on the next call of pointChanged().
	 */
	private void makePathParametersInvalid() {
		if (pointsOnConic == null) {
			return;
		}

		// eigenvectors have changed: we need to force an update of the
		// path parameters of all points on this conic.
		getAffineTransform();
		if (oldTransform == null) {
			oldTransform = AwtFactory.getPrototype().newAffineTransform();
		}
		boolean eigenVectorsSame = DoubleUtil.isEqual(transform.getScaleX(),
				oldTransform.getScaleX(), Kernel.MIN_PRECISION)
				|| DoubleUtil.isEqual(transform.getScaleY(),
						oldTransform.getScaleY(), Kernel.MIN_PRECISION)
				|| DoubleUtil.isEqual(transform.getShearX(),
						oldTransform.getShearX(), Kernel.MIN_PRECISION)
				|| DoubleUtil.isEqual(transform.getShearY(),
						oldTransform.getShearY(), Kernel.MIN_PRECISION);

		if (!eigenVectorsSame) {
			// updated old transform
			oldTransform.setTransform(transform);

			int size = pointsOnConic.size();
			for (int i = 0; i < size; i++) {
				GeoPointND point = pointsOnConic.get(i);
				if (point.getPath() == this) {
					point.getPathParameter().setT(Double.NaN);
				}
			}
		}
	}

	/**
	 * Update conic type and properties
	 */
	protected void classifyConic() {
		classifyConic(false);
	}

	/**
	 * @param degenerate
	 *            true to allow classification as degenerate
	 */
	public void classifyConic(boolean degenerate) {
		defined = degenerate || checkDefined();
		if (!defined) {
			return;
		}

		// det of S lets us distinguish between
		// parabolic and midpoint conics
		// det(S) = A[0] * A[1] - A[3] * A[3]
		if (isDetSzero()) {
			classifyParabolicConic(degenerate);
		} else {
			// det(S) = A[0] * A[1] - A[3] * A[3]
			detS = matrix[0] * matrix[1] - matrix[3] * matrix[3];
			classifyMidpointConic(degenerate);
		}
		setAffineTransform();

		// Application.debug("conic: " + this.getLabel() + " type " +
		// getTypeString() );
		// Application.debug(" detS: " + (A0A1 - A3A3));ELLIPSE
	}

	/**
	 * Returns whether det(S) = A[0] * A[1] - A[3] * A[3] is zero. This method
	 * takes care of possibly large coefficients and adapts the precision used
	 * for the zero test automatically.
	 */
	private boolean isDetSzero() {
		// get largest abs of A0, A1, A3
		// double maxAbs = Math.abs(matrix[0]);
		// double abs = Math.abs(matrix[1]);
		// if (abs > maxAbs) {
		// maxAbs = abs;
		// }
		// abs = Math.abs(matrix[3]);

		// det(S) = 0
		// A[0] * A[1] = A[3] * A[3]
		// normalized: A[0]/maxAbs * A[1]/maxAbs = A[3]/maxAbs * A[3]/maxAbs
		// use precision: eps * maxAbs^2
		/*
		 * double eps;
		 * 
		 * if (maxAbs > 1) { eps = kernel.getSTANDARD_PRECISION() * maxAbs *
		 * maxAbs; } else { eps = kernel.getSTANDARD_PRECISION() * maxAbs *
		 * maxAbs; //TODO: Also need to care for small coeff }
		 */
		return DoubleUtil.isEqual(matrix[0] * matrix[1], matrix[3] * matrix[3],
				this.errDetS);
	}

	/*************************************
	 * midpoint conics
	 *************************************/

	final private void classifyMidpointConic(boolean degenerate) {
		// calc eigenvalues and eigenvectors
		if (DoubleUtil.isZero(matrix[3])) {
			// special case: submatrix S is allready diagonal
			eigenval[0] = matrix[0];
			eigenval[1] = matrix[1];
			eigenvecX = 1.0d;
			eigenvecY = 0.0d;
		} else {
			// eigenvalues are solutions of
			// 0 = det(S - x E) = x\u00b2 - spurS x + detS
			// detS was computed in classifyConic()
			// init array for solver
			eigenval[0] = detS;
			eigenval[1] = -(matrix[0] + matrix[1]); // -spurS
			eigenval[2] = 1.0d;

			int nRoots = EquationSolver.solveQuadraticS(
					eigenval, eigenval, Kernel.STANDARD_PRECISION);
			if (nRoots == 1) {
				eigenval[1] = eigenval[0];
			}

			// set first eigenvector
			eigenvecX = -matrix[3];
			eigenvecY = -eigenval[0] + matrix[0];
		}

		// calc translation vector b = midpoint
		// b = -Inverse[S] . a, where a = (A[4], A[5])
		/*
		 * b.x = (matrix[3] * matrix[5] - matrix[1] * matrix[4]) / detS; b.y =
		 * (matrix[3] * matrix[4] - matrix[0] * matrix[5]) / detS;
		 */
		setMidpoint(new double[] {
				(matrix[3] * matrix[5] - matrix[1] * matrix[4]) / detS,
				(matrix[3] * matrix[4] - matrix[0] * matrix[5]) / detS });

		// beta = a . b + alpha, where alpha = A[2]
		double beta = matrix[4] * b.getX() + matrix[5] * b.getY() + matrix[2];

		// beta lets us distinguish between Ellipse, Hyperbola,
		// single singlePoint and intersecting lines
		// if (Kernel.isZero(beta)) {
		if (degenerate || DoubleUtil.isZero(beta)) {
			findEigenvectors();
			// single point or intersecting lines
			mu[0] = eigenval[0] / eigenval[1];
			if (DoubleUtil.isZero(mu[0])) {
				mu[0] = 0.0;
				intersectingLines(mu);
			} else if (mu[0] < 0.0d) {
				mu[0] = Math.sqrt(-mu[0]);
				intersectingLines(mu);
			} else {
				singlePoint();
			}
		} else {
			// Hyperbola, Ellipse, empty
			mu[0] = -eigenval[0] / beta;
			mu[1] = -eigenval[1] / beta;
			if (detS < 0) {
				hyperbola(mu);
			} else {
				if (mu[0] > 0 && mu[1] > 0) {
					ellipse(mu);
				} else {
					empty();
				}
			}
		}
	}

	@Override
	final protected void singlePoint() {
		type = GeoConicNDConstants.CONIC_SINGLE_POINT;

		if (singlePoint == null) {
			singlePoint = new GeoPoint(cons);
		}
		singlePoint.setCoords(b.getX(), b.getY(), 1.0d);
		// Application.debug("singlePoint : " + b);

		halfAxes[0] = 0;
		halfAxes[1] = 0;
	}

	final private void intersectingLines(double[] mu1) {
		type = GeoConicNDConstants.CONIC_INTERSECTING_LINES;

		// set intersecting lines
		getLines();
		// n = T . (-mu, 1)
		temp1 = eigenvec[0].getX() * mu1[0];
		temp2 = eigenvec[0].getY() * mu1[0];
		nx = eigenvec[1].getX() - temp1;
		ny = eigenvec[1].getY() - temp2;

		// take line with smallest change of direction
		if (Math.abs(nx * lines[0].x + ny * lines[0].y) < Math
				.abs(nx * lines[1].x + ny * lines[1].y)) {
			index = 1;
		} else {
			index = 0;
		}

		lines[index].x = nx;
		lines[index].y = ny;
		lines[index].z = -(nx * b.getX() + ny * b.getY());

		// n = T . (mu, 1)
		nx = eigenvec[1].getX() + temp1;
		ny = eigenvec[1].getY() + temp2;
		index = 1 - index;
		lines[index].x = nx;
		lines[index].y = ny;
		lines[index].z = -(nx * b.getX() + ny * b.getY());

		setStartPointsForLines();
		// Application.debug("intersectingLines: " + lines[0] + ", " +
		// lines[1]);
	}

	final private void ellipse(double[] mu1) {

		// circle
		if (DoubleUtil.isEqual(mu1[0] / mu1[1], 1.0)) {

			// sets eigen vecs parallel to Ox and Oy
			eigenvecX = 1;
			eigenvecY = 0;
			findEigenvectors();

			type = GeoConicNDConstants.CONIC_CIRCLE;
			halfAxes[0] = Math.sqrt(1.0d / mu1[0]);
			halfAxes[1] = halfAxes[0];
			linearEccentricity = 0.0d;
			eccentricity = 0.0d;
			// Application.debug("circle: M = " + b + ", r = " + halfAxes[0]);
		} else { // elipse

			if (mu1[0] > mu1[1]) {
				// swap eigenvectors and mu
				temp = mu1[0];
				mu1[0] = mu1[1];
				mu1[1] = temp;

				// rotate eigenvector 90
				temp = eigenvecX;
				eigenvecX = -eigenvecY;
				eigenvecY = temp;
			}
			findEigenvectors();

			type = GeoConicNDConstants.CONIC_ELLIPSE;
			mu1[0] = 1.0d / mu1[0];
			mu1[1] = 1.0d / mu1[1];
			halfAxes[0] = Math.sqrt(mu1[0]);
			halfAxes[1] = Math.sqrt(mu1[1]);
			linearEccentricity = Math.sqrt(mu1[0] - mu1[1]);
			eccentricity = linearEccentricity / Math.sqrt(mu1[0]);

			/*
			 * Application.debug("Ellipse"); Application.debug("a : " +
			 * halfAxes[0]); Application.debug("b : " + halfAxes[1]);
			 * Application.debug("e : " + excent);
			 */
		}
	}

	final private void hyperbola(double[] mu1) {
		type = GeoConicNDConstants.CONIC_HYPERBOLA;
		if (mu1[0] < 0) {
			// swap eigenvectors and mu
			temp = mu1[0];
			mu1[0] = mu1[1];
			mu1[1] = temp;

			// rotate eigenvector 90
			temp = eigenvecX;
			eigenvecX = -eigenvecY;
			eigenvecY = temp;
		}
		findEigenvectors();

		mu1[0] = 1.0d / mu1[0];
		mu1[1] = -1.0d / mu1[1];
		halfAxes[0] = Math.sqrt(mu1[0]);
		halfAxes[1] = Math.sqrt(mu1[1]);
		linearEccentricity = Math.sqrt(mu1[0] + mu1[1]);
		eccentricity = linearEccentricity / Math.sqrt(mu1[0]);
	}

	/*
	 * final private void empty() { type = GeoConic.CONIC_EMPTY; //
	 * Application.debug("empty conic"); }
	 */

	/*************************************
	 * parabolic conics
	 *************************************/

	final private void classifyParabolicConic(boolean degenerate) {
		// calc eigenvalues and first eigenvector
		if (DoubleUtil.isZero(matrix[3])) {
			// special cases: submatrix S is allready diagonal
			// either A[0] or A[1] have to be zero (due to detS = 0)
			if (DoubleUtil.isZero(matrix[0])) {

				// special case: the submatrix S is zero!!!
				if (DoubleUtil.isZero(matrix[1])) {
					handleSzero();
					return;
				}

				// else
				lambda = matrix[1];
				// set first eigenvector
				eigenvecX = 1.0d;
				eigenvecY = 0.0d;
				// c = a . T = a,
				// where T is the matrix of the eigenvectors and a = (A[4],
				// A[5])
				c.setX(matrix[4]);
				c.setY(matrix[5]);
			} else { // A[1] is zero
				lambda = matrix[0];
				eigenvecX = 0.0d; // set first eigenvector
				eigenvecY = 1.0d;
				// c = a . T,
				// where T is the matrix of the eigenvectors and a = (A[4],
				// A[5])
				c.setX(matrix[5]);
				c.setY(-matrix[4]);
			}
		} else { // A[3] != 0
					// eigenvalues are solutions of
					// 0 = det(S - x E) = x^2 - spurS x + detS = x (x - spurS)
			lambda = matrix[0] + matrix[1]; // spurS
			// set first eigenvector as a unit vector (needed fo computing
			// vector c)
			length = MyMath.length(matrix[3], matrix[0]);
			eigenvecX = matrix[3] / length;
			eigenvecY = -matrix[0] / length;
			// c = a . T,
			// where T is the matrix of the eigenvectors and a = (A[4], A[5])
			c.setX(matrix[4] * eigenvecX + matrix[5] * eigenvecY);
			c.setY(matrix[5] * eigenvecX - matrix[4] * eigenvecY);
		}

		if (degenerate || DoubleUtil.isZero(c.getX())) {
			findEigenvectors();
			// b = T . (0, -c.y/lambda)
			temp = c.getY() / lambda;
			/*
			 * b.x = temp * eigenvecY; b.y = -temp * eigenvecX;
			 */
			setMidpoint(new double[] { temp * eigenvecY, -temp * eigenvecX });
			mu[0] = -temp * temp + matrix[2] / lambda;
			if (DoubleUtil.isZero(mu[0])) {
				doubleLine();
			} else if (mu[0] < 0) {
				mu[0] = Math.sqrt(-mu[0]);
				parallelLines(mu);
			} else {
				empty();
			}
		} else { // parabola
			parabola();
		}

	}

	final private void doubleLine() {
		type = GeoConicNDConstants.CONIC_DOUBLE_LINE;

		// set double line
		getLines();
		nx = -eigenvec[0].getY();
		ny = eigenvec[0].getX();
		lines[0].x = nx;
		lines[0].y = ny;
		lines[0].z = -(b.getX() * nx + b.getY() * ny);

		lines[1].x = lines[0].x;
		lines[1].y = lines[0].y;
		lines[1].z = lines[0].z;

		setStartPointsForDoubleLine();
		// setStartPointsForLines();
		// Log.debug("\ndouble line : " + lines[0]+"\nstart point :
		// "+startPoints[0]);
	}

	/**
	 * Change this conic to double line
	 */
	final public void enforceDoubleLine() {
		defined = true;
		doubleLine();
	}

	// if S is the zero matrix, set conic as double line or empty
	final private void handleSzero() {
		// conic is line 2*A[4] * x + 2*A[5] * y + A[2] = 0
		if (DoubleUtil.isZero(matrix[4])) {
			if (DoubleUtil.isZero(matrix[5])) {
				empty();
				return;
			}

			// A[5] not zero
			// make b a point on the line
			/*
			 * b.x = 0; b.y = -matrix[2] / (2*matrix[5]);
			 */
			setMidpoint(new double[] { 0, -matrix[2] / (2 * matrix[5]) });
		} else {
			// A[4] not zero
			// make b a point on the line
			/*
			 * b.x = -matrix[2] / (2*matrix[4]); b.y = 0;
			 */
			setMidpoint(new double[] { -matrix[2] / (2 * matrix[4]), 0 });
		}

		eigenvecX = matrix[5];
		eigenvecY = -matrix[4];
		findEigenvectors();

		doubleLine();
	}

	/**
	 * @param mu1
	 *            mu
	 */
	protected final void parallelLines(double[] mu1) {
		type = GeoConicNDConstants.CONIC_PARALLEL_LINES;

		// set double line
		getLines();
		nx = -eigenvec[0].getY();
		ny = eigenvec[0].getX();
		temp1 = b.getX() * nx + b.getY() * ny;
		lines[0].x = nx;
		lines[0].y = ny;
		lines[1].x = nx;
		lines[1].y = ny;
		// smallest change:
		temp2 = mu1[0] - temp1;
		if (Math.abs(lines[0].z - temp2) < Math.abs(lines[1].z - temp2)) {
			lines[0].z = temp2;
			lines[1].z = -temp1 - mu1[0];
		} else {
			lines[0].z = -temp1 - mu1[0];
			lines[1].z = temp2;
		}

		setStartPointsForLines();

		// Application.debug("parallel lines : " + lines[0] + ", " + lines[1]);
		// Application.debug("coeff : " + mu[0]);
	}

	/**
	 * @param mu1
	 *            sqrt(-eigenval[0] / eigenval[1])
	 */
	protected final void singleLine(double[] mu1) {
		type = GeoConicNDConstants.CONIC_LINE;

		// set double line
		getLines();
		nx = -eigenvec[0].getY();
		ny = eigenvec[0].getX();
		temp1 = b.getX() * nx + b.getY() * ny;
		lines[0].x = nx;
		lines[0].y = ny;

		// smallest change:
		temp2 = mu1[0] - temp1;

		lines[0].z = temp2;

		setStartPointsForLines();

		// Application.debug("parallel lines : " + lines[0] + ", " + lines[1]);
		// Application.debug("coeff : " + mu[0]);
	}

	private void setStartPointsForLines() {
		// make sure we have a start point to compute line parameter
		if (startPoints == null) {
			startPoints = new GeoPoint[2];
			for (int i = 0; i < 2; i++) {
				startPoints[i] = new GeoPoint(cons);
			}
		}

		// update start points
		for (int i = 0; i < 2; i++) {
			lines[i].setStartPoint(null);
			lines[i].getPointOnLine(startPoints[i]);
			lines[i].setStartPoint(startPoints[i]);
			
		}

	}

	private void setStartPointsForDoubleLine() {
		// make sure we have a start point to compute line parameter
		if (startPoints == null) {
			startPoints = new GeoPoint[2];
			for (int i = 0; i < 2; i++) {
				startPoints[i] = new GeoPoint(cons);
			}
		}

		// update start points
		lines[0].setStartPoint(null);
		lines[0].getPointOnLine(startPoints[0]);
		lines[0].setStartPoint(startPoints[0]);

		lines[1].setStartPoint(startPoints[0]);

	}

	final private void parabola() {
		type = GeoConicNDConstants.CONIC_PARABOLA;

		// calc vertex = b
		// b = T . ((c.y\u00b2/lambda - A2)/(2 c.x) , -c.y/lambda)
		temp2 = c.getY() / lambda;
		temp1 = (c.getY() * temp2 - matrix[2]) / (2 * c.getX());
		/*
		 * b.x = eigenvecY * temp2 + eigenvecX * temp1; b.y = eigenvecY * temp1
		 * - eigenvecX * temp2;
		 */
		setMidpoint(new double[] { eigenvecY * temp2 + eigenvecX * temp1,
				eigenvecY * temp1 - eigenvecX * temp2 });
		setParabolicEigenvectors();

		// parameter p of parabola
		p = -c.getX() / lambda;
		if (p < 0) { // change orientation of first eigenvector
			eigenvec[0].setX(-eigenvec[0].getX());
			eigenvec[0].setY(-eigenvec[0].getY());
			p = -p;
		}

		linearEccentricity = p / 2;
		eccentricity = 1;

		/*
		 * Application.debug("parabola"); Application.debug("Vertex: " + b);
		 * Application.debug("p = " + p);
		 */
	}

	/**********************************************************
	 * CACLCULATIONS ON CONIC (det, evaluate, intersect, ...)
	 **********************************************************/

	/**
	 * Computes the determinant of a conic's 3x3 matrix.
	 * 
	 * @param matrix
	 *            flat matrix of conic section
	 * @return matrix determinant
	 */
	public static double det(double[] matrix) {
		return matrix[0] * (matrix[1] * matrix[2] - matrix[5] * matrix[5])
				- matrix[2] * matrix[3] * matrix[3]
				- matrix[1] * matrix[4] * matrix[4]
				+ 2 * matrix[3] * matrix[4] * matrix[5];
	}

	/**
	 * Returns true iff the determinant of 2x2 matrix of eigenvectors is
	 * positive.
	 * 
	 * @return true iff the determinant of 2x2 matrix of eigenvectors is
	 *         positive.
	 */
	protected final boolean hasPositiveEigenvectorOrientation() {
		// return eigenvec[0].x * eigenvec[1].y - eigenvec[0].y * eigenvec[1].x
		// > 0;
		return eigenvec[0].getX() * eigenvec[1].getY() > eigenvec[0].getY()
				* eigenvec[1].getX();
	}

	/**
	 * Sets orientation of eigenvectors to positive or negative
	 * 
	 * @param flag
	 *            true for positive, false for negative
	 */
	protected final void setPositiveEigenvectorOrientation(boolean flag) {
		if (flag != hasPositiveEigenvectorOrientation()) {
			eigenvec[1].setX(-eigenvec[1].getX());
			eigenvec[1].setY(-eigenvec[1].getY());

			setAffineTransform();
		}
	}

	/**
	 * states wheter P lies on this conic or not
	 * 
	 * @return true iff P lies on this conic
	 * @param P
	 *            point
	 * @param eps
	 *            precision
	 */
	public boolean isIntersectionPointIncident(GeoPoint P, double eps) {
		return isOnFullConic(P, eps);
	}

	/**
	 * states wheter P lies on this conic or not
	 * 
	 * @return true iff P lies on this conic
	 * @param P
	 *            point (2D coords in conic coordsys)
	 * @param eps
	 *            precision
	 */
	public boolean isIntersectionPointIncident(Coords P, double eps) {
		return isOnFullConic(P, eps);
	}

	/**
	 * return wheter this conic represents the same conic as c (this = lambda *
	 * c).
	 */
	@Override
	public boolean isEqual(GeoElementND geo) {
		if (!geo.isGeoConic()) {
			return false;
		}

		GeoConicND conic = (GeoConicND) geo;
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

	/**
	 * evaluates P . A . P
	 * 
	 * @param P
	 *            point for the conic to be evaluated at
	 * @return 0 iff P lies on conic
	 */
	final public double evaluate(GeoPoint P) {
		return P.x * (matrix[0] * P.x + matrix[3] * P.y + matrix[4] * P.z)
				+ P.y * (matrix[3] * P.x + matrix[1] * P.y + matrix[5] * P.z)
				+ P.z * (matrix[4] * P.x + matrix[5] * P.y + matrix[2] * P.z);
	}

	/**
	 * evaluates (p.x, p.y, 1) . A . (p.x, p.y, 1)
	 * 
	 * @param pt
	 *            inhomogenous coords of a point
	 * @return 0 iff (p.x, p.y, 1) lies on conic
	 */
	public final double evaluate(GeoVec2D pt) {
		return evaluate(pt.getX(), pt.getY());
	}

	/**
	 * evaluates (x, y, 1) . A . (x, y, 1)
	 * 
	 * @param x
	 *            inhomogenous x-coord of a point
	 * @param y
	 *            inhomogenous y-coord of a point
	 * @return 0 iff (p.x, p.y, 1) lies on conic
	 */
	public final double evaluate(double x, double y) {
		return matrix[2] + matrix[4] * x + matrix[5] * y
				+ y * (matrix[5] + matrix[3] * x + matrix[1] * y)
				+ x * (matrix[4] + matrix[0] * x + matrix[3] * y);
	}

	/**
	 * Sets the GeoLine polar to A.P, the polar line of P relativ to this conic.
	 * 
	 * @param P
	 *            point to which we want the polar
	 * @param polar
	 *            GeoLine in which the result should be stored
	 */
	final public void polarLine(GeoPoint P, GeoLine polar) {
		if (!isDefined()) {
			polar.setUndefined();
		} else {
			polar.x = matrix[0] * P.x + matrix[3] * P.y + matrix[4] * P.z;
			polar.y = matrix[3] * P.x + matrix[1] * P.y + matrix[5] * P.z;
			polar.z = matrix[4] * P.x + matrix[5] * P.y + matrix[2] * P.z;
		}
	}

	/**
	 * Sets the GeoPoint polar to the pole of line
	 * 
	 * @param line
	 *            to which we want the pole
	 * @param polar
	 *            GeoPoint in which the result should be stored
	 */
	final public void polarPoint(GeoLine line, GeoPoint polar) {
		polarPoint(line.x, line.y, line.z, polar);
	}

	/**
	 * @param equation
	 *            line coefficients
	 * @param polar
	 *            output point
	 */
	final public void polarPoint(Coords equation, GeoPoint polar) {
		polarPoint(equation.getX(), equation.getY(), equation.getZ(), polar);
	}

	/**
	 * @param x
	 *            line x coefficient
	 * @param y
	 *            line y coefficient
	 * @param z
	 *            line z coefficient
	 * @param polar
	 *            output point
	 */
	final public void polarPoint(double x, double y, double z, GeoPoint polar) {
		if (!isDefined()) {
			polar.setUndefined();
			return;
		}
		if (polarMatrix == null) {
			polarMatrix = new GgbMat(3, 3);
		}

		polarMatrix.set3x3fromConic(matrix);
		polarMatrix.inverseImmediate();

		if (polarMatrix.isUndefined()) {
			polar.setUndefined();
			return;
		}

		polar.setCoords(
				polarMatrix.getEntry(0, 0) * x + polarMatrix.getEntry(0, 1) * y
						+ polarMatrix.getEntry(0, 2) * z,
				polarMatrix.getEntry(1, 0) * x + polarMatrix.getEntry(1, 1) * y
						+ polarMatrix.getEntry(1, 2) * z,
				polarMatrix.getEntry(2, 0) * x + polarMatrix.getEntry(2, 1) * y
						+ polarMatrix.getEntry(2, 2) * z);

	}

	/**
	 * Sets the GeoLine polar to A.P, the polar line of P relativ to this conic.
	 * 
	 * @param project
	 *            2D projected coords of the point to which we want the polar
	 * @param polar
	 *            GeoLine in which the result should be stored
	 */
	final public void polarLine(Coords project, GeoLine polar) {
		if (!isDefined()) {
			polar.setUndefined();
		} else {
			polar.x = matrix[0] * project.getX() + matrix[3] * project.getY()
					+ matrix[4] * project.getZ();
			polar.y = matrix[3] * project.getX() + matrix[1] * project.getY()
					+ matrix[5] * project.getZ();
			polar.z = matrix[4] * project.getX() + matrix[5] * project.getY()
					+ matrix[2] * project.getZ();
		}
	}

	/**
	 * Sets the GeoLine diameter to X.S.v + a.v (v is a direction), the diameter
	 * line parallel to v relativ to this conic.
	 * 
	 * @param v
	 *            direction of diameter
	 * @param diameter
	 *            GeoLine for storing the result
	 */
	final public void diameterLine(GeoVector v, GeoLine diameter) {
		diameterLine(v.x, v.y, diameter);
	}

	/**
	 * Sets the GeoLine diameter to X.S.v + a.v (v is a direction), the diameter
	 * line parallel to v relativ to this conic.
	 * 
	 * @param vx
	 *            vector x coord
	 * @param vy
	 *            vector y coord
	 * @param diameter
	 *            GeoLine for storing the result
	 */
	final public void diameterLine(double vx, double vy, GeoLine diameter) {
		diameter.x = matrix[0] * vx + matrix[3] * vy;
		diameter.y = matrix[3] * vx + matrix[1] * vy;
		diameter.z = matrix[4] * vx + matrix[5] * vy;
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		// line thickness and type
		getLineStyleXML(sb);

		sb.append("\t<eigenvectors ");
		sb.append(" x0=\"" + eigenvec[0].getX() + "\"");
		sb.append(" y0=\"" + eigenvec[0].getY() + "\"");
		sb.append(" z0=\"1.0\"");
		sb.append(" x1=\"" + eigenvec[1].getX() + "\"");
		sb.append(" y1=\"" + eigenvec[1].getY() + "\"");
		sb.append(" z1=\"1.0\"");
		sb.append("/>\n");

		// matrix must be saved after eigenvectors
		// as only <matrix> will cause a call to classifyConic()
		// see geogebra.io.MyXMLHandler: handleMatrix() and handleEigenvectors()
		sb.append("\t<matrix");
		for (int i = 0; i < 6; i++) {
			sb.append(" A" + i + "=\"" + matrix[i] + "\"");
		}
		sb.append("/>\n");
		XMLBuilder.appendEquationTypeConic(sb, getToStringMode(), parameter);
	}

	@Override
	public String getSpecificEquation() {
		String ret = null;
		switch (type) {
		default:
			// do nothing
			break;
		case GeoConicNDConstants.CONIC_CIRCLE:
			ret = getLoc().getMenu("CircleEquation");
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:
			ret = getLoc().getMenu("EllipseEquation");
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
			ret = getLoc().getMenu("HyperbolaEquation");
			break;

		case GeoConicNDConstants.CONIC_PARABOLA:
			ret = getLoc().getMenu("ParabolaEquation");
			break;

		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
			ret = getLoc().getMenu("DoubleLineEquation");
			break;

		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
			ret = getLoc().getMenu("ConicLinesEquation");
			break;

		case GeoConicNDConstants.CONIC_LINE:
			ret = getLoc().getMenu("DoubleLineEquation");
			break;

		}
		return ret;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	final public boolean isGeoConic() {
		return true;
	}

	@Override
	public void setZero() {
		setCoeffs(1, 0, 1, 0, 0, 0);
	}

	@Override
	protected void setMidpoint(double[] coords) {
		b.setX(coords[0]);
		b.setY(coords[1]);

		// GeoQuadridND compatibility
		super.setMidpoint(coords[0], coords[1]);

	}

	@Override
	protected void setMidpoint(double x, double y) {
		b.setX(x);
		b.setY(y);

		// GeoQuadridND compatibility
		super.setMidpoint(x, y);

	}

	/**
	 * @return :
	 */
	@Override
	public String getAssignmentOperator() {
		return ": ";
	}

	/*
	 * Region interface implementation
	 */

	@Override
	public boolean isRegion() {
		return true;
	}

	@Override
	public boolean isInRegion(GeoPointND PI) {
		Coords coords = PI.getCoordsInD2IfInPlane(getCoordSys());

		if (coords == null) { // point is not in plane containing the polygon
			return false;
		}

		return isInRegion(coords.getX(), coords.getY());

	}

	/**
	 * Returns true if point is in circle/ellipse. Coordinates of PointInterface
	 * are used directly to avoid rounding errors.
	 * 
	 * @author Michael Borcherds
	 * @version 2010-05-17
	 * @param x0
	 *            x-coord
	 * @param y0
	 *            y-coord
	 * @return true if point (x0,y0) is inside the connic
	 */
	@Override
	public boolean isInRegion(double x0, double y0) {
		return DoubleUtil.isGreaterEqual(
				evaluate(x0, y0) / evaluateInSignificantPoint(), 0);

	}

	/**
	 * @return value of this quadratic form in significant point (midpoint,
	 *         focus)
	 */
	public double evaluateInSignificantPoint() {
		switch (type) {
		case CONIC_INTERSECTING_LINES:
			return evaluate(b.getX() + lines[0].x + lines[1].x,
					b.getY() + lines[0].y + lines[1].y);
		case CONIC_HYPERBOLA:
			return -evaluate(b.getX(), b.getY());
		case CONIC_PARABOLA:
			return evaluate(b.getX() + p * eigenvec[0].getX(),
					b.getY() + p * eigenvec[0].getY());
		case CONIC_SINGLE_POINT:
			return evaluate(b.getX() + 1, b.getY());
		default:
			return evaluate(b.getX(), b.getY());
		}
	}

	/**
	 * Point's parameters are set to its EV coordinates
	 * 
	 * @version 2010-07-30
	 * @param PI
	 *            point
	 */
	@Override
	public void pointChangedForRegion(GeoPointND PI) {
		PI.updateCoords2D();

		RegionParameters rp = PI.getRegionParameters();

		Coords coords = PI.getCoordsInD2(getCoordSys());

		if (!isInRegion(coords.getX(), coords.getY())) {
			moveBackToRegion(PI, rp);
		} else {
			rp.setIsOnPath(false);

			coordsRWtoEV(coords);
			if (type != CONIC_PARABOLA) {
				rp.setT1(coords.getX() / this.halfAxes[0]);
				rp.setT2(coords.getY() / this.halfAxes[1]);
			} else {
				rp.setT1(coords.getX());
				rp.setT2(coords.getY() / Math.sqrt(this.p));

			}
			coordsEVtoRW(coords);
		}

		PI.updateCoordsFrom2D(false, null);
	}

	/**
	 * Move a point back to region
	 * 
	 * @param pi
	 *            point
	 * @param rp
	 *            region parameters
	 */
	protected void moveBackToRegion(GeoPointND pi, RegionParameters rp) {
		pointChanged(pi);
		rp.setIsOnPath(true);
	}

	/**
	 * When elipse is moved, the points moves as well and its EV coordinates
	 * remain the same
	 * 
	 * @version 2010-07-30
	 * @param PI
	 *            point
	 */

	@Override
	public void regionChanged(GeoPointND PI) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(PI)
				|| PI.getRegionParameters().isNaN()) {
			pointChangedForRegion(PI);
			return;
		}

		// GeoPoint P = (GeoPoint) PI;
		RegionParameters rp = PI.getRegionParameters();

		if (rp.isOnPath()) {
			pathChanged(PI);
		} else {
			Coords P = new Coords(3);
			if (P.isDefined()) {
				if (type != CONIC_PARABOLA) {
					P.setX(rp.getT1() * halfAxes[0]);
					P.setY(rp.getT2() * halfAxes[1]);
				} else {
					P.setX(rp.getT1());
					P.setY(rp.getT2() * Math.sqrt(this.p));

				}
				P.setZ(1.0);
				coordsEVtoRW(P);
				PI.setCoords2D(P.getX(), P.getY(), P.getZ());
				PI.updateCoordsFrom2D(false, getCoordSys());

			}

			// in some cases (e.g. ellipse becomes an hyperbola), point goes
			// outside
			if (!isInRegion(PI)) {
				moveBackToRegion(PI, rp);
			}
		}
	}

	@Override
	public void toGeoCurveCartesian(GeoCurveCartesianND curve) {
		FunctionVariable fv = new FunctionVariable(kernel, "t");
		ExpressionNode evX = null, evY = null;
		double min = 0, max = 0;
		if (type == CONIC_CIRCLE && curve.getDimension() == 2) {
			evX = new ExpressionNode(kernel,
					new ExpressionNode(kernel, fv, Operation.COS, null),
					Operation.MULTIPLY, new MyDouble(kernel, halfAxes[0]));
			evY = new ExpressionNode(kernel,
					new ExpressionNode(kernel, fv, Operation.SIN, null),
					Operation.MULTIPLY, new MyDouble(kernel, halfAxes[1]));
			ExpressionNode rwX = new ExpressionNode(kernel, evX, Operation.PLUS,
					new MyDouble(kernel, b.getX()));
			ExpressionNode rwY = new ExpressionNode(kernel, evY, Operation.PLUS,
					new MyDouble(kernel, b.getY()));
			curve.setFun(0, new Function(rwX, fv));
			curve.setFun(1, new Function(rwY, fv));
			curve.setInterval(0, 2 * Math.PI);
			if (curve.getDimension() == 3) {
				curve.setFun(2,
						new Function(new ExpressionNode(kernel, 0.0), fv));
			}
			return;

		}
		if (type == CONIC_ELLIPSE
				|| type == CONIC_CIRCLE && curve.getDimension() == 3) {
			evX = new ExpressionNode(kernel,
					new ExpressionNode(kernel, fv, Operation.COS, null),
					Operation.MULTIPLY, new MyDouble(kernel, halfAxes[0]));
			evY = new ExpressionNode(kernel,
					new ExpressionNode(kernel, fv, Operation.SIN, null),
					Operation.MULTIPLY, new MyDouble(kernel, halfAxes[1]));
			min = 0;
			max = 2 * Math.PI;

		} else if (type == CONIC_HYPERBOLA) {
			evX = new ExpressionNode(kernel,
					new ExpressionNode(kernel, fv, Operation.COSH, null),
					Operation.MULTIPLY, new MyDouble(kernel, halfAxes[0]));
			evY = new ExpressionNode(kernel,
					new ExpressionNode(kernel, fv, Operation.SINH, null),
					Operation.MULTIPLY, new MyDouble(kernel, halfAxes[1]));
			min = -2 * Math.PI;
			max = 2 * Math.PI;
		} else if (type == CONIC_PARABOLA) {
			evY = new ExpressionNode(kernel, new ExpressionNode(kernel, fv),
					Operation.MULTIPLY, new MyDouble(kernel, Math.sqrt(2 * p)));
			evX = new ExpressionNode(kernel, fv, Operation.MULTIPLY, fv);
			min = kernel.getXminForFunctions();
			max = kernel.getXmaxForFunctions();
		} else {
			return;
		}

		if (curve.getDimension() == 3) {
			Coords e0 = this.getEigenvec3D(0);
			Coords e1 = this.getEigenvec3D(1);
			Coords m = getMidpoint3D();
			ExpressionNode rwX = linComb(evX, evY, e0.getX(), e1.getX(),
					m.getX());
			ExpressionNode rwY = linComb(evX, evY, e0.getY(), e1.getY(),
					m.getY());
			ExpressionNode rwZ = linComb(evX, evY, e0.getZ(), e1.getZ(),
					m.getZ());
			curve.setFun(0, new Function(rwX, fv));
			curve.setFun(1, new Function(rwY, fv));
			curve.setFun(2, new Function(rwZ, fv));
		} else {
			ExpressionNode rwX = linComb(evX, evY, eigenvec[0].getX(),
					eigenvec[0].getY(), b.getX());
			ExpressionNode rwY = linComb(evX, evY, eigenvec[0].getY(),
					-eigenvec[0].getX(), b.getY());
			curve.setFun(0, new Function(rwX, fv));
			curve.setFun(1, new Function(rwY, fv));
		}
		curve.setInterval(min, max);

	}

	private ExpressionNode linComb(ExpressionNode evX, ExpressionNode evY,
			double cX, double cY, double c1) {
		return new ExpressionNode(kernel,
				new ExpressionNode(kernel,
						new ExpressionNode(kernel, evX, Operation.MULTIPLY,
								new MyDouble(kernel, cX)),
						Operation.PLUS,
						new ExpressionNode(kernel, evY, Operation.MULTIPLY,
								new MyDouble(kernel, cY))),
				Operation.PLUS, new MyDouble(kernel, c1));
	}

	/**
	 * Set implicit curve to this conic
	 * 
	 * @param curve
	 *            Implicit curve to store this conic
	 */
	public void toGeoImplicitCurve(GeoImplicit curve) {
		FunctionVariable v1 = new FunctionVariable(kernel, "x");
		FunctionVariable v2 = new FunctionVariable(kernel, "y");
		MyDouble d = new MyDouble(kernel, 1.0);
		ExpressionNode x = new ExpressionNode(kernel, v1, Operation.MULTIPLY,
				d);
		ExpressionNode y = new ExpressionNode(kernel, v2, Operation.MULTIPLY,
				d);
		ExpressionNode lhs = x.square().multiply(matrix[0])
				.plus(y.square().multiply(matrix[1])).plus(matrix[2])
				.plus(x.multiply(y).multiply(2 * matrix[3]))
				.plus(x.multiply(2 * matrix[4]))
				.plus(y.multiply(2 * matrix[5]));
		ExpressionNode rhs = new ExpressionNode(kernel, 0.0);
		curve.fromEquation(new Equation(kernel, lhs, rhs), getImplicitCoeff());
	}

	private double[][] getImplicitCoeff() {
		double[][] coeff = new double[3][3];
                coeff[0][0] = matrix[2];
                coeff[1][1] = 2 * matrix[3];
                coeff[2][2] = 0;
                coeff[1][0] = 2 * matrix[4];
                coeff[0][1] = 2 * matrix[5];
                coeff[2][0] = matrix[0];
                coeff[0][2] = matrix[1];
                coeff[2][1] = coeff[1][2] = 0;
		return coeff;
	}

	/**
	 * Some ellipses might be circles by accident. This method tells us whether
	 * we can rely on this conic being circle after some points are moved.
	 * 
	 * @return true iff the conic will remain circle after changing parent
	 *         inputs
	 */
	public boolean keepsType() {
		if (getParentAlgorithm() == null) {
			return true;
		}
		if (getParentAlgorithm() instanceof AlgoConicFivePoints) {
			return false;
		}
		if (getParentAlgorithm() instanceof AlgoEllipseHyperbolaFociPoint) {
			return false;
		}
		if (getParentAlgorithm() instanceof AlgoEllipseFociLength) {
			return false;
		}
		return true;
	}

	/**
	 * Calculates the euclidian distance between this GeoConic and GeoPoint P.
	 * used for compound paths
	 */
	@Override
	public double distance(GeoPoint pt) {
		// if (!isCircle()) return Double.POSITIVE_INFINITY;

		/*
		 * double xC = - matrix[4]; double yC = - matrix[5]; double r =
		 * halfAxes[0];
		 * 
		 * double x = p.inhomX; double y = p.inhomY;
		 * 
		 * double d = MyMath.length(x - xC, y - yC); return Math.abs(d - r);
		 */

		GeoPoint closestPoint = new GeoPoint(cons, pt.x, pt.y, pt.z);
		closestPoint.setPath(this);
		pointChanged(closestPoint);

		closestPoint.updateCoords();

		// Application.debug("closest point =
		// "+closestPoint.inhomX+","+closestPoint.inhomY);
		// Application.debug("distance = "+p.distance(closestPoint));

		return pt.distance(closestPoint);

	}

	@Override
	public String getTypeString() {
		switch (type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
			return "Circle";
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
			return "DoubleLine";
		case GeoConicNDConstants.CONIC_ELLIPSE:
			return "Ellipse";
		case GeoConicNDConstants.CONIC_EMPTY:
			return "EmptySet";
		case GeoConicNDConstants.CONIC_HYPERBOLA:
			return "Hyperbola";
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
			return "IntersectingLines";
		case GeoConicNDConstants.CONIC_LINE:
			return "Line";
		case GeoConicNDConstants.CONIC_PARABOLA:
			return "Parabola";
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
			return "ParallelLines";
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			return "Point";

		default:
			return "Conic";
		}
	}

	//////////////////////////
	// AREA
	//////////////////////////

	// for 3D
	/**
	 * set if this is end of a quadric
	 * 
	 * @param flag
	 *            end of quadric
	 */
	public void setIsEndOfQuadric(boolean flag) {
		isEndOfQuadric = flag;
	}

	/**
	 * 
	 * @return true if this is end of a quadric
	 */
	public boolean isEndOfQuadric() {
		return isEndOfQuadric;
	}

	@Override
	public void doRemove() {
		if (pointsOnConic != null) {
			for (int i = 0; i < pointsOnConic.size(); ++i) {
				GeoPointND pt = pointsOnConic.get(i);
				pt.removeIncidence(this);
			}
		}

		super.doRemove();
	}

	//////////////////////////////////////////////
	// HIT STUFF
	//////////////////////////////////////////////

	/**
	 * @param type
	 *            hit type
	 */
	final public void setLastHitType(HitType type) {
		lastHitType = type;
	}

	@Override
	final public HitType getLastHitType() {
		return lastHitType;
	}

	// make sure all Conics go in same group in AlgebraView
	@Override
	public String getTypeStringForAlgebraView() {
		// return getTypeString();
		return "Conic";
	}

	@Override
	public Coords getLabelPosition() {
		if (labelPosition == null) {
			labelPosition = new Coords(0, 0, 1);
			labelParameter = new PathParameter(0);
			labelPosition3D = Coords.createInhomCoorsInD3();
		}

		pathChangedWithoutCheck(labelPosition, labelParameter, true);

		return getCoordSys().getPoint(labelPosition, labelPosition3D);
	}

	// //////////////////////////////////////
	// REGION3D INTERFACE
	// //////////////////////////////////////

	@Override
	public Coords[] getNormalProjection(Coords coords) {
		return getCoordSys().getNormalProjection(coords);
	}

	@Override
	public Coords getPoint(double x2d, double y2d, Coords coords) {
		return getCoordSys().getPoint(x2d, y2d, coords);
	}

	@Override
	public Coords[] getProjection(Coords oldCoords, Coords willingCoords,
			Coords willingDirection) {

		Coords[] result = new Coords[] { new Coords(4), new Coords(4) };
		willingCoords.projectPlaneThruVIfPossible(
				getCoordSys().getMatrixOrthonormal(), oldCoords,
				willingDirection, result[0], result[1]);

		return result;
	}

	@Override
	public int getMetasLength() {
		if (metas == null) {
			return 0;
		}

		return metas.size();
	}

	@Override
	public GeoElement[] getMetas() {
		GeoElement[] ret = new GeoElement[metas.size()];
		metas.toArray(ret);
		return ret;
	}

	/**
	 * add the limited quadric has meta geo for this
	 * 
	 * @param limitedQuadric
	 *            polyhedron
	 */
	public void addMeta(GeoElement limitedQuadric) {
		if (metas == null) {
			metas = new TreeSet<>();
		}

		metas.add(limitedQuadric);
	}

	/**
	 * remove limited quadric as meta for this
	 * 
	 * @param limitedQuadric
	 *            limited quadric
	 */
	public void removeMeta(GeoElement limitedQuadric) {
		if (metas != null) {
			metas.remove(limitedQuadric);
		}
	}

	/**
	 * 
	 * @return midpoint in correct dimension
	 */
	public Coords getMidpointND() {
		return getMidpoint2D();
	}

	/**
	 * @param geo
	 *            input
	 * @return true - if input is congruent with this conic false - otherwise
	 */
	@Override
	public ExtendedBoolean isCongruent(GeoElement geo) {
		if (!geo.isGeoConic()) {
			return ExtendedBoolean.FALSE;
		}
		GeoConicND conic = (GeoConicND) geo;
		// Circles are congruent if their radius are of equal length:
		if (this.isCircle() && conic.isCircle()) {
			return ExtendedBoolean.newExtendedBoolean((DoubleUtil
					.isEqual(this.getCircleRadius(), conic.getCircleRadius())));
		}

		// Two parabolas are congruent if they have the same distance between
		// the focus and directrix:
		if (this.isParabola() && conic.isParabola()) {
			GeoElement[] ge1 = (this.getParentAlgorithm().input);
			GeoElement[] ge2 = (conic.getParentAlgorithm().input);
			if (ge1.length == 2 && ge2.length == 2) {
				// Easy case: definition by focus and directrix:
				GeoPoint F = (GeoPoint) ge1[0];
				GeoLine d = (GeoLine) ge1[1];
				double d1 = getKernel().getAlgoDispatcher()
						.getNewAlgoClosestPoint(cons, d, F).getP().distance(F);

				F = (GeoPoint) ge2[0];
				d = (GeoLine) ge2[1];
				double d2 = getKernel().getAlgoDispatcher()
						.getNewAlgoClosestPoint(cons, d, F).getP().distance(F);
				return ExtendedBoolean
						.newExtendedBoolean(DoubleUtil.isEqual(d1, d2));
			}
			// TODO: Handle the other case(s).
		}

		// Two ellipses are congruent if they have the same distances between
		// the two foci,
		// and also the sum of generatrixes are equal:
		if (this.isEllipse() && conic.isEllipse()) {
			GeoElement[] ge1 = (this.getParentAlgorithm().input);
			GeoElement[] ge2 = (conic.getParentAlgorithm().input);
			if (ge1.length == 3 && ge2.length == 3) {
				// Easy case: definition with 2 foci and one point on the
				// ellipse:
				GeoPoint F1 = (GeoPoint) ge1[0];
				GeoPoint F2 = (GeoPoint) ge1[1];
				GeoPoint P = (GeoPoint) ge1[2];
				double d1 = F1.distance(F2);
				double d2 = F1.distance(P) + F2.distance(P);

				F1 = (GeoPoint) ge2[0];
				F2 = (GeoPoint) ge2[1];
				P = (GeoPoint) ge2[2];
				double d1_ = F1.distance(F2);
				double d2_ = F1.distance(P) + F2.distance(P);

				return ExtendedBoolean.newExtendedBoolean(
						(DoubleUtil.isEqual(d1, d1_)) && (DoubleUtil.isEqual(d2, d2_)));
			}
			// TODO: Handle the other case(s).
		}

		// Two hyperbolas are congruent if they have the same distances between
		// the two foci,
		// and also the absolute value of the subtraction of generatrixes are
		// equal:
		if ((this.isHyperbola() && conic.isHyperbola())
				|| (this.isDoubleLine() && conic.isDoubleLine())) {
			GeoElement[] ge1 = (this.getParentAlgorithm().input);
			GeoElement[] ge2 = (conic.getParentAlgorithm().input);
			if (ge1.length == 3 && ge2.length == 3) {
				// Easy case: definition with 2 foci and one point on the
				// hyperbola:
				GeoPoint F1 = (GeoPoint) ge1[0];
				GeoPoint F2 = (GeoPoint) ge1[1];
				GeoPoint P = (GeoPoint) ge1[2];
				double d1 = F1.distance(F2);
				double d2 = Math.abs(F1.distance(P) - F2.distance(P));

				F1 = (GeoPoint) ge2[0];
				F2 = (GeoPoint) ge2[1];
				P = (GeoPoint) ge2[2];
				double d1_ = F1.distance(F2);
				double d2_ = Math.abs(F1.distance(P) - F2.distance(P));

				return ExtendedBoolean.newExtendedBoolean(
						(DoubleUtil.isEqual(d1, d1_)) && (DoubleUtil.isEqual(d2, d2_)));
				// TODO: Consider unifying this case with the ellipse case.
			}
			// TODO: Handle the other case(s).
		}

		return ExtendedBoolean.FALSE;
	}

	final private void createTmpCoords() {
		if (tmpCoords1 == null) {
			tmpCoords1 = new Coords(3);
			tmpCoords2 = new Coords(3);
		}
	}

	/**
	 * @param tpl
	 *            demplate
	 * @param dim
	 *            dimension
	 * @return parametric description of this conic in given dimension
	 */
	protected StringBuilder buildParametricValueString(StringTemplate tpl,
			int dim) {
		StringBuilder sbBuildValueString = new StringBuilder();
		if (!isDefined()) {
			sbBuildValueString.append("?");
			return sbBuildValueString;
		}
		sbBuildValueString.append("X = ");
		switch (getType()) {
		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
			buildValueStringMidpointConic(false, "cos(", "sin(", tpl,
					sbBuildValueString, dim);
			break;

		case CONIC_HYPERBOLA:
			buildValueStringMidpointConic(true, "cosh(", "sinh(", tpl,
					sbBuildValueString, dim);
			break;

		case CONIC_PARABOLA:
			buildValueString(false, parameter + "\u00b2", parameter,
					linearEccentricity, 2 * linearEccentricity, tpl,
					sbBuildValueString, dim);
			break;

		case CONIC_SINGLE_POINT:
			Coords center = getMidpoint3D();
			GeoPoint.buildValueStringCoordCartesian3D(kernel, tpl,
					center.getX(), center.getY(), center.getZ(),
					sbBuildValueString);
			break;

		case CONIC_INTERSECTING_LINES:
			center = getMidpoint3D();
			Coords d1 = getDirection3D(0);
			Coords d2 = getDirection3D(1);
			createTmpCoords();
			tmpCoords1.setAdd(d1, d2).mulInside(0.5);
			tmpCoords2.setSub(d2, d1).mulInside(0.5);
			tmpCoords2.checkReverseForFirstValuePositive();
			sbBuildValueString.append("(");
			sbBuildValueString.append(kernel.format(center.getX(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(center.getY(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(center.getZ(), tpl));
			sbBuildValueString.append(") + ");
			sbBuildValueString.append(Unicode.lambda);
			sbBuildValueString.append(" (");
			kernel.appendTwoCoeffs(tmpCoords1.getX(), tmpCoords2.getX(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(", ");
			kernel.appendTwoCoeffs(tmpCoords1.getY(), tmpCoords2.getY(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(", ");
			kernel.appendTwoCoeffs(tmpCoords1.getZ(), tmpCoords2.getZ(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(")");
			break;

		case CONIC_PARALLEL_LINES:
			Coords c1 = getOrigin3D(0);
			Coords c2 = getOrigin3D(1);
			createTmpCoords();
			tmpCoords1.setAdd(c1, c2).mulInside(0.5);
			tmpCoords2.setSub(c2, c1).mulInside(0.5);
			tmpCoords2.checkReverseForFirstValuePositive();
			sbBuildValueString.append("(");
			kernel.appendTwoCoeffs(tmpCoords1.getX(), tmpCoords2.getX(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(", ");
			kernel.appendTwoCoeffs(tmpCoords1.getY(), tmpCoords2.getY(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(", ");
			kernel.appendTwoCoeffs(tmpCoords1.getZ(), tmpCoords2.getZ(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(") + ");
			sbBuildValueString.append(Unicode.lambda);

			Coords d = getDirection3D(0);
			sbBuildValueString.append(" (");
			sbBuildValueString.append(kernel.format(d.getX(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(d.getY(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(d.getZ(), tpl));
			sbBuildValueString.append(")");
			break;

		case CONIC_DOUBLE_LINE:
			center = getMidpoint3D();
			d = getDirection3D(0);
			sbBuildValueString.append("(");
			sbBuildValueString.append(kernel.format(center.getX(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(center.getY(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(center.getZ(), tpl));
			sbBuildValueString.append(") + ");
			sbBuildValueString.append(Unicode.lambda);
			sbBuildValueString.append(" (");
			sbBuildValueString.append(kernel.format(d.getX(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(d.getY(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(d.getZ(), tpl));
			sbBuildValueString.append(")");
			break;

		case CONIC_EMPTY:
			sbBuildValueString.append("?");
			break;

		default:
			Log.debug("unknown conic type");
			Log.debug(this.coeffs);
			sbBuildValueString.append("?");
			break;
		}

		return sbBuildValueString;
	}

	private void buildValueStringMidpointConic(boolean plusMinusX, String s1,
			String s2, StringTemplate tpl, StringBuilder sbBuildValueString,
			int dim) {
		buildValueString(plusMinusX, s1 + parameter + ")", s2 + parameter + ")",
				getHalfAxis(0), getHalfAxis(1), tpl, sbBuildValueString, dim);
	}

	private void buildValueString(boolean plusMinusX, String s1, String s2,
			double r1, double r2, StringTemplate tpl,
			StringBuilder sbBuildValueString, int dim) {
		if (dim < 3) {
			Coords center = getMidpoint();
			GeoPoint.buildValueString(kernel, tpl, Kernel.COORD_CARTESIAN,
					center.getX(), center.getY(), sbBuildValueString);
		} else {
			Coords center = getMidpoint3D();
			GeoPoint.buildValueStringCoordCartesian3D(kernel, tpl,
					center.getX(), center.getY(), center.getZ(),
					sbBuildValueString);
		}

		Coords ev1 = getEigenvec3D(0);
		Coords ev2 = getEigenvec3D(1);

		String separator = GeoPoint.buildValueStringSeparator(kernel, tpl);

		sbBuildValueString.append(" + (");

		kernel.appendTwoCoeffs(plusMinusX, r1 * ev1.getX(), r2 * ev2.getX(), s1,
				s2, tpl, sbBuildValueString);

		sbBuildValueString.append(separator);
		sbBuildValueString.append(" ");

		kernel.appendTwoCoeffs(plusMinusX, r1 * ev1.getY(), r2 * ev2.getY(), s1,
				s2, tpl, sbBuildValueString);
		if (dim > 2) {
			sbBuildValueString.append(separator);
			sbBuildValueString.append(" ");

			kernel.appendTwoCoeffs(plusMinusX, r1 * ev1.getZ(), r2 * ev2.getZ(),
					s1, s2, tpl, sbBuildValueString);
		}
		sbBuildValueString.append(')');
	}

	/**
	 * @return circumfernece assuming this is an ellipse
	 */
	public double getEllipseCircumference() {
		return EllipticArcLength.getEllipseCircumference(halfAxes[0],
				halfAxes[1]);
	}

	/**
	 * @param param
	 *            parameter name
	 */
	public void toParametric(String param) {
		this.toStringMode = GeoConicND.EQUATION_PARAMETRIC;
		this.parameter = param;
	}

	@Override
	public boolean isShape() {
		return isShape;
	}

	@Override
	public void setIsShape(boolean isShape) {
		this.isShape = isShape;
	}

	private void setModeIfEquationFormIsNotForced(int mode) {
		if (isEquationFormEnforced()) {
			toStringMode = cons.getApplication().getConfig().getEnforcedConicEquationForm();
		} else {
			toStringMode = mode;
		}
	}

	private boolean isEquationFormEnforced() {
		if (cons.getApplication().getConfig().getEnforcedConicEquationForm() == -1) {
			return false;
		} else {
			return true;
		}
	}

	private void setModeWithImplicitEquationAsDefault(int mode) {
		switch (mode) {
			case EQUATION_SPECIFIC:
			case EQUATION_EXPLICIT:
			case EQUATION_USER:
			case EQUATION_PARAMETRIC:
			case EQUATION_VERTEX:
			case EQUATION_CONICFORM:
				toStringMode = mode;
				break;

			default:
				toStringMode = EQUATION_IMPLICIT;
		}
	}

}
