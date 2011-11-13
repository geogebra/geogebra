package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * First[ <List>,n ]
 * Michael Borcherds
 * 2008-03-04
 */
public class CmdFirst extends CommandProcessor {

	public CmdFirst(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernel.First(c.getLabel(),
						(GeoList) arg[0], null ) };
				return ret;
			} else if (arg[0].isGeoText()) {
				GeoElement[] ret = { 
						kernel.First(c.getLabel(),
						(GeoText) arg[0], null ) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		case 2:
			boolean list = arg[0].isGeoList();
			boolean string = arg[0].isGeoText();
			boolean locus = arg[0].isGeoLocus();
			if ( list && arg[1].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernel.First(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1] ) };
				return ret;
			} else if ( string && arg[1].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernel.First(c.getLabel(),
						(GeoText) arg[0], (GeoNumeric) arg[1] ) };
				return ret;
			} else if ( locus && arg[1].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernel.FirstLocus(c.getLabel(),
						(GeoLocus) arg[0], (GeoNumeric) arg[1] ) };
				return ret;
			} else
				throw argErr(app, c.getName(), (list && string) ? arg[1] : arg[0]);
			
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
