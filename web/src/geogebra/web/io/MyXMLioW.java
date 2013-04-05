package geogebra.web.io;

import geogebra.common.io.DocHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;

public class MyXMLioW extends geogebra.common.io.MyXMLio {

	private DocHandler handler, ggbDocHandler;
	private XmlParser xmlParser;

	public MyXMLioW(Kernel kernel, Construction cons) {
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

}
