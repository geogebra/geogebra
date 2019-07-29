package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * abstract class for Commands with one list argument eg Mean[ &lt;List> ]
 * 
 * if more than one argument, then they are put into a list
 * 
 * Michael Borcherds 2008-04-12
 */
public abstract class CmdOneListFunction extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOneListFunction(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 0:
			throw argNumErr(c);
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = {
						doCommand(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			} else if (arg[0].isGeoFunction()) {

				// allow FitXXX[ <Freehand Function> ], eg FitSin

				GeoFunction fun = (GeoFunction) arg[0];

				if (fun.getParentAlgorithm() instanceof AlgoFunctionFreehand) {

					GeoList list = wrapFreehandFunctionArgInList(kernel,
							(AlgoFunctionFreehand) fun.getParentAlgorithm());

					if (list != null) {
						GeoElement[] ret = { doCommand(c.getLabel(), list) };
						return ret;
					}

				}

			}
			throw argErr(c, arg[0]);

			// more than one argument
		default:

			// two lists (e.g. grouped mean)
			if (n == 2 && arg[0].isGeoList() && arg[1].isGeoList()) {
				GeoElement[] ret = { doCommand(c.getLabel(), c,
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
			}
			// two lists plus flag to indicate useFrequency (needed for SIGMAXX)
			else if (n == 3 && arg[0].isGeoList() && arg[1].isGeoList()
					&& arg[2].isGeoBoolean()) {
				GeoElement[] ret = {
						doCommand(c.getLabel(), c, (GeoList) arg[0],
								(GeoList) arg[1], (GeoBoolean) arg[2]) };
				return ret;
			}

			else if (arg[0] instanceof GeoNumberValue) {
				// try to create list of numbers
				GeoList list = wrapInList(kernel, arg, arg.length,
						GeoClass.NUMERIC);
				if (list != null) {
					list.setDefinedWithCurlyBrackets(false);
					GeoElement[] ret = { doCommand(c.getLabel(), list) };
					return ret;
				}
			} else if (arg[0] instanceof VectorValue) {
				// try to create list of points (eg FitExp[])
				GeoList list = wrapInList(kernel, arg, arg.length,
						GeoClass.POINT);
				if (list != null) {
					GeoElement[] ret = { doCommand(c.getLabel(), list) };
					return ret;
				}

			}
			if (n == 2) {
				throw argErr(c, arg[0].isGeoList() ? arg[1] : arg[0]);
			}
			throw argNumErr(c);
		}
	}

	/**
	 * Perform the actual command
	 * 
	 * @param label
	 *            label for output
	 * @param list
	 *            input list
	 * @return resulting element
	 */
	abstract protected GeoElement doCommand(String label, GeoList list);

	/**
	 * Perform the actual command with frequency data
	 * 
	 * @param label
	 *            label for output
	 * @param c
	 *            command being processed (needed for error message)
	 * @param list
	 *            input list
	 * @param list2
	 *            another list (data frequencies)
	 * @return resulting element
	 */
	protected GeoElement doCommand(String label, Command c, GeoList list,
			GeoList list2) {
		throw argNumErr(c);
	}

	/**
	 * Perform the actual command with frequency data and a flag (needed for
	 * CmdSigmaXX)
	 * 
	 * @param label
	 *            label for result
	 * @param c
	 *            command being processed (needed for error message)
	 * @param list
	 *            input list
	 * @param list2
	 *            second list (frequencies)
	 * @param flag
	 *            flag to distinguish between two syntaxes (eg SigmaXX)
	 * @return resulting element
	 */
	protected GeoElement doCommand(String label, Command c, GeoList list,
			GeoList list2, GeoBoolean flag) {
		throw argNumErr(c);
	}

}
