package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.statistics.AlgoProduct;
import geogebra.common.main.MyError;

/**
 * Product[ list ]
 * @author Michael Borcherds
 * @version 2008-02-16
 */
public class CmdProduct extends CommandProcessor{
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdProduct(Kernel kernel) {
		super(kernel);
	}

	

	@Override
	public GeoElement[] process(geogebra.common.kernel.arithmetic.Command c)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		// needed for Sum[]
		if (arg.length == 0) {
			throw argNumErr(app, c.getName(), n);
		}
		if(!arg[0].isGeoList())
			throw argErr(app, c.getName(), arg[0]);
		GeoList list = (GeoList)arg[0];
		switch (n) {
		case 1:
				if (((GeoList)arg[0]).getGeoElementForPropertiesDialog().isNumberValue()) 
				{
					AlgoProduct algo = new AlgoProduct(cons, c.getLabel(), list);

					GeoElement[] ret = { algo.getResult() };
					return ret;
				}
			throw argErr(app, c.getName(), arg[0]);
		case 2:
			// Product[<List of Numbers>, <Number>]
			if (arg[1].isGeoNumeric()) {

				if (((GeoList)arg[0]).getGeoElementForPropertiesDialog().isNumberValue()) 
				{
					
					AlgoProduct algo = new AlgoProduct(cons, c.getLabel(),list,(GeoNumeric)arg[1]);

					GeoElement[] ret = { algo.getResult() };
					return ret;
				}
				throw argErr(app, c.getName(), arg[0]);
			}
			// Product[<List of Numbers>, <Frequency>]
			else if (arg[1].isGeoList()){
				if (((GeoList)arg[0]).getGeoElementForPropertiesDialog().isNumberValue()) 
				{
					
					AlgoProduct algo = new AlgoProduct(cons, c.getLabel(),list,(GeoList)arg[1]);

					GeoElement[] ret = { algo.getResult() };
					return ret;
				}
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[1]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	}


