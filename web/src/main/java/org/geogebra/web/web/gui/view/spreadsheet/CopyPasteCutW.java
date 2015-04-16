package org.geogebra.web.web.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

import com.google.gwt.dom.client.Element;

public class CopyPasteCutW extends CopyPasteCut {

	private static String staticClipboardString = "";

	public CopyPasteCutW(App app) {
		super(app);
	}

	/**
	 * Just copying the selection as string text format, independently!
	 */
	public String copyString(int column1, int row1, int column2, int row2) {
		StringBuilder cellBufferStrLoc = new StringBuilder();
		for (int row = row1; row <= row2; ++row) {
			for (int column = column1; column <= column2; ++column) {
				GeoElement value = RelativeCopy.getValue(app, column, row);
				if (value != null) {
					String valueString = value
							.toValueString(StringTemplate.maxPrecision);

					// for aesthetical copying, it is also good to remove
					// trailing zeroes (zero is nothing anyway):
					int indx = valueString.indexOf(app.getKernel().getLocalization().unicodeDecimalPoint);
					if (indx > -1) {
						int end = valueString.length() - 1;
						// only in this case, we should remove trailing zeroes!
						while (valueString.charAt(end) == '0')
							end--;
						if (end == indx)
							end--;
						valueString = valueString.substring(0, end + 1);
					}

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

	@Override
	public void copy(int column1, int row1, int column2, int row2,
			boolean skipGeoCopy) {
		copy(column1, row1, column2, row2, skipGeoCopy, false);
	}

	public void copy(int column1, int row1, int column2, int row2,
			boolean skipGeoCopy, boolean nat) {
		sourceColumn1 = column1;
		sourceRow1 = row1;

		// copy tab-delimited geo values into the external buffer
		if (cellBufferStr == null) {
			cellBufferStr = new StringBuilder();
		} else {
			cellBufferStr.setLength(0);
		}
		for (int row = row1; row <= row2; ++row) {
			for (int column = column1; column <= column2; ++column) {
				GeoElement value = RelativeCopy.getValue(app, column, row);
				if (value != null) {
					cellBufferStr.append(value
							.toValueString(StringTemplate.maxPrecision));
				}
				if (column != column2) {
					cellBufferStr.append('\t');
				}
			}
			if (row != row2) {
				cellBufferStr.append('\n');
			}
		}

		// store the tab-delimited values in the clipboard
		/*Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSelection = new StringSelection(cellBufferStr);
		clipboard.setContents(stringSelection, null);*/

		// a clipboard inside this application is better than nothing
		//staticClipboardString = new String(cellBufferStr);
		if (nat) {
			// if called from native event, setting clipboard contents
			// is not crucial, and redundant/harmful in IE...
			setInternalClipboardContents(new String(cellBufferStr));
		} else {
			setClipboardContents(new String(cellBufferStr));
		}

		// store copies of the actual geos in the internal buffer
		if (skipGeoCopy) {
			cellBufferGeo = null;
		} else {
			cellBufferGeo = RelativeCopy.getValues(app, column1, row1, column2,
					row2);
		}
	}
	


	@Override
	/** Paste data from the clipboard */
	public boolean paste(int column1, int row1, int column2, int row2) {
		/*Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);*/
		String contents = getClipboardContents();
		return paste( column1,  row1,  column2,  row2,  contents);
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
		transferString = contents;//DataImport.convertTransferableToString(contents);
		if (transferString == null)
			return false;

		// isCSV = DataImport.hasHTMLFlavor(contents);
		// App.debug("transfer string: " + transferString);

		// test if the transfer string is the same as the internal cell copy
		// string. If true, then we have a tab-delimited list of cell geos and
		// can paste them with relative cell references
		boolean doInternalPaste = cellBufferStr != null
				&& transferString.equals(cellBufferStr.toString());

		if (doInternalPaste && cellBufferGeo != null) {

			// use the internal field cellBufferGeo to paste geo copies
			// with relative cell references
			succ = pasteInternalMultiple(column1, row1, column2, row2);

		} else {

			// use the transferString data to create and paste new geos
			// into the target cells without relative cell references
			
			boolean isCSV = false;

			String[][] data = DataImport.parseExternalData(app, transferString, isCSV);
			succ = pasteExternalMultiple(data, column1, row1, column2, row2);

			/* old hack
			// in theory
			// special case: hacking in Web, input is coming from us
			String[] data0 = transferString.split("\n");
			String[] data00 = data0[0].split("\t");
			String[][] data = new String[data0.length][data00.length];
			for (int i = 0; i < data0.length; i++)
				data[i] = data0[i].split("\t");
			// String[][] data = DataImportW.parseExternalData(app,
			// transferString, null,
			// isCSV);
			succ = pasteExternalMultiple(data, column1, row1, column2, row2);*/

			// Application.debug("newline index "+buf.indexOf("\n"));
			// Application.debug("length "+buf.length());
		}

		return succ;
	}

	/**
	 * Just for the copy, removing redundancy runtime
	 */
	public boolean cut(int column1, int row1, int column2, int row2, boolean nat) {

		copy(column1, row1, column2, row2, false, nat);
		// null out the external buffer so that paste will not do a relative
		// copy
		cellBufferStr = null;
		return delete(column1, row1, column2, row2);
	}

	/**
	 * When using the default functionality of the browser,
	 * getting/setting clipboard contents is solved quite well,
	 * and it uses the external clipboard. Thus this method is
	 * redundant in case it's called from paste event! For the
	 * same reason, it is not called from there.
	 * 
	 * However, we may call the same thing from GeoGebraWeb
	 * context menu, and in that case the form of this method
	 * is just Okay.
	 * 
	 * @return String
	 */
	private String getClipboardContents() {
		String clipboard = null;
		if (isChromeWebapp()) { // use chrome web app paste API
			clipboard = getSystemClipboardChromeWebapp();
			getTable().editCellAt(sourceColumn1, sourceRow1); // reset focus
		} else if (isInternetExplorer()) {
			clipboard = getSystemClipboardIE();
		} else { // use internal clipboard
			clipboard = staticClipboardString;
		}
		return clipboard;
	}

	/**
	 * When using the default functionality of the browser,
	 * getting/setting clipboard contents is solved quite well,
	 * and it uses the external clipboard. Thus this method is
	 * redundant in case it's called from copy or cut events,
	 * maybe does the same thing twice in Internet Explorer.
	 * 
	 * However, we may call the same thing from GeoGebraWeb
	 * context menu, and in that case the form of this method
	 * is just Okay. Note that in order to make the
	 * staticClipboardString function well from context menu,
	 * we need to set it every case this method is called,
	 * and every case the cut/paste events happen.  
	 * 
	 * @param value String
	 */
	private void setClipboardContents(String value) {
		if (isChromeWebapp()) { // use chrome web app copy API
			copyToSystemClipboardChromeWebapp(value);
			getTable().editCellAt(sourceColumn1, sourceRow1); // reset focus
		} else if (isInternetExplorer()) {
			//App.debug("is IE");
			copyToSystemClipboardIE(value);
		}
		// use internal clipboard too, every time
		staticClipboardString = value;
	}

	/**
	 * As copying to system clipboard is supposed to have done
	 * @param value String
	 */
	private void setInternalClipboardContents(String value) {
		staticClipboardString = value;
	}

	private static native boolean isChromeWebapp() /*-{
		// check if the app is running in chrome and is installed (has an id)
		// the function is defined in app.html
		return $doc.isChromeWebapp();
	}-*/;
	
	private static native boolean isInternetExplorer() /*-{
		// check if app is running in IE5 or greater
		// clipboardData object is available from IE5 and onwards
		var userAgent = $wnd.navigator.userAgent;
		if ((userAgent.indexOf('MSIE ') > -1)
				|| (userAgent.indexOf('Trident/') > -1)) {
			return true;
		}
		return false;
	}-*/;
	
	private static native Element getHiddenTextArea() /*-{
		var hiddenTextArea = $doc.getElementById('hiddenCopyPasteTextArea');
		if (!hiddenTextArea) {
			hiddenTextArea = $doc.createElement("textarea");
			hiddenTextArea.id = 'hiddenCopyPasteTextArea';
			hiddenTextArea.style.position = 'absolute';
			hiddenTextArea.style.zIndex = '100';
			hiddenTextArea.style.left = '-1000px';
			$doc.getElementsByTagName('body')[0].appendChild(hiddenTextArea);
		}
		//hiddenTextArea.value = '';
		return hiddenTextArea;
	}-*/;
	
	private static native String getSystemClipboardChromeWebapp() /*-{
		var copyFrom = @org.geogebra.web.web.gui.view.spreadsheet.CopyPasteCutW::getHiddenTextArea()();
		copyFrom.select();
		$doc.execCommand('paste');
		var contents = copyFrom.value;
		return contents;
	}-*/;

	private static native void copyToSystemClipboardChromeWebapp(String value) /*-{
		var copyFrom = @org.geogebra.web.web.gui.view.spreadsheet.CopyPasteCutW::getHiddenTextArea()();
		copyFrom.value = value;
		copyFrom.select();
		$doc.execCommand('copy');
	}-*/;
	
	private static native String getSystemClipboardIE() /*-{
		return $wnd.clipboardData.getData('Text');
	}-*/;

	private static native void copyToSystemClipboardIE(String value) /*-{
		return $wnd.clipboardData.setData('Text', value);
	}-*/;

	// default pasteFromFile: clear spreadsheet and then paste from upper left
	// corner
	/*public boolean pasteFromURL(URL url) {

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
			BufferedReader input = new BufferedReader(new InputStreamReader(is));
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

		// App.debug(dataFile.getName() + ": " + contents.capacity());

		boolean succ = true;

		String[][] data = DataImport.parseExternalData(app,
				contents.toString(), null, isCSV);

		if (data != null) {
			if (clearSpreadsheet)
				deleteAll();
			succ = pasteExternalMultiple(data, targetRange);
		} else {
			succ = false;
		}

		return succ;

	}*/
	
	/**
	 * Return the extension portion of the file's name.
	 * 
	 * @param f
	 * @return "ext" for file "filename.ext"
	 */
	/*private static String getExtension(String filename) {
		if (filename != null) {
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1)
				return filename.substring(i + 1).toLowerCase(Locale.US);
		}
		return null;
	}*/


}
