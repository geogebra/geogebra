package org.geogebra.common.kernel.commands;

import static org.geogebra.common.kernel.commands.Commands.CSolutions;
import static org.geogebra.common.kernel.commands.Commands.CSolve;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.cas.AlgoComplexSolve;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Allows use of CAS commands Solve and NSolve in input bar
 * @author Zbynek
 */
public class CmdSolve extends CommandProcessor {

	private Commands type;

	/**
	 * @param kernel kernel
	 * @param command one of (N?)Solve, (N?)Solutions
	 */
	public CmdSolve(Kernel kernel, Commands command) {
		super(kernel);
		this.type = command;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) {
		GeoElement[] args = this.resArgs(c, info);
		switch (args.length) {
		case 1:
			return solve(args[0], null, c, info);
		case 2:
			if (type == Commands.PlotSolve) {
				throw argNumErr(c);
			}
			return solve(args[0], args[1], c, info);
		}
		throw argNumErr(c);
	}

	private GeoElement[] solve(GeoElement arg, GeoElement hint, Command c,
			EvalInfo info) {
		if (arg.isGeoList() || arg instanceof EquationValue || arg instanceof GeoFunction) {
			AlgoSolve solve;
			if (c.getName().equals(CSolve.name()) || c.getName().equals(CSolutions.name())) {
				solve = new AlgoComplexSolve(cons, arg, hint, type);
			} else {
				solve = new AlgoSolve(cons, arg, hint, type);
			}

			if (info.isLabelOutput()) {
				solve.getOutput(0).setLabel(c.getLabel());
			}
			return solve.getOutput();
		}
		throw argErr(arg, c);
	}
}
