package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoAngleConic;
import org.geogebra.common.kernel.algos.AlgoAngleNumeric;
import org.geogebra.common.kernel.algos.AlgoAngleVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Angle[ number ] Angle[ &lt;GeoPolygon&gt; ]
 * 
 * Angle[ &lt;GeoConic&gt; ] Angle[ &lt;GeoVector&gt; ]
 * 
 * Angle[ &lt;GeoPoint&gt; ] Angle[ &lt;GeoVector&gt;, &lt;GeoVector&gt; ]
 * 
 * Angle[ &lt;GeoLine&gt;, &lt;GeoLine&gt; ]
 * 
 * Angle[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 * 
 * Angle[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt;, &lt;Number&gt; ]
 */
public class CmdAngle extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAngle(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];

		return process(c, n, ok);
	}

	/**
	 * 
	 * @param c
	 *            command
	 * @param n
	 *            arguments length
	 * @param ok
	 *            ok check
	 * @return result
	 * @throws MyError
	 *             argument / length error
	 */
	protected GeoElement[] process(Command c, int n, boolean[] ok)
			throws MyError {

		GeoElement[] arg;

		switch (n) {
		// Angle[ constant number ]
		case 1:
			arg = resArgs(c);

			// wrap angle as angle (needed to avoid ambiguities between numbers
			// and angles in XML)
			if (arg[0].isGeoAngle()) {
				// maybe we have to set a label here
				if (!cons.isSuppressLabelsActive() && !arg[0].isLabelSet()) {
					arg[0].setLabel(c.getLabel());

					// make sure that arg[0] is in construction list
					if (arg[0].isIndependent()) {
						cons.addToConstructionList(arg[0], true);
					} else {
						cons.addToConstructionList(arg[0].getParentAlgorithm(),
								true);
					}
				}
				GeoElement[] ret = { arg[0] };
				return ret;
			}
			// angle from number
			else if (arg[0].isGeoNumeric()) {

				AlgoAngleNumeric algo = new AlgoAngleNumeric(cons, c.getLabel(),
						(GeoNumeric) arg[0]);

				GeoElement[] ret = { algo.getAngle() };
				return ret;
			}
			// angle from number
			else if (arg[0].isGeoPoint() || arg[0].isGeoVector()) {

				return anglePointOrVector(c.getLabel(), arg[0]);
			}
			// angle of conic or polygon
			else {
				if (arg[0].isGeoConic()) {
					return angle(c.getLabel(), (GeoConicND) arg[0]);
				} else if (arg[0].isGeoPolygon()) {
					return angle(c.getLabels(), (GeoPolygon) arg[0]);
				}
			}

			throw argErr(c, arg[0]);

		case 2:
			arg = resArgs(c);

			GeoElement[] ret = process2(c, arg, ok);

			if (ret != null) {
				return ret;
			}

			// syntax error
			if (ok[0] && !ok[1]) {
				throw argErr(c, arg[1]);
			}
			throw argErr(c, arg[0]);

		case 3:
			arg = resArgs(c);

			ret = process3(c, arg, ok);

			if (ret != null) {
				return ret;
			}

			// syntax error
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * process angle when 2 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 */
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok) {

		GeoElement arg0 = arg[0];
		GeoElement arg1 = arg[1];

		if (arg0.isGeoPoint()) {
			arg0 = kernel.wrapInVector((GeoPointND) arg0);
		}

		if (arg1.isGeoPoint()) {
			arg1 = kernel.wrapInVector((GeoPointND) arg1);
		}

		// angle between vectors
		if ((ok[0] = (arg0.isGeoVector())) && (ok[1] = (arg1.isGeoVector()))) {
			return angle(c.getLabel(), (GeoVectorND) arg0, (GeoVectorND) arg1);
		}

		// angle between lines
		if ((ok[0] = (arg[0].isGeoLine())) && (ok[1] = (arg[1].isGeoLine()))) {
			return angle(c.getLabel(), (GeoLineND) arg[0], (GeoLineND) arg[1]);
		}

		return null;
	}

	/**
	 * process angle when 3 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 */
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok) {

		// angle between three points
		if ((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1].isGeoPoint()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			return angle(c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
					(GeoPointND) arg[2]);
		}

		// fixed angle
		if ((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1].isGeoPoint()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			return angle(c.getLabels(), (GeoPointND) arg[0],
					(GeoPointND) arg[1], (GeoNumberValue) arg[2]);
		}

		return null;
	}

	/**
	 * fixed angle
	 * 
	 * @param labels
	 *            labels
	 * @param p1
	 *            point to rotate
	 * @param p2
	 *            center
	 * @param a
	 *            angle
	 * @return angle and rotated point
	 */
	protected GeoElement[] angle(String[] labels, GeoPointND p1, GeoPointND p2,
			GeoNumberValue a) {
		return getAlgoDispatcher().angle(labels, (GeoPoint) p1, (GeoPoint) p2,
				a, true);
	}

	/**
	 * @param label
	 *            label
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @param p3
	 *            third point
	 * @return angle between 3 points
	 */
	protected GeoElement[] angle(String label, GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		GeoElement[] ret = { getAlgoDispatcher().angle(label, (GeoPoint) p1,
				(GeoPoint) p2, (GeoPoint) p3) };
		return ret;
	}

	/**
	 * @param label
	 *            label
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 * @return angle between lines
	 */
	protected GeoElement[] angle(String label, GeoLineND g, GeoLineND h) {
		GeoElement[] ret = {
				getAlgoDispatcher().angle(label, (GeoLine) g, (GeoLine) h) };
		return ret;
	}

	/**
	 * @param label
	 *            label
	 * @param v
	 *            first vector
	 * @param w
	 *            second vector
	 * @return angle between vectors
	 */
	protected GeoElement[] angle(String label, GeoVectorND v, GeoVectorND w) {
		GeoElement[] ret = { getAlgoDispatcher().angle(label, (GeoVector) v,
				(GeoVector) w) };
		return ret;
	}

	/**
	 * @param label
	 *            label
	 * @param v
	 *            vector or point
	 * @return angle between Ox and vector/point
	 */
	protected GeoElement[] anglePointOrVector(String label, GeoElement v) {
		AlgoAngleVector algo = new AlgoAngleVector(cons, (GeoVec3D) v);
		GeoElement[] ret = { algo.getAngle() };
		ret[0].setLabel(label);
		return ret;
	}

	/**
	 * @param label
	 *            label
	 * @param c
	 *            conic
	 * @return angle between Ox and conic first eigen vector
	 */
	protected GeoElement[] angle(String label, GeoConicND c) {
		AlgoAngleConic algo = new AlgoAngleConic(cons, label, (GeoConic) c);
		GeoElement[] ret = { algo.getAngle() };
		return ret;
	}

	/**
	 * @param labels
	 *            label
	 * @param p
	 *            polygon
	 * @return angles of the polygon
	 */
	protected GeoElement[] angle(String[] labels, GeoPolygon p) {
		return getAlgoDispatcher().angles(labels, p);
	}

}