package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 * FrequencyTable[ &lt;List of Raw Data L> ]
FrequencyTable[ &lt;Boolean Cumulative C>, &lt;List of Raw Data L>]
FrequencyTable[&lt;List of Class Boundaries C>, &lt;List of Raw Data L> ]
FrequencyTable[ &lt;Boolean Cumulative>,&lt;List of Class Boundaries C>,&lt;List of Raw Data L>]
FrequencyTable[&lt;List of Class Boundaries>, &lt;List of Raw Data>, &lt;Use Density> , &lt;Density Scale Factor> (optional) ]
FrequencyTable[ &lt;Boolean Cumulative>, &lt;List of Class Boundaries>, &lt;List of Raw Data>, &lt;Use Density> , &lt;Density Scale Factor> (optional) ] 
 *
 */
public class CmdFrequencyTable extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFrequencyTable(Kernel kernel) {
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
		// raw data
		if (arg[0].isGeoList()) {
			GeoElement[] ret = { FrequencyTable(c.getLabel(),
					(GeoList) arg[0]) };
			return ret;
		}
		
		// chart
		else if(arg[0].isGeoNumeric()){
			GeoElement[] ret = { FrequencyTable(c.getLabel(),
					(GeoNumeric) arg[0]) };
			return ret;
			
		}else{
			throw argErr(app, c.getName(), arg[0]);
		}

		case 2:

			//  arg[0] = is cumulative,  arg[1] = data list,
			if ((arg[0].isGeoBoolean()) && (arg[1].isGeoList())) {
				GeoElement[] ret = { FrequencyTable(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1]) };
				return ret;
			}

			// arg[0] = class list, arg[1] = data list
			else if ((arg[0].isGeoList()) && (arg[1].isGeoList())) {
				if (arg[1].isGeoList()) {
					GeoElement[] ret = { FrequencyTable(c.getLabel(),
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
				GeoElement[] ret = { FrequencyTable(c.getLabel(), (GeoBoolean) arg[0],
								(GeoList) arg[1], (GeoList) arg[2]) };
				return ret;

			} 
			// arg[0] = class list, arg[1] = data list, arg[2] = useDensity
			else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { FrequencyTable(c.getLabel(), (GeoList) arg[0],
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
				GeoElement[] ret = { FrequencyTable(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], (GeoNumeric) arg[3]) };
				return ret;
			}
			
			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list, arg[3] = useDensity
			else if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				GeoElement[] ret = { FrequencyTable(c.getLabel(),
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
				GeoElement[] ret = { FrequencyTable(c.getLabel(), 
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
	 * FrequencyTable[dataList] Zbynek Konecny
	 */
	final private GeoText FrequencyTable(String label, GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, null,
				null, dataList);
		GeoText table = algo.getResult();
		return table;
	}

	/**
	 * FrequencyTable[isCumulative, dataList] Zbynek Konecny
	 */
	final private GeoText FrequencyTable(String label, GeoBoolean isCumulative,
			GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label,
				isCumulative, null, dataList);
		GeoText table = algo.getResult();
		return table;
	}

	/**
	 * FrequencyTable[classList, dataList] Zbynek Konecny
	 */
	final private GeoText FrequencyTable(String label, GeoList classList,
			GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, null,
				classList, dataList);
		GeoText table = algo.getResult();
		return table;
	}

	/**
	 * FrequencyTable[classList, dataList, useDensity] Zbynek Konecny
	 */
	final private GeoText FrequencyTable(String label, GeoList classList,
			GeoList dataList, GeoBoolean useDensity) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, null,
				classList, dataList, useDensity, null);
		GeoText table = algo.getResult();
		return table;
	}

	/**
	 * FrequencyTable[classList, dataList, useDensity, scaleFactor] Zbynek
	 * Konecny
	 */
	final private GeoText FrequencyTable(String label, GeoList classList,
			GeoList dataList, GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, null,
				classList, dataList, useDensity, scaleFactor);
		GeoText table = algo.getResult();
		return table;
	}

	/**
	 * FrequencyTable[isCumulative, classList, dataList] Zbynek Konecny
	 */
	final private GeoText FrequencyTable(String label, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label,
				isCumulative, classList, dataList, null, null);
		GeoText table = algo.getResult();
		return table;
	}

	/**
	 * FrequencyTable[isCumulative, classList, dataList, useDensity] Zbynek
	 * Konecny
	 */
	final private GeoText FrequencyTable(String label, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label,
				isCumulative, classList, dataList, useDensity, null);
		GeoText table = algo.getResult();
		return table;
	}

	/**
	 * FrequencyTable[isCumulative, classList, dataList, useDensity,
	 * scaleFactor] Zbynek Konecny
	 */
	final private GeoText FrequencyTable(String label, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity,
			GeoNumeric scaleFactor) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label,
				isCumulative, classList, dataList, useDensity, scaleFactor);
		GeoText table = algo.getResult();
		return table;
	}
	
	/**
	 * FrequencyTable[chart (Histogram or BarChart)] Zbynek Konecny
	 */
	final private GeoText FrequencyTable(String label, GeoNumeric chart) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, chart);
		GeoText table = algo.getResult();
		return table;
	}	
}
