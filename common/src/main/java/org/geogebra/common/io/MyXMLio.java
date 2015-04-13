/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.common.io;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;

/**
 * Converts GeoGebra constructions to strings and vice versa
 */
public abstract class MyXMLio {

	/**
	 * All xml output is zipped. The created zip archive contains an entry named
	 * XML_FILE for the construction
	 */
	final public static String XML_FILE = "geogebra.xml";

	/**
	 * All xml output is zipped. The created zip archive contains an entry named
	 * XML_FILE_MACRO for the macros
	 */
	final public static String XML_FILE_MACRO = "geogebra_macro.xml";
	
	final public static String XML_FILE_DEFAULTS = "geogebra_defaults.xml";

	/** library JavaScript available to objects with JavaScript scripts */
	final public static String JAVASCRIPT_FILE = "geogebra_javascript.js";

	/**
	 * All xml output is zipped. The created zip archive *may* contain an entry
	 * named XML_FILE_THUMBNAIL for the construction
	 */
	final public static String XML_FILE_THUMBNAIL = "geogebra_thumbnail.png";
	/** max no of horizontal pixels of thumbnail */
	final public static double THUMBNAIL_PIXELS_X = 256.0;
	/** max no of vertical pixels of thumbnail */
	final public static double THUMBNAIL_PIXELS_Y = 256.0;
	/** application */
	protected App app;
	/** kernel */
	protected Kernel kernel;
	/** construction */
	protected Construction cons;

	/**
	 * Returns XML representation of all settings and construction needed for
	 * undo.
	 * 
	 * @param c
	 *            construction
	 * @return construction XML for undo step
	 */
	public final static synchronized StringBuilder getUndoXML(Construction c,
			boolean getListenersToo) {

		Kernel constructionKernel = c.getKernel();
		boolean kernelIsGettingUndo = constructionKernel.isGettingUndo();
		constructionKernel.setIsGettingUndo(true);

		App consApp = c.getApplication();

		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, consApp.getUniqueId());

		// save euclidianView settings
		consApp.getCompanion().getEuclidianViewXML(sb, false);

		// save kernel settings
		c.getKernel().getKernelXML(sb, false);

		// save construction
		c.getConstructionXML(sb, getListenersToo);

		// save ProbabilityCalculator settings
		if (consApp.isUsingFullGui() && consApp.getGuiManager() != null
				&& consApp.getGuiManager().hasProbabilityCalculator()) {
			consApp.getGuiManager().getProbabilityCalculatorXML(sb);
		}

		sb.append("</geogebra>");

		constructionKernel.setIsGettingUndo(kernelIsGettingUndo);

		return sb;
	}

	/**
	 * @param xml
	 *            XML string
	 * @param clearConstruction
	 *            true to clear construction before processing
	 * @param isGgtFile
	 *            true for macro files
	 * @throws Exception
	 *             if XML is invalid or there was a problem while processing
	 */
	public void processXMLString(String xml, boolean clearConstruction,
			boolean isGgtFile) throws Exception {
		cons.setFileLoading(true);
		processXMLString(xml, clearConstruction, isGgtFile, true);
		cons.setFileLoading(false);
	}

	/**
	 * @param xml
	 *            XML string
	 * @param clearConstruction
	 *            true to clear construction before processing
	 * @param isGgtFile
	 *            true for macro files
	 * @param settingsBatch
	 *            true to process ettings changes as a batch
	 * @throws Exception
	 *             if XML is invalid or there was a problem while processing
	 */
	public abstract void processXMLString(String xml,
			boolean clearConstruction, boolean isGgtFile, boolean settingsBatch)
			throws Exception;

	/**
	 * Appends the &lt;geogebra> tag to given builder, including XSD link and
	 * construction ID
	 * 
	 * @param sb
	 *            builder
	 * @param isMacro
	 *            true for ggt files
	 * @param uniqueId
	 *            construction ID
	 */
	protected final static void addGeoGebraHeader(StringBuilder sb,
			boolean isMacro, String uniqueId) {

		// make sure File -> Share works in HTML5 App
		// (GeoGebraTube doesn't display 5.0 applets)
		String format = GeoGebraConstants.XML_FILE_FORMAT;

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
			sb.append(GeoGebraConstants.GGT_XSD_FILENAME); // eg ggt.xsd
		else
			sb.append(GeoGebraConstants.GGB_XSD_FILENAME); // eg ggb.xsd
		sb.append("\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");
	}

	/**
	 * Appends &lt;?xml ... ?> header to given builder
	 * 
	 * @param sb
	 *            builder
	 */
	protected final static void addXMLHeader(StringBuilder sb) {
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
	}

	/**
	 * @return XML representation of all settings and construction Returns XML
	 *         representation of all settings and construction. GeoGebra File
	 *         Format.
	 */
	public String getFullXML() {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, app.getUniqueId());
		// sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		// sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\"");
		// sb.append(" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/");
		// sb.append(GeoGebra.GGB_XSD_FILENAME); //eg ggb.xsd
		// sb.append("\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");

		// save gui settings
		sb.append(app.getCompleteUserInterfaceXML(false));
		
		// save construction
		cons.getConstructionXML(sb, false);

		sb.append("</geogebra>");
		return sb.toString();
	}

	/**
	 * Returns XML representation of given macros in the kernel, including
	 * header.
	 * 
	 * @param macros
	 *            list of macros
	 * @return XML representation of given macros in the kernel.
	 */
	public String getFullMacroXML(ArrayList<Macro> macros) {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, true, null);
		// save construction
		sb.append(kernel.getMacroXML(macros));

		sb.append("</geogebra>");
		return sb.toString();
	}

	/**
	 * Returns XML representation of all settings WITHOUT construction.
	 * 
	 * @return XML representation of all settings WITHOUT construction.
	 */
	public String getPreferencesXML() {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, null);
		// sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		// sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT
		// + "\">\n");

		// save gui settings
		sb.append(app.getCompleteUserInterfaceXML(true));

		sb.append("</geogebra>");
		return sb.toString();
	}

	/**
	 * Returns .out representation for regression testing.
	 * 
	 * @return .out representation for regression testing.
	 */
	public String getConstructionRegressionOut() {
		StringBuilder sb = new StringBuilder();
		cons.getConstructionRegressionOut(sb);
		return sb.toString();
	}

}
