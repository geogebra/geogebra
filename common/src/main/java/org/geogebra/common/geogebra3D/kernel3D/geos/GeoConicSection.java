package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicSectionInterface;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 * Partial conic for section of (limited) cylinders and cones
 * 
 * @author mathieu
 *
 */
public class GeoConicSection extends GeoConic3D
		implements GeoConicSectionInterface {

	private double[] paramStart;
	private double[] paramEnd;
	private double[] paramExtent;

	private double[] edgeStartX;
	private double[] edgeStartY;
	private double[] edgeEndX;
	private double[] edgeEndY;
	private double[] edgeStartParam;
	private double[] edgeEndParam;

	private boolean[] edgeExists;
	private TreeSet<IndexedParameter> parametersTree = new TreeSet<>();
	private IndexedParameter[] parametersArray = new IndexedParameter[4];

	/**
	 * @param c
	 *            construction
	 * @param isIntersection
	 *            says if this is an intersection curve
	 */
	public GeoConicSection(Construction c, boolean isIntersection) {
		super(c, isIntersection);

		paramStart = new double[2];
		paramEnd = new double[2];
		paramExtent = new double[2];
		edgeStartX = new double[2];
		edgeStartY = new double[2];
		edgeEndX = new double[2];
		edgeEndY = new double[2];
		edgeStartParam = new double[2];
		edgeEndParam = new double[2];
		edgeExists = new boolean[2];

	}

	private static class IndexedParameter
			implements Comparable<IndexedParameter> {
		protected double value;
		protected int index;

		public IndexedParameter(double value, int index) {
			this.value = value;
			this.index = index;
		}

		@Override
		public int compareTo(IndexedParameter parameter) {

			// NaN are the greatest
			if (Double.isNaN(value)) {
				return 1;
			}

			if (Double.isNaN(parameter.value)) {
				return -1;
			}

			// compare values
			if (DoubleUtil.isGreater(parameter.value, value)) {
				return -1;
			}

			if (DoubleUtil.isGreater(value, parameter.value)) {
				return 1;
			}

			// compare indices
			if (index < parameter.index) {
				return -1;
			}

			return 1; // never return 0 to ensure having four parameters
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof IndexedParameter) {
				return compareTo((IndexedParameter) o) == 0;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return index + 43 * DoubleUtil.hashCode(value);
		}

	}

	/**
	 * set parameters for "segments holes" regarding the index
	 * 
	 * @param bottom0
	 *            first parameter for bottom
	 * @param bottom1
	 *            second parameter for bottom
	 * @param top0
	 *            first parameter for top
	 * @param top1
	 *            second parameter for top
	 */
	final public void setParameters(double bottom0, double bottom1, double top0,
			double top1) {

		// restart edges
		for (int i = 0; i < 2; i++) {
			edgeExists[i] = false;
		}

		// handle conic types
		switch (type) {
		default:
			// do nothing
			break;
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:

			parametersTree.clear();
			parametersTree.add(new IndexedParameter(bottom0, 1));
			parametersTree.add(new IndexedParameter(bottom1, 1));
			parametersTree.add(new IndexedParameter(top0, 2));
			parametersTree.add(new IndexedParameter(top1, 2));
			parametersTree.toArray(parametersArray);

			double start1, end1, start2, end2;
			if (parametersArray[0].index == parametersArray[1].index) {
				start1 = parametersArray[0].value;
				end1 = parametersArray[1].value;
				start2 = parametersArray[2].value;
				end2 = parametersArray[3].value;
			} else {
				start1 = parametersArray[1].value;
				end1 = parametersArray[2].value;
				start2 = parametersArray[3].value;
				end2 = parametersArray[0].value;
			}

			// Log.debug(start1+","+end1+","+start2+","+end2);

			// if no parameter for second hole (NaN), set second parameter to
			// NaN
			if (start2 == end2) {
				start2 = start1;
				end2 = Double.NaN;
				start1 = Double.NaN;
			} else if (start1 == end1) {
				end1 = end2;
				end2 = Double.NaN;
				start1 = Double.NaN;
			}

			// set parameters
			paramStart[0] = DoubleUtil.convertToAngleValue(end1);
			paramEnd[0] = DoubleUtil.convertToAngleValue(start2);
			paramExtent[0] = paramEnd[0] - paramStart[0];
			if (paramExtent[0] < 0) {
				paramExtent[0] += Kernel.PI_2;
			}

			paramStart[1] = DoubleUtil.convertToAngleValue(end2);
			paramEnd[1] = DoubleUtil.convertToAngleValue(start1);
			paramExtent[1] = paramEnd[1] - paramStart[1];
			if (paramExtent[1] < 0) {
				paramExtent[1] += Kernel.PI_2;
			}

			// set edges
			if (!Double.isNaN(paramStart[0])) { // at least one edge
				double x0 = getEigenvec(0).getX() * getHalfAxis(0);
				double y0 = getEigenvec(0).getY() * getHalfAxis(0);
				double x1 = getEigenvec(1).getX() * getHalfAxis(1);
				double y1 = getEigenvec(1).getY() * getHalfAxis(1);
				if (Double.isNaN(paramStart[1])) { // only one edge
					edgeEndX[0] = b.getX() + x0 * Math.cos(paramStart[0])
							+ x1 * Math.sin(paramStart[0]);
					edgeEndY[0] = b.getY() + y0 * Math.cos(paramStart[0])
							+ y1 * Math.sin(paramStart[0]);
					edgeEndParam[0] = paramStart[0];
					edgeStartX[0] = b.getX() + x0 * Math.cos(paramEnd[0])
							+ x1 * Math.sin(paramEnd[0]);
					edgeStartY[0] = b.getY() + y0 * Math.cos(paramEnd[0])
							+ y1 * Math.sin(paramEnd[0]);
					edgeStartParam[0] = paramEnd[0];
					if (edgeStartParam[0] > edgeEndParam[0]) {
						edgeStartParam[0] -= 2 * Math.PI;
					}
					edgeExists[0] = true;
				} else {
					edgeEndX[0] = b.getX() + x0 * Math.cos(paramStart[0])
							+ x1 * Math.sin(paramStart[0]);
					edgeEndY[0] = b.getY() + y0 * Math.cos(paramStart[0])
							+ y1 * Math.sin(paramStart[0]);
					edgeEndParam[0] = paramStart[0];
					edgeStartX[0] = b.getX() + x0 * Math.cos(paramEnd[1])
							+ x1 * Math.sin(paramEnd[1]);
					edgeStartY[0] = b.getY() + y0 * Math.cos(paramEnd[1])
							+ y1 * Math.sin(paramEnd[1]);
					edgeStartParam[0] = paramEnd[1];
					if (edgeStartParam[0] > edgeEndParam[0]) {
						edgeStartParam[0] -= 2 * Math.PI;
					}
					edgeExists[0] = true;

					edgeEndX[1] = b.getX() + x0 * Math.cos(paramStart[1])
							+ x1 * Math.sin(paramStart[1]);
					edgeEndY[1] = b.getY() + y0 * Math.cos(paramStart[1])
							+ y1 * Math.sin(paramStart[1]);
					edgeEndParam[1] = paramStart[1];
					edgeStartX[1] = b.getX() + x0 * Math.cos(paramEnd[0])
							+ x1 * Math.sin(paramEnd[0]);
					edgeStartY[1] = b.getY() + y0 * Math.cos(paramEnd[0])
							+ y1 * Math.sin(paramEnd[0]);
					edgeStartParam[1] = paramEnd[0];
					if (edgeStartParam[1] > edgeEndParam[1]) {
						edgeStartParam[1] -= 2 * Math.PI;
					}
					edgeExists[1] = true;
				}
			}

			break;

		case CONIC_INTERSECTING_LINES:
		case CONIC_PARALLEL_LINES:

			if (bottom0 < bottom1) {
				start1 = bottom0;
				start2 = bottom1;
			} else {
				start1 = bottom1;
				start2 = bottom0;
			}

			if (top0 < top1) {
				end1 = top0;
				end2 = top1;
			} else {
				end1 = top1;
				end2 = top0;
			}

			paramStart[0] = PathNormalizer.infFunction(start1);
			paramEnd[0] = PathNormalizer.infFunction(end1);

			paramStart[1] = PathNormalizer.infFunction(start2 - 2);
			paramEnd[1] = PathNormalizer.infFunction(end2 - 2);

			break;

		case CONIC_DOUBLE_LINE:
			paramStart[0] = bottom0;
			paramEnd[0] = top0;

			break;

		case CONIC_HYPERBOLA:

			paramStart[0] = Double.NaN;
			paramEnd[0] = Double.NaN;
			paramStart[1] = Double.NaN;
			paramEnd[1] = Double.NaN;

			setInfParameter(paramStart, bottom0);
			setInfParameter(paramStart, top0);
			setInfParameter(paramEnd, bottom1);
			setInfParameter(paramEnd, top1);

			sortParameters();

			// set edges
			for (int i = 0; i < 2; i++) {
				if (!Double.isNaN(paramStart[i])) {
					double s = paramEnd[i];
					double x = (1 - 2 * i) * halfAxes[0] * MyMath.cosh(s);
					double y = halfAxes[1] * MyMath.sinh(s);
					double x1 = b.getX() + x * getEigenvec(0).getX()
							+ y * getEigenvec(1).getX();
					double y1 = b.getY() + x * getEigenvec(0).getY()
							+ y * getEigenvec(1).getY();

					s = paramStart[i];
					x = (1 - 2 * i) * halfAxes[0] * MyMath.cosh(s);
					y = halfAxes[1] * MyMath.sinh(s);
					double x2 = b.getX() + x * getEigenvec(0).getX()
							+ y * getEigenvec(1).getX();
					double y2 = b.getY() + x * getEigenvec(0).getY()
							+ y * getEigenvec(1).getY();

					if (i == 0) {
						edgeStartX[i] = x2;
						edgeStartY[i] = y2;
						edgeStartParam[i] = PathNormalizer
								.inverseInfFunction(paramStart[i]);
						edgeEndX[i] = x1;
						edgeEndY[i] = y1;
						edgeEndParam[i] = PathNormalizer
								.inverseInfFunction(paramEnd[i]);
					} else {
						edgeStartX[i] = x1;
						edgeStartY[i] = y1;
						edgeStartParam[i] = PathNormalizer
								.inverseInfFunction(paramEnd[i]) + 2;
						edgeEndX[i] = x2;
						edgeEndY[i] = y2;
						edgeEndParam[i] = PathNormalizer
								.inverseInfFunction(paramStart[i]) + 2;
					}

					edgeExists[i] = true;

				} else { // prevent second branch
					double x1 = b.getX() - getEigenvec(1).getX();
					double y1 = b.getY() - getEigenvec(1).getY();
					double x2 = b.getX() + getEigenvec(1).getX();
					double y2 = b.getY() + getEigenvec(1).getY();
					if (i == 0) {
						edgeStartX[i] = x1;
						edgeStartY[i] = y1;
						edgeEndX[i] = x2;
						edgeEndY[i] = y2;
					} else {
						edgeStartX[i] = x2;
						edgeStartY[i] = y2;
						edgeEndX[i] = x1;
						edgeEndY[i] = y1;
					}
				}
			}

			break;

		case CONIC_PARABOLA:
			if (bottom0 < bottom1) {
				paramStart[0] = bottom0;
				paramEnd[0] = bottom1;
			} else {
				paramStart[0] = bottom1;
				paramEnd[0] = bottom0;
			}

			// set edges
			double y = bottom0 * p;
			double x = y * bottom0 / 2.0;
			edgeEndX[0] = b.getX() + x * getEigenvec(0).getX()
					+ y * getEigenvec(1).getX();
			edgeEndY[0] = b.getY() + x * getEigenvec(0).getY()
					+ y * getEigenvec(1).getY();
			edgeEndParam[0] = bottom0;

			y = bottom1 * p;
			x = y * bottom1 / 2.0;
			edgeStartX[0] = b.getX() + x * getEigenvec(0).getX()
					+ y * getEigenvec(1).getX();
			edgeStartY[0] = b.getY() + x * getEigenvec(0).getY()
					+ y * getEigenvec(1).getY();
			edgeStartParam[0] = bottom1;

			edgeExists[0] = true;

			break;

		}

		// Log.debug(getType()+":"+paramStart[0]+","+paramEnd[0]+","+paramStart[1]+","+paramEnd[1]);
	}

	private void sortParameters() {
		for (int i = 0; i < 2; i++) {
			if (DoubleUtil.isZero(paramStart[i])) {
				paramStart[i] = 0;
			}
			if (DoubleUtil.isZero(paramEnd[i])) {
				paramEnd[i] = 0;
			}
			// if (Math.abs(paramStart[i])>Math.abs(paramEnd[i])){
			if (paramStart[i] > paramEnd[i]) {
				double tmp = paramStart[i];
				paramStart[i] = paramEnd[i];
				paramEnd[i] = tmp;
			}
		}
	}

	/**
	 * set the value to the correct branch, converted from [-1,1] (or [1,3]) to
	 * -inf, +inf
	 * 
	 * @param param
	 *            output array of branch parameters
	 * @param value
	 *            number from [-1,1] (or [1,3])
	 */
	private static void setInfParameter(double[] param, double value) {
		if (Double.isNaN(value)) {
			return;
		}

		if (value < 1) {
			param[0] = PathNormalizer.infFunction(value);
		} else {
			param[1] = PathNormalizer.infFunction(value - 2);
		}
	}

	/**
	 * @param index
	 *            index of the hole
	 * @return start parameter
	 */
	@Override
	final public double getParameterStart(int index) {
		return paramStart[index];
	}

	/**
	 * @param index
	 *            index of the hole
	 * @return end parameter
	 */
	@Override
	final public double getParameterEnd(int index) {
		return paramEnd[index];
	}

	/**
	 * @param index
	 *            index of the hole
	 * @return end parameter - start parameter
	 */
	@Override
	final public double getParameterExtent(int index) {
		return paramExtent[index];
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CONICSECTION;
	}

	@Override
	public boolean isInRegion(double x0, double y0) {

		if (!super.isInRegion(x0, y0)) {
			return false;
		}

		return isInsideEdges(x0, y0);

	}

	private boolean isInsideEdges(double x0, double y0) {
		for (int i = 0; i < 2; i++) {
			if (edgeExists[i] || type == GeoConicNDConstants.CONIC_HYPERBOLA) {
				if ((edgeStartX[i] - x0) * (edgeEndY[i] - y0)
						- (edgeEndX[i] - x0) * (edgeStartY[i] - y0) < 0) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void pointChanged(Coords P, PathParameter pp, boolean checkSection) {

		if (checkSection) {
			double xOld = P.getX() / P.getZ();
			double yOld = P.getY() / P.getZ();
			double distance = Double.POSITIVE_INFINITY;

			// calc point on conic and check it
			super.pointChanged(P, pp, checkSection);

			if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
				if (edgeExists[0]) {
					if (pp.t > 1) { // wrong branch: force correct branch apex
						pp.t = 0;
						P.setX(getHalfAxis(0));
						P.setY(0);
						P.setZ(1.0);
						coordsEVtoRW(P);
					}
				} else {
					if (pp.t < 1) { // wrong branch: force correct branch apex
						pp.t = 2;
						P.setX(-getHalfAxis(0));
						P.setY(0);
						P.setZ(1.0);
						coordsEVtoRW(P);
					}
				}
			}

			P.setInhomCoords();
			if (isInsideEdges(P.getX(), P.getY())) {
				double dx = P.getX() - xOld;
				double dy = P.getY() - yOld;
				distance = dx * dx + dy * dy;
			}

			// calc points on edges
			for (int i = 0; i < 2; i++) {
				if (edgeExists[i]) {
					double parameter = getParameterOnSegment(xOld, yOld,
							edgeStartX[i], edgeStartY[i], edgeEndX[i],
							edgeEndY[i]);
					double x = edgeStartX[i] * (1 - parameter)
							+ edgeEndX[i] * parameter;
					double y = edgeStartY[i] * (1 - parameter)
							+ edgeEndY[i] * parameter;
					double dx = x - xOld;
					double dy = y - yOld;
					double d = dx * dx + dy * dy;
					if (d < distance) {
						distance = d;
						P.setX(x);
						P.setY(y);
						P.setZ(1);
						switch (type) {
						default:
							pp.t = Double.NaN;
							break;
						case GeoConicNDConstants.CONIC_CIRCLE:
						case GeoConicNDConstants.CONIC_ELLIPSE:
							// we map the [0,1] parameter to edge parameters
							pp.t = edgeStartParam[i] * (1 - parameter)
									+ edgeEndParam[i] * parameter;
							if (pp.t > Math.PI) {
								pp.t -= Kernel.PI_2;
							}
							break;
						case GeoConicNDConstants.CONIC_PARABOLA:
							// we add edge parameter to start parameter
							if (edgeStartParam[0] < edgeEndParam[0]) {
								parameter = -parameter;
							}
							pp.t = edgeStartParam[0] + parameter;
							break;
						case GeoConicNDConstants.CONIC_HYPERBOLA:
							pp.t = edgeEndParam[i] * parameter
									+ (1 - parameter);
							break;
						}
					}
				}
			}
		} else {
			// calc point on conic and check it
			super.pointChanged(P, pp, checkSection);
		}

	}

	private static double getParameterOnSegment(double x, double y,
			double startX, double startY, double endX, double endY) {
		double dx = endX - startX;
		double dy = endY - startY;
		double parameter = ((x - startX) * (endX - startX)
				+ (y - startY) * (endY - startY)) / (dx * dx + dy * dy);
		if (parameter < 0) {
			return 0;
		}
		if (parameter > 1) {
			return 1;
		}
		return parameter;
	}

	@Override
	protected void pathChangedWithoutCheckEllipse(Coords P, PathParameter pp,
			boolean checkSection) {

		if (checkSection) {
			for (int i = 0; i < 2; i++) {
				if (edgeExists[i]) {
					// get parameter in [-pi,pi]
					double parameter = pp.t % Kernel.PI_2;
					if (parameter > Math.PI) {
						parameter -= Kernel.PI_2;
					}

					// check if in edge
					boolean inEdge = false;
					if (edgeStartParam[i] > Math.PI) {
						parameter += Kernel.PI_2;
						inEdge = parameter >= edgeStartParam[i]
								&& parameter <= edgeEndParam[i];
					} else if (edgeEndParam[i] > Math.PI) {
						if (parameter >= edgeStartParam[i]) {
							inEdge = true;
						} else {
							parameter += Kernel.PI_2;
							inEdge = parameter <= edgeEndParam[i];
						}
					} else {
						inEdge = parameter >= edgeStartParam[i]
								&& parameter <= edgeEndParam[i];
					}

					if (inEdge) {
						double a = (parameter - edgeStartParam[i])
								/ (edgeEndParam[i] - edgeStartParam[i]);
						P.setX(edgeStartX[i] * (1 - a) + edgeEndX[i] * a);
						P.setY(edgeStartY[i] * (1 - a) + edgeEndY[i] * a);
						P.setZ(1);
						return;
					}

				}
			}
		}

		super.pathChangedWithoutCheckEllipse(P, pp, checkSection);
	}

	@Override
	protected void pathChangedWithoutCheckParabola(Coords P, PathParameter pp,
			boolean checkSection) {

		if (checkSection) {
			if (edgeExists[0]) {
				if (edgeStartParam[0] < edgeEndParam[0]) {
					if (pp.t < edgeStartParam[0]) {
						double a = -pp.t + edgeStartParam[0];
						if (a < 1) {
							P.setX(edgeStartX[0] * (1 - a) + edgeEndX[0] * a);
							P.setY(edgeStartY[0] * (1 - a) + edgeEndY[0] * a);
						} else { // prevent outside of edge when path changes
							P.setX(edgeEndX[0]);
							P.setY(edgeEndY[0]);
						}
						P.setZ(1);
						return;
					} else if (pp.t > edgeEndParam[0]) {
						P.setX(edgeEndX[0]);
						P.setY(edgeEndY[0]);
						P.setZ(1);
						return;
					}
				} else {
					if (pp.t > edgeStartParam[0]) {
						double a = pp.t - edgeStartParam[0];
						if (a < 1) {
							P.setX(edgeStartX[0] * (1 - a) + edgeEndX[0] * a);
							P.setY(edgeStartY[0] * (1 - a) + edgeEndY[0] * a);
						} else { // prevent outside of edge when path changes
							P.setX(edgeEndX[0]);
							P.setY(edgeEndY[0]);
						}
						P.setZ(1);
						return;
					} else if (pp.t < edgeEndParam[0]) {
						P.setX(edgeEndX[0]);
						P.setY(edgeEndY[0]);
						P.setZ(1);
						return;
					}
				}
			}
		}

		super.pathChangedWithoutCheckParabola(P, pp, checkSection);
	}

	@Override
	protected void pathChangedWithoutCheckHyperbola(Coords P, PathParameter pp,
			boolean checkSection) {

		double oldParameter = pp.t;

		if (checkSection) {

			// reverse branch if needed
			int i;
			if (pp.t < 1) {
				if (edgeExists[0]) {
					i = 0;
				} else {
					i = 1;
					pp.t = 2 - pp.t;
				}
			} else {
				if (edgeExists[1]) {
					i = 1;
				} else {
					i = 0;
					pp.t = 2 - pp.t;
				}
			}

			if (i == 0 ^ pp.t < edgeEndParam[i]) {
				double a = (pp.t - edgeEndParam[i]) / (1 - edgeEndParam[i]); // pp.t
																				// is
																				// from
																				// edgeEndParam[i]
																				// to
																				// 1
				if (a < 1) {
					P.setX(edgeStartX[i] * a + edgeEndX[i] * (1 - a));
					P.setY(edgeStartY[i] * a + edgeEndY[i] * (1 - a));
				} else { // prevent outside of edge when path changes
					P.setX(edgeEndX[i]);
					P.setY(edgeEndY[i]);
				}
				P.setZ(1);
				return;
			}

		}

		super.pathChangedWithoutCheckHyperbola(P, pp, checkSection);

		if (checkSection) {
			pp.t = oldParameter;
		}
	}

}
