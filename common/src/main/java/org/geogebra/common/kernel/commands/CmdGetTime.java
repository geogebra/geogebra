package org.geogebra.common.kernel.commands;

import java.util.Date;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;

/**
 * CmdGetTime
 *
 * @author Michael Borcherds
 * @author Himanshu Gupta
 */
public class CmdGetTime extends CommandProcessor {

	private static final int[] month_days = { 31, 28, 31, 30, 31, 30, 31, 31,
			30, 31, 30, 31 };

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdGetTime(Kernel kernel) {
		super(kernel);
	}

	@Override
	@SuppressWarnings("deprecation")
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			GeoText monthStr1 = new GeoText(cons);
			GeoText dayStr1 = new GeoText(cons);
			Date cal = new Date();
			GeoNumeric mins1 = new GeoNumeric(cons, cal.getMinutes());
			int d = cal.getDay() + 1;

			int m = cal.getMonth() + 1;

			GeoNumeric secs1 = new GeoNumeric(cons, cal.getSeconds());
			GeoNumeric hours1 = new GeoNumeric(cons, cal.getHours());
			GeoNumeric ms1 = new GeoNumeric(cons, cal.getTime() % 1000);

			monthStr1.setTextString(loc.getMenu("Month." + m));

			dayStr1.setTextString(loc.getMenu("Day." + d));
			GeoList list = new GeoList(cons);
			list.setLabel(c.getLabel());

			list.add(ms1);
			list.add(secs1);
			list.add(mins1);
			list.add(hours1);
			list.add(new GeoNumeric(cons, cal.getDate()));
			list.add(new GeoNumeric(cons, m));
			list.add(new GeoNumeric(cons, cal.getYear() + 1900));
			list.add(monthStr1);
			list.add(dayStr1);
			list.add(new GeoNumeric(cons, d));
			list.update();

			GeoElement[] ret = { list };
			return ret;

		case 1:

			String date = buildLocalizedDate(c.getArgument(0).toValueString(
					StringTemplate.defaultTemplate), new Date(), loc);

			GeoText retText = new GeoText(cons, date);
			retText.setLabel(c.getLabel());
			GeoElement[] ret1 = { retText };
			return ret1;
		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @return formatted date
	 * @param format
	 *            date format
	 * @param cal
	 *            date
	 * @param loc
	 *            application
	 */
	public static String buildLocalizedDate(String format, Date cal,
			Localization loc) {
		StringBuilder sb = new StringBuilder(20);
		char[] cArray = format.toCharArray();
		for (int i = 0; i < cArray.length; i++) {

			if (cArray[i] == '\\' && i < cArray.length - 1) {
				decode(cal, cArray[i + 1], sb, loc);
				i++;
			} else {
				sb.append(cArray[i]);
			}
		}
		return sb.toString();

	}

	@SuppressWarnings("deprecation")
	private static void decode(Date cal, char c, StringBuilder sb,
			Localization loc) {

		// GeoNumeric mins1 = new GeoNumeric(cons, cal.getMinutes());
		int d = cal.getDay() + 1;
		// GeoNumeric day = new GeoNumeric(cons, d);
		int m = cal.getMonth() + 1;
		// GeoNumeric month1 = new GeoNumeric(cons, m);
		// GeoNumeric year1 = new GeoNumeric(cons, cal.getYear() + 1900);
		// GeoNumeric secs1 = new GeoNumeric(cons, cal.getSeconds());
		// GeoNumeric hours1 = new GeoNumeric(cons, cal.getHours());
		// GeoNumeric date1 = new GeoNumeric(cons, cal.getDate());
		// GeoNumeric ms1 = new GeoNumeric(cons, cal.getTime() % 1000);
		int date = cal.getDate();
		int month = cal.getMonth();
		int year = cal.getYear() + 1900;
		int hours = cal.getHours();
		int mins = cal.getMinutes();
		int secs = cal.getSeconds();
		int yearday = 0;
		String dayStr = loc == null ? "" : loc.getMenu("Day." + d);
		String monthStr = loc == null ? "" : loc.getMenu("Month." + m);

		switch (c) {

		case 'd':
			if (date < 10) {
				sb.append(0).append(date);
			} else {
				sb.append(date);
			}
			break;
		case 'D':
			sb.append(dayStr.substring(0, 3));
			break;
		case 'j':
			sb.append(date);
			break;
		case 'l':
			sb.append(dayStr);
			break;
		case 'N':
			if (d == 1) {
				sb.append(7);
			} else {
				sb.append(d - 1);
			}
			break;
		case 'S':
			String ordinal = loc == null ? ""
					: (loc.getLanguage().getOrdinalNumber(date) + "");
			ordinal = ordinal.replaceFirst(String.valueOf(date), "");
			sb.append(ordinal);
			break;
		case 'w':
			sb.append(d - 1);
			break;
		case 'z':
			yearday = 0;
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
				month_days[1] = 29;
			}
			for (int j = 0; j < month; j++) {
				yearday += month_days[j];
			}
			yearday += date - 1;
			sb.append(yearday);
			break;
		case 'W':
			yearday = 0;
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
				month_days[1] = 29;
			}
			for (int j = 0; j < month; j++) {
				yearday += month_days[j];
			}
			yearday += date - 1;
			Date temp = new Date(cal.getYear(), 0, 1);
			int d1 = temp.getDay() + 1;
			yearday -= (9 - d1) % 7;
			if (yearday < 0) {
				yearday = -7;
			}
			sb.append((yearday / 7) + 1);
			break;
		case 'F':
			sb.append(monthStr);
			break;
		case 'm':
			if (m < 10) {
				sb.append(0).append(m);
			} else {
				sb.append(m);
			}
			break;
		case 'M':
			sb.append(monthStr.substring(0, 3));
			break;
		case 'n':
			sb.append(m);
			break;
		case 't':
			switch (m) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				sb.append(31);
				break;
			case 2:
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					sb.append(29);
				} else {
					sb.append(28);
				}
				break;
			default:
				sb.append(30);
				break;
			}
			break;
		case 'L':
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
				sb.append(1);
			} else {
				sb.append(0);
			}
			break;
		case 'Y':
			sb.append(year);
			break;
		case 'y':
			sb.append(year % 100);
			break;
		case 'a':
			if (hours >= 12) {
				sb.append("pm");
			} else {
				sb.append("am");
			}
			break;
		case 'A':
			if (hours >= 12) {
				sb.append("PM");
			} else {
				sb.append("AM");
			}
			break;
		// case 'B': break; Internet Swatch Time not supported
		case 'g':
			sb.append((hours % 12) + 1);
			break;
		case 'G':
			sb.append(hours);
			break;
		case 'h':
			if (((hours % 12) + 1) < 10) {
				sb.append(0).append((hours % 12) + 1);
			} else {
				sb.append((hours % 12) + 1);
			}
			break;
		case 'H':
			if (hours < 10) {
				sb.append(0).append(hours);
			} else {
				sb.append(hours);
			}
			break;
		case 'i':
			if (mins < 10) {
				sb.append(0).append(mins);
			} else {
				sb.append(mins);
			}
			break;
		case 's':
			if (secs < 10) {
				sb.append(0).append(secs);
			} else {
				sb.append(secs);
			}
			break;

		/*
		 * Cases for TimeZone specifications cannot be dealt presently as GWT
		 * does not support java.util.TimeZone and java.util.Date does not
		 * provide these features. case 'u': break; case 'e': break; case 'I':
		 * break; case 'O': break; case 'P': break; case 'T': break; case 'Z':
		 * break; case 'c': break; case 'r': break;
		 */
		case 'U':
			sb.append(cal.getTime() / 1000);
			break;
		case '\\':
			sb.append('\\');
			break;
		default:
			sb.append("?");
			break;
		}
	}

}
