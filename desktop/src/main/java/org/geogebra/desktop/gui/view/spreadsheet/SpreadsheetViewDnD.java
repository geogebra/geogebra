package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.desktop.gui.view.algebra.AlgebraViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.AlgebraViewTransferHandler;

/**
 * Handles drag and drop for the spreadsheet
 * 
 * @author G. Sturr
 *
 */
public class SpreadsheetViewDnD
		implements DragGestureListener, DragSourceListener, DropTargetListener {

	private AppD app;
	private MyTableD table;

	// current drag over cell
	private GPoint currentCell = new GPoint(0, 0);

	// flags
	private boolean isTranspose = false;
	private boolean isCopyByValue = true;
	boolean allowDrop = true;

	static DataFlavor HTMLflavor;
	static {
		try {
			HTMLflavor = new DataFlavor("text/html;class=java.lang.String");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	/***************************************
	 * Constructor
	 * 
	 * @param app
	 * @param view
	 */
	@SuppressWarnings("unused")
	public SpreadsheetViewDnD(AppD app, SpreadsheetViewD view) {
		this.app = app;
		this.table = (MyTableD) view.getSpreadsheetTable();

		new DragSource();
		// DragGestureRecognizer dgr =
		// ds.createDefaultDragGestureRecognizer(table,
		// DnDConstants.ACTION_COPY, this);

		new DropTarget(table, this);

	}

	// ===========================================
	// Getters/setters
	// =============================================

	public void setAllowDrop(boolean allowDrop) {
		this.allowDrop = allowDrop;
	}

	public boolean isTranspose() {
		return isTranspose;
	}

	public void setTranspose(boolean isTranspose) {
		this.isTranspose = isTranspose;
	}

	public boolean isCopyByValue() {
		return isCopyByValue;
	}

	public void setCopyByValue(boolean isCopyByValue) {
		this.isCopyByValue = isCopyByValue;
	}

	// ======================================
	// Drag Source Listeners
	// ======================================

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {

		if (!table.isOverDnDRegion) {
			return;
		}

		/*
		 * ----- code from AlgebraView Dnd, to be adapted later
		 * 
		 * if(geoLabelList == null) geoLabelList = new ArrayList<String>(); else
		 * geoLabelList.clear();
		 * 
		 * for(GeoElement geo : app.getSelectedGeos()){
		 * geoLabelList.add(geo.getLabel()); }
		 * 
		 * // if we have something ... do the drag! if(geoLabelList.size() > 0){
		 * 
		 * // create drag image StringBuilder sb = new StringBuilder();
		 * sb.append("\\fbox{\\begin{array}{l}"); for(GeoElement
		 * geo:app.getSelectedGeos()){
		 * sb.append(geo.getLaTeXAlgebraDescription(true)); sb.append("\\\\"); }
		 * sb.append("\\end{array}}"); ImageIcon ic =
		 * GeoGebraIcon.createLatexIcon(app, sb.toString(), app.getPlainFont(),
		 * false, Color.DARK_GRAY, null);
		 * 
		 * // start drag ds.startDrag(dge, DragSource.DefaultCopyDrop,
		 * ic.getImage(), new Point(-5,-ic.getIconHeight()+5), new
		 * TransferableAlgebraView(geoLabelList), this); }
		 * 
		 */

	}

	@Override
	public void dragDropEnd(DragSourceDropEvent e) {
		// nothing to do
	}

	@Override
	public void dragEnter(DragSourceDragEvent e) {
		// nothing to do
	}

	@Override
	public void dragExit(DragSourceEvent e) {
		// nothing to do
	}

	@Override
	public void dragOver(DragSourceDragEvent e) {
		// nothing to do
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent e) {
		// nothing to do
	}

	// ======================================
	// Drop Target Listeners
	// ======================================

	/**
	 * Listener that notifies the table to prepare for dragging
	 */
	@Override
	public void dragEnter(DropTargetDragEvent dte) {
		table.setTableMode(MyTable.TABLE_MODE_DROP);
	}

	/**
	 * Listener that notifies the table to stop handling drag events
	 */
	@Override
	public void dragExit(DropTargetEvent dte) {
		table.setTableMode(MyTable.TABLE_MODE_STANDARD);
	}

	/**
	 * Listener to keep track of current mouseOver cell and update the target
	 * cell border
	 */
	@Override
	public void dragOver(DropTargetDragEvent dte) {

		GPoint overCell = table.getIndexFromPixel(dte.getLocation().x,
				dte.getLocation().y);

		// if mouse over a new cell then update currentCell and repaint the
		// target cell border
		if (!overCell.equals(currentCell)) {
			currentCell = overCell;
			table.setTargetcellFrame(table.getCellBlockRect(currentCell.x,
					currentCell.y, currentCell.x, currentCell.y, true));
			table.repaint();
		}

	}

	/**
	 * Handles drops.
	 */
	@Override
	public void drop(DropTargetDropEvent dte) {

		Transferable t = dte.getTransferable();
		isTranspose = false;
		isCopyByValue = true;

		// case(1) algebraViewFlavor
		if (t.isDataFlavorSupported(
				AlgebraViewTransferHandler.algebraViewFlavor)) {

			// if modifier key down, open a dialog to get user drop options
			if (app.getShiftDown() || app.getControlDown()) {
				DialogCopyToSpreadsheet id = new DialogCopyToSpreadsheet(app,
						this);
				id.setVisible(true);
			}

			if (!allowDrop) {
				allowDrop = true;
				handleDropComplete(dte, false);

				return;
			}

			boolean success = handleHtmlFlavorDrop(dte);
			handleDropComplete(dte, success);
			return;
		}

		// case(2) String or HTML flavor
		else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)
				|| t.isDataFlavorSupported(HTMLflavor)) {

			boolean success = table.copyPasteCut.paste(currentCell.x,
					currentCell.y, currentCell.x, currentCell.y, t);
			handleDropComplete(dte, success);
			return;
		}

	}

	/**
	 * Handles drops from the AlgebraView
	 * 
	 * @param dte
	 * @return
	 */
	private boolean handleHtmlFlavorDrop(DropTargetDropEvent dte) {

		Transferable t = dte.getTransferable();

		// accept the drop
		dte.acceptDrop(dte.getDropAction());

		try {

			// get list of selected geo labels
			ArrayList<String> list;
			list = (ArrayList<String>) t.getTransferData(
					AlgebraViewTransferHandler.algebraViewFlavor);

			// exit if empty list
			if (list.size() == 0) {
				return false;
			}

			GeoElement[] geoArray = new GeoElement[list.size()];
			for (int i = 0; i < geoArray.length; i++) {
				GeoElement geo = app.getKernel().lookupLabel(list.get(i));
				if (geo != null) {
					if (GeoElementSpreadsheet.hasSpreadsheetLabel(geo)) {
						return false;
					}
				}
				geoArray[i] = geo;
			}

			// create 2D ArrayList of string expressions for the geos
			// if copy by value, use geo.toValueString
			// else use " =geo.label" or "=Element[ ...

			int rowCount = list.size();
			int columnCount = 1;
			GeoList tempList, tempMatrix;
			ArrayList<ArrayList<String>> dataList = new ArrayList<>();

			for (int geoIndex = 0; geoIndex < list.size(); geoIndex++) {

				// list geo
				if (geoArray[geoIndex].isGeoList()
						&& !((GeoList) geoArray[geoIndex]).isMatrix()) {

					tempList = (GeoList) geoArray[geoIndex];
					ArrayList<String> currentRow = new ArrayList<>();
					columnCount = Math.max(columnCount, tempList.size());

					for (int k = 0; k < tempList.size(); k++) {
						if (isCopyByValue) {
							currentRow.add(tempList.get(k).toValueString(
									StringTemplate.defaultTemplate));
						} else {
							currentRow.add("=Element["
									+ tempList.getLabel(
											StringTemplate.defaultTemplate)
									+ "," + (k + 1) + "]");
						}
					}
					dataList.add(currentRow);
				}

				// matrix geo
				else if ((geoArray[geoIndex].isGeoList()
						&& ((GeoList) geoArray[geoIndex]).isMatrix())) {

					tempMatrix = (GeoList) geoArray[geoIndex];
					rowCount += tempMatrix.size() - 1;
					for (int row = 0; row < tempMatrix.size(); row++) {
						tempList = (GeoList) tempMatrix.get(row);
						ArrayList<String> currentRow = new ArrayList<>();
						columnCount = Math.max(columnCount, tempList.size());
						for (int col = 0; col < tempList.size(); col++) {
							if (isCopyByValue) {
								currentRow.add(tempList.get(col).toValueString(
										StringTemplate.defaultTemplate));
							} else {
								currentRow.add("=Element["
										+ tempMatrix.getLabel(
												StringTemplate.defaultTemplate)
										+ "," + (row + 1) + "," + (col + 1)
										+ "]");
							}
						}
						dataList.add(currentRow);
					}
				}

				// single geo
				else {
					ArrayList<String> currentRow = new ArrayList<>();
					if (isCopyByValue) {
						currentRow.add(geoArray[geoIndex]
								.toValueString(StringTemplate.maxPrecision));
					} else {
						currentRow
								.add("=" + geoArray[geoIndex].getLabelSimple());
					}
					dataList.add(currentRow);
				}

			}

			// create 2D String arrays to hold expressions for the transfer geos
			String[][] data = new String[rowCount][columnCount];
			String[][] dataTranspose = null;

			for (int r = 0; r < rowCount; r++) {
				for (int c = 0; c < dataList.get(r).size(); c++) {
					data[r][c] = dataList.get(r).get(c);
				}
			}

			// create a transposed array
			if (isTranspose) {
				dataTranspose = new String[columnCount][rowCount];
				for (int r = 0; r < rowCount; r++) {
					for (int c = 0; c < columnCount; c++) {
						dataTranspose[c][r] = data[r][c];
					}
				}
			}

			if (!isTranspose) {
				table.copyPasteCut.pasteExternal(data, currentCell.x,
						currentCell.y, currentCell.x + columnCount - 1,
						currentCell.y + rowCount - 1);
			} else {
				table.copyPasteCut.pasteExternal(dataTranspose, currentCell.x,
						currentCell.y, currentCell.x + rowCount - 1,
						currentCell.y + columnCount - 1);
			}

			return true;

		} catch (UnsupportedFlavorException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}

		return false;
	}

	private void handleDropComplete(DropTargetDropEvent dte, boolean success) {
		dte.dropComplete(success);
		table.setTableMode(MyTable.TABLE_MODE_STANDARD);
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dte) {
		// TODO Auto-generated method stub

	}

	/**
	 * Extension of Transferable for exporting AlgegraView selections as a list
	 * of Geo labels
	 */
	static class TransferableAlgebraView implements Transferable {

		public final DataFlavor algebraViewFlavor = new DataFlavor(
				AlgebraViewD.class, "geoLabel list");
		private final DataFlavor supportedFlavors[] = { algebraViewFlavor };

		private ArrayList<String> geoLabelList;

		public TransferableAlgebraView(ArrayList<String> geoLabelList) {
			this.geoLabelList = geoLabelList;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return supportedFlavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (flavor.equals(algebraViewFlavor)) {
				return true;
			}
			return false;
		}

		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (flavor.equals(algebraViewFlavor)) {
				return geoLabelList;
			}
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public static SpreadsheetViewDnD get(AppD app2,
			SpreadsheetViewD spreadsheetView) {
		return new SpreadsheetViewDnD(app2, spreadsheetView);

	}

}
