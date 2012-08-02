package geogebra.util;

import geogebra.cas.view.CASTableD;
import geogebra.common.cas.view.CASView;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


public class CASDropTargetListener implements DropTargetListener {
	
	private CASTableD table; 
	private AppD app;
	private Kernel kernel; 
	private CASView view;
	private DropTarget dropTarget;

	/**
	 * creates a new drop target listener 
	 * @param app  the current application
	 * @param view the cas view
	 * @param table the cas table
	 */
	public CASDropTargetListener(AppD app, CASView view, CASTableD table)
	{
		this.app = app;		
		this.table = table;
		this.view = view;
		kernel = app.getKernel();
	}

	/**
	 * enables Drag and Drop for this listener
	 */
	public void enableDnD(){
		if(dropTarget==null)
			dropTarget = new DropTarget(table ,this);
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
		Transferable t = dropEvent.getTransferable();
		int row = table.rowAtPoint(dropEvent.getLocation());
		try {	
			
			
			/**
			 * handle a drop from an other cas cell, use this for substitution
			 */
			if(t.isDataFlavorSupported(CASTransferHandler.casTableFlavor)) {
				int cellnumber = (Integer) t.getTransferData(CASTransferHandler.casTableFlavor);
				String tableRef = "$"+(cellnumber+1);
				
				GeoCasCell cell =  table.getGeoCasCell(row);
				GeoCasCell source =  table.getGeoCasCell(cellnumber);
				if(cell.getInputVE()==null || source.getInputVE()==null){
					return;
				}
				//get output of the source cell, this should be changed for dynamic reference
				String substitution = view.resolveCASrowReferences(tableRef, row);
				
				//dont use the same cell as source and destination
				if(cell.getRowNumber() == source.getRowNumber())
					return;
				//check if in the variables in the source cell appears also in the destination
				//if not, do not substitute!
				HashSet<GeoElement> varsDest = cell.getInputVE().getVariables();
				HashSet<GeoElement> varsSource = source.getInputVE().getVariables();
				boolean match = false;
				if (varsDest != null && varsSource != null) {
					//data = new Vector<Vector<String>>(vars.size() + 1);
					Iterator<GeoElement> iterD = varsDest.iterator();
					Iterator<GeoElement> iterS = varsSource.iterator();
					while (iterS.hasNext()) {
						GeoElement varS = iterS.next();
						String labelS = varS.getLabel(StringTemplate.defaultTemplate);
						while (iterD.hasNext()){
							GeoElement varD = iterD.next();
							String labelD = varD.getLabel(StringTemplate.defaultTemplate);
							if(labelS.equals(labelD)){ //match!!!
								match = true;
								break;
							}
						}
					}
				} else {
					return;
				}
				if(!match)  //no match => nothing to substitute
					return;

				
				
				String subCmd = "Substitute[" + cell.getEvalText() + ", " + substitution  + "]";	
				cell.setProcessingInformation(cell.getPrefix(), subCmd, cell.getPostfix());				
				cell.setEvalCommand("Substitute");
				cell.setEvalComment(substitution);	
				view.processRowThenEdit(row, true);	
				
							
				//ToDo: dynamic!!!
				//String subCmd = "Substitute[" + cell.getEvalText() + ", {" + tableRef + "}]";
				//cell.setInput(subCmd);
				//cell.addInVar(tableRef);	
				//cell.getGeoElementVariables();  //update geoelements from invars
				//table.startEditingRow(row);
				//String[] params = {tableRef};
				//view.getInputHandler().processCurrentRow("Substitute", params);
				//updateEvalVariables(evalVE);
				//view.setMode(EuclidianConstants.MODE_CAS_EVALUATE);	
				//view.getInputHandler().processCurrentRow("", params);
				
				app.storeUndoInfo();
				return;
			}
			
			 
			/**handle drops from algebra view
			 * 
			 * creates a new row in the CASTable and fills it up only with the value of the geo
			 * eg: Drop Element g: Line[A,B]
			 *  -> CAS Cell: a*x + b*y = c  without any assignment 
			 */
			if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)) {
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
						textImport = geo.getCASString(StringTemplate.defaultTemplate, false);
						GeoCasCell cell = new GeoCasCell(kernel.getConstruction());
						
						//insert new row and start editing
						table.insertRow(cell, true);		
						
						//change the input accordingly to the drop
						if(geo.isGeoText()){
							cell.setUseAsText(true);
							cell.setGeoText((GeoText) geo);
						}
						else{
							cell.setInput(textImport);
						}
						
						//stop editing and evaluate the new input
						app.setMode(EuclidianConstants.MODE_CAS_EVALUATE); 
						table.stopEditing();
					}
				}
				table.updateAllRows();
				table.repaint();
				dropEvent.dropComplete(false);
				return;
			}
			
		} catch (Exception e) {
			App.debug("DEBUG: CASDropTargetListener: exception in drop");
			e.printStackTrace();
		}
		
	}



	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

}