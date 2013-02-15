package geogebra.web.io;

import geogebra.common.io.DocHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

public class MyXMLio extends geogebra.common.io.MyXMLio {

	private DocHandler handler, ggbDocHandler;
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

	@Override
    public void processXMLString(String str, boolean clearAll, boolean isGGTfile, boolean settingsBatch) throws Exception {
		doParseXML(str, clearAll, isGGTfile, clearAll, settingsBatch);
	}

	private void doParseXML(String xml, boolean clearConstruction,
			boolean isGGTFile, boolean mayZoom,boolean settingsBatch) throws Exception {
		boolean oldVal = kernel.isNotifyViewsActive();
		boolean oldVal2 = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);
		
		if (!isGGTFile && mayZoom) {
			kernel.setNotifyViewsActive(false);
		}

		if (clearConstruction) {
			// clear construction
			kernel.clearConstruction(false);
		}

		try {
			kernel.setLoadingMode(true);
			if(settingsBatch && !isGGTFile){
				app.getSettings().beginBatch();
				xmlParser.parse(handler, xml);
				app.getSettings().endBatch();
			}
			else
				xmlParser.parse(handler, xml);
			//xmlParser.reset();
			kernel.setLoadingMode(false);
		} catch (Error e) {
			// e.printStackTrace();
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			kernel.setUseInternalCommandNames(oldVal2);
			if (!isGGTFile && mayZoom) {
				kernel.updateConstruction();
				kernel.setNotifyViewsActive(oldVal);				
			}
			if (!isGGTFile) {
				// needs to be done after call to updateConstruction() to avoid spurious traces
				app.getTraceManager().loadTraceGeoCollection();
			}

		}
	}

	/**
	 * Returns XML representation of all settings and construction needed for
	 * undo.
	 */
	@Override
    public synchronized StringBuilder getUndoXML(Construction c) {
		App app = c.getApplication();

		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, app.getUniqueId());
		//sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		//sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\">\n");

		// save euclidianView settings
		app.getEuclidianViewXML(sb,false);
		
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
