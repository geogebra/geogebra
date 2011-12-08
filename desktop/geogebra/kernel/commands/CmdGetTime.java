package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoText;

import java.util.Calendar;

/**
 *CmdGetTime
 */
class CmdGetTime extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdGetTime(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			Calendar cal = Calendar.getInstance();
			GeoNumeric ms = new GeoNumeric(cons, cal.get(Calendar.MILLISECOND));
			GeoNumeric secs = new GeoNumeric(cons, cal.get(Calendar.SECOND));
			GeoNumeric mins = new GeoNumeric(cons, cal.get(Calendar.MINUTE));
			GeoNumeric hours = new GeoNumeric(cons, cal.get(Calendar.HOUR_OF_DAY));
			GeoNumeric date = new GeoNumeric(cons, cal.get(Calendar.DAY_OF_MONTH));
			int d = cal.get(Calendar.DAY_OF_WEEK);
			GeoNumeric day = new GeoNumeric(cons, d);
			int m = cal.get(Calendar.MONTH) + 1;
			GeoNumeric month = new GeoNumeric(cons, m);
			GeoNumeric year = new GeoNumeric(cons, cal.get(Calendar.YEAR));
			GeoText monthStr = new GeoText(cons);
			monthStr.setTextString(app.getPlain("Month."+m));
			
			GeoText dayStr = new GeoText(cons);
			dayStr.setTextString(app.getPlain("Day."+d));

			GeoList list = new GeoList(cons);
			list.setLabel(c.getLabel());
			
			list.add(ms);
			list.add(secs);
			list.add(mins);
			list.add(hours);
			list.add(date);
			list.add(month);
			list.add(year);
			list.add(monthStr);
			list.add(dayStr);
			list.add(day);
			list.update();

			GeoElement[] ret = { list };
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
