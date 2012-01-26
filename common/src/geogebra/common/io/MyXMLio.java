package geogebra.common.io;

import java.util.ArrayList;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MacroInterface;
import geogebra.common.main.AbstractApplication;

public abstract class MyXMLio {

	protected AbstractApplication app;
	protected Kernel kernel;
	protected Construction cons;

	public abstract StringBuilder getUndoXML(Construction construction);
	public abstract void processXMLString(String xml, boolean clearConstruction, boolean isGgtFile) throws Exception;


	protected final static void addGeoGebraHeader(StringBuilder sb, boolean isMacro, String uniqueId) {
		sb.append("<geogebra format=\"");
		sb.append(GeoGebraConstants.XML_FILE_FORMAT);
		sb.append("\" ");
		sb.append("version=\"");
		sb.append(GeoGebraConstants.VERSION_STRING);
		sb.append("\" ");
		if (uniqueId != null) {
			sb.append("id=\"");
			sb.append(uniqueId); // unique id to identify ggb file
			sb.append("\" ");
		}
		sb.append(" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/");
		if (isMacro)
			sb.append(GeoGebraConstants.GGT_XSD_FILENAME); //eg	ggt.xsd
		else
			sb.append(GeoGebraConstants.GGB_XSD_FILENAME); //eg	ggb.xsd
		sb.append("\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");
	}

	protected final static void addXMLHeader(StringBuilder sb) {
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
	}

	/**
	 * Returns XML representation of all settings and construction. GeoGebra
	 * File Format.
	 */
	public String getFullXML() {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, app.getUniqueId());
		//sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		//sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\"");
		//sb.append(" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/");
		//sb.append(GeoGebra.GGB_XSD_FILENAME); //eg	ggb.xsd
		//sb.append("\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");

		// save gui settings
		sb.append(app.getCompleteUserInterfaceXML(false));		

		// save construction
		cons.getConstructionXML(sb);
		
		sb.append("</geogebra>");
		return sb.toString();
	}

	/**
	 * Returns XML representation of given macros in the kernel.
	 */
	public String getFullMacroXML(ArrayList<MacroInterface> macros) {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, true, null);
		//sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		//sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT
		//				+ "\">\n");

		// save construction
		sb.append(kernel.getMacroXML(macros));

		sb.append("</geogebra>");
		return sb.toString();
	}

	/**
	 * Returns XML representation of all settings WITHOUT construction.
	 */
	public String getPreferencesXML() {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, null);
		//sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		//sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT
		//				+ "\">\n");

		// save gui settings
		sb.append(app.getCompleteUserInterfaceXML(true));
		
		sb.append("</geogebra>");
		return sb.toString();
	}

	/** 
	* Returns .out representation for regression testing. 
	*/ 
	public String getConstructionRegressionOut() {
		StringBuilder sb = new StringBuilder();
		cons.getConstructionRegressionOut(sb);
		return sb.toString();
	}

}
