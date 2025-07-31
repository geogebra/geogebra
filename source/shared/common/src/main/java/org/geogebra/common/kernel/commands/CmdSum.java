package org.geogebra.common.kernel.commands;

import java.util.Arrays;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFoldExpression;
import org.geogebra.common.kernel.algos.AlgoFoldFunctions;
import org.geogebra.common.kernel.algos.AlgoSum;
import org.geogebra.common.kernel.algos.FoldComputer;
import org.geogebra.common.kernel.algos.FunctionFold;
import org.geogebra.common.kernel.algos.FunctionNvarFold;
import org.geogebra.common.kernel.algos.ListFold;
import org.geogebra.common.kernel.algos.NumberFold;
import org.geogebra.common.kernel.algos.PointNDFold;
import org.geogebra.common.kernel.algos.TextFold;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

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
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// needed for Sum[]
		if (c.getArgumentNumber() == 0) {
			throw argNumErr(c);
		}
		if (c.getArgumentNumber() == 4) {
			GeoElement[] res = processSymb(this, c, Operation.PLUS);
			if (res != null) {
				return res;
			}
		}
		arg = resArgs(c, info);
		// set all to either true or false

		GeoList list = null;
		FoldComputer fold = null;
		if (arg[0].isGeoList()) {
			list = (GeoList) arg[0];
			fold = getFoldComputer(list);
		}

		// this is bad - list can be saved later with size 0
		// if (size == 0) throw argErr(app, c, arg[0]);

		switch (n) {
		case 1:
			if (fold instanceof NumberFold) {
				GeoElement[] ret = { sum(c.getLabel(), list) };
				return ret;
			} else if (fold != null) {
				GeoElement[] ret = { sum(c.getLabel(), list, null, fold) };
				return ret;
			}
			throw argErr(c, arg[0]);

		case 2:
			if (arg[1].isGeoNumeric()) {

				if (fold instanceof NumberFold) {
					AlgoSum algo = new AlgoSum(cons, list, (GeoNumeric) arg[1]);
					algo.getResult().setLabel(c.getLabel());
					GeoElement[] ret = { algo.getResult() };
					return ret;
				} else if (fold != null) {
					GeoElement[] ret = { sum(c.getLabel(), list,
							(GeoNumeric) arg[1], fold) };
					return ret;
				} else {
					throw argErr(c, arg[0]);
				}
			} else if (arg[1].isGeoList()) {
				if (((GeoList) arg[0])
						.getGeoElementForPropertiesDialog() instanceof GeoNumberValue) {

					AlgoSum algo = new AlgoSum(cons, list, (GeoList) arg[1]);
					algo.getResult().setLabel(c.getLabel());
					GeoElement[] ret = { algo.getResult() };
					return ret;
				}
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[0]);

		default:
			// try to create list of numbers
			if (arg[0] instanceof GeoNumberValue) {
				GeoList wrapList = wrapInList(kernel, arg, arg.length,
						GeoClass.NUMERIC);
				if (wrapList != null) {
					GeoElement[] ret = { sum(c.getLabel(), wrapList) };
					return ret;
				}
			} else if (arg[0] instanceof VectorValue) {
				// try to create list of points
				GeoList wrapList = wrapInList(kernel, arg, arg.length,
						GeoClass.POINT);
				if (wrapList != null) {
					GeoElement[] ret = {
							sum(c.getLabel(), wrapList, null,
									new PointNDFold()) };
					return ret;
				}
			} else if (arg[0].isGeoFunction()) {
				// try to create list of functions
				GeoList wrapList = wrapInList(kernel, arg, arg.length,
						GeoClass.FUNCTION);
				if (wrapList != null) {
					GeoElement[] ret = {
							sum(c.getLabel(), wrapList, null,
									new FunctionFold()) };
					return ret;
				}
			} else if (arg[0].isGeoFunctionNVar()) {
				// try to create list of functions
				GeoList wrapList = wrapInList(kernel, arg, arg.length,
						GeoClass.FUNCTION_NVAR);
				if (wrapList != null) {
					GeoElement[] ret = {
							sum(c.getLabel(), wrapList, null,
									new FunctionNvarFold()) };
					return ret;
				}
			} else if (arg[0].isGeoText()) {
				// try to create list of functions
				GeoList wrapList = wrapInList(kernel, arg, arg.length,
						GeoClass.TEXT);
				if (wrapList != null) {
					GeoElement[] ret = {
							sum(c.getLabel(), wrapList, null,
									new TextFold()) };
					return ret;
				}
			} else if (arg[0].isGeoList()) {
				// try to create list of functions
				GeoList wrapList = wrapInList(kernel, arg, arg.length,
						GeoClass.LIST);
				if (wrapList != null) {
					GeoElement[] ret = {
							sum(c.getLabel(), wrapList, null,
									new ListFold()) };
					return ret;
				}
			}
			throw argNumErr(c);
		}
	}

	/**
	 * @param list
	 *            list
	 * @return helper for Sum/Product commands
	 */
	public static FoldComputer getFoldComputer(GeoList list) {
		int size = list.size();

		if (size == 0 && !StringUtil.empty(list.getTypeStringForXML())) {
			return getFoldComputerForEmptyList(list);
		}
		boolean allNumbers = true;
		boolean allFunctions = allNumbers;
		boolean allFunctionsND = allNumbers;
		boolean allNumbersVectorsPoints = allNumbers;
		boolean allText = allNumbers;
		boolean allList = allNumbers;

		for (int i = 0; i < size; i++) {
			GeoElement geo = list.get(i);
			if (!geo.isRealValuedFunction() && !geo.isGeoFunctionNVar()) {
				allFunctionsND = false;
			}
			if (!geo.isRealValuedFunction() || (geo.isGeoFunction()
					&& ((GeoFunction) geo).isFunctionOfY())) {
				allFunctions = false;
			}
			if (!(geo instanceof GeoNumberValue)) {
				allNumbers = false;
			}
			if (!(geo.isGeoList())) {
				allList = false;
			}
			if (!(geo instanceof GeoNumberValue) && !geo.isGeoVector()
					&& !geo.isGeoPoint()) {
				allNumbersVectorsPoints = false;
			}
			if (!geo.isGeoText()) {
				allText = false;
			}
		}
		if (allNumbers) {
			return new NumberFold();
		}
		if (allNumbersVectorsPoints) {
			return new PointNDFold();
		}
		if (allFunctions) {
			return new FunctionFold();
		}
		if (allFunctionsND) {
			return new FunctionNvarFold();
		}
		if (allText) {
			return new TextFold();
		}
		if (allList) {
			return new ListFold();
		}
		return null;
	}

	private static FoldComputer getFoldComputerForEmptyList(GeoList list) {
		GeoClass cl = Arrays.stream(GeoClass.values()).filter(c ->
				c.xmlName.equals(list.getTypeStringForXML()))
				.findFirst().orElse(GeoClass.NUMERIC);
		switch (cl) {
		case TEXT:
			return new TextFold();
		case FUNCTION:
			return new FunctionFold();
		case FUNCTION_NVAR:
			return new FunctionNvarFold();
		case POINT:
		case POINT3D:
			return new PointNDFold();
		default:
			return new NumberFold();
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
		GeoElement[] arg = proc.resArgsLocalNumVar(c, 1, 2, 3);
		if (!arg[1].isGeoNumeric() || !arg[2].isGeoNumeric()
				|| !arg[3].isGeoNumeric()) {
			return null;
		}
		AlgoFoldExpression algo = new AlgoFoldExpression(proc.cons,
				c.getLabel(), arg[0], (GeoNumeric) arg[1], (GeoNumeric) arg[2],
				(GeoNumeric) arg[3], op);
		return algo.getOutput();
	}

	final private GeoElement sum(String label, GeoList list) {
		AlgoSum algo = new AlgoSum(cons, list);
		algo.getResult().setLabel(label);
		GeoElement ret = algo.getResult();
		return ret;
	}

	/**
	 * Sum[list of text,n] Michael Borcherds
	 */
	final private GeoElement sum(String label, GeoList list, GeoNumeric num,
			FoldComputer fold) {
		AlgoFoldFunctions algo = new AlgoFoldFunctions(cons, label, list, num,
				Operation.PLUS, fold);
		GeoElement ret = algo.getResult();
		return ret;
	}

}
