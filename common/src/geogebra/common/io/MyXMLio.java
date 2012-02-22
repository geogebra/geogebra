package geogebra.common.io;

import java.util.ArrayList;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.AbstractApplication;

public abstract class MyXMLio {

	// All xml output is zipped. The created zip archive contains
	// an entry named XML_FILE for the construction
	final public static String XML_FILE = "geogebra.xml";
	// Added for Intergeo File Format (Yves Kreis) -->
	final public static String I2G_FILE = "construction/intergeo.xml";
	// <-- Added for Intergeo File Format (Yves Kreis)

	// All xml output is zipped. The created zip archive contains
	// an entry named XML_FILE_MACRO for the macros
	final public static String XML_FILE_MACRO = "geogebra_macro.xml";

	// library javascript available to GeoJavaScriptButton objects
	final public static String JAVASCRIPT_FILE = "geogebra_javascript.js";
	final public static String PYTHON_FILE = "geogebra_python.py";

	// All xml output is zipped. The created zip archive *may* contain
	// an entry named XML_FILE_THUMBNAIL for the construction
	final public static String XML_FILE_THUMBNAIL = "geogebra_thumbnail.png";
	// Added for Intergeo File Format (Yves Kreis) -->
	final public static String I2G_FILE_THUMBNAIL = "construction/preview.png";
	// <-- Added for Intergeo File Format (Yves Kreis)
	final public static double THUMBNAIL_PIXELS_X = 200.0; // max no of
															// horizontal pixels
	final public static double THUMBNAIL_PIXELS_Y = 200.0; // max no of
															// vertical pixels
	// Added for Intergeo File Format (Yves Kreis) -->
	final public static String I2G_IMAGES = "resources/images/";
	final public static String I2G_PRIVATE = "private/org.geogebra/";
	final public static String I2G_PRIVATE_IMAGES = "private/org.geogebra/images/";
	// <-- Added for Intergeo File Format (Yves Kreis)




	protected AbstractApplication app;
	protected Kernel kernel;
	protected Construction cons;

	public abstract StringBuilder getUndoXML(Construction construction);

	public void processXMLString(String xml, boolean clearConstruction, boolean isGgtFile) throws Exception {
		processXMLString(xml,clearConstruction,isGgtFile,true);
	}

	public abstract void processXMLString(String xml, boolean clearConstruction, boolean isGgtFile, boolean settingsBatch) throws Exception;


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
	public String getFullMacroXML(ArrayList<Macro> macros) {
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
