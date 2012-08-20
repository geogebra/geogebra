package geogebra.gui.view.algebra;


import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.GuiManagerD;
import geogebra.main.AppD;
import geogebra.util.AlgebraViewTransferHandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

/**
 * Transfer handler for InputBar
 * @author gsturr
 *
 */
public class AlgebraInputTransferHandler extends TransferHandler implements Transferable {

	private static final long serialVersionUID = 1L;
	
	private AppD app;
	private JTextComponent ta;

	// supported data flavors
	private static final DataFlavor supportedFlavors[] = { 
		DataFlavor.javaFileListFlavor,
		DataFlavor.stringFlavor,
		AlgebraViewTransferHandler.algebraViewFlavor };

	private boolean debug  = false;

	private String text;


	/****************************************
	 * Constructor
	 * @param ev
	 */
	public AlgebraInputTransferHandler(AppD app, JTextComponent ta){
		this.ta = ta;
		this.app = app;
	}


	/**
	 * Ensures that transfers are done in COPY mode
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	/**
	 * Returns true if any element of the DataFlavor parameter array is a supported flavor.
	 */
	@Override
	public boolean canImport(JComponent comp, DataFlavor flavor[]) {

		for (int i = 0, n = flavor.length; i < n; i++) {
			for (int j = 0, m = supportedFlavors.length; j < m; j++) {
				if (flavor[i].equals(supportedFlavors[j])) {
					return true;
				}
			}
		}
		return false;
	}



	/**
	 * Handles data import.
	 */
	@Override
	public boolean importData(JComponent comp, Transferable t) {

		// handle text
		if (t.isDataFlavorSupported(DataFlavor.stringFlavor)
				|| t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)) {
			try {

				// handle plain text flavor
				if(t.isDataFlavorSupported(DataFlavor.stringFlavor)){
					text = (String) t.getTransferData(DataFlavor.stringFlavor);
				}

				// handle algebraView flavor
				else if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)){
					
					// get list of selected geo labels 
					ArrayList<String> list = (ArrayList<String>) t
					.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);
					
					// exit if empty list
					if(list.size()==0) return false;
					
					// if only one geo, get definition string 
					if(list.size()==1){
						GeoElement geo = app.getKernel().lookupLabel(list.get(0));
						if(geo != null)
							text = geo.getDefinitionForInputBar();
					}
					
					// if more than one geo, create list string
					else{
						text = list.toString();
						text = text.replace("]", "}");
						text = text.replace("[", "{");
					}
				}

				ta.setText(text);
				return true;

			} catch (UnsupportedFlavorException ignored) {
			} catch (IOException ignored) {
			}
		}

		// handle potential ggb file drop
		((GuiManagerD)app.getGuiManager()).handleGGBFileDrop(t);

		return false;
	}


	@Override
	public Transferable createTransferable(JComponent comp) {
		return null;
	}

	public Object getTransferData(DataFlavor flavor) {
		return null;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for(int i = 0; i < supportedFlavors.length; i++){
			if (supportedFlavors[i].equals(flavor))
				return true;
		}
		return false;
	}
}




