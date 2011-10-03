
package geogebra.gui.view.spreadsheet;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class CopyPasteCut {

	// ggb support classes
	protected Kernel kernel;
	protected Application app;
	protected MyTable table;
	protected DefaultTableModel tableModel;
	protected SpreadsheetView view;


	/**
	 * Stores copied cell geo values as a tab-delimited string.
	 */
	protected String cellBufferStr;


	/**
	 * Stores copied cell geos as GeoElement[columns][rows]
	 */
	protected GeoElement[][] cellBufferGeo;


	/**
	 * Records the first row and first column of the current cell range copy source
	 */
	protected int sourceColumn1, sourceRow1;



	/**
	 * Stores construction index values while performing a paste
	 */
	private Object [] constructionIndexes;



	/***************************************
	 * Constructor
	 */
	public CopyPasteCut(MyTable table0, Kernel kernel0) {

		table = table0;
		tableModel = (DefaultTableModel) table.getModel();
		kernel = kernel0;	
		app = kernel.getApplication();

		view = table.getView();

	}


	/**
	 * Combines the GeoElement.toValueStrings from a given block of cell geos
	 * into a single tab-delimited string. This string is stored in (1) the
	 * global String field cellBufferStr and (2) the system clipboard.
	 * 
	 * If skipGeoCopy = false, the geos are also stored in the global
	 * GeoElement[][] field cellBufferGeo
	 * 
	 * The cell block is defined by upper-left corner (column1, row1) 
	 * and lower left corner (column2, row2)
	 * 
	 * @param column1
	 * @param row1
	 * @param column2
	 * @param row2
	 * @param skipGeoCopy
	 */
	public void copy(int column1, int row1, int column2, int row2, boolean skipGeoCopy) {

		// copy tab-delimited geo values into the external buffer 
		cellBufferStr = "";
		for (int row = row1; row <= row2; ++ row) {
			for (int column = column1; column <= column2; ++ column) {
				GeoElement value = RelativeCopy.getValue(table, column, row);
				if (value != null) {
					cellBufferStr += value.toValueString();
				}
				if (column != column2) {
					cellBufferStr += "\t";
				}
			}
			if (row != row2) {
				cellBufferStr += "\n";
			}
		}

		// store the tab-delimited values in the clipboard
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSelection = new StringSelection(cellBufferStr);
		clipboard.setContents(stringSelection, null);


		// store copies of the actual geos in the internal buffer
		if (skipGeoCopy) {
			cellBufferGeo = null;
		}
		else
		{
			sourceColumn1 = column1;
			sourceRow1 = row1;
			cellBufferGeo = RelativeCopy.getValues(table, column1, row1, column2, row2);
		}
	}


	/**
	 * Copies the contents of the cell block defined by upper-left corner
	 * (column1, row1) and lower left corner (column2, row2) into the system
	 * clipboard and then deletes these geos.
	 * 
	 * TODO: The external buffer is nulled out so that a followup paste will
	 * not perform a relative copy. This needs to be fixed, relative copy is
	 * expected by the user.
	 * 
	 * @param column1
	 * @param row1
	 * @param column2
	 * @param row2
	 * @return
	 */
	public boolean cut(int column1, int row1, int column2, int row2) {

		copy(column1, row1, column2, row2, false);
		// null out the external buffer so that paste will not do a relative copy
		cellBufferStr = null;
		return delete(column1, row1, column2, row2);	
	}


	/** Paste data from the clipboard using a cell range target */
	public boolean paste(CellRange cr) {
		return paste(cr.getMinColumn(),cr.getMinRow(),cr.getMaxColumn(),cr.getMaxRow());
	}

	/** Paste data from the clipboard */
	public boolean paste(int column1, int row1, int column2, int row2) {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);

		return paste( column1,  row1,  column2,  row2,  contents);
	}

	
	
	public boolean paste(int column1, int row1, int column2, int row2, Transferable contents) {

		boolean succ = false;

		// extract a String from the Transferable 
		String transferString = getTransferString(contents); 


		// test if the transfer string is the same as the internal cell copy string,
		// if true, then we have a tab-delimited list of cell geos and can paste them with relative cell references
		boolean doInternalPaste = transferString != null && cellBufferStr != null && transferString.equals(cellBufferStr);



		// create new geos from cellBufferStr and then paste them into the 
		// target cells with relative cell references
		// ========================================================

		if (doInternalPaste  && cellBufferGeo != null) {
			Construction cons = kernel.getConstruction();
			kernel.getApplication().setWaitCursor();
			try {
				succ = true;
				int columnStep = cellBufferGeo.length;
				int rowStep = cellBufferGeo[0].length;

				int maxColumn = column2;
				int maxRow = row2;
				// paste all data if just one cell selected
				// ie overflow selection rectangle 
				if (row2 == row1 && column2 == column1)
				{
					maxColumn = column1 + columnStep;
					maxRow = row1 + rowStep;
				}

				// collect all redefine operations	
				cons.startCollectingRedefineCalls();

				// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
				for (int c = column1 ; c <= column2 ; c+= columnStep)
					for (int r = row1 ; r <= row2 ; r+= rowStep)
						succ = succ && pasteInternal(c, r, maxColumn, maxRow);

				// now do all redefining and build new construction 
				cons.processCollectedRedefineCalls();


			} catch (Exception ex) {
				ex.printStackTrace(System.out);
				app.showError(ex.getMessage());

				//======================================	
				// TODO: Why is this call here ????
				// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
				//succ = pasteExternalMultiple(buf, column1, row1, column2, row2);
				//==========================================

			} finally {
				cons.stopCollectingRedefineCalls();
				kernel.getApplication().setDefaultCursor();
			}
		}



		// Use the transferString data to create and paste new geos 
		// into the target cells without relative cell references
		// ========================================================

		else if (transferString != null) {
			//Application.debug("newline index "+buf.indexOf("\n"));
			//Application.debug("length "+buf.length());

			// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
			succ = pasteExternalMultiple(transferString, column1, row1, column2, row2);
		}

		return succ;
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
	private String getTransferString(Transferable contents){


		// exit if no content
		if(contents == null) return null;


		// print available data formats in Transferable contents 
		//Application.debug(Arrays.toString(contents.getTransferDataFlavors()));


		// now try to extract a string from the Transferable

		String buf = null;
		try {
			DataFlavor HTMLflavor = new	DataFlavor("text/html;class=java.lang.String");

			//System.out.println("is HTML? " + contents.isDataFlavorSupported(HTMLflavor));

			if(contents.isDataFlavorSupported(HTMLflavor)){
				buf = convertHTMLTableToCSV((String) contents.getTransferData(HTMLflavor));
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		// no HTML found, try plain text
		if (buf == null && (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				buf = (String)contents.getTransferData(DataFlavor.stringFlavor);
				//Application.debug("pasting from String: "+buf);
			} catch (Exception ex) {
				Application.debug("transferable has no String");
				//ex.printStackTrace();
				//app.showError(ex.getMessage());
			}
		}

		return buf;

	}



	/** 
	 * Converts HTML table into CSV
	 */
	private String convertHTMLTableToCSV(String HTMLTableString){

		final StringBuilder sbHTML = new StringBuilder();

		try {
			// prepare the parser
			HTMLEditorKit.ParserCallback callback = 
				new HTMLEditorKit.ParserCallback () {
				boolean foundTable = false;
				boolean firstInRow = true;
				boolean firstColumn = true;
				boolean finished = false;
				public void handleText(char[] data, int pos) {

					if (foundTable && !finished) {

						// if string contains a comma, surround the string with quotes ""
						boolean containsComma = false;
						boolean appendQuotes = false;
						for (int i = 0 ; i < data.length ; i++)
							if (data[i] == ',') containsComma=true;

						if (containsComma && (data[0] != '"' || data[data.length-1] != '"'))
							appendQuotes = true;

						if (containsComma) {
							boolean isNumber = true;
							int noOfCommas = 0;
							for (int i = 0 ; i < data.length ; i++) {
								if (data[i] == ',') noOfCommas++;
								else if (data[i] < '0' || data[i] > '9') isNumber = false;
							}

							// check for European-style decimal comma
							if (isNumber && noOfCommas == 1)
								for (int i = 0 ; i < data.length ; i++)
									if (data[i] == ',') {
										//Application.debug("replacing , with .");
										data[i] = '.';
									}
						}

						if (appendQuotes) sbHTML.append('"');
						for (int i = 0 ; i < data.length ; i++)
							sbHTML.append(data[i]);
						if (appendQuotes) sbHTML.append('"');
					}
					//System.out.println(data);
				}

				public void handleStartTag(HTML.Tag tag, 
						MutableAttributeSet attrSet, int pos) {
					if (tag == HTML.Tag.TABLE) {
						//Application.debug("table");	
						if (foundTable) finished = true;
						foundTable = true;
						firstColumn = true;
						sbHTML.setLength(0);
					} else if (foundTable && tag == HTML.Tag.TR) {
						//Application.debug("TR");	            
						if (!firstColumn) sbHTML.append("\n");
						firstInRow = true;
						firstColumn = false;
					} else if (foundTable && (tag == HTML.Tag.TD || tag == HTML.Tag.TH)) {
						//Application.debug("TD");	     
						if (!firstInRow)
							sbHTML.append(",");
						firstInRow = false;
					} else if (!foundTable) {
						//Application.debug("TR without table");
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
			Application.debug("clipboard: no HTML");
		}			


		if (sbHTML.length() != 0) 	//found HTML table to paste as CSV		
			return sbHTML.toString();  
		else
			return null;
	}





	/**
	 * Creates copies of the geos stored in the global field cellBufferGeo. The
	 * copied values are named as spreadsheet cells corresponding to the given
	 * target cell range and the original source cell locations. Relative cell
	 * references are then applied to match the location of these new geos.
	 * 
	 * The target cell range is defined by upper left corner (column1, row1) and
	 * lower right corner (maxColumn, maxRow).
	 * 
	 * @param column1
	 *            minimum target column
	 * @param row1
	 *            minimum target row
	 * @param maxColumn
	 *            maximum target column
	 * @param maxRow
	 *            maximum target row
	 * @return
	 * @throws Exception
	 */
	public boolean pasteInternal(int column1, int row1, int maxColumn, int maxRow) throws Exception {		
		int width = cellBufferGeo.length;
		if (width == 0) return false;
		int height = cellBufferGeo[0].length;
		if (height == 0) return false;

		app.setWaitCursor();
		boolean succ = false; 

		//Application.debug("height = " + height+" width = "+width);
		int x1 = sourceColumn1;
		int y1 = sourceRow1;
		int x2 = sourceColumn1 + width - 1;
		int y2 = sourceRow1 + height - 1;
		int x3 = column1;
		int y3 = row1;
		int x4 = column1 + width - 1;
		int y4 = row1 + height - 1;
		GeoElement[][] values2 = RelativeCopy.getValues(table, x3, y3, x4, y4);
		/*
		for (int i = 0; i < values2.length; ++ i) {
			for (int j = 0; j < values2[i].length; ++ j) {
				if (values2[i][j] != null) {
					values2[i][j].remove();
					values2[i][j] = null;
				}
			}
		}
		/**/

		int size = (x2-x1+1)*(y2-y1+1);
		if (constructionIndexes == null || constructionIndexes.length < size)
			constructionIndexes = new Object[size];

		int count = 0;


		// ensure the table is large enough to contain the new data
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		if (model.getRowCount() < y4 + 1) {
			model.setRowCount(y4 + 1);
		}
		if (model.getColumnCount() < x4 + 1) {
			table.setMyColumnCount(x4 + 1);
		}



		GeoElement[][] values1 = cellBufferGeo;//RelativeCopy.getValues(table, x1, y1, x2, y2);
		try {
			for (int x = x1; x <= x2; ++ x) {
				int ix = x - x1;
				for (int y = y1; y <= y2; ++ y) {
					int iy = y - y1;

					// check if we're pasting back into what we're copying from
					boolean inSource =  x + (x3-x1) <= x2 &&
					x + (x3-x1) >= x1 &&
					y + (y3-y1) <= y2 &&
					y + (y3-y1) >= y1;


					//Application.debug("x1="+x1+" x2="+x2+" x3="+x3+" x4="+x4+" x="+x+" ix="+ix);
					//Application.debug("y1="+y1+" y2="+y2+" y3="+y3+" y4="+y4+" y="+y+" iy="+iy);

					if (ix+column1 <= maxColumn && iy+row1 <= maxRow//) { // check not outside selection rectangle
							&& (!inSource) ) { // check we're not pasting over what we're copying

						if (values1[ix][iy] != null) {

							// just record the coordinates for pasting
							constructionIndexes[count] = (Object)new Record(values1[ix][iy].getConstructionIndex(),ix, iy, x3 - x1, y3 - y1);
							count ++;
						}
						//values2[ix][iy] = RelativeCopy.doCopyNoStoringUndoInfo0(kernel, table, values1[ix][iy], values2[ix][iy], x3 - x1, y3 - y1);
						//if (values1[ix][iy] != null && values2[ix][iy] != null)
						//  values2[ix][iy].setAllVisualProperties(values1[ix][iy]);
					}
				}
			}

			// sort according to the construction index
			// so that objects are pasted in the correct order
			Arrays.sort(constructionIndexes, 0, count, getComparator());

			// do the pasting
			for (int i = 0 ; i < count ; i++) {
				Record r = (Record)constructionIndexes[i];
				int ix = r.getx1();
				int iy = r.gety1();
				values2[ix][iy] = RelativeCopy.doCopyNoStoringUndoInfo0(kernel, table, values1[ix][iy], values2[ix][iy], r.getx2(), r.gety2());

			}

			succ = true;
		}
		catch (Exception e)
		{			
			e.printStackTrace();	
		}
		finally {
			app.setDefaultCursor();
		}

		return succ;
	}






	//protected static Pattern pattern = Pattern.compile("\\s*(\\\"([^\\\"]+)\\\")|([^,\\t\\\"]+)");
	//protected static Pattern pattern = Pattern.compile("\\s*(\\\"([^\\\"]+)\\\")|([^\\t\\\"]+)");
	
	protected static Pattern pattern1 = Pattern.compile("((\\\"([^\\\"]+)\\\")|([^\\t\\\"\\(]+)|(\\([^)]+\\)))?(\\t|$)");
	protected static Pattern pattern2 = Pattern.compile("((\\\"([^\\\"]+)\\\")|([^,\\\"\\(]+)|(\\([^)]+\\)))?(,|$)");

	public static String[][] parseData(String input) {

		//Application.debug("parse data: "+input);

		String[] lines = input.split("\\r*\\n", -1);
		String[][] data = new String[lines.length][];
		for (int i = 0; i < lines.length; ++ i) {

			// trim() removes tabs which we need
			lines[i] = geogebra.util.Util.trimSpaces(lines[i]);
			LinkedList list = new LinkedList();

			int firstCommaIndex = lines[i].indexOf(",");
			int lastCommaIndex = lines[i].lastIndexOf(",");
			int firstBracketIndex = lines[i].indexOf("[");
			int lastBracketIndex = lines[i].lastIndexOf("]");
			int firstTabIndex = lines[i].indexOf("\t");
			
			// no commas, brackets or tabs, so add entire line
			if (firstCommaIndex == -1 && firstBracketIndex == -1 && firstTabIndex == -1)
					list.addLast(lines[i]);	
			
			// brackets enclose the expression, so assume it's a GeoGebra command and add entire line
			else if (firstCommaIndex > firstBracketIndex && lastCommaIndex < lastBracketIndex) {
				list.addLast(lines[i]);
				
			// otherwise split on commas and tabs	
			} else {

				Matcher matcher = null;
				if (firstTabIndex != -1) {
					matcher = pattern1.matcher(lines[i]);
				}
				else {
					matcher = pattern2.matcher(lines[i]);
				}

				while (matcher.find()) {
					String data1 = matcher.group(3);
					String data2 = matcher.group(4);
					String data3 = matcher.group(5);

					//Application.debug("data1: "+data1);
					//Application.debug("data2: "+data2);
					//Application.debug("data3: "+data3);

					if (data1 != null) {
						data1 = data1.trim();
						data1 = checkDecimalComma(data1); // allow decimal comma
						list.addLast(data1);
					}
					else if (data2 != null) {
						data2 = data2.trim();
						data2 = checkDecimalComma(data2); // allow decimal comma
						list.addLast(data2);
					}
					else if (data3 != null) {
						data3 = data3.trim();
						list.addLast(data3);
					}
					else {
						list.addLast("");
					}
				}
			}
			if (list.size() > 0 && list.getLast().equals("")) {
				list.removeLast();
			}
			data[i] = (String[])list.toArray(new String[0]);
		}
		return data;		
	}


	/*
	 * change 3,4 to 3.4
	 * leave {3,4,5} alone
	 */
	private static String checkDecimalComma(String str) {
		if (str.indexOf("{") == -1 && str.indexOf(",") == str.lastIndexOf(",")) {
			str = str.replaceAll(",", "."); // allow decimal comma
		}

		return str;
	}



	private boolean pasteExternalMultiple(String buf,int column1, int row1, int column2, int row2) {
		/*
		int newlineIndex = buf.indexOf("\n");
		int rowStep = 1;
		if ( newlineIndex == -1 || newlineIndex == buf.length()-1) { 
			rowStep = 1; // no linefeeds in string
		}
		else
		{
		    for (int i = 0; i < buf.length()-1 ; i++) { // -1 : don't want to count a newline if it's the last char
		        char c = buf.charAt(i);
		        if (c == '\n') rowStep++; // count no of linefeeds in string
		    }
		}*/
		boolean succ = true;

		// convert the input String buf into a 2D array defined by tab or comma delimiters
		String[][] data = parseData(buf);


		int rowStep = data.length;
		int columnStep = data[0].length;

		if (columnStep == 0) return false;

		int maxColumn = column2;
		int maxRow = row2;

		// paste all data if just one cell selected
		// ie overflow selection rectangle 
		if (row2 == row1 && column2 == column1)
		{
			maxColumn = column1 + columnStep;
			maxRow = row1 + rowStep;
		}

		// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
		for (int c = column1 ; c <= column2 ; c += columnStep)
			for (int r = row1 ; r <= row2 ; r+= rowStep)
				succ = succ && pasteExternal(data, c, r, maxColumn, maxRow);

		return succ;

	}

	/**
	 * Creates new cell geos using the string values stored in the given String[][].
	 * Cells are named to correspond with the target cell range defined by upper
	 * left corner (column1, row1) and lower right corner (maxColumn, maxRow).
	 * Does not apply relative cell references.
	 * 
	 * @param data
	 * @param column1
	 * @param row1
	 * @param maxColumn
	 * @param maxRow
	 * @return
	 */
	public boolean pasteExternal(String[][] data, int column1, int row1, int maxColumn, int maxRow) {
		app.setWaitCursor();
		boolean succ = false;			

		try {
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			if (model.getRowCount() < row1 + data.length) {
				model.setRowCount(row1 + data.length);
			}
			GeoElement[][] values2 = new GeoElement[data.length][];
			int maxLen = -1;
			for (int row = row1; row < row1 + data.length; ++ row) {
				if (row < 0 || row > maxRow) continue;
				int iy = row - row1;
				values2[iy] = new GeoElement[data[iy].length];
				if (maxLen < data[iy].length) maxLen = data[iy].length;
				if (model.getColumnCount() < column1 + data[iy].length) {
					table.setMyColumnCount(column1 + data[iy].length);						
				}
				for (int column = column1; column < column1 + data[iy].length; ++ column) {
					if (column < 0 || column > maxColumn) continue;
					int ix = column - column1;
					//Application.debug(iy + " " + ix + " [" + data[iy][ix] + "]");
					if(data[iy][ix] == null) continue;
					data[iy][ix] = data[iy][ix].trim();
					if (data[iy][ix].length() == 0) {
						GeoElement value0 = RelativeCopy.getValue(table, column, row);
						if (value0 != null) {
							//Application.debug(value0.toValueString());
							//MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column, row);
							//value0.remove();
							value0.removeOrSetUndefinedIfHasFixedDescendent();
						}	
					}
					else {
						GeoElement value0 = RelativeCopy.getValue(table, column, row);
						values2[iy][ix] = RelativeCopy.prepareAddingValueToTableNoStoringUndoInfo(kernel, table, data[iy][ix], value0, column, row);
						//values2[iy][ix].setAuxiliaryObject(values2[iy][ix].isGeoNumeric()); 
						values2[iy][ix].setAuxiliaryObject(true); 
						table.setValueAt(values2[iy][ix], row, column);
					}
				}
			}
			//Application.debug("maxLen=" + maxLen);
			table.getView().repaintView();

			/*
			if (values2.length == 1 || maxLen == 1) {
				createPointsAndAList1(values2);
			}
			if (values2.length == 2 || maxLen == 2) {
				createPointsAndAList2(values2);
			}*/

			succ = true;
		} catch (Exception ex) {
			//app.showError(ex.getMessage());
			//Util.handleException(table, ex);
			ex.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}

		return succ;
	}



	public boolean delete(int column1, int row1, int column2, int row2)  {
		boolean succ = false;
		for (int column = column1; column <= column2; ++ column) {
			//int column3 = table.convertColumnIndexToModel(column);
			for (int row = row1; row <= row2; ++ row) {
				GeoElement value0 = RelativeCopy.getValue(table, column, row);
				if (value0 != null && !value0.isFixed()) {
					//value0.remove();
					value0.removeOrSetUndefinedIfHasFixedDescendent();
					succ = true;
				}
				//try {
				//	MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column3, row);
				//} catch (Exception e) {
				//	Application.debug("spreadsheet.delete: " + e.getMessage());
				//}
			}
		}

		// Let the trace manager know about the delete 
		// TODO add SelectAll
		if(table.getSelectionType()==MyTable.COLUMN_SELECT){
			view.getTraceManager().handleColumnDelete(column1, column2);
		}

		return succ;
	}


	public void deleteAll() {

		table.copyPasteCut.delete(0, 0, tableModel.getColumnCount(), tableModel.getRowCount());

	}







	private static class Record {
		int id, x1, y1, x2, y2;
		public Record(int id, int x1, int y1, int x2, int y2){
			this.id = id;
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}

		public int getId() {
			return id;
		}
		public int getx1() {
			return x1;
		}
		public int getx2() {
			return x2;
		}
		public int gety1() {
			return y1;
		}
		public int gety2() {
			return y2;
		}
		public int compareTo(Object o) {
			Application.debug(o.getClass()+"");
			//int id = ((Record) o).getId();
			//return id - this.id;
			return 0;
		}
	}

	/**
	 * used to sort Records based on the id (which is the construction index)
	 */
	public static Comparator getComparator() {
		if (comparator == null) {
			comparator = new Comparator() {
				public int compare(Object a, Object b) {
					Record itemA = (Record) a;
					Record itemB = (Record) b;

					return itemA.id - itemB.id;
				}

			};

		}

		return comparator;
	}
	private static Comparator comparator;





	//====================================================
	//   File and URL 
	//====================================================


	// default pasteFromFile: clear spreadsheet and then paste from upper left corner
	public boolean pasteFromURL(URL url) {

		CellRange cr = new CellRange(table, 0,0,0,0);
		return pasteFromURL(url, cr, true);

	}


	public boolean pasteFromURL(URL url, CellRange targetRange, boolean clearSpreadsheet) {

		// read file 
		StringBuilder contents = new StringBuilder();

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

		//System.out.println(dataFile.getName() + ": " + contents.capacity());

		// copy file contents to clipboard		
		StringSelection stringSelection = new StringSelection(contents.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable oldContent = clipboard.getContents(null);
		clipboard.setContents(stringSelection, null);


		// paste from clipboard into spreadsheet
		if(clearSpreadsheet){
			deleteAll();
		}

		boolean oldEqualsSetting = view.isEqualsRequired();
		view.setEqualsRequired(true);
		boolean succ = paste(targetRange);
		clipboard.setContents(oldContent, null);
		view.setEqualsRequired(oldEqualsSetting);

		return succ;

	}


}
