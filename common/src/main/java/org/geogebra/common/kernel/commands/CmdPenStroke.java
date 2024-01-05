package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
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
		switch (n) {
		case 0:
			throw argNumErr(c);
		default:
			return genericPolyline(c);
		}
	}

	private GeoElement[] genericPolyline(Command c) {
		int size = c.getArgumentNumber();
		ArrayList<MyPoint> myPoints = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			if (!(c.getArgument(i).unwrap() instanceof MyVecNode)) {
				throw argErr(c, c.getArgument(i).unwrap());
			}
			MyVecNode vec = (MyVecNode) c.getArgument(i).unwrap();
			myPoints.add(new MyPoint(vec.getX().evaluateDouble(),
					vec.getY().evaluateDouble()));
		}
		AlgoLocusStroke algo = new AlgoLocusStroke(cons, myPoints);
		algo.getOutput(0).setLabel(c.getLabel());
		return algo.getOutput();
	}
}
