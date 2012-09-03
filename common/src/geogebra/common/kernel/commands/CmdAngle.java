package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoAngleConic;
import geogebra.common.kernel.algos.AlgoAngleNumeric;
import geogebra.common.kernel.algos.AlgoAngleVector;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;


/**
 * Angle[ number ] Angle[ <GeoPolygon> ] Angle[ <GeoConic> ] Angle[ <GeoVector>
 * ] Angle[ <GeoPoint> ] Angle[ <GeoVector>, <GeoVector> ] Angle[ <GeoLine>,
 * <GeoLine> ] Angle[ <GeoPoint>, <GeoPoint>, <GeoPoint> ] Angle[ <GeoPoint>,
 * <GeoPoint>, <Number> ]
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
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		/**
		 * // Anlge[ constant number ] // get number value ExpressionNode en =
		 * null; ExpressionValue eval; double value = 0.0; // check if we got
		 * number: // ExpressionNode && NumberValue || Assignment // build
		 * ExpressionNode from one of these cases ok[0] = false; Object ob =
		 * c.getArgument(0); if (ob instanceof ExpressionNode) { en =
		 * (ExpressionNode) ob; eval = en.evaluate(); if (eval .isNumberValue()
		 * && !(eval .isGeoPolygon())) { value = ((NumberValue)
		 * eval).getDouble(); ok[0] = true; } } else if (ob instanceof
		 * Assignment) { GeoElement geo = cons.lookupLabel(((Assignment)
		 * ob).getVariable()); if (geo .isGeoNumeric()) { // wrap GeoNumeric int
		 * ExpressionNode for // kernel.DependentNumer() en = new
		 * ExpressionNode(kernel, (NumberValue) geo,
		 * ExpressionNode.NO_OPERATION, null); ok[0] = true; } }
		 */
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
				
				AlgoAngleVector algo = new AlgoAngleVector(cons, c.getLabel(),
						(GeoVec3D) arg[0]);

				GeoElement[] ret = { algo.getAngle() };
				return ret;
			}
			// angle of conic or polygon
			else {
				if (arg[0].isGeoConic()) {
					
					AlgoAngleConic algo = new AlgoAngleConic(cons, c.getLabel(),
							(GeoConic) arg[0]);

					GeoElement[] ret = { algo.getAngle() };
					return ret;
				} else if (arg[0].isGeoPolygon()) {
					return getAlgoDispatcher().Angles(c.getLabels(), (GeoPolygon) arg[0]);
				}
			}

			throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);

			// angle between vectors
			if ((ok[0] = (arg[0].isGeoVector()))
					&& (ok[1] = (arg[1].isGeoVector()))) {
				GeoElement[] ret = { getAlgoDispatcher().Angle(c.getLabel(),
						(GeoVector) arg[0], (GeoVector) arg[1]) };
				return ret;
			}
			// angle between lines
			else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = { getAlgoDispatcher().Angle(c.getLabel(),
						(GeoLine) arg[0], (GeoLine) arg[1]) };
				return ret;
			}
			// syntax error
			else {
				if (ok[0] && !ok[1]) {
					throw argErr(app, c.getName(), arg[1]);
				}
				throw argErr(app, c.getName(), arg[0]);
			}

		case 3:
			arg = resArgs(c);

			// angle between three points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { getAlgoDispatcher().Angle(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1],
						(GeoPoint) arg[2]) };
				return ret;
			}
			// fixed angle
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				return getAlgoDispatcher().Angle(c.getLabels(), (GeoPoint) arg[0],
						(GeoPoint) arg[1], (NumberValue) arg[2], true);
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}