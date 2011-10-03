package geogebra.util;


import geogebra.gui.view.algebra.AlgebraView;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Transfer handler for AlgebraView.
 * 
 * Exports its own data flavor, AlgebraViewFlavor. This contains an arrayList of
 * the labels of all selected geos in the AlgebraView
 * 
 * @author gsturr
 * 
 */
public class AlgebraViewTransferHandler extends TransferHandler implements Transferable {

	private Application app;

	public static DataFlavor algebraViewFlavor = new DataFlavor(AlgebraView.class, "algebraView");
	private static final DataFlavor supportedFlavors[] = { algebraViewFlavor };

	private ArrayList<String> geoLabelList;


	/****************************************
	 * Constructor
	 * @param ev
	 */
	public AlgebraViewTransferHandler(Application app){
		this.app = app;
	}



	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	public boolean canImport(JComponent comp, DataFlavor flavor[]) {	
		return false;
	}

	public Transferable createTransferable(JComponent comp) {
		
		if(geoLabelList == null)
			geoLabelList = new ArrayList<String>();
		else
			geoLabelList.clear();
		
		if (comp instanceof AlgebraView) {
			ArrayList<GeoElement> geos = app.getSelectedGeos();		
			for(GeoElement geo : geos){
				geoLabelList.add(geo.getLabel());
			}
			return this;
		}
		return null;
	}

	public boolean importData(JComponent comp, Transferable t) {
		return false;
	}


	public Object getTransferData(DataFlavor flavor) {
		if (isDataFlavorSupported(flavor)) {
			return geoLabelList;
		}
		return null;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for(int i = 0; i < supportedFlavors.length; i++){
			//System.out.println(flavor.getMimeType());
			//System.out.println(supportedFlavors[i].getMimeType());
			//System.out.println("------------");
			if (supportedFlavors[i].equals(flavor))
				return true;
		}
		return false;
	}
}

