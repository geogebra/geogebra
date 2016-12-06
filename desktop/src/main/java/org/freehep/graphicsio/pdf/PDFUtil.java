// Copyright 2004, FreeHEP.
package org.freehep.graphicsio.pdf;

import java.util.Date;

import org.geogebra.common.jre.util.ScientificFormat;
import org.geogebra.common.kernel.commands.CmdGetTime;

/**
 * Utility functions for the PDFWriter. This class handles escaping of strings,
 * formatting of dates, ...
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFUtil.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFUtil implements PDFConstants {

	// static class
	private PDFUtil() {
	}

	public static String escape(String string) {
		StringBuffer escape = new StringBuffer();

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
			case '(':
			case ')':
			case '\\':
				escape.append('\\');
				escape.append(c);
				break;
			default:
				escape.append(c);
				break;
			}
		}
		return escape.toString();
	}

	public static String date(Date date) {

		// GeoGebra: change from Calendar to Date (GWT-friendly but no
		// timezones)
		// int offset = date.get(Calendar.ZONE_OFFSET)
		// + date.get(Calendar.DST_OFFSET);
		//
		// String tz;
		// if (offset == 0) {
		// tz = "Z";
		// } else {
		// DecimalFormat fmt = new DecimalFormat("00");
		// int tzh = Math.abs(offset / 3600000);
		// int tzm = Math.abs(offset % 3600000);
		// if (offset > 0) {
		// tz = "+" + fmt.format(tzh) + "'" + fmt.format(tzm) + "'";
		// } else {
		// tz = "-" + fmt.format(tzh) + "'" + fmt.format(tzm) + "'";
		// }
		// }

		// http://www.verypdf.com/pdfinfoeditor/pdf-date-format.htm
		// (D:YYYYMMDDHHmmSSOHH'mm')
		// eg D:19981223195200-08'00'

		// get this bit
		// YYYYMMDDHHmmSS
		String now = CmdGetTime.buildLocalizedDate("\\Y\\m\\d\\H\\i\\s", date,
				null);

		// assume GMT
		String tz = "+00'00'";

		// return "(D:" + dateFormat.format(date.getTime()) + tz + ")";
		return "(D:" + now + tz + ")";
	}

	private static final ScientificFormat scientific = new ScientificFormat(5,
			100, false);

	public static String fixedPrecision(double v) {
		return scientific.format(v);
	}

}
