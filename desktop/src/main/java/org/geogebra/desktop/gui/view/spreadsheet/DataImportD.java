package org.geogebra.desktop.gui.view.spreadsheet;

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

import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;

/**
 * Utility class with methods to handle importing data into the spreadsheet.
 * 
 * @author G. Sturr
 * 
 */
public class DataImportD extends DataImport {

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
				transferString = DataImportD.convertHTMLTableToCSV(
						(String) contents.getTransferData(HTMLflavor));
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
				Log.debug("transferable has no String");
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
						for (int i = 0; i < data.length; i++) {
							if (data[i] == ',') {
								containsComma = true;
							}
						}

						if (containsComma && (data[0] != '"'
								|| data[data.length - 1] != '"')) {
							appendQuotes = true;
						}

						if (appendQuotes) {
							sbHTML.append('"');
						}
						for (int i = 0; i < data.length; i++) {
							sbHTML.append(data[i]);
						}
						if (appendQuotes) {
							sbHTML.append('"');
						}
					}
				}

				@Override
				public void handleStartTag(HTML.Tag tag,
						MutableAttributeSet attrSet, int pos) {
					if (tag == HTML.Tag.TABLE) {
						// Application.debug("table");
						if (foundTable) {
							finished = true;
						}
						foundTable = true;
						firstColumn = true;
						sbHTML.setLength(0);
					} else if (foundTable && tag == HTML.Tag.TR) {
						// Application.debug("TR");
						if (!firstColumn) {
							sbHTML.append("\n");
						}
						firstInRow = true;
						firstColumn = false;
					} else if (foundTable
							&& (tag == HTML.Tag.TD || tag == HTML.Tag.TH)) {
						// Application.debug("TD");
						if (!firstInRow) {
							sbHTML.append(",");
						}
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
			Log.debug("clipboard: no HTML");
		}

		if (sbHTML.length() != 0) {
			// found HTML table to paste as CSV
			return sbHTML.toString();
		}
		return null;
	}

	static String[] getDefaultSeparators(App app) {

		String[] separators = new String[2];

		// Get decimal and thousands separators
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(
				((AppD) app).getLocale());
		separators[0] = Character.toString(dfs.getDecimalSeparator());
		separators[1] = Character.toString(dfs.getGroupingSeparator());

		return separators;
	}

}
