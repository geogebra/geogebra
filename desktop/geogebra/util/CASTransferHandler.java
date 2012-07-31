package geogebra.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class CASTransferHandler extends TransferHandler {  
	
	// supported data flavors
	public static final DataFlavor casTableFlavor = new DataFlavor(Integer.class, "cell reference");
	public static final DataFlavor casLaTeXFlavor = new DataFlavor(String.class, "cell latex output");
	private static DataFlavor supportedFlavors[] = {};// {
	
	private void setSupportedFlavours() {
		
	}

	
	
    /**
     * We only support importing strings.
     */
    public boolean canImport(JComponent comp, DataFlavor flavor[]) {
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
        return null;
    }
    
    /**
     * We support both copy and move actions.
     */
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }
    
    /**
     * Perform the actual import.  This demo only supports drag and drop.
     */
    public boolean importData(JComponent comp, DataFlavor flavor[]) {
        return true;
    }

    /**
     * Remove the items moved from the list.
     */
    protected void exportDone(JComponent c, Transferable data, int action) {
    }
}
