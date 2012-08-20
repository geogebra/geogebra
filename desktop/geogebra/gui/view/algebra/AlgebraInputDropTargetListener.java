package geogebra.gui.view.algebra;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.GuiManagerD;
import geogebra.main.AppD;
import geogebra.util.AlgebraViewTransferHandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.JTextComponent;

public class AlgebraInputDropTargetListener implements DropTargetListener {

	private AppD app;
	private JTextComponent textComp;

	// supported data flavors
	private static final DataFlavor supportedFlavors[] = { 
		DataFlavor.javaFileListFlavor,
		DataFlavor.stringFlavor,
		AlgebraViewTransferHandler.algebraViewFlavor };

	private boolean debug  = false;

	private String textImport;
	private String textExport;


	public AlgebraInputDropTargetListener(AppD app, JTextComponent textComp){
		this.app = app;
		this.textComp = textComp;
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

		dropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		Transferable t = dropEvent.getTransferable();

		try {

			// handle text
			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				textImport = (String) t.getTransferData(DataFlavor.stringFlavor);

				// set the text and complete the drop
				
				textComp.replaceSelection(textImport);
				
				
				dropEvent.dropComplete(textImport != null);
				
				
				
			}


			// handle algebraView flavor
			else if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)){

				// get list of selected geo labels 
				ArrayList<String> list = (ArrayList<String>) t
				.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);

				// exit if empty list
				if(list.size()==0) {
					dropEvent.dropComplete(false);
					return;
				}

				// if only one geo, get definition string 
				if(list.size()==1){
					GeoElement geo = app.getKernel().lookupLabel(list.get(0));
					if(geo != null)
						textImport = geo.getDefinitionForInputBar();
					else{
						dropEvent.dropComplete(false);
						return;
					}
				}

				// if more than one geo, create list string
				else{
					textImport = list.toString();
					textImport = textImport.replace("]", "}");
					textImport = textImport.replace("[", "{");
				}

				// set the text and complete the drop
				textComp.setText(textImport);
				dropEvent.dropComplete(true);
			}


			// handle ggb file drop
			else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				((GuiManagerD)app.getGuiManager()).handleGGBFileDrop(t);
			}

		} catch (UnsupportedFlavorException ignored) {
		} catch (IOException ignored) {
		}
	}



	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

}
