package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFoldExpression;
import org.geogebra.common.kernel.algos.AlgoFoldFunctions;
import org.geogebra.common.kernel.algos.AlgoSum;
import org.geogebra.common.kernel.algos.FunctionFold;
import org.geogebra.common.kernel.algos.FunctionNvarFold;
import org.geogebra.common.kernel.algos.PointNDFold;
import org.geogebra.common.kernel.algos.TextFold;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Sum[ list ] adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdSum extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSum(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;


		// needed for Sum[]
		if (c.getArgumentNumber() == 0) {
			throw argNumErr(app, c.getName(), n);
		}
		if (c.getArgumentNumber() == 4) {
			GeoElement[] res = processSymb(this, c, Operation.PLUS);
			if (res != null) {
				return res;
			}
		}
		arg = resArgs(c);
		// set all to either true or false
		boolean allNumbers = arg[0].isGeoList();
		boolean allFunctions = allNumbers;
		boolean allFunctionsND = allNumbers;
		boolean allNumbersVectorsPoints = allNumbers;
		boolean allText = allNumbers;

		GeoList list = null;
		int size = -1;

		if (arg[0].isGeoList()) {
			list = (GeoList) arg[0];
			size = list.size();

			for (int i = 0; i < size; i++) {
				GeoElement geo = list.get(i);
				if (!geo.isGeoFunctionable() && !geo.isGeoFunctionNVar()) {
					allFunctionsND = false;
				}
				if (!geo.isGeoFunctionable()) {
					allFunctions = false;
				}
				if (!(geo instanceof GeoNumberValue)) {
					allNumbers = false;
				}
				if (!(geo instanceof GeoNumberValue) && !geo.isGeoVector()
						&& !geo.isGeoPoint()) {
					allNumbersVectorsPoints = false;
				}
				if (!geo.isGeoText()) {
					allText = false;
				}
			}
		}

		// this is bad - list can be saved later with size 0
		// if (size == 0) throw argErr(app, c.getName(), arg[0]);

		switch (n) {
		case 1:
			if (allNumbers) {
				GeoElement[] ret = { Sum(c.getLabel(), list) };
				return ret;
			} else if (allNumbersVectorsPoints) {
				GeoElement[] ret = { SumPoints(c.getLabel(), list, null) };
				return ret;
			} else if (allFunctionsND) {
				GeoElement[] ret = { SumFunctions(c.getLabel(), list, null,
						allFunctions) };
				return ret;
			} else if (allText) {
				GeoElement[] ret = { SumText(c.getLabel(), list, null) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:
			if (arg[1].isGeoNumeric()) {

				if (allNumbers) {
					AlgoSum algo = new AlgoSum(cons, c.getLabel(), list,
							(GeoNumeric) arg[1]);

					GeoElement[] ret = { algo.getResult() };
					return ret;
				} else if (allFunctionsND) {
					GeoElement[] ret = { SumFunctions(c.getLabel(), list,
							(GeoNumeric) arg[1], allFunctions) };
					return ret;
				} else if (allNumbersVectorsPoints) {
					GeoElement[] ret = { SumPoints(c.getLabel(), list,
							(GeoNumeric) arg[1]) };
					return ret;
				} else if (allText) {
					GeoElement[] ret = { SumText(c.getLabel(), list,
							(GeoNumeric) arg[1]) };
					return ret;

				} else {
					throw argErr(app, c.getName(), arg[0]);
				}
			} else if (arg[1].isGeoList()) {
				if (((GeoList) arg[0]).getGeoElementForPropertiesDialog() instanceof GeoNumberValue) {

					AlgoSum algo = new AlgoSum(cons, c.getLabel(), list,
							(GeoList) arg[1]);

					GeoElement[] ret = { algo.getResult() };
					return ret;
				}
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[0]);


		default:
			// try to create list of numbers
			if (arg[0] instanceof GeoNumberValue) {
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.NUMERIC);
				if (wrapList != null) {
					GeoElement[] ret = { Sum(c.getLabel(), wrapList) };
					return ret;
				}
			} else if (arg[0] instanceof VectorValue) {
				// try to create list of points
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.POINT);
				if (wrapList != null) {
					GeoElement[] ret = { SumPoints(c.getLabel(), wrapList, null) };
					return ret;
				}
			} else if (arg[0].isGeoFunction()) {
				// try to create list of functions
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.FUNCTION);
				if (wrapList != null) {
					GeoElement[] ret = { SumFunctions(c.getLabel(), wrapList,
							null, true) };
					return ret;
				}
			} else if (arg[0].isGeoFunction()) {
				// try to create list of functions
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.FUNCTION_NVAR);
				if (wrapList != null) {
					GeoElement[] ret = { SumFunctions(c.getLabel(), wrapList,
							null, false) };
					return ret;
				}
			} else if (arg[0].isGeoText()) {
				// try to create list of functions
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.TEXT);
				if (wrapList != null) {
					GeoElement[] ret = { SumText(c.getLabel(), wrapList,
							null) };
					return ret;
				}
			}
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * @param proc
	 *            processor (product/sum)
	 * @param c
	 *            command
	 * @param op
	 *            operation
	 * @return sum/product object if applicable
	 */
	static GeoElement[] processSymb(CommandProcessor proc, Command c,
			Operation op) {
		GeoElement[] arg = proc.resArgsLocalNumVar(c, 1, 2);
		if (!arg[1].isGeoNumeric() || !arg[2].isGeoNumeric()
				|| !arg[3].isGeoNumeric()) {
			return null;
		}
		AlgoFoldExpression algo = new AlgoFoldExpression(proc.cons,
				c.getLabel(),
				arg[0], (GeoNumeric) arg[1], (GeoNumeric) arg[2],
				(GeoNumeric) arg[3], op);
		return algo.getOutput();
	}

	final private GeoElement Sum(String label, GeoList list) {
		AlgoSum algo = new AlgoSum(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}

	/**
	 * Sum[list of functions,n] Michael Borcherds
	 */
	final private GeoElement SumFunctions(String label, GeoList list,
			GeoNumeric num, boolean oneVar) {
		AlgoFoldFunctions algo = new AlgoFoldFunctions(cons, label, list, num,
				Operation.PLUS, oneVar ?

				new FunctionFold() : new FunctionNvarFold());
		GeoElement ret = algo.getResult();
		return ret;
	}



	/**
	 * Sum[list of points,n] Michael Borcherds
	 */
	final private GeoElement SumPoints(String label, GeoList list,
			GeoNumeric num) {
		AlgoFoldFunctions algo = new AlgoFoldFunctions(cons, label, list, num,
				Operation.PLUS, new PointNDFold());
		GeoElement ret = algo.getResult();
		return ret;
	}



	/**
	 * Sum[list of text,n] Michael Borcherds
	 */
	final private GeoElement SumText(String label, GeoList list, GeoNumeric num) {
		AlgoFoldFunctions algo = new AlgoFoldFunctions(cons, label, list, num,
				Operation.PLUS, new TextFold());
		GeoElement ret = algo.getResult();
		return ret;
	}

}
