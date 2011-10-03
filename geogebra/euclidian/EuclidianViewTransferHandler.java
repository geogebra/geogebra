package geogebra.euclidian;


import geogebra.gui.view.spreadsheet.statdialog.PlotPanelEuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.main.Application;
import geogebra.util.AlgebraViewTransferHandler;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Transfer handler for EuclidianView 
 * @author G. Sturr
 *
 */
public class EuclidianViewTransferHandler extends TransferHandler implements Transferable {

	private EuclidianView ev;
	private Application app;

	static DataFlavor textReaderFlavor;
	static {

		try { 
			textReaderFlavor = 
				new DataFlavor ("text/plain;class=java.io.Reader"); 			
		} catch (ClassNotFoundException cnfe) { 
			cnfe.printStackTrace( );
		}
	}


	// supported data flavors
	private static DataFlavor supportedFlavors[] = null;//{
		//null,//DataFlavor.imageFlavor,
		//null,//DataFlavor.stringFlavor,
		//null,//DataFlavor.javaFileListFlavor,
		//null,//AlgebraViewTransferHandler.algebraViewFlavor,
		//null};//PlotPanelEuclidianView.plotPanelFlavor};
	
	private void setSupportedFlavours() {
		if (supportedFlavors == null) {
			if (app.useFullGui()) {
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

	private boolean debug  = true;


	/****************************************
	 * Constructor
	 * @param ev
	 */
	public EuclidianViewTransferHandler(EuclidianView ev){
		this.ev = ev;
		this.app = ev.getApplication();
	}


	/**
	 * Ensures that transfer are done in COPY mode
	 */
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	/**
	 * Returns true if any element of the DataFlavor parameter array is a supported flavor.
	 */
	public boolean canImport(JComponent comp, DataFlavor flavor[]) {
		
		setSupportedFlavours();
		
		for (int i = 0, n = flavor.length; i < n; i++) {
			//System.out.println(flavor[i].getMimeType());
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
	public boolean importData(JComponent comp, Transferable t) {

		
		// give the drop target (this EV) the view focus
		requestViewFocus();

		// get context info
		Construction cons = ev.getApplication().getKernel().getConstruction();
		Point mousePos = ev.getMousePosition();
		GeoPoint startPoint = new GeoPoint(cons);

		double x = ev.toRealWorldCoordX(mousePos.x);
		double y = ev.toRealWorldCoordX(mousePos.y);

		startPoint.setCoords(x,y, 1.0);

		//------------------------------------------
		// Import handling is done in this order:
		// 1) Images
		// 2) Text
		// 3) GGB files
		//------------------------------------------

		
		// first try to get an image
		if (t.isDataFlavorSupported(PlotPanelEuclidianView.plotPanelFlavor)){
			if(ev == app.getEuclidianView())
				app.getGuiManager().getProbabilityCalculator().exportGeosToEV(1);
			else if(ev == app.getEuclidianView2())
				app.getGuiManager().getProbabilityCalculator().exportGeosToEV(2);
			return true;
		}
		
		// first try to get an image
		boolean imageDropped = ev.getApplication().getGuiManager().loadImage(startPoint, t, false);
		if(imageDropped) return true;


		// handle all text flavors
		if (t.isDataFlavorSupported(DataFlavor.stringFlavor)
				|| t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)) {
			try {

				String text = null; // expression to be converted into GeoText 
				boolean isLaTeX = false;


				// get text from AlgebraView flavor 
				if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)){

					isLaTeX = true;

					// get list of selected geo labels
					ArrayList<String> list = (ArrayList<String>) t
					.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);

					// exit if empty list
					if(list.size()==0) return false;

					// single geo
					if(list.size()==1){
						text = "FormulaText[" + list.get(0) + ", true, true]";
					}

					// multiple geos, wrap in TableText
					else{
						GeoElement geo;
						text = "TableText[";
						for(int i=0; i<list.size(); i++){
							geo = app.getKernel().lookupLabel(list.get(i));

							text += "{FormulaText[" + list.get(i) + ", true, true]}";
							if(i<list.size()-1){
								text += ",";
							}
						}
						text += "]";
					}
				}

				// get text from String flavor
				else{ 	
					try {
						// first try to read text line-by-line
						Reader r = textReaderFlavor.getReaderForText(t);
						if(r!=null){
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
					}
					catch (Exception e) {
						Application.debug("Caught exception decoding text transfer:" + e.getMessage());
					}

					// if the reader didn't work, try to get whatever string is available
					if(text == null)
						text = (String) t.getTransferData(DataFlavor.stringFlavor);

					// exit if no text found
					if(text == null) return false;

					//TODO --- validate the text? e.g. no quotes for a GeoText

					// wrap text in quotes
					text = "\"" + text + "\"";

				}


				//---------------------------------
				// create GeoText 

				GeoElement[] ret = ev.getApplication().getKernel().getAlgebraProcessor()
				.processAlgebraCommand(text, true);

				if (ret != null && ret[0].isTextValue()) {
					GeoText geo = (GeoText) ret[0];
					geo.setLaTeX(isLaTeX, false);
					
					//TODO: h should equal the geo height, this is just an estimate
					double h = 2*app.getFontSize();
					
					geo.setRealWorldLoc(ev.toRealWorldCoordX(mousePos.x), ev.toRealWorldCoordY(mousePos.y-h));
					geo.updateRepaint();
					
				}

				return true;

			} catch (UnsupportedFlavorException ignored) {
			} catch (IOException ignored) {
			}
		}



		// check for ggb file drop
		boolean ggbFileDropped =  app.getGuiManager().handleGGBFileDrop(t);
		if(ggbFileDropped) return true;

		return false;
	}


	/**
	 * Sets the focus to this EV. 
	 * TODO: use this view's id directly to set the focus (current code assumes only 2 EVs)
	 */
	private void requestViewFocus(){
		if(ev.equals(app.getEuclidianView()))
			app.getGuiManager().getLayout().getDockManager().setFocusedPanel(Application.VIEW_EUCLIDIAN);
		else
			app.getGuiManager().getLayout().getDockManager().setFocusedPanel(Application.VIEW_EUCLIDIAN2);
	}



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




