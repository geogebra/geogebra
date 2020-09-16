package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.util.CopyPasteW;

public class CopyPasteCutW extends CopyPasteCut {

	public CopyPasteCutW(App app) {
		super(app);
	}

	/**
	 * Just copying the selection as string text format, independently!
	 * 
	 * @return selection content as tab separated string
	 */
	public String copyString(int column1, int row1, int column2, int row2) {
		StringBuilder cellBufferStrLoc = new StringBuilder();
		for (int row = row1; row <= row2; ++row) {
			for (int column = column1; column <= column2; ++column) {
				GeoElement value = RelativeCopy.getValue(app, column, row);
				if (value != null) {
					String valueString = value
							.toValueString(StringTemplate.maxPrecision);

					valueString = removeTrailingZeros(valueString);

					cellBufferStrLoc.append(valueString);
				}
				if (column != column2) {
					cellBufferStrLoc.append('\t');
				}
			}
			if (row != row2) {
				cellBufferStrLoc.append('\n');
			}
		}
		return new String(cellBufferStrLoc);
	}

	private String removeTrailingZeros(String valueString) {
		int indx = valueString
				.indexOf(app.getKernel().getLocalization().getDecimalPoint());
		if (indx > -1) {
			int end = valueString.length() - 1;
			// only in this case, we should remove trailing zeroes!
			while (valueString.charAt(end) == '0') {
				end--;
			}
			if (end == indx) {
				end--;
			}
			return valueString.substring(0, end + 1);
		}
		return valueString;
	}

	@Override
	public void copy(int column1, int row1, int column2, int row2,
			boolean skipGeoCopy) {
		copy(column1, row1, column2, row2, skipGeoCopy, false);
	}

	public static native boolean checkClipboardSupported() /*-{
		if ($doc.queryCommandSupported("copy")) {
			return true;
		}
		return false;
	}-*/;

	/**
	 * @param column1
	 *            left column
	 * @param row1
	 *            top row
	 * @param column2
	 *            wright column
	 * @param row2
	 *            bottom row
	 * @param skipGeoCopy
	 *            wehether to skip updating geo buffer
	 * @param nat
	 *            called from native event?
	 */
	public void copy(int column1, int row1, int column2, int row2,
			boolean skipGeoCopy, boolean nat) {
		sourceColumn1 = column1;
		sourceRow1 = row1;

		// copy tab-delimited geo values into the external buffer
		if (getCellBufferStr() == null) {
			setCellBufferStr(new StringBuilder());
		} else {
			getCellBufferStr().setLength(0);
		}
		for (int row = row1; row <= row2; ++row) {
			for (int column = column1; column <= column2; ++column) {
				GeoElement geo = RelativeCopy.getValue(app, column, row);
				if (geo != null) {
					getCellBufferStr().append(geo.toValueString(StringTemplate.defaultTemplate));
				}
				if (column != column2) {
					getCellBufferStr().append('\t');
				}
			}
			if (row != row2) {
				getCellBufferStr().append('\n');
			}
		}

		// a clipboard inside this application is better than nothing
		if (!nat) {
			app.getCopyPaste().copyTextToSystemClipboard(new String(getCellBufferStr()));
			getTable().editCellAt(sourceColumn1, sourceRow1);
		}

		// store copies of the actual geos in the internal buffer
		if (skipGeoCopy) {
			setCellBufferGeo(null);
		} else {
			setCellBufferGeo(RelativeCopy.getValues(app, column1, row1, column2,
					row2));
		}
	}

	@Override
	public boolean paste(int column1, int row1, int column2, int row2) {
		CopyPasteW.pasteNative(app, (content) -> {
			getTable().editCellAt(sourceColumn1, sourceRow1);
			paste(column1, row1, column2, row2, content);
		});

		return true;
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
	 * 	          string to paste into cells
	 * @return
	 *            true if pasting was successful
	 */
	public boolean paste(int column1, int row1, int column2, int row2,
			String contents) {

		boolean succ = false;
		//boolean isCSV = false;
		String transferString = null;

		// extract a String from the Transferable contents
		transferString = contents;
	
		if (transferString == null) {
			return false;
		}

		// isCSV = DataImport.hasHTMLFlavor(contents);
		// App.debug("transfer string: " + transferString);

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
			
			boolean isCSV = false;

			String[][] data = DataImport.parseExternalData(app, transferString, isCSV);
			succ = pasteExternalMultiple(data, column1, row1, column2, row2);
		}

		return succ;
	}

	/**
	 * Just for the copy, removing redundancy runtime
	 * 
	 * @return if at least one object was deleted
	 */
	public boolean cut(int column1, int row1, int column2, int row2, boolean nat) {

		copy(column1, row1, column2, row2, false, nat);
		// null out the external buffer so that paste will not do a relative
		// copy
		setCellBufferStr(null);
		return delete(column1, row1, column2, row2);
	}
}
