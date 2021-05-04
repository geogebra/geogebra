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

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

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

	/**
	 * defaults for 2D geos
	 */
	final public static String XML_FILE_DEFAULTS_2D = "geogebra_defaults2d.xml";

	/**
	 * defaults for 3D geos
	 */
	final public static String XML_FILE_DEFAULTS_3D = "geogebra_defaults3d.xml";

	/** library JavaScript available to objects with JavaScript scripts */
	final public static String JAVASCRIPT_FILE = "geogebra_javascript.js";

	/**
	 * All xml output is zipped. The created zip archive *may* contain an entry
	 * named XML_FILE_THUMBNAIL for the construction
	 */
	final public static String XML_FILE_THUMBNAIL = "geogebra_thumbnail.png";
	/** max no of horizontal pixels of thumbnail */
	final public static double THUMBNAIL_PIXELS_X = 512.0;
	/** max no of vertical pixels of thumbnail */
	final public static double THUMBNAIL_PIXELS_Y = 512.0;
	/** application */
	@Weak
	protected App app;
	/** kernel */
	@Weak
	protected Kernel kernel;
	/** construction */
	@Weak
	protected Construction cons;
	/** handler for GGB files */
	protected MyXMLHandler handler;

	/**
	 * @param kernel
	 *            Kernel
	 * @param cons
	 *            Construction
	 */
	public MyXMLio(Kernel kernel, Construction cons) {
		this.kernel = kernel;
		this.cons = cons;
		app = kernel.getApplication();

		createXMLParser();
		handler = getGGBHandler();
	}

	/**
	 * create XML parser
	 */
	abstract protected void createXMLParser();

	/**
	 * @return handler for GGB files
	 */
	protected MyXMLHandler getGGBHandler() {
		if (handler == null) {
			// ggb3D : to create also a MyXMLHandler3D
			// ggbDocHandler = new MyXMLHandler(kernel, cons);
			handler = kernel.newMyXMLHandler(cons);
		}
		return handler;
	}

	/**
	 * Returns XML representation of all settings and construction needed for
	 * undo.
	 * 
	 * @param c
	 *            construction
	 * @param getListenersToo
	 *            whether listeners (js) should be included
	 * @return construction XML for undo step
	 */
	public final static synchronized StringBuilder getUndoXML(Construction c,
			boolean getListenersToo) {

		App consApp = c.getApplication();

		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, consApp);

		// save euclidianView settings
		consApp.getCompanion().getEuclidianViewXML(sb, false);

		// save kernel settings
		c.getKernel().getKernelXML(sb, false);
		consApp.getSettings().getTable().getXML(sb);
		// save construction
		c.getConstructionXML(sb, getListenersToo);

		// save ProbabilityCalculator settings
		if (consApp.isUsingFullGui() && consApp.getGuiManager() != null) {
			consApp.getGuiManager().getViewsXML(sb, false);
		}

		sb.append("</geogebra>");

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
		try {
			handler.setNeedsConstructionDefaults(!clearConstruction && !isGgtFile);
			processXMLString(xml, clearConstruction, isGgtFile, true);
		} finally {
			handler.setNeedsConstructionDefaults(false);
		}
	}

	/**
	 * @param xml
	 *            XML string
	 * @param clearConstruction
	 *            true to clear construction before processing
	 * @param isGgtFile
	 *            true for macro files
	 * @param randomize
	 *            whether to randomize numbers
	 * @throws Exception
	 *             if XML is invalid or there was a problem while processing
	 */
	public void processXMLString(String xml, boolean clearConstruction,
			boolean isGgtFile, boolean randomize) throws Exception {
		if (cons != null) {
			cons.setFileLoading(true);
		}
		if (!isGgtFile) {
			app.resetUniqueId();
		}
		processXMLString(xml, clearConstruction, isGgtFile, true, randomize);
		if (cons != null) {
			cons.setFileLoading(false);
		}
	}

	private static void addGeoGebraHeader(StringBuilder sb, App app) {
		addGeoGebraHeader(sb, false, app.getUniqueId(), app);
	}

	/**
	 * Appends the &lt;geogebra> tag and the &lt;subapp> tag (if the app is a subapp)
	 * to the given builder, including XSD link and construction ID
	 *
	 * @param sb       builder
	 * @param isMacro  true for ggt files
	 * @param uniqueId construction ID
	 * @param app      app
	 */
	public static void addGeoGebraHeader(StringBuilder sb, boolean isMacro, String uniqueId,
										 App app) {
		AppConfig config = app.getConfig();
		addGeoGebraHeader(
				sb,
				isMacro,
				uniqueId,
				app.getPlatform(),
				config.getAppCode(),
				config.getSubAppCode());
	}

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
	 * @param platform
	 *            app platform
	 */
	public static void addGeoGebraHeader(
			StringBuilder sb,
			boolean isMacro,
			String uniqueId,
			Platform platform,
			String appCode,
			@CheckForNull String subAppCode) {

		// make sure File -> Share works in HTML5 App
		// (GeoGebraTube doesn't display 5.0 applets)
		String format = GeoGebraConstants.XML_FILE_FORMAT;

		sb.append("<geogebra format=\"");
		sb.append(format);
		sb.append("\" ");
		sb.append("version=\"");
		sb.append(GeoGebraConstants.VERSION_STRING);
		sb.append("\" ");
		sb.append("app=\"");
		sb.append(appCode);
		sb.append("\" ");
		if (subAppCode != null) {
			sb.append("subApp=\"");
			sb.append(subAppCode);
			sb.append("\" ");
		}
		sb.append("platform=\"");
		sb.append(platform.getName());
		sb.append("\" ");
		if (uniqueId != null) {
			sb.append("id=\"");
			sb.append(uniqueId); // unique id to identify ggb file
			sb.append("\" ");
		}
		sb.append(" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/apps/xsd/");
		if (isMacro) {
			sb.append(GeoGebraConstants.GGT_XSD_FILENAME); // eg ggt.xsd
		}
		else {
			sb.append(GeoGebraConstants.GGB_XSD_FILENAME); // eg ggb.xsd
		}
		sb.append(
				"\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");
	}

	/**
	 * Appends &lt;?xml ... ?> header to given builder
	 * 
	 * @param sb
	 *            builder
	 */
	public final static void addXMLHeader(StringBuilder sb) {
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
		addGeoGebraHeader(sb, app);
		// sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		// sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\"");
		// sb.append("
		// xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/");
		// sb.append(GeoGebra.GGB_XSD_FILENAME); //eg ggb.xsd
		// sb.append("\" xmlns=\"\"
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");

		// save gui settings
		sb.append(app.getCompleteUserInterfaceXML(false));

		// save construction
		cons.getConstructionXML(sb, false);

		sb.append("</geogebra>");
		return sb.toString();
	}

	/**
	 * Returns XML representation of given macros and/or exercise in the kernel,
	 * including header.
	 * 
	 * @param macros
	 *            list of macros
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
	 * 
	 * @return XML representation of all settings WITHOUT construction.
	 */
	public String getPreferencesXML() {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false, null, app);
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

	/**
	 * @param str
	 *            XML string
	 * @param clearAll
	 *            true to clear construction before processing
	 * @param isGGTOrDefaults
	 *            true for macro files and defaults
	 * @param settingsBatch
	 *            true to process ettings changes as a batch
	 * @param randomize
	 *            whether to randomize numbers afterwards
	 * @throws Exception
	 *             if XML is invalid or there was a problem while processing
	 */
	final public void processXMLString(String str, boolean clearAll,
			boolean isGGTOrDefaults, boolean settingsBatch, boolean randomize)
			throws Exception {
		doParseXML(createXMLStreamString(str), clearAll, isGGTOrDefaults,
				clearAll, settingsBatch, randomize);
	}

	/**
	 * @param stream
	 *            xml stream
	 * @param clearConstruction
	 *            true to clear construction before processing
	 * @param isGGTOrDefaults
	 *            true for macro files and defaults
	 * @param mayZoom
	 *            true if reading the string may change the zoom
	 * @param settingsBatch
	 *            true if we should use batch mode for settings
	 * @param randomize
	 *            whether to randomize random numbers
	 * @throws Exception
	 *             if a problem occurs
	 */
	final protected void doParseXML(XMLStream stream, boolean clearConstruction,
			boolean isGGTOrDefaults, boolean mayZoom, boolean settingsBatch,
			boolean randomize) throws Exception {
		boolean oldVal = kernel.isNotifyViewsActive();
		boolean oldVal2 = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);

		if (!isGGTOrDefaults && mayZoom) {
			kernel.setNotifyViewsActive(false);
		}

		if (clearConstruction) {
			// clear construction
			kernel.clearConstruction(false);
		}
		try {
			kernel.setLoadingMode(true);
			if (settingsBatch && !isGGTOrDefaults) {
				try {
					app.getSettings().beginBatch();
					parseXML(handler, stream);
				} finally {
					app.getSettings().endBatch();
				}
			} else {
				parseXML(handler, stream);
			}
			resetXMLParser();
			kernel.setLoadingMode(false);

			if (app.isWhiteboardActive()) {
				for (GeoElement geo : cons.getGeoSetConstructionOrder()) {
					if (geo instanceof GeoPolygon) {
						((GeoPolygon) geo).hideSegments();
					}
				}
			}
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Error | Exception e) {
			Log.error(e.getMessage());
			if (!isGGTOrDefaults) {
				throw e;
			}
		} finally {
			kernel.setUseInternalCommandNames(oldVal2);
			if (!isGGTOrDefaults && mayZoom) {
				kernel.updateConstruction(randomize, 1);
				kernel.setNotifyViewsActive(oldVal);
			}

			// #2153
			if (!isGGTOrDefaults && cons != null
					&& cons.hasSpreadsheetTracingGeos()) {
				// needs to be done after call to updateConstruction() to avoid
				// spurious traces
				app.getTraceManager().loadTraceGeoCollection();
			}
		}

		// handle construction step stored in XMLhandler
		// do this only if the construction protocol navigation is showing
		if (!isGGTOrDefaults && oldVal && app.showConsProtNavigation()) {
			// ((GuiManagerD)app.getGuiManager()).setConstructionStep(handler.getConsStep());

			if (app.getGuiManager() != null
					&& app.getGuiManager().isUsingConstructionProtocol()) {
				// if there is a ConstructionProtocolView, then update its
				// navigation bars
				app.getGuiManager().getConstructionProtocolView()
						.setConstructionStep(handler.getConsStep());
			} else {
				// otherwise this is not needed
				app.getKernel().getConstruction()
						.setStep(handler.getConsStep());
			}

		}

	}

	/**
	 * reset XML parser
	 */
	abstract protected void resetXMLParser();

	/**
	 * parse XML string
	 * 
	 * @param xmlHandler
	 *            handler
	 * 
	 * @param stream
	 *            XML stream
	 * @throws Exception
	 *             exception
	 */
	abstract protected void parseXML(MyXMLHandler xmlHandler, XMLStream stream)
			throws Exception;

	/**
	 * @param perspectiveXML
	 *            string with &lt;perspective> tag
	 */
	public void parsePerspectiveXML(String perspectiveXML) {
		try {
			MyXMLHandler h = getGGBHandler();
			parseXML(h, createXMLStreamString(perspectiveXML));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * class for XML content streams (zip, buffers, String, etc.)
	 * 
	 * @author mathieu
	 *
	 */
	public interface XMLStream {
		// tagging interface
	}

	/**
	 * 
	 * @param str
	 *            XML string
	 * @return XML stream for string
	 */
	abstract protected XMLStream createXMLStreamString(String str);

	/**
	 * Reads zipped file from String that includes the construction saved in xml
	 * format and maybe image files.
	 * 
	 * @param zipFile
	 *            zip bytes
	 * @throws Exception
	 *             when problem occurs
	 */
	abstract public void readZipFromString(ZipFile zipFile) throws Exception;

	/**
	 * @return whether errors were produced by parsing last file
	 */
	public boolean hasErrors() {
		return getGGBHandler().hasErrors();
	}
}
