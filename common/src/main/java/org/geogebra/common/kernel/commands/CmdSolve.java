package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Allows use of CAS commands Solve and NSolve in input bar
 * 
 * @author Zbynek
 */
public class CmdSolve extends CommandProcessor {

	private Commands type;

	/**
	 * @param kernel
	 *            kernel
	 * @param command
	 *            one of (N?)Solve, (N?)Solutions
	 */
	public CmdSolve(Kernel kernel, Commands command) {
		super(kernel);
		this.type = command;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) {
		GeoElement[] args = this.resArgs(c);
		switch(args.length){
		case 1:
			if (args[0].isGeoList() || args[0] instanceof EquationValue
					|| args[0].isGeoFunction()) {
				AlgoSolve solve = new AlgoSolve(cons, args[0], type);

				if (info.isLabelOutput()) {
					solve.getOutput(0).setLabel(c.getLabel());
				}
				return solve.getOutput();
			}
			throw argErr(args[0], c);
		}
		throw argNumErr(c);
	}

}
