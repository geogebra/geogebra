package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.web.html5.util.CopyPasteW;

import elemental2.core.Function;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

public class CopyPasteCutW extends CopyPasteCut {

	public CopyPasteCutW(App app) {
		super(app);
	}

	@Override
	public void copy(int column1, int row1, int column2, int row2,
			boolean skipGeoCopy) {
		copy(column1, row1, column2, row2, skipGeoCopy, false);
	}

	/**
	 * @return whether copy to clipboard is supported
	 */
	public static boolean checkClipboardSupported() {
		Function commandCheck = Js.uncheckedCast(Js.asPropertyMap(DomGlobal.document)
				.get("queryCommandSupported")) ;
		return Js.isTruthy(commandCheck.call(DomGlobal.document, "copy"));
	}

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
		String tsv = copyStringToBuffer(column1, row1, column2, row2);

		// a clipboard inside this application is better than nothing
		if (!nat) {
			app.getCopyPaste().copyTextToSystemClipboard(tsv);
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
	 *            string to paste into cells
	 * @return
	 *            true if pasting was successful
	 */
	public boolean paste(int column1, int row1, int column2, int row2,
			String contents) {

		boolean succ;
		//boolean isCSV = false;

		if (contents == null) {
			return false;
		}

		// isCSV = DataImport.hasHTMLFlavor(contents);
		// App.debug("transfer string: " + transferString);

		// test if the transfer string is the same as the internal cell copy
		// string. If true, then we have a tab-delimited list of cell geos and
		// can paste them with relative cell references

		boolean doInternalPaste = isCellBuffer(contents);
	
		if (doInternalPaste && getCellBufferGeo() != null) {

			// use the internal field cellBufferGeo to paste geo copies
			// with relative cell references
			succ = pasteInternalMultiple(column1, row1, column2, row2);

		} else {

			// use the transferString data to create and paste new geos
			// into the target cells without relative cell references
			
			boolean isCSV = false;

			String[][] data = DataImport.parseExternalData(app, contents, isCSV);
			succ = pasteExternalMultiple(data, new TabularRange(row1, column1, row2, column2));
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
		resetCellBuffer();
		return delete(column1, row1, column2, row2);
	}
}
