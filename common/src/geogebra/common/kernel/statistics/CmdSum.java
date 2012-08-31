package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoSumFunctions;
import geogebra.common.kernel.algos.AlgoSumPoints;
import geogebra.common.kernel.algos.AlgoSumText;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/**
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdSum extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSum(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		// needed for Sum[]
		if (arg.length == 0) {
			throw argNumErr(app, c.getName(), n);
		}

		// set all to either true or false
		boolean allNumbers = arg[0].isGeoList();
		boolean allFunctions = allNumbers;
		boolean allNumbersVectorsPoints = allNumbers;
		boolean allText = allNumbers;

		GeoList list = null;
		int size = -1;

		if (arg[0].isGeoList()) {
			list = (GeoList) arg[0];
			size = list.size();

			for (int i = 0; i < size; i++) {
				GeoElement geo = list.get(i);
				if (!geo.isGeoFunctionable()) {
					allFunctions = false;
				}
				if (!geo.isNumberValue()) {
					allNumbers = false;
				}
				if (!geo.isNumberValue() && !geo.isGeoVector()
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
				GeoElement[] ret = { SumPoints(c.getLabel(), list) };
				return ret;
			} else if (allFunctions) {
				GeoElement[] ret = { SumFunctions(c.getLabel(), list) };
				return ret;
			} else if (allText) {
				GeoElement[] ret = { SumText(c.getLabel(), list) };
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
				} else if (allFunctions) {
					GeoElement[] ret = { SumFunctions(c.getLabel(),
							list, (GeoNumeric) arg[1]) };
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
			}
			else if(arg[1].isGeoList()){
				if (((GeoList)arg[0]).getGeoElementForPropertiesDialog().isNumberValue()) 
				{
					
					AlgoSum algo = new AlgoSum(cons, c.getLabel(),list,(GeoList)arg[1]);

					GeoElement[] ret = {algo.getResult() };
					return ret;
				}
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			// try to create list of numbers
			if (arg[0].isNumberValue()) {
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.NUMERIC);
				if (wrapList != null) {
					GeoElement[] ret = { Sum(c.getLabel(), wrapList) };
					return ret;
				}
			} else if (arg[0].isVectorValue()) {
				// try to create list of points
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.POINT);
				if (wrapList != null) {
					GeoElement[] ret = { SumPoints(c.getLabel(),
							wrapList) };
					return ret;
				}
			} else if (arg[0].isGeoFunction()) {
				// try to create list of functions
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.FUNCTION);
				if (wrapList != null) {
					GeoElement[] ret = { SumFunctions(c.getLabel(),
							wrapList) };
					return ret;
				}
			}
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	final private GeoElement Sum(String label, GeoList list) {
		AlgoSum algo = new AlgoSum(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/**
	 * Sum[list of functions] Michael Borcherds
	 */
	final private GeoElement SumFunctions(String label, GeoList list) {
		AlgoSumFunctions algo = new AlgoSumFunctions(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}

	/**
	 * Sum[list of functions,n] Michael Borcherds
	 */
	final private GeoElement SumFunctions(String label, GeoList list,
			GeoNumeric num) {
		AlgoSumFunctions algo = new AlgoSumFunctions(cons, label, list, num);
		GeoElement ret = algo.getResult();
		return ret;
	}

	/**
	 * Sum[list of points] Michael Borcherds
	 */
	final private GeoElement SumPoints(String label, GeoList list) {
		AlgoSumPoints algo = new AlgoSumPoints(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}

	/**
	 * Sum[list of points,n] Michael Borcherds
	 */
	final private GeoElement SumPoints(String label, GeoList list, GeoNumeric num) {
		AlgoSumPoints algo = new AlgoSumPoints(cons, label, list, num);
		GeoElement ret = algo.getResult();
		return ret;
	}

	/**
	 * Sum[list of points] Michael Borcherds
	 */
	final private GeoElement SumText(String label, GeoList list) {
		AlgoSumText algo = new AlgoSumText(cons, label, list);
		GeoText ret = algo.getResult();
		return ret;
	}

	/**
	 * Sum[list of text,n] Michael Borcherds
	 */
	final private GeoElement SumText(String label, GeoList list, GeoNumeric num) {
		AlgoSumText algo = new AlgoSumText(cons, label, list, num);
		GeoText ret = algo.getResult();
		return ret;
	}



}
