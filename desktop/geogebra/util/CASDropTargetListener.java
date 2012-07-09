package geogebra.util;

import geogebra.cas.view.CASTable;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;


public class CASDropTargetListener implements DropTargetListener {
	
	private CASTable table; 
	private AppD app;
	private Kernel kernel; 
	
	public CASDropTargetListener(AppD app, CASTable table)
	{
		this.app = app;		
		this.table = table;
		kernel = app.getKernel();
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
	}

	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

	public void drop(DropTargetDropEvent dropEvent) {
		App.debug("drop");
		
		Transferable t = dropEvent.getTransferable();
		int row = table.rowAtPoint(dropEvent.getLocation());
		App.debug("==> " + row);
		
		
		
		try {
/*			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				//By me, geogebra does not come to this point
				String textImport = (String) t.getTransferData(DataFlavor.stringFlavor);
				GeoCasCell cell = new GeoCasCell(kernel.getConstruction());
				cell.setInput(textImport);
				cell.computeOutput();
				table.insertRow(row, cell, false);
			}
*/			
			
			if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)) {
				
				App.debug(t.toString());
				String textImport;
				// get list of selected geo labels 
				ArrayList<String> list = (ArrayList<String>) t
				.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);
				
				// exit if empty list
				if(list.size()==0) {
					dropEvent.dropComplete(false);
					return;
				}
	
				// for one or more geos
				for(int i=0; i<list.size(); i++){
					GeoElement geo = kernel.lookupLabel(list.get(0));
					if(geo != null){
						//textImport = geo.getDefinitionForInputBar();
						//textImport = geo.toString(StringTemplate.defaultTemplate);
						textImport = geo.getCASString(StringTemplate.defaultTemplate, false);
						//textImport = geo.toCasAssignment(StringTemplate.defaultTemplate);
						GeoCasCell cell = new GeoCasCell(kernel.getConstruction());
						
						//cell.setInput(textImport);

						if(geo.isGeoText()){
								cell.setUseAsText(true);
								cell.setGeoText((GeoText) geo);
						}
						
						try{	
							//cell.computeOutput();
							//geo.getVariables();
						} 
						/*if comuteOutput fails set cell as textcell fall back 
						 *toCASAssigment works not perfect, eg. when geo is a Point, toCasAssimgent gives a 
						 *lower case variable name
						 */
						catch(Exception e) 
						{
							App.debug("DEBUG: CASDropTargetListener: error in generating new cell");
							//cell.setUseAsText(true);
							//textImport = geo.toCasAssignment(StringTemplate.defaultTemplate);
							//cell.setInput(textImport);
							//cell.computeOutput();
						}
						//if(table.isEditing())
						//	table.stopEditing();
						//table.startEditingRow(table.getModel().getRowCount());
						
						table.insertRow(cell, true);
						cell.setInput(textImport);
						//CASTableCellEditor editor = (CASTableCellEditor) table.getCellEditor(table.getModel().getRowCount(), 0);
						//editor.
						
						//editor.stopCellEditing();
						app.setMode(EuclidianConstants.MODE_CAS_EVALUATE); //with this it works better
						table.stopEditing();	
						row = row + 1;
					}
				}
				table.updateAllRows();
				table.repaint();
				dropEvent.dropComplete(false);
				return;
			}
			
		} catch (Exception ignored) {
		}
		
	}



	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

}