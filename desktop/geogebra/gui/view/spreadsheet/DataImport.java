package geogebra.gui.view.spreadsheet;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import au.com.bytecode.opencsv.CSVParser;

/**
 * Utility class with methods to handle importing data into the spreadsheet.
 * 
 * 
 * @author G. Sturr
 * 
 */
public class DataImport {

	HashSet<String> decimalDotLocale = newHashSet("de");
	static CSVParser commaParser, tabParser;

	static DataFlavor HTMLflavor;
	static {
		try {
			HTMLflavor = new DataFlavor("text/html;class=java.lang.String");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static HashSet<String> newHashSet(String... strings) {
		HashSet<String> set = new HashSet<String>();

		for (String s : strings) {
			set.add(s);
		}
		return set;
	}

	public static boolean hasHTMLFlavor(Transferable t) {
		return t.isDataFlavorSupported(HTMLflavor);
	}

	/**
	 * Returns a string object extracted from the given Transferable. If the
	 * DataFlavor is "text/html;class=java.lang.String" an attempt is made to
	 * convert the transferable object into a CSV string (e.g. data transferred
	 * from Excel)
	 * 
	 * @param contents
	 * @return
	 */
	public static String convertTransferableToString(Transferable contents) {

		String transferString = null;

		// exit if no content
		if (contents == null) {
			return null;
		}

		// print available data formats in Transferable contents
		for (int i = 0; i < contents.getTransferDataFlavors().length; i++) {
			// System.out.println(contents.getTransferDataFlavors()[i]);
		}

		// try to extract a string from the Transferable
		try {

			// System.out.println("is HTML? " +
			// contents.isDataFlavorSupported(HTMLflavor));

			if (hasHTMLFlavor(contents)) {
				transferString = DataImport
						.convertHTMLTableToCSV((String) contents
								.getTransferData(HTMLflavor));
			}

		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		transferString = null;

		// no HTML found, try plain text
		if (transferString == null
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				transferString = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
				// Application.debug("pasting from String: "+buf);
			} catch (Exception ex) {
				AbstractApplication.debug("transferable has no String");
				// ex.printStackTrace();
				// app.showError(ex.getMessage());
			}
		}

		return transferString;
	}

	/**
	 * Converts HTML table into CSV
	 */
	public static String convertHTMLTableToCSV(String HTMLTableString) {

		final StringBuilder sbHTML = new StringBuilder();
		// System.out.println("html: " + HTMLTableString);
		try {
			// prepare the parser
			HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
				boolean foundTable = false;
				boolean firstInRow = true;
				boolean firstColumn = true;
				boolean finished = false;

				@Override
				public void handleText(char[] data, int pos) {

					if (foundTable && !finished) {
						// if string contains a comma, surround the string with
						// quotes ""
						boolean containsComma = false;
						boolean appendQuotes = false;
						for (int i = 0; i < data.length; i++)
							if (data[i] == ',')
								containsComma = true;

						if (containsComma
								&& (data[0] != '"' || data[data.length - 1] != '"'))
							appendQuotes = true;

						if (appendQuotes)
							sbHTML.append('"');
						for (int i = 0; i < data.length; i++)
							sbHTML.append(data[i]);
						if (appendQuotes)
							sbHTML.append('"');
					}
				}

				@Override
				public void handleStartTag(HTML.Tag tag,
						MutableAttributeSet attrSet, int pos) {
					if (tag == HTML.Tag.TABLE) {
						// Application.debug("table");
						if (foundTable)
							finished = true;
						foundTable = true;
						firstColumn = true;
						sbHTML.setLength(0);
					} else if (foundTable && tag == HTML.Tag.TR) {
						// Application.debug("TR");
						if (!firstColumn)
							sbHTML.append("\n");
						firstInRow = true;
						firstColumn = false;
					} else if (foundTable
							&& (tag == HTML.Tag.TD || tag == HTML.Tag.TH)) {
						// Application.debug("TD");
						if (!firstInRow)
							sbHTML.append(",");
						firstInRow = false;
					} else if (!foundTable) {
						// Application.debug("TR without table");
						sbHTML.setLength(0);
						if (tag == HTML.Tag.TR) {
							foundTable = true; // HTML fragment without <TABLE>
							firstInRow = true;
							firstColumn = false;
						}
					}

				}
			};

			// parse the text
			Reader reader = new StringReader(HTMLTableString);
			new ParserDelegator().parse(reader, callback, true);
		}

		catch (Exception e) {
			AbstractApplication.debug("clipboard: no HTML");
		}

		if (sbHTML.length() != 0) // found HTML table to paste as CSV
			return sbHTML.toString();
		else
			return null;
	}

	/**
	 * Parses external (non-ggb) data.
	 * 
	 * @param app
	 * @param source
	 *            string to be parsed
	 * @param isCSV
	 *            flag to determine of the data should be handled as comma
	 *            delimited
	 * @return 2D string array with values formatted for the spreadsheet.
	 */
	public static String[][] parseExternalData(AbstractApplication app,
			String source, boolean isCSV) {

		String[][] data;

		if (isCSV) {
			// convert the given CSV file string into a 2D string array
			data = parseCSVdata(source);
		} else {
			// convert the given string into a 2D array defined by tab or
			// comma delimiters (auto-detected)
			data = parseTabData(source);
		}

		// traverse the 2D array to prepare strings for the spreadsheet
		for (int i = 0; i < data.length; i++) {
			for (int k = 0; k < data[i].length; k++) {

				// prevent empty string conversion to "null"
				if (data[i][k].length() == 0) {
					data[i][k] = " ";
				}

				// remove localized number formatting
				// e.g. 3,400 ---> 3400 or 3,400 --> 3.400 depending on locale
				data[i][k] = adjustNumberString(app, data[i][k]);
			}
		}

		return data;

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
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		return data;
	}

	public static String[][] parseTabData(String input) {

		// Application.debug("parse data: " + input);

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

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * Tests if a given string represents a number. If true then an unformatted
	 * string representation of the number is returned (e.g. 3,200 --> 3200).
	 * Otherwise the original string is returned.
	 * 
	 * The method uses java's NumberFormat class to test for a number by parsing
	 * the string using format rules for the current locale.
	 * 
	 */
	private static String adjustNumberString2(AbstractApplication app, String s) {

		boolean isNumber;

		// attempt to parse the number using NumberFormat with current locale
		NumberFormat nf = NumberFormat.getInstance(((Application) app)
				.getLocale());
		ParsePosition pp = new ParsePosition(0);
		Number n = nf.parse(s, pp);

		// test: string is a number if parser uses the entire string
		isNumber = (s.length() == pp.getIndex());

		String radixMark = ",";
		String groupingMark = " ";

		String s2 = s.replaceAll(groupingMark, "").replaceFirst(radixMark, ".");

		// if the string is a number return it without formatting
		// if (isNumber) {
		// return n + "";
		// }

		System.out.println("====== testing this string: " + s2);
		Double nv = app.getKernel().getAlgebraProcessor()
				.evaluateToDouble(s2, true);
		System.out.println("result number: " + nv);

		if (nv != Double.NaN)
			return nv + "";

		// otherwise return the string
		return s;

	}

	private static String adjustNumberString(AbstractApplication app, String s) {

		// set symbols for decimal point and thousands separator
		// TODO: use locale to set these strings
		String radixMark = ",";
		String groupingMark = " ";

		// remove thousands separators
		// and replace radix symbol with dot
		String s2 = s.replaceAll(groupingMark, "").replaceFirst(radixMark, ".");

		// if(Character.isDigit(c) || c == '.'
		// || c=='-' || c=='+' || c == Unicode.degreeChar || c == '\u2212') s2 =
		// null;

		// parse to number
		NumberValue nv = app.getKernel().getAlgebraProcessor()
				.evaluateToNumeric(s2, true);

		// if we have a number return this as an unformatted string
		if (nv != null)
			return nv.getDouble() + "";

		// otherwise return the given string
		return s;

	}

}
