package geogebra.touch.gui.elements.header;

import geogebra.common.io.DocHandler;
import geogebra.common.io.MyXMLio;
import geogebra.common.kernel.Construction;
import geogebra.web.io.GwtXmlParser;
import geogebra.web.io.XmlParser;

public class XMLBuilder extends MyXMLio
{

	private DocHandler handler, ggbDocHandler;
	private XmlParser xmlParser;

	public XMLBuilder(Construction cons)
	{
		this.cons = cons;
		this.kernel = cons.getKernel();
		this.app = cons.getApplication();

		this.xmlParser = new GwtXmlParser();
		this.handler = getGGBHandler();
	}

	private DocHandler getGGBHandler()
	{
		if (this.ggbDocHandler == null)
			this.ggbDocHandler = this.kernel.newMyXMLHandler(this.cons);
		return this.ggbDocHandler;
	}

	@Override
	public StringBuilder getUndoXML(Construction construction)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processXMLString(String xml, boolean clearConstruction, boolean isGgtFile) throws Exception
	{
		doParseXML(xml, clearConstruction, isGgtFile);

	}

	private void doParseXML(String xml, boolean clearConstruction, boolean isGGTFile) throws Exception
	{
		boolean oldVal = this.kernel.isNotifyViewsActive();
		boolean oldVal2 = this.kernel.isUsingInternalCommandNames();
		this.kernel.setUseInternalCommandNames(true);

		if (clearConstruction)
		{
			// clear construction
			this.kernel.clearConstruction(true);
		}

		try
		{
			this.kernel.setLoadingMode(true);
			if (!isGGTFile)
			{
				this.app.getSettings().beginBatch();
				this.xmlParser.parse(this.handler, xml);
				this.app.getSettings().endBatch();
			}
			else
				this.xmlParser.parse(this.handler, xml);
			// xmlParser.reset();
			this.kernel.setLoadingMode(false);
		}
		catch (Error e)
		{
			// e.printStackTrace();
			throw e;
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			this.kernel.setUseInternalCommandNames(oldVal2);
			if (!isGGTFile)
			{
				this.kernel.updateConstruction();
				this.kernel.setNotifyViewsActive(oldVal);
			}
			if (!isGGTFile)
			{
				// needs to be done after call to updateConstruction() to avoid spurious
				// traces
				this.app.getTraceManager().loadTraceGeoCollection();
			}

		}
	}

	@Override
	public void processXMLString(String xml, boolean clearConstruction, boolean isGgtFile, boolean settingsBatch) throws Exception
	{
		// TODO Auto-generated method stub

	}

}
