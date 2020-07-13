package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.TransformRotate;
import org.geogebra.common.kernel.advanced.AlgoRotateText;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Rotate[ &lt;GeoPoint>, &lt;NumberValue> ] Rotate[ &lt;GeoVector>,
 * &lt;NumberValue> ] Rotate[ &lt;GeoLine>, &lt;NumberValue> ] Rotate[
 * &lt;GeoConic>, &lt;NumberValue> ] Rotate[ &lt;GeoPolygon>, &lt;NumberValue> ]
 * 
 * Rotate[ &lt;GeoPoint>, &lt;NumberValue>, &lt;GeoPoint> ] Rotate[
 * &lt;GeoLine>, &lt;NumberValue>, &lt;GeoPoint> ] Rotate[ &lt;GeoConic>,
 * &lt;NumberValue>, &lt;GeoPoint> ] Rotate[ &lt;GeoPolygon>, &lt;NumberValue>,
 * &lt;GeoPoint> ]
 */
public class CmdRotate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRotate(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			// ROTATE AROUND CENTER (0,0)
			arg = resArgs(c);
			return process2(c, arg, ok);

		case 3:
			// ROTATION AROUND POINT
			arg = resArgs(c);
			return process3(c, arg, ok);

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * process for 2 args
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            args
	 * @param ok
	 *            is that ok ?
	 * @return geos
	 */
	final protected GeoElement[] process2(Command c, GeoElement[] arg,
			boolean[] ok) {

		if (arg[1] instanceof GeoNumberValue) {
			if (arg[0] instanceof GeoText) {
				// c.setName("RotateText");
				// return kernelA.getAlgebraProcessor().processCommand(c,
				// new EvalInfo(false));
				AlgoRotateText algo = new AlgoRotateText(cons,
						(GeoText) arg[0], (GeoNumberValue) arg[1]);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[] { algo.getResult() };

			}
			GeoNumberValue phi = (GeoNumberValue) arg[1];

			return rotate(c.getLabel(), arg[0], phi);
		}

		throw argErr(c, arg[0]);
	}

	/**
	 * process for 3 args
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            args
	 * @param ok
	 *            is that ok ?
	 * @return geos
	 */
	final protected GeoElement[] process3(Command c, GeoElement[] arg,
			boolean[] ok) {

		if ((ok[0] = true) && (ok[1] = (arg[1] instanceof GeoNumberValue))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			if (arg[0] instanceof GeoText) {
				c.setName("RotateText");
				return kernel.getAlgebraProcessor().processCommand(c,
						new EvalInfo(false));
			}
			GeoNumberValue phi = (GeoNumberValue) arg[1];
			GeoPointND Q = (GeoPointND) arg[2];

			return getAlgoDispatcher().rotate(c.getLabel(), arg[0], phi, Q);
		}

		throw argErr(c, getBadArg(ok, arg));
	}

	/**
	 * rotate geoRot by angle phi around (0,0)
	 */
	final private GeoElement[] rotate(String label, GeoElement geoRot,
			GeoNumberValue phi) {
		Transform t = new TransformRotate(cons, phi);
		return t.transform(geoRot, label);
	}

}
