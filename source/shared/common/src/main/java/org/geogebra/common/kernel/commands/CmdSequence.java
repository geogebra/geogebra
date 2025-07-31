package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoSequence;
import org.geogebra.common.kernel.algos.AlgoSequenceRange;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Sequence[ &lt;expression&gt;, &lt;number-var&gt;, &lt;from&gt;, &lt;to&gt; ]
 * 
 * Sequence[ &lt;expression&gt;, &lt;number-var&gt;, &lt;from&gt;, &lt;to&gt;, &lt;step&gt; ]
 * 
 * Sequence[ &lt;number-var&gt;]
 */
public class CmdSequence extends CommandProcessor {
	/**
	 * Creates new sequence command
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSequence(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		// avoid
		// "Command Sequence not known eg
		// Sequence[If[Element[list1,i]=="b",0,1]]
		if (n < 1 || n > 5) {
			throw argNumErr(c);
		}

		boolean[] ok = new boolean[n];

		// create local variable at position 1 and resolve arguments
		GeoElement[] arg;
		if (n > 3) {
			arg = resArgsLocalNumVar(c, 1, 2, n - 1);
		} else {
			arg = resArgs(c, info);
		}
		switch (n) {
		case 1:
			if (arg[0] instanceof GeoNumberValue) {

				AlgoSequenceRange algo = new AlgoSequenceRange(cons,
						(GeoNumberValue) arg[0]);
				algo.getOutput(0).setLabel(c.getLabel());
				return algo.getOutput();
			}
			throw argErr(c, arg[0]);
		case 2:
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {

				AlgoSequenceRange algo = new AlgoSequenceRange(cons,
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1], null);
				algo.getOutput(0).setLabel(c.getLabel());
				return algo.getOutput();
			}
			throw argErr(c, getBadArg(ok, arg));
		case 3:
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {

				AlgoSequenceRange algo = new AlgoSequenceRange(cons,
						(GeoNumberValue) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2]);
				algo.getOutput(0).setLabel(c.getLabel());
				return algo.getOutput();
			}
			throw argErr(c, getBadArg(ok, arg));
		case 4:
			if ((ok[0] = arg[0].isGeoElement())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3] instanceof GeoNumberValue)) {

				AlgoSequence algo = new AlgoSequence(cons, c.getLabel(), arg[0],
						(GeoNumeric) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3], null);

				return algo.getOutput();
			}
			throw argErr(c, getBadArg(ok, arg));

		case 5:
			if ((ok[0] = arg[0].isGeoElement())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3] instanceof GeoNumberValue)
					&& (ok[4] = arg[4] instanceof GeoNumberValue)) {

				AlgoSequence algo = new AlgoSequence(cons, c.getLabel(), arg[0],
						(GeoNumeric) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3], (GeoNumberValue) arg[4]);
				return algo.getOutput();

			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

}