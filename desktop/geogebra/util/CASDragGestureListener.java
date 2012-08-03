package geogebra.util;

import geogebra.cas.view.CASTableD;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class CASDragGestureListener implements DragGestureListener, DragSourceListener{
	
	private Kernel kernel;
	private App app;
	private CASTableD table;
	private DragSource ds;

	private ArrayList<String> geoLabelList;
	private DragGestureRecognizer dgr;
	
	public CASDragGestureListener(Kernel kern, CASTableD table)
	{
		super();
		kernel = kern;
		app = kernel.getApplication();
		this.table = table;
		ds = new DragSource();

	}
	
	
	/**
	 * enables Drag and Drop for this listener
	 */
	public void enableDnD() {
		if(dgr==null){
			dgr =  ds.createDefaultDragGestureRecognizer(
				table, 
				DnDConstants.ACTION_COPY_OR_MOVE, 
				this);
		}
	}
	
	public void dragGestureRecognized(DragGestureEvent dge) {	
		table.stopEditing();
		
		if(geoLabelList == null)
			geoLabelList = new ArrayList<String>();
		else
			geoLabelList.clear();
		
		int row = table.rowAtPoint(dge.getDragOrigin());
		GeoCasCell cell = table.getGeoCasCell(row);
		
		geoLabelList.add(cell.getLabel(StringTemplate.defaultTemplate));
		int cellnumber = cell.getRowNumber();
		//String tableRef = "$" + (cellnumber+1);
		
		
		String latex;
		latex = cell.getLaTeXAlgebraDescription(true, StringTemplate.latexTemplate);		
		ImageIcon ic  = GeoGebraIcon.createLatexIcon((AppD)app, latex, ((AppD)app).getPlainFont(), false, Color.DARK_GRAY, null);

		TransferableCAS transferable = new TransferableCAS(geoLabelList, cellnumber);
		transferable.setIsAssignment(cell.isAssignmentVariableDefined());
			
		
		// start drag
		ds.startDrag(dge, DragSource.DefaultCopyDrop, ic.getImage(), 
				new Point(-5, -30),  transferable,  this);
	}

	
	/**
	 * 	Extension of Transferable for exporting CAS selections as a list of Geo labels
	 * can transfer:
	 * algebraViewFlavor: geoLabelList (ArrayList) only with the labels of the transfered variables
	 * casTableFlavor: tableRef (int) return the reference to the specific cell number
	 * casLaTeXFlavor: latexText (String) for not dynamic functionality, return the content of the cas cell as latex string
	 */
	class TransferableCAS implements Transferable {

		private final DataFlavor supportedFlavors[] = { AlgebraViewTransferHandler.algebraViewFlavor, 
														CASTransferHandler.casTableFlavor, 
														};

		private ArrayList<String> geoLabels;
		private int tableRef;
		private boolean isAssignment;

		/**
		 * @param geoLabelList list of labels
		 * @param tableRef table reference 
		 */
		public TransferableCAS(ArrayList<String> geoLabelList, int tableRef) {
			this.geoLabels = geoLabelList;
			this.tableRef = tableRef;
			isAssignment = false;
		}
		

		public DataFlavor[] getTransferDataFlavors() {
			return supportedFlavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			//algebraViewFlavor can only be used if the cas cell contains an assignment
			if (flavor.equals(AlgebraViewTransferHandler.algebraViewFlavor) && isAssignment)
				return true;
			if(flavor.equals(CASTransferHandler.casTableFlavor))
				return true;	

			return false;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (flavor.equals(AlgebraViewTransferHandler.algebraViewFlavor))
				return geoLabels;			
			if (flavor.equals(CASTransferHandler.casTableFlavor))
				return tableRef;
			throw new UnsupportedFlavorException(flavor);
		}
		
	
		/**
		 * @param ass whether the cell contains assignment
		 */
		public void setIsAssignment(boolean ass){
			isAssignment = ass;
		}

	}


	//=====================================================
	// Drag and Drop 
	//=====================================================
	
	public void dragDropEnd(DragSourceDropEvent e) {
		//do nothing
	}
	public void dragEnter(DragSourceDragEvent e) {
		//do nothing
	}
	public void dragExit(DragSourceEvent e) {
		//do nothing
	}
	public void dragOver(DragSourceDragEvent e) {
		//do nothing
	}
	public void dropActionChanged(DragSourceDragEvent e) {
		//do nothing
	}
}
