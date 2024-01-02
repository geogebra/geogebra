package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

public class CmdPenStroke extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdPenStroke(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoPoint()) {

				if (!arg[1].isGeoPoint() && !(arg[1].isGeoBoolean()
						&& arg[1].evaluateDouble() > 0)) {
					throw argErr(c, arg[1]);
				}

				return genericPolyline(arg[1], arg, c);
			}
			throw argErr(c, arg[0]);
		default:
			GeoElement lastArg = resArgSilent(c, n - 1, info.withLabels(false));
			return genericPolyline(lastArg, null, c);
		}
	}

	private GeoElement[] genericPolyline(GeoElement lastArg, GeoElement[] arg0, Command c) {
		boolean penStroke = false;
		int size = c.getArgumentNumber();
		if (lastArg.isGeoBoolean()) {
			// pen stroke
			// last argument is boolean (normally true)
			size = size - 1;
			penStroke = ((GeoBoolean) lastArg).getBoolean();
		}
		if (penStroke) {
			ArrayList<MyPoint> myPoints = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				MyVecNode vec = (MyVecNode) c.getArgument(i).unwrap();
				myPoints.add(new MyPoint(vec.getX().evaluateDouble(),
						vec.getY().evaluateDouble()));
			}
			AlgoLocusStroke algo = new AlgoLocusStroke(cons, myPoints);
			algo.getOutput(0).setLabel(c.getLabel());
			return algo.getOutput();
		}
		return null;
	}
}
