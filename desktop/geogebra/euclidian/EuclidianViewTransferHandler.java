package geogebra.euclidian;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.view.spreadsheet.statdialog.PlotPanelEuclidianView;
import geogebra.main.AppD;
import geogebra.util.AlgebraViewTransferHandler;
import geogebra.util.CASTransferHandler;

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

/**
 * Transfer handler for EuclidianView
 * 
 * @author G. Sturr
 * 
 */
public class EuclidianViewTransferHandler extends TransferHandler implements
		Transferable {

	private static final long serialVersionUID = 1L;

	private EuclidianViewND ev;
	private AppD app;

	static DataFlavor textReaderFlavor;
	static {

		try {
			textReaderFlavor = new DataFlavor("text/plain;class=java.io.Reader");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	// supported data flavors
	private static DataFlavor supportedFlavors[] = null;// {

	// null,//DataFlavor.imageFlavor,
	// null,//DataFlavor.stringFlavor,
	// null,//DataFlavor.javaFileListFlavor,
	// null,//AlgebraViewTransferHandler.algebraViewFlavor,
	// null};//PlotPanelEuclidianView.plotPanelFlavor};

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

	@SuppressWarnings("unused")
	private boolean debug = true;

	/****************************************
	 * Constructor
	 * 
	 * @param ev euclidian view
	 */
	public EuclidianViewTransferHandler(EuclidianViewND ev) {
		this.ev = ev;
		this.app = ev.getApplication();
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

		Point mousePos = ev.getMousePosition();

		// ------------------------------------------
		// Import handling is done in this order:
		// 1) PlotPanel GeoElement copies
		// 2) Images
		// 3) Text
		// 4) CASTableCells 		
		// 5) GGB files
		// ------------------------------------------

		
		
		// try to get PlotPanel GeoElement copies
		if (t.isDataFlavorSupported(PlotPanelEuclidianView.plotPanelFlavor)) {
	
			try {
				AbstractAction act = (AbstractAction) t.getTransferData(PlotPanelEuclidianView.plotPanelFlavor);
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
		boolean imageDropped = ev.getApplication().getGuiManagerD()
				.loadImage(t, false);
		if (imageDropped)
			return true;

		
		
		
		
		
		//handle CAS table cells as simple latex string (not dynamic!!)
		//ToDo: make it dynamic (after ticket 2449 is finished)
		DataFlavor[] df =t.getTransferDataFlavors();
		for(DataFlavor d:df){
			App.debug(d);
		}
		if(t.isDataFlavorSupported(CASTransferHandler.casTableFlavor)){
			try{
				
				
				
				//after it is possible to refer to cas cells with "$1" we can refer dynamically 
				
				//String tableRef;
				StringBuilder sb = new StringBuilder("FormulaText[$");
				sb.append(1+(Integer)t.getTransferData(CASTransferHandler.casTableFlavor));
				sb.append("]");
				//tableRef = "$" + (cellnumber+1);
				
				
				//create a GeoText on the specific mouse position
				GeoElement[] ret = ev.getApplication().getKernel()
						.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(sb.toString(), true,false,false);

				if (ret != null && ret[0].isTextValue()) {
					GeoText geo = (GeoText) ret[0];
					geo.setLaTeX(true, false);

					// TODO: h should equal the geo height, this is just an
					// estimate
					double h = 2 * app.getFontSize();

					geo.setRealWorldLoc(ev.toRealWorldCoordX(mousePos.x),
							ev.toRealWorldCoordY(mousePos.y - h));
					geo.updateRepaint();

				}

						
				return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
				

		// check for ggb file drop
		boolean ggbFileDropped = app.getGuiManagerD().handleGGBFileDrop(t);
		if (ggbFileDropped)
			return true;

		// handle all text flavors
				if (t.isDataFlavorSupported(DataFlavor.stringFlavor)
						|| t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)) {
					try {

						String text = null; // expression to be converted into GeoText
						boolean isLaTeX = false;

						// get text from AlgebraView flavor
						if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)) {

							isLaTeX = true;

							// get list of selected geo labels
							ArrayList<String> list = (ArrayList<String>) t
									.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);

							// exit if empty list
							if (list.size() == 0)
								return false;

							// single geo
							if (list.size() == 1) {
								text = "FormulaText[" + list.get(0) + ", true, true]";
							}

							// multiple geos, wrap in TableText
							else {
								text = "TableText[";
								for (int i = 0; i < list.size(); i++) {

									text += "{FormulaText[" + list.get(i)
											+ ", true, true]}";
									if (i < list.size() - 1) {
										text += ",";
									}
								}
								text += "]";
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
								App
										.debug("Caught exception decoding text transfer:"
												+ e.getMessage());
							}

							// if the reader didn't work, try to get whatever string is
							// available
							if (text == null)
								text = (String) t
										.getTransferData(DataFlavor.stringFlavor);

							// exit if no text found
							if (text == null)
								return false;

							// TODO --- validate the text? e.g. no quotes for a GeoText

							// wrap text in quotes
							text = "\"" + text + "\"";
						}

						// ---------------------------------
						// create GeoText

						GeoElement[] ret = ev.getApplication().getKernel()
								.getAlgebraProcessor()
								.processAlgebraCommand(text, true);

						if (ret != null && ret[0].isTextValue()) {
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
		if (ev.equals(app.getEuclidianView1()))
			app.getGuiManagerD().getLayout().getDockManager()
					.setFocusedPanel(App.VIEW_EUCLIDIAN);
		else
			app.getGuiManagerD().getLayout().getDockManager()
					.setFocusedPanel(App.VIEW_EUCLIDIAN2);
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
		for (int i = 0; i < supportedFlavors.length; i++) {
			if (supportedFlavors[i].equals(flavor))
				return true;
		}
		return false;
	}
}
