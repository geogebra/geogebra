package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.geogebra.common.gui.view.spreadsheet.CellRange;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.StringUtil;

public class CopyPasteCutD extends CopyPasteCut {

	public CopyPasteCutD(App app) {
		super(app);
	}

	@Override
	public void copy(int column1, int row1, int column2, int row2,
			boolean skipGeoCopy) {

		/*
		 * disabled as we don't want commas when pasting from spreadsheet into
		 * other parts of GeoGebra eg input bar also see
		 * DataImport.parseExternalData()
		 * 
		 * //boolean changeDecimalSeparator = '.' != decimalSeparator; if
		 * (changeDecimalSeparator) { Log.debug(
		 * "changing decimal separator to: "+decimalSeparator); }
		 */

		// copy tab-delimited geo values into the external buffer
		if (getCellBufferStr() == null) {
			setCellBufferStr(new StringBuilder());
		} else {
			getCellBufferStr().setLength(0);
		}
		for (int row = row1; row <= row2; ++row) {
			for (int column = column1; column <= column2; ++column) {
				GeoElement value = RelativeCopy.getValue(app, column, row);
				if (value != null) {
					String valueStr = value
							.toValueString(StringTemplate.maxPrecision);

					getCellBufferStr().append(valueStr);

				}
				if (column != column2) {
					getCellBufferStr().append('\t');
				}
			}
			if (row != row2) {
				getCellBufferStr().append('\n');
			}
		}

		// store the tab-delimited values in the clipboard
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSelection = new StringSelection(
				getCellBufferStr().toString());
		clipboard.setContents(stringSelection, null);

		// store copies of the actual geos in the internal buffer
		if (skipGeoCopy) {
			setCellBufferGeo(null);
		} else {
			sourceColumn1 = column1;
			sourceRow1 = row1;
			setCellBufferGeo(RelativeCopy.getValues(app, column1, row1, column2,
					row2));
		}
	}

	@Override
	/** Paste data from the clipboard */
	public boolean paste(int column1, int row1, int column2, int row2) {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);

		return paste(column1, row1, column2, row2, contents);
	}

	/**
	 * Pastes data from given Transferable into the given spreadsheet cells.
	 * 
	 * @param column1
	 *            first column of the target cell range
	 * @param row1
	 *            first row of the target cell range
	 * @param column2
	 *            last column of the target cell range
	 * @param row2
	 *            last row of the target cell range
	 * @param contents
	 * @return
	 */
	public boolean paste(int column1, int row1, int column2, int row2,
			Transferable contents) {

		boolean succ = false;
		boolean isCSV = false;
		String transferString = null;

		// extract a String from the Transferable contents
		transferString = DataImportD.convertTransferableToString(contents);
		if (transferString == null) {
			return false;
		}

		isCSV = DataImportD.hasHTMLFlavor(contents);
		// System.out.println("transfer string: " + transferString);

		// test if the transfer string is the same as the internal cell copy
		// string. If true, then we have a tab-delimited list of cell geos and
		// can paste them with relative cell references
		boolean doInternalPaste = getCellBufferStr() != null
				&& transferString.equals(getCellBufferStr().toString());

		if (doInternalPaste && getCellBufferGeo() != null) {

			// use the internal field cellBufferGeo to paste geo copies
			// with relative cell references
			succ = pasteInternalMultiple(column1, row1, column2, row2);

		} else {

			// use the transferString data to create and paste new geos
			// into the target cells without relative cell references

			String[][] data = DataImport.parseExternalData(app, transferString,
					isCSV);
			succ = pasteExternalMultiple(data, column1, row1, column2, row2);

			// Application.debug("newline index "+buf.indexOf("\n"));
			// Application.debug("length "+buf.length());

		}

		return succ;
	}

	// default pasteFromFile: clear spreadsheet and then paste from upper left
	// corner
	public boolean pasteFromURL(URL url) {

		CellRange cr = new CellRange(app, 0, 0, 0, 0);
		return pasteFromURL(url, cr, true);

	}

	public boolean pasteFromURL(URL url, CellRange targetRange,
			boolean clearSpreadsheet) {

		// read file
		StringBuilder contents = new StringBuilder();

		boolean isCSV = getExtension(url.getFile()).equals("csv");

		try {
			InputStream is = url.openStream();
			BufferedReader input = new BufferedReader(
					new InputStreamReader(is, Charsets.getUtf8()));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();

			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}

		// System.out.println(dataFile.getName() + ": " + contents.capacity());

		boolean succ = true;

		String[][] data = DataImport.parseExternalData(app,
				contents.toString(), isCSV);

		if (data != null) {
			if (clearSpreadsheet) {
				deleteAll();
			}
			succ = pasteExternalMultiple(data, targetRange);
		} else {
			succ = false;
		}

		return succ;

	}

	/**
	 * Return the extension portion of the file's name.
	 * 
	 * @param f
	 * @return "ext" for file "filename.ext"
	 */
	private static String getExtension(String filename) {
		if (filename != null) {
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return StringUtil.toLowerCaseUS(filename.substring(i + 1));
			}
		}
		return null;
	}

}
