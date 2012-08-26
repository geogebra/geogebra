package geogebra.gui.view.spreadsheet;

import geogebra.common.gui.view.spreadsheet.RelativeCopy;
import geogebra.common.main.App;
import geogebra.gui.view.opencsv.CSVParser;
import geogebra.main.AppD;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DecimalFormatSymbols;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;


/**
 * Utility class with methods to handle importing data into the spreadsheet.
 * 
 * @author G. Sturr
 * 
 */
public class DataImport {

	static CSVParser commaParser, tabParser;

	static DataFlavor HTMLflavor;
	static {
		try {
			HTMLflavor = new DataFlavor("text/html;class=java.lang.String");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
				// transferString = (String) contents
				// .getTransferData(DataFlavor.stringFlavor);
			}

		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// no HTML found, try plain text
		if (transferString == null
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				transferString = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
				// Application.debug("pasting from String: "+buf);
			} catch (Exception ex) {
				App.debug("transferable has no String");
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
			App.debug("clipboard: no HTML");
		}

		if (sbHTML.length() != 0) {
			// found HTML table to paste as CSV
			return sbHTML.toString();
		}
		return null;
	}

	/**
	 * Parses external non-ggb data.
	 * 
	 * @param app
	 * @param source
	 *            string to be parsed
	 * @param separator
	 *            separator[0] = decimal separator, separator[1] = grouping
	 *            separator; if null then defaults for the locale will be used
	 * @param isCSV
	 *            true = comma delimited parsing, false = tab delimited parsing
	 * @return 2D string array with values formatted for the spreadsheet.
	 */
	public static String[][] parseExternalData(App app, String source,
			String[] separator, boolean isCSV) {

		String decimalSeparator, groupingSeparator;
		if (separator == null) {
			String[] defaultSeparator = getDefaultSeparators(app);
			decimalSeparator = defaultSeparator[0];
			groupingSeparator = defaultSeparator[1];
		} else {
			decimalSeparator = separator[0];
			groupingSeparator = separator[1];
		}

		String[][] data;

		if (isCSV) {
			// convert the given string into a 2D array defined by comma
			// delimiters
			data = parseCSVdata(source);
		} else {
			// convert the given string into a 2D array defined by tab
			// delimiters
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
				data[i][k] = adjustNumberString(data[i][k], decimalSeparator,
						groupingSeparator);
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
	 * Returns an unformatted number string (e.g. "3,200" --> "3200") if the
	 * given string is a number that Geogebra's parser recognizes. If cannot be
	 * parsed to a number, then the original string is returned.
	 */
	private static String adjustNumberString(String s, String decimalSeparator,
			String groupingSeparator) {

		if (s == null || s.equals(""))
			return s;

		// System.out.println("====================");
		// System.out.println(decimalSeparator + " | " + groupingSeparator);
		// System.out.println("test string: " + s);
		String s2 = s.replaceAll(groupingSeparator, "");
		if (!decimalSeparator.equals("."))
			s2 = s2.replaceAll(decimalSeparator, ".");

		// System.out.println("converted string: " + s2);
		// System.out.println("is number: " + RelativeCopy.isNumber(s2));

		if (RelativeCopy.isNumber(s2)) {
			return s2;
		}

		return s;

	}

	private static String[] getDefaultSeparators(App app) {

		String[] separators = new String[2];

		// Get decimal and thousands separators
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(
				((AppD) app).getLocale());
		separators[0] = Character.toString(dfs.getDecimalSeparator());
		separators[1] = Character.toString(dfs.getGroupingSeparator());

		return separators;
	}

}
