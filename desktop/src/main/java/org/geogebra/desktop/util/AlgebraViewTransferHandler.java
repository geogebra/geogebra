package org.geogebra.desktop.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.gui.view.algebra.AlgebraViewD;
import org.geogebra.desktop.main.AppD;

/**
 * Transfer handler for AlgebraView.
 * 
 * Exports its own data flavor, AlgebraViewFlavor. This contains an arrayList of
 * the labels of all selected geos in the AlgebraView
 * 
 * @author gsturr
 * 
 */
public class AlgebraViewTransferHandler extends TransferHandler
		implements Transferable {
	private static final long serialVersionUID = 1L;
	private AppD app;
	/** for transfering geos into AV */
	public static final DataFlavor algebraViewFlavor = new DataFlavor(
			AlgebraViewD.class, "algebraView");
	private static final DataFlavor supportedFlavors[] = { algebraViewFlavor };

	private ArrayList<String> geoLabelList;

	/****************************************
	 * Constructor
	 */
	public AlgebraViewTransferHandler(AppD app) {
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
		if (geoLabelList == null) {
			geoLabelList = new ArrayList<>();
		} else {
			geoLabelList.clear();
		}
		if (comp instanceof AlgebraViewD) {
			ArrayList<GeoElement> geos = app.getSelectionManager()
					.getSelectedGeos();
			for (GeoElement geo : geos) {
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

	@Override
	public Object getTransferData(DataFlavor flavor) {
		if (isDataFlavorSupported(flavor)) {
			return geoLabelList;
		}
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (int i = 0; i < supportedFlavors.length; i++) {
			// System.out.println(flavor.getMimeType());
			// System.out.println(supportedFlavors[i].getMimeType());
			// System.out.println("------------");
			if (supportedFlavors[i].equals(flavor)) {
				return true;
			}
		}
		return false;
	}
}
