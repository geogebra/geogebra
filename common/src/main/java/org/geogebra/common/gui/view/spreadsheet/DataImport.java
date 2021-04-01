package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.main.App;
import org.geogebra.common.util.opencsv.CSVException;
import org.geogebra.common.util.opencsv.CSVParser;
import org.gwtproject.regexp.shared.RegExp;

/**
 * Utility class with methods to handle importing data into the spreadsheet.
 * 
 * @author G. Sturr
 * 
 */

public class DataImport {

	static CSVParser commaParser;
	static CSVParser tabParser;

	/*
	 * disabled option to change as we don't want commas when pasting from
	 * spreadsheet into other parts of GeoGebra eg input bar also see
	 * CopyPasteCutD.copy()
	 */
	final static String decimalSeparator = ".";
	// match numbers with commas every 3 digits eg 1,234
	// 1,234,567
	// 12,456
	// 123,566
	// -123,566
	// don't match
	// 123
	// 12
	// 1
	// 0,123
	// 123,456789
	final private static RegExp regex = RegExp
			.compile("^-?\\d?\\d?\\d,(\\d\\d\\d,)*\\d\\d\\d$");
	
	/**
	 * Parses external non-ggb data.
	 * 
	 * @param app
	 *            application
	 * @param source
	 *            string to be parsed
	 * @param isCSV
	 *            true = comma delimited parsing, false = tab delimited parsing
	 * @return 2D string array with values formatted for the spreadsheet.
	 */
	public static String[][] parseExternalData(App app, String source,
			boolean isCSV) {

		String[][] data;

		// ignore isCSV parameter, just check for <Tab> \t
		if (source.indexOf('\t') == -1) {
			// convert the given string into a 2D array defined by comma
			// delimiters
			data = parseCSVdata(source);
		} else {
			// convert the given string into a 2D array defined by tab
			// delimiters
			data = parseTabData(source);
		}

		int maxLength = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i].length > maxLength) {
				maxLength = data[i].length;
			}
		}

		// copy the data into new array
		// so that we return an array with all rows the same length
		String[][] dataRet = new String[data.length][maxLength];

		// traverse the 2D array to prepare strings for the spreadsheet
		for (int i = 0; i < data.length; i++) {
			for (int k = 0; k < maxLength; k++) {

				if (data[i].length > k) {

					// prevent empty string conversion to "null"
					if (data[i][k].length() == 0) {
						data[i][k] = " ";
					}

					// remove localized number formatting
					// e.g. 3,400 ---> 3400 or 3,4567 --> 3.4567
					dataRet[i][k] = adjustNumberString(data[i][k]);

				} else {
					dataRet[i][k] = " ";
				}
			}
		}

		return dataRet;

	}

	private static CSVParser getCommaParser() {
		if (commaParser == null) {
			commaParser = new CSVParser();
		}
		return commaParser;
	}

	private static CSVParser getTabParser() {
		if (tabParser == null) {
			tabParser = new CSVParser('\t');
		}
		return tabParser;
	}

	private static String[][] parseCSVdata(String input) {

		// split lines using "\r?\n|\r" to handle win/linux/mac cases
		String[] lines = input.split("\r?\n|\r", -1);
		if (lines.length == 0) {
			return null;
		}

		// create 2D data array
		int numLines = lines[lines.length - 1].length() == 0 ? lines.length - 1
				: lines.length;
		String[][] data = new String[numLines][];

		// parse each line and add to data array
		for (int i = 0; i < numLines; ++i) {
			try {
				data[i] = getCommaParser().parseLineMulti(lines[i]);
			} catch (CSVException e) {
				e.printStackTrace();
				return null;
			}
		}

		return data;
	}

	/**
	 * @param input
	 *            CSV string
	 * @return tabular data
	 */
	public static String[][] parseTabData(String input) {
		// split lines using "\r?\n|\r" to handle win/linux/mac cases
		String[] lines = input.split("\r?\n|\r", -1);
		if (lines.length == 0) {
			return null;
		}

		// create 2D data array
		int numLines = lines[lines.length - 1].length() == 0 ? lines.length - 1
				: lines.length;
		String[][] data = new String[numLines][];

		// parse each line and add to data array
		for (int i = 0; i < numLines; ++i) {

			// trim() removes tabs which we need
			// lines[i] = StringUtil.trimSpaces(lines[i]);

			try {
				// .out.println("parse line: " + lines[i]);
				data[i] = getTabParser().parseLineMulti(lines[i]);

			} catch (CSVException e) {
				e.printStackTrace();
			}
		}

		return data;
	}

	/**
	 * Returns an unformatted number string (e.g. "1,234,567" --> "1234567")
	 * otherwise the comma is replaced with a . eg 1,234567 -> 1.234567
	 * 
	 * Note: 1,234 is ambiguous, convert to 1234
	 * 
	 * if the given string is a number that Geogebra's parser recognizes. If
	 * cannot be parsed to a number, then the original string is returned.
	 */
	private static String adjustNumberString(String s) {

		if (s == null || "".equals(s)) {
			return s;
		}

		String s2 = s;

		// System.out.println("====================");
		// System.out.println(decimalSeparator + " | " + groupingSeparator);
		// System.out.println("test string: " + s);

		if (regex.test(s)) {
			// change 1,234,567 to 1234567
			s2 = s2.replace(",", "");
		} else {
			// change 0,12345 to 012345
			s2 = s2.replace(",", ".");
		}

		// System.out.println("converted string: " + s2);
		// System.out.println("is number: " + RelativeCopy.isNumber(s2));

		if (RelativeCopy.isNumber(s2)) {
			return s2;
		}

		return s;

	}
}
