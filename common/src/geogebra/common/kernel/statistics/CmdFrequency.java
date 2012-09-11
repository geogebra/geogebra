package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/**
 * Frequency
 */
public class CmdFrequency extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFrequency(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { Frequency(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;

			} 
			throw argErr(app, c.getName(), arg[0]);
			

		case 2:

			//  arg[0] = is cumulative,  arg[1] = data list,
			if ((arg[0].isGeoBoolean()) && (arg[1].isGeoList())) {
				GeoElement[] ret = { Frequency(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1]) };
				return ret;
			}

			// arg[0] = class list, arg[1] = data list
			else if ((arg[0].isGeoList()) && (arg[1].isGeoList())) {
				if (arg[1].isGeoList()) {
					GeoElement[] ret = { Frequency(c.getLabel(),
							(GeoList) arg[0], (GeoList) arg[1]) };
					return ret;
				}
				
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else {
				throw argErr(app, c.getName(), arg[1]);
			}

		case 3:

			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list
			if ((ok[0] = arg[0].isGeoBoolean()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())) {
				GeoElement[] ret = { Frequency(c.getLabel(), (GeoBoolean) arg[0],
								(GeoList) arg[1], (GeoList) arg[2]) };
				return ret;

			} 
			// arg[0] = class list, arg[1] = data list, arg[2] = useDensity
			else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { Frequency(c.getLabel(), (GeoList) arg[0],
								(GeoList) arg[1], (GeoBoolean) arg[2]) };
				return ret;

			} 
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else 
				throw argErr(app, c.getName(), arg[2]);

		case 4:
			arg = resArgs(c);
			// arg[0] = class list, arg[2] = data list, arg[2] = useDensity, arg[3]= density scale factor
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoBoolean()))
					&& (ok[3] = (arg[3].isGeoNumeric()))) {
				GeoElement[] ret = { Frequency(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], (GeoNumeric) arg[3]) };
				return ret;
			}
			
			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list, arg[3] = useDensity
			else if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				GeoElement[] ret = { Frequency(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoBoolean) arg[3]) };
				return ret;
			}
			
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

			
		case 5:
			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list,
			// arg[3] = useDensity, arg[4] = density scale factor,
			if ((ok[0] = arg[0].isGeoBoolean()) 
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())  
					&& (ok[3] = arg[3].isGeoBoolean())
					&& (ok[4] = arg[4].isGeoNumeric())) 
			{
				GeoElement[] ret = { Frequency(c.getLabel(), 
								(GeoBoolean) arg[0],
								(GeoList) arg[1], 
								(GeoList) arg[2], 
								(GeoBoolean) arg[3],
								(GeoNumeric) arg[4]) 
				};
				return ret;
			}		

			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	/**
	 * Frequency[dataList] G. Sturr
	 */
	final private GeoList Frequency(String label, GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, null, null,
				dataList);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[isCumulative, dataList] G. Sturr
	 */
	final private GeoList Frequency(String label, GeoBoolean isCumulative,
			GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, isCumulative, null,
				dataList);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[classList, dataList] G. Sturr
	 */
	final private GeoList Frequency(String label, GeoList classList,
			GeoList dataList) {
		AlgoFrequency algo;

		if(classList.getElementType() == GeoClass.TEXT){
			algo = new AlgoFrequency(cons, label, classList, dataList, true);
		}else{
			algo = new AlgoFrequency(cons, label, null, classList,
					dataList);
		}
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[classList, dataList, useDensity] G. Sturr
	 */
	final private GeoList Frequency(String label, GeoList classList,
			GeoList dataList, GeoBoolean useDensity) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, null, classList,
				dataList, useDensity, null);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[classList, dataList, useDensity, scaleFactor] G. Sturr
	 */
	final private GeoList Frequency(String label, GeoList classList,
			GeoList dataList, GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, null, classList,
				dataList, useDensity, scaleFactor);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[isCumulative, classList, dataList] G. Sturr
	 */
	final private GeoList Frequency(String label, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, isCumulative,
				classList, dataList, null, null);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[isCumulative, classList, dataList, useDensity] G. Sturr
	 */
	final private GeoList Frequency(String label, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, isCumulative,
				classList, dataList, useDensity, null);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[isCumulative, classList, dataList, useDensity, scaleFactor] G.
	 * Sturr
	 */
	final private GeoList Frequency(String label, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity,
			GeoNumeric scaleFactor) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, isCumulative,
				classList, dataList, useDensity, scaleFactor);
		GeoList list = algo.getResult();
		return list;
	}
}
