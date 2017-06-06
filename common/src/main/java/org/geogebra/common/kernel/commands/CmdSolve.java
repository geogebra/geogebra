package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.geos.GeoElement;

public class CmdSolve extends CommandProcessor {

	public CmdSolve(Kernel kernel) {
		super(kernel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) {
		GeoElement[] args = this.resArgs(c);
		switch(args.length){
		case 1:
			if (args[0].isGeoList() || args[0] instanceof EquationValue
					|| args[0].isGeoFunction()) {
				AlgoSolve solve = new AlgoSolve(cons, args[0]);
				if (info.isLabelOutput()) {
					solve.getOutput(0).setLabel(c.getLabel());
				}
				return solve.getOutput();
			}

		}
		throw argNumErr(args.length, c);
	}

}
