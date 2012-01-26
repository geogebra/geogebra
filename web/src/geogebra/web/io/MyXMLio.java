package geogebra.web.io;

import geogebra.common.GeoGebraConstants;
import geogebra.common.io.DocHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.main.AbstractApplication;
import geogebra.common.kernel.Kernel;
import geogebra.web.main.Application;

public class MyXMLio extends geogebra.common.io.MyXMLio {

	private DocHandler handler, ggbDocHandler;//, i2gDocHandler;
	private XmlParser xmlParser;

	public MyXMLio(Kernel kernel, Construction cons) {
		this.kernel = kernel;
		this.cons = cons;	
		app = kernel.getApplication();

		xmlParser = new GwtXmlParser();
		handler = getGGBHandler();
	}

	private DocHandler getGGBHandler() {
		if (ggbDocHandler == null)	
			ggbDocHandler = kernel.newMyXMLHandler(cons);
		return ggbDocHandler;
	}

	public void processXMLString(String xml, boolean clearConstruction, boolean isGgtFile) throws Exception {
		boolean oldVal = kernel.isNotifyViewsActive();
		if (!isGgtFile) {
			kernel.setNotifyViewsActive(false);
		}

		if (clearConstruction) {
			kernel.clearConstruction();
		}

		try {
			xmlParser.parse(handler, xml);
		} finally {
			if (!isGgtFile) {
				kernel.updateConstruction();
				kernel.setNotifyViewsActive(oldVal);				
			}
		}

		// handle construction step stored in XMLhandler
		// do this only if the construction protocol navigation is showing	
		/*AGif (!isGGTFile && oldVal &&
				app.showConsProtNavigation()) 
		{
				app.getGuiManager().setConstructionStep(handler.getConsStep());
		}*/
	}
	
	/**
	 * Returns XML representation of all settings and construction needed for
	 * undo.
	 */
	public synchronized StringBuilder getUndoXML(Construction c) {
		AbstractApplication app = c.getApplication();

		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, app.getUniqueId());
		//sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		//sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\">\n");

		// save euclidianView settings
		//app.getEuclidianView().getXML(sb);
		
		// save kernel settings
		c.getKernel().getKernelXML(sb, false);

		// save construction
		c.getConstructionXML(sb);

		// save cas session
		//AGif (app.hasFullGui() && app.getGuiManager().hasCasView()) {
		//AG	app.getGuiManager().getCasView().getSessionXML(sb);
		//AG}
		
		// save spreadsheetView settings
		//AGapp.getGuiManager().getSpreadsheetViewXML(sb);
		

		sb.append("</geogebra>");

		/*
		 * Application.debug("*******************");
		 * Application.debug(sb.toString());
		 * Application.debug("*******************");
		 */

		return sb;
	}

}
