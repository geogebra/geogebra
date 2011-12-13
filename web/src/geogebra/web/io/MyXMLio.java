package geogebra.web.io;

import geogebra.common.GeoGebraConstants;
import geogebra.common.io.DocHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.main.AbstractApplication;
import geogebra.web.kernel.Kernel;
import geogebra.web.main.Application;

public class MyXMLio implements geogebra.common.io.MyXMLio {
	
	private AbstractApplication app;
	private Kernel kernel;
	private DocHandler handler, ggbDocHandler;//, i2gDocHandler;
	private XmlParser xmlParser;
	private Construction cons;
	
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
	
	private final static void addXMLHeader(StringBuilder sb) {
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
	}
	
	private final static void addGeoGebraHeader(StringBuilder sb, boolean isMacro) {
		sb.append("<geogebra format=\"" + GeoGebraConstants.XML_FILE_FORMAT + "\"");
		sb.append(" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/");
		if (isMacro)
			sb.append(GeoGebraConstants.GGT_XSD_FILENAME); //eg	ggt.xsd
		else
			sb.append(GeoGebraConstants.GGB_XSD_FILENAME); //eg	ggb.xsd
		sb.append("\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");
	}

	public String getFullXML() {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false);
		//sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		//sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\"");
		//sb.append(" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/");
		//sb.append(GeoGebra.GGB_XSD_FILENAME); //eg	ggb.xsd
		//sb.append("\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");

		// save gui settings
		sb.append(app.getCompleteUserInterfaceXML(false));		

		// save construction
		cons.getConstructionXML(sb);
		
		// save cas session
		/*AGif (app.hasFullGui() && app.getGuiManager().hasCasView()) {
			app.getGuiManager().getCasView().getSessionXML(sb);
		}*/

		sb.append("</geogebra>");
		return sb.toString();
	}
	
	public void processXmlString(String xml, boolean clearConstruction, boolean isGgtFile) throws Exception {
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
		addGeoGebraHeader(sb, false);
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
