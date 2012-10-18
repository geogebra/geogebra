/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.io;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;

import java.util.ArrayList;

/**
 * Converts GeoGebra constructions to strings and vice versa
 */
public abstract class MyXMLio {

	/** All xml output is zipped. The created zip archive contains
	* an entry named XML_FILE for the construction*/
	final public static String XML_FILE = "geogebra.xml";

	/** All xml output is zipped. The created zip archive contains
	* an entry named XML_FILE_MACRO for the macros*/
	final public static String XML_FILE_MACRO = "geogebra_macro.xml";

	/** library JavaScript available to objects with JavaScript scripts */
	final public static String JAVASCRIPT_FILE = "geogebra_javascript.js";
	/** library Python script available to objects with Python scripts */
	final public static String PYTHON_FILE = "geogebra_python.py";
	/** library LOGO script available to objects with LOGO scripts */
	final public static String LOGO_FILE = "geogebra_logo.logo";

	/** All xml output is zipped. The created zip archive *may* contain
	* an entry named XML_FILE_THUMBNAIL for the construction */
	final public static String XML_FILE_THUMBNAIL = "geogebra_thumbnail.png";
	/** max no of horizontal pixels of thumbnail */
	final public static double THUMBNAIL_PIXELS_X = 200.0;
	/** max no of vertical pixels of thumbnail */
	final public static double THUMBNAIL_PIXELS_Y = 200.0; 
	/** application */
	protected App app;
	/** kernel */
	protected Kernel kernel;
	/** construction */
	protected Construction cons;
	/** returns construction XML for undo step
	 * @param construction construction
	 * @return construction XML for undo step*/
	public abstract StringBuilder getUndoXML(Construction construction);

	/**
	 * @param xml XML string
	 * @param clearConstruction true to clear construction before processing
	 * @param isGgtFile true for macro files
	 * @throws Exception if XML is invalid or there was a problem while processing
	 */
	public void processXMLString(String xml, boolean clearConstruction, boolean isGgtFile) throws Exception {
		cons.setFileLoading(true);
		processXMLString(xml,clearConstruction,isGgtFile,true);
		cons.setFileLoading(false);
	}

	/**
	 * @param xml XML string
	 * @param clearConstruction true to clear construction before processing
	 * @param isGgtFile true for macro files
	 * @param settingsBatch true to process ettings changes as a batch
	 * @throws Exception if XML is invalid or there was a problem while processing
	 */
	public abstract void processXMLString(String xml, boolean clearConstruction, boolean isGgtFile, boolean settingsBatch) throws Exception;


	/**
	 * Appends the &lt;geogebra> tag to given builder, including XSD link and construction ID
	 * @param sb builder
	 * @param isMacro true for ggt files
	 * @param uniqueId construction ID
	 * @param app application (may afect XML version) //TODO remove once Tube supports 5.0 applets
	 */
	protected final static void addGeoGebraHeader(StringBuilder sb, boolean isMacro, String uniqueId, App app) {
		
		// make sure File -> Share works in HTML5 App
		// (GeoGebraTube doesn't display 5.0 applets)
		String format = app.isHTML5Applet() ? "4.2" : GeoGebraConstants.XML_FILE_FORMAT;
		
		sb.append("<geogebra format=\"");
		sb.append(format);
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

	/**
	 * Appends &lt;?xml ... ?> header to given builder
	 * @param sb builder
	 */
	protected final static void addXMLHeader(StringBuilder sb) {
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
	}

	/**
	 * @return XML representation of all settings and construction
	 * Returns XML representation of all settings and construction. GeoGebra
	 * File Format.
	 */
	public String getFullXML() {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, app.getUniqueId(), app);
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
	 * Returns XML representation of given macros in the kernel,
	 * including header.
	 * @param macros list of macros
	 * @return XML representation of given macros in the kernel.
	 */
	public String getFullMacroXML(ArrayList<Macro> macros) {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, true, null, app);
		// save construction
		sb.append(kernel.getMacroXML(macros));

		sb.append("</geogebra>");
		return sb.toString();
	}

	/**
	 * Returns XML representation of all settings WITHOUT construction.
	 * @return XML representation of all settings WITHOUT construction.
	 */
	public String getPreferencesXML() {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, null, app);
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
	* @return .out representation for regression testing.
	*/ 
	public String getConstructionRegressionOut() {
		StringBuilder sb = new StringBuilder();
		cons.getConstructionRegressionOut(sb);
		return sb.toString();
	}

}
