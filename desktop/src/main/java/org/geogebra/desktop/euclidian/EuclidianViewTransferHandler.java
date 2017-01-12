package org.geogebra.desktop.euclidian;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.view.data.PlotPanelEuclidianViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.AlgebraViewTransferHandler;
import org.geogebra.desktop.util.CASTransferHandler;

/**
 * Transfer handler for EuclidianView
 * 
 * @author G. Sturr
 * 
 */
public class EuclidianViewTransferHandler extends TransferHandler
		implements Transferable {

	private static final long serialVersionUID = 1L;

	private EuclidianView ev;
	private AppD app;

	static DataFlavor textReaderFlavor;
	static {

		try {
			textReaderFlavor = new DataFlavor(
					"text/plain;class=java.io.Reader");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	// supported data flavors
	private static volatile DataFlavor supportedFlavors[] = null;// {

	// null,//DataFlavor.imageFlavor,
	// null,//DataFlavor.stringFlavor,
	// null,//DataFlavor.javaFileListFlavor,
	// null,//AlgebraViewTransferHandler.algebraViewFlavor,
	// null};//PlotPanelEuclidianView.plotPanelFlavor};

	private void setSupportedFlavours() {
		if (supportedFlavors == null) {
			DataFlavor[] supportedFlavors0;
			if (app.isUsingFullGui()) {
				supportedFlavors0 = new DataFlavor[5];
				supportedFlavors0[0] = DataFlavor.imageFlavor;
				supportedFlavors0[1] = DataFlavor.stringFlavor;
				supportedFlavors0[2] = DataFlavor.javaFileListFlavor;
				supportedFlavors0[3] = AlgebraViewTransferHandler.algebraViewFlavor;
				supportedFlavors0[4] = PlotPanelEuclidianViewD.plotPanelFlavor;
			} else {
				supportedFlavors0 = new DataFlavor[3];
				supportedFlavors0[0] = DataFlavor.imageFlavor;
				supportedFlavors0[1] = DataFlavor.stringFlavor;
				supportedFlavors0[2] = DataFlavor.javaFileListFlavor;
			}

			supportedFlavors = supportedFlavors0;

		}
	}

	/****************************************
	 * Constructor
	 * 
	 * @param ev
	 *            euclidian view
	 */
	public EuclidianViewTransferHandler(EuclidianView ev) {
		this.ev = ev;
		this.app = (AppD) ev.getApplication();
	}

	/**
	 * Ensures that transfer are done in COPY mode
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	/**
	 * Returns true if any element of the DataFlavor parameter array is a
	 * supported flavor.
	 */
	@Override
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
	 * Handles data import.
	 */
	@Override
	public boolean importData(JComponent comp, Transferable t) {

		// give the drop target (this EV) the view focus
		requestViewFocus();

		Point mousePos = ((EuclidianViewInterfaceD) ev).getMousePosition();

		// ------------------------------------------
		// Import handling is done in this order:
		// 1) PlotPanel GeoElement copies
		// 2) Images
		// 3) Text
		// 4) CASTableCells
		// 5) GGB files
		// ------------------------------------------

		// try to get PlotPanel GeoElement copies
		if (t.isDataFlavorSupported(PlotPanelEuclidianViewD.plotPanelFlavor)) {

			try {
				AbstractAction act = (AbstractAction) t.getTransferData(
						PlotPanelEuclidianViewD.plotPanelFlavor);
				act.putValue("euclidianViewID", ev.getViewID());
				act.actionPerformed(new ActionEvent(act, 0, null));
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		// try to get an image
		boolean imageDropped = ((GuiManagerD) ev.getApplication()
				.getGuiManager()).loadImage(t, false);
		if (imageDropped) {
			return true;
		}

		// handle CAS table cells as simple latex string (not dynamic!!)
		// ToDo: make it dynamic (after ticket 2449 is finished)
		DataFlavor[] df = t.getTransferDataFlavors();
		for (DataFlavor d : df) {
			Log.debug(d);
		}
		if (t.isDataFlavorSupported(CASTransferHandler.casTableFlavor)) {
			try {

				// after it is possible to refer to cas cells with "$1" we can
				// refer dynamically

				// String tableRef;
				StringBuilder sb = new StringBuilder("FormulaText[$");
				sb.append(1 + (Integer) t
						.getTransferData(CASTransferHandler.casTableFlavor));
				sb.append("]");
				// tableRef = "$" + (cellnumber+1);

				// create a GeoText on the specific mouse position
				GeoElementND[] ret = ev.getApplication().getKernel()
						.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionsOrErrors(
								sb.toString(), false);

				if (ret != null && ret[0] instanceof TextValue) {
					GeoText geo = (GeoText) ret[0];
					geo.setLaTeX(true, false);

					// TODO: h should equal the geo height, this is just an
					// estimate
					double h = 2 * app.getFontSize();

					geo.setRealWorldLoc(ev.toRealWorldCoordX(mousePos.x),
							ev.toRealWorldCoordY(mousePos.y - h));
					geo.updateRepaint();
					app.storeUndoInfo();
				}

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		// check for ggb file drop
		boolean ggbFileDropped = ((GuiManagerD) app.getGuiManager())
				.handleGGBFileDrop(t);
		if (ggbFileDropped) {
			return true;
		}

		// handle all text flavors
		if (t.isDataFlavorSupported(DataFlavor.stringFlavor)
				|| t.isDataFlavorSupported(
						AlgebraViewTransferHandler.algebraViewFlavor)) {
			try {

				String text = null; // expression to be converted into GeoText
				boolean isLaTeX = false;

				// get text from AlgebraView flavor
				if (t.isDataFlavorSupported(
						AlgebraViewTransferHandler.algebraViewFlavor)) {

					isLaTeX = true;

					// get list of selected geo labels
					ArrayList<String> list = (ArrayList<String>) t
							.getTransferData(
									AlgebraViewTransferHandler.algebraViewFlavor);

					text = EuclidianView.getDraggedLabels(list);

					if (text == null) {
						return false;
					}
				}

				// get text from String flavor
				else {
					try {
						// first try to read text line-by-line
						Reader r = textReaderFlavor.getReaderForText(t);
						if (r != null) {
							StringBuilder sb = new StringBuilder();
							String line = null;
							BufferedReader br = new BufferedReader(r);
							line = br.readLine();
							while (line != null) {
								sb.append(line + "\n");
								line = br.readLine();
							}
							br.close();
							text = sb.toString();
						}
					} catch (Exception e) {
						Log.debug("Caught exception decoding text transfer:"
								+ e.getMessage());
					}

					// if the reader didn't work, try to get whatever string is
					// available
					if (text == null) {
						text = (String) t
								.getTransferData(DataFlavor.stringFlavor);
					}

					// exit if no text found
					if (text == null) {
						return false;
					}

					// TODO --- validate the text? e.g. no quotes for a GeoText

					// wrap text in quotes
					text = "\"" + text + "\"";
				}

				// ---------------------------------
				// create GeoText

				GeoElementND[] ret = ev.getApplication().getKernel()
						.getAlgebraProcessor()
						.processAlgebraCommand(text, true);

				if (ret != null && ret[0] instanceof TextValue) {
					GeoText geo = (GeoText) ret[0];
					geo.setLaTeX(isLaTeX, false);

					// TODO: h should equal the geo height, this is just an
					// estimate
					double h = 2 * app.getFontSize();

					geo.setRealWorldLoc(ev.toRealWorldCoordX(mousePos.x),
							ev.toRealWorldCoordY(mousePos.y - h));
					geo.updateRepaint();

				}

				return true;

			} catch (UnsupportedFlavorException ignored) {
				// TODO
			} catch (IOException ignored) {
				// TODO
			}
		}
		return false;
	}

	/**
	 * Sets the focus to this EV. TODO: use this view's id directly to set the
	 * focus (current code assumes only 2 EVs)
	 */
	private void requestViewFocus() {
		if (ev.equals(app.getEuclidianView1())) {
			((GuiManagerD) app.getGuiManager()).getLayout()
					.getDockManager().setFocusedPanel(App.VIEW_EUCLIDIAN);
		} else {
			((GuiManagerD) app.getGuiManager()).getLayout()
					.getDockManager().setFocusedPanel(App.VIEW_EUCLIDIAN2);
		}
	}

	@Override
	public Transferable createTransferable(JComponent comp) {
		return null;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) {
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (int i = 0; i < supportedFlavors.length; i++) {
			if (supportedFlavors[i].equals(flavor)) {
				return true;
			}
		}
		return false;
	}
}
