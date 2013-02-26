package geogebra.util;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.view.algebra.AlgebraViewD;
import geogebra.main.AppD;

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
	private static final long serialVersionUID = 1L;
	private AppD app;

	public static DataFlavor algebraViewFlavor = new DataFlavor(AlgebraViewD.class, "algebraView");
	private static final DataFlavor supportedFlavors[] = { algebraViewFlavor };

	private ArrayList<String> geoLabelList;


	/****************************************
	 * Constructor
	 */
	public AlgebraViewTransferHandler(AppD app){
		this.app = app;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor flavor[]) {	
		return false;
	}

	@Override
	public Transferable createTransferable(JComponent comp) {
		if(geoLabelList == null)
			geoLabelList = new ArrayList<String>();
		else
			geoLabelList.clear();		
		if (comp instanceof AlgebraViewD) {
			ArrayList<GeoElement> geos = app.getSelectionManager().getSelectedGeos();		
			for(GeoElement geo : geos){
				geoLabelList.add(geo.getLabel(StringTemplate.defaultTemplate));
			}

			return this;
		}
		return null;
	}

	@Override
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

