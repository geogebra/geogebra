package geogebra.common.main;

import java.util.ArrayList;

import geogebra.common.euclidian.EuclidianViewInterface2D;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.util.DebugPrinter;
import geogebra.common.util.ResourceBundleAdapter;

public abstract class AbstractApplication {
public static final String LOADING_GIF = "http://www.geogebra.org/webstart/loading.gif";
	
	public static final String WIKI_OPERATORS = "Predefined Functions and Operators";
	public static final String WIKI_MANUAL = "Manual:Main Page";
	public static final String WIKI_TUTORIAL = "Tutorial:Main Page";
	public static final String WIKI_EXPORT_WORKSHEET = "Export_Worksheet_Dialog";
	public static final String WIKI_ADVANCED = "Advanced Features";
	public static final String WIKI_TEXT_TOOL = "Insert Text Tool";
	
	public static final int VIEW_NONE = 0;
	public static final int VIEW_EUCLIDIAN = 1;
	public static final int VIEW_ALGEBRA = 2;
	public static final int VIEW_SPREADSHEET = 4;
	public static final int VIEW_CAS = 8;
	public static final int VIEW_EUCLIDIAN2 = 16;
	public static final int VIEW_CONSTRUCTION_PROTOCOL = 32;
	public static final int VIEW_PROBABILITY_CALCULATOR = 64;
	public static final int VIEW_FUNCTION_INSPECTOR = 128;
	public static final int VIEW_INSPECTOR = 256;
    public static final int VIEW_EUCLIDIAN3D = 512;
    public static final int VIEW_EUCLIDIAN_FOR_PLANE = 1024;
    public static final int VIEW_PLOT_PANEL = 2048;
    public static final int VIEW_TEXT_PREVIEW = 4096;
	public static final int VIEW_PROPERTIES = 4097;
	public static final int VIEW_ASSIGNMENT = 8192;

	// For eg Hebrew and Arabic. 	
	public static char unicodeDecimalPoint = '.';
	public static char unicodeComma = ','; // \u060c for Arabic comma
	public static char unicodeZero = '0';

	public enum CasType { NO_CAS, MATHPIPER, MAXIMA, MPREDUCE };
		
	// moved to Application from EuclidianView as the same value is used across multiple EVs 
	public int maxLayerUsed = 0;
	


	public abstract ResourceBundleAdapter initAlgo2IntergeoBundle();
	public abstract ResourceBundleAdapter initAlgo2CommandBundle();
	public abstract String getCommand(String cmdName);
	public abstract String getPlain(String cmdName);
	public abstract String getPlain(String cmdName,String param);
	public abstract String getPlain(String cmdName,String param,String param2);
	public abstract String getPlain(String cmdName,String param,String param2,String param3);
	public abstract String getPlain(String cmdName,String param,String param2,String param3,String param4);
	public abstract String getPlain(String cmdName,String param,String param2,String param3,String param4,String param5);
	public abstract String getMenu(String cmdName);
	public abstract String getError(String cmdName);
	public abstract boolean isRightToLeftReadingOrder();
	public abstract void setTooltipFlag();
	public abstract void clearTooltipFlag();
	public abstract boolean isApplet();
	public abstract void storeUndoInfo();
	public abstract boolean isUsingFullGui();
	public abstract boolean showView(int view);
	public abstract void callJavaScript(String jsFunction, Object [] args);
	public abstract boolean isUsingLocalizedLabels();
	public abstract String getLanguage();
	public abstract boolean languageIs(String s);
	public abstract boolean letRedefine();
	public abstract String translationFix(String s);
	public abstract void traceToSpreadsheet(Object o);
	public abstract void resetTraceColumn(Object o);
	public abstract boolean isReverseNameDescriptionLanguage();
	public abstract boolean isBlockUpdateScripts();
	public abstract void setBlockUpdateScripts(boolean flag);
	public abstract String getScriptingLanguage();
	public abstract void setScriptingLanguage(String lang);
	public abstract String getInternalCommand(String s);
	public abstract void showError(String s);
	public abstract boolean isScriptingDisabled();
	public abstract boolean useBrowserForJavaScript();
	public abstract void initJavaScriptViewWithoutJavascript();
	public abstract Object getTraceXML(Object geoElement);
	public abstract void removeSelectedGeo(Object geoElement, boolean b);
	public abstract void changeLayer(Object ge, int layer, int layer2);
	public abstract Object getExternalImage(String fileName);
	public abstract boolean freeMemoryIsCritical();
	public abstract long freeMemory();
	public abstract int getLabelingStyle();
	public abstract String getOrdinalNumber(int i);
	public abstract double getXmin();
	public abstract double getXmax();
	public abstract double getXminForFunctions();
	public abstract double getXmaxForFunctions();
	public abstract double countPixels(double min, double max);
	public abstract int getMaxLayerUsed();
	public abstract Object getAlgebraView();	
	public abstract EuclidianViewInterfaceSlim getEuclidianView();
	public abstract EuclidianViewInterfaceSlim getActiveEuclidianView();
	public abstract EuclidianViewInterface2D createEuclidianViewForPlane(Object o);
	public abstract boolean isRightToLeftDigits();
	public abstract boolean isShowingEuclidianView2();		
	public abstract AbstractKernel getKernel();
	public abstract Object getImageManager();

	// Michael Borcherds 2008-06-22
	public static void printStacktrace(String message) {
		try {

			throw new Exception(message);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public static void debug(Object s) {
		if(s==null)
			doDebug("<null>", false, false, 0);
		else
			doDebug(s.toString(), false, false, 0);
	}
	
	public static void debug(Object s[]) {
		debug(s, 0);
	}
	
	static StringBuilder debugSb = null;
	
	public static void debug(Object[] s, int level) {
		if (debugSb == null) debugSb = new StringBuilder(); 
		else debugSb.setLength(0);
		
		for (int i = 0 ; i < s.length ; i++) {
			debugSb.append(s[i]);
			debugSb.append('\n');
		}
		
		debug(debugSb, level);
	}
	
	public static void debug(Object s, int level) {
		doDebug(s.toString(), false, false, level);
	}
	
	public static void debug(Object s, boolean showTime, boolean showMemory, int level) {
		doDebug(s.toString(), showTime, showMemory, level);
	}
	public static DebugPrinter dbg;	
	// Michael Borcherds 2008-06-22
	private static void doDebug(String s, boolean showTime, boolean showMemory, int level) {
		if (s == null) s = "<null>";
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();

		// String calleeMethod = elements[0].getMethodName();
		String callerMethodName = elements[2].getMethodName();
		String callerClassName = elements[2].getClassName();
		
		StringBuilder sb = new StringBuilder("*** Message from ");
		sb.append("[");
		sb.append(callerClassName);
		sb.append(".");
		sb.append(callerMethodName);
		sb.append("]");

		if (showTime) {
			dbg.getTimeInfo(sb);	}
		
		if (showMemory) {
			dbg.getMemoryInfo(sb);
			
						
		}
		dbg.print(s,sb.toString(),level);
	}
	public abstract String translateCommand(String name);
	
	@SuppressWarnings("deprecation")
	public static boolean isWhitespace(char charAt) {
		return Character.isSpace(charAt);
	}
	
	public String toLowerCase(String substring) {
		return substring.toLowerCase();		
	}
	public abstract void evalScript(AbstractApplication app, String script, String arg);
	public boolean fileVersionBefore(int[] subValues) {
		// TODO Auto-generated method stub
		return false;
	}
	public abstract ArrayList<GeoElement> getSelectedGeos();
	public abstract int getMode();
	public abstract void addSelectedGeo(GeoElement selGeo, boolean b);
	
	/**
	 * @deprecated added when refactoring
	 */
	public abstract void updateConstructionProtocol();
	

}
