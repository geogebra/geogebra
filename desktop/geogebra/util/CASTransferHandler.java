package geogebra.util;

import geogebra.gui.view.algebra.AlgebraView;
import geogebra.gui.view.spreadsheet.statdialog.PlotPanelEuclidianView;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class CASTransferHandler extends TransferHandler {  
	
	// supported data flavors
	private static DataFlavor supportedFlavors[] = null;// {
	
	private void setSupportedFlavours() {
		if (supportedFlavors == null) {
			if (app.isUsingFullGui()) {
				supportedFlavors = new DataFlavor[5];
				supportedFlavors[0] = DataFlavor.imageFlavor;
				supportedFlavors[1] = DataFlavor.stringFlavor;
				supportedFlavors[2] = DataFlavor.javaFileListFlavor;
				supportedFlavors[3] = AlgebraViewTransferHandler.algebraViewFlavor;
				supportedFlavors[4] = PlotPanelEuclidianView.plotPanelFlavor;
			} else {
				supportedFlavors = new DataFlavor[3];
				supportedFlavors[0] = DataFlavor.imageFlavor;
				supportedFlavors[1] = DataFlavor.stringFlavor;
				supportedFlavors[2] = DataFlavor.javaFileListFlavor;
			}

		}
	}

	
	
    /**
     * We only support importing strings.
     */
    public boolean canImport(JComponent comp, DataFlavor flavor[]) {
    	System.out.println("canImport");

		setSupportedFlavours();

		for (int i = 0, n = flavor.length; i < n; i++) {
			// System.out.println(flavor[i].getMimeType());
			for (int j = 0, m = supportedFlavors.length; j < m; j++) {
				if (flavor[i].equals(supportedFlavors[j])) {
					return true;
				}
			}
		}
		return false;
   }

    /**
     * Bundle up the selected items in a single list for export.
     * Each line is separated by a newline.
     */
    protected Transferable createTransferable(JComponent c) {
    	System.out.println("createTransferable");
        return null;
    }
    
    /**
     * We support both copy and move actions.
     */
    public int getSourceActions(JComponent c) {
    	System.out.println("getSourceActions");
        return TransferHandler.COPY_OR_MOVE;
    }
    
    /**
     * Perform the actual import.  This demo only supports drag and drop.
     */
    public boolean importData(JComponent comp, DataFlavor flavor[]) {
    	System.out.println("importData");
        return true;
    }

    /**
     * Remove the items moved from the list.
     */
    protected void exportDone(JComponent c, Transferable data, int action) {
    	System.out.println("exportDone");
    }
}
