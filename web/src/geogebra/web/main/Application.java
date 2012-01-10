package geogebra.web.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;

import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.view.spreadsheet.AbstractSpreadsheetTableModel;
import geogebra.common.gui.view.spreadsheet.SpreadsheetTraceManager;

import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.AbstractUndoManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.View;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.GlobalKeyDispatcher;
import geogebra.common.main.MyError;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.ResourceBundleAdapter;
import geogebra.web.euclidian.EuclidianController;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.io.ConstructionException;
import geogebra.web.io.MyXMLio;
import geogebra.web.util.DataUtil;


public class Application extends AbstractApplication {
	
	
	
	MyXMLio myXMLio;
	
	private boolean[] showAxes = {true,true};
	private boolean showGrid = false;
	
	private Map<String, ImageElement> images = new HashMap<String, ImageElement>();

	private Canvas canvas;
	
	public Application(){
		this.init(Canvas.createIfSupported());
		
		// init settings
		settings = new Settings();
	}

	@Override
	public String getCommand(String cmdName) {
		if(cmdName.equals("CurveCartesian"))
			return "Curve";
		return cmdName;
	}

	@Override
	public String getPlain(String cmdName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2,
	        String param3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2,
	        String param3, String param4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2,
	        String param3, String param4, String param5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMenu(String cmdName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getError(String cmdName) {
		// TODO Auto-generated method stub
		return cmdName;
	}

	@Override
	public boolean isRightToLeftReadingOrder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTooltipFlag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearTooltipFlag() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isApplet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void storeUndoInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUsingFullGui() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean showView(int view) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean letRedefine() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void traceToSpreadsheet(GeoElement o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetTraceColumn(GeoElement o) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReverseNameDescriptionLanguage() {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	public String getInternalCommand(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showError(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initJavaScriptViewWithoutJavascript() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getTraceXML(GeoElement geoElement) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public void changeLayer(GeoElement ge, int layer, int layer2) {
		// TODO Auto-generated method stub

	}

	@Override
    public boolean freeMemoryIsCritical() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long freeMemory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getOrdinalNumber(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAlgebraView() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public EuclidianViewInterfaceSlim getActiveEuclidianView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRightToLeftDigits() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShowingEuclidianView2() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void evalScript(AbstractApplication app, String script, String arg) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param canvas Canvas
	 * Initializes the application, seeds factory prototypes, creates Kernel and MyXXMLIO
	 */
	public void init(Canvas canvas) {
		geogebra.common.factories.AwtFactory.prototype = new geogebra.web.factories.AwtFactory();
		geogebra.common.factories.FormatFactory.prototype = new geogebra.web.factories.FormatFactory();
		geogebra.common.util.StringUtil.prototype = new geogebra.common.util.StringUtil();
		// TODO: probably there is better way
		geogebra.common.awt.Color.black = geogebra.web.awt.Color.black;
		geogebra.common.awt.Color.white = geogebra.web.awt.Color.white;
		geogebra.common.awt.Color.blue = geogebra.web.awt.Color.blue;
		geogebra.common.awt.Color.gray = geogebra.web.awt.Color.gray;
		geogebra.common.awt.Color.lightGray = geogebra.web.awt.Color.lightGray;
		geogebra.common.awt.Color.darkGray = geogebra.web.awt.Color.darkGray;
		
		geogebra.common.euclidian.HatchingHandler.prototype = new geogebra.web.euclidian.HatchingHandler();
		geogebra.common.euclidian.EuclidianStatic.prototype = new geogebra.web.euclidian.EuclidianStatic();
		this.canvas = canvas;
		kernel = new Kernel(this);
		initEuclidianViews();
		
		myXMLio = new MyXMLio(kernel, kernel.getConstruction());
	    // TODO Auto-generated method stub
	    
    }
	
	public void loadGgbFile(Map<String, String> archiveContent) throws Exception {
		((EuclidianView) euclidianView).setDisableRepaint(true);
		loadFile(archiveContent);
		((EuclidianView) euclidianView).setDisableRepaint(false);
		euclidianView.repaintView();
	}

	public static void log(String message) {
	   GWT.log(message);
    }
	
	private void loadFile(Map<String, String> archive) throws Exception {
		// Reset file
		images.clear();
		
		// Handling of construction and macro file
		String construction = archive.remove("geogebra.xml");
		String macros = archive.remove("geogebra_macro.xml");
		
		// Construction (required)
		if (construction == null) {
			throw new ConstructionException("File is corrupt: No GeoGebra data found");
		}
		
		// Macros (optional)
		if (macros != null) {
			macros = DataUtil.utf8Decode(macros);
			//tmpaddMacroXML(macros);
		}	
		
		/* This code is buggy, maybe because of GWT versions?
		if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
			//tmpmaybeProcessImage(entry.getKey(), entry.getValue());
				GWT.log(entry.getKey()+" "+entry.getValue());
			}
		}
		*/
		ArrayList<String> keys = new ArrayList<String>(archive.keySet());
		for (String key : keys) {
			//GWT.log(key+" :  "+archive.remove(key));
			maybeProcessImage(key,archive.remove(key));
		}
		
		// Process Construction
		construction = DataUtil.utf8Decode(construction);
		myXMLio.processXmlString(construction, true, false);
	}
	
	private static final ArrayList<String> IMAGE_EXTENSIONS = new ArrayList<String>();
	static {
		IMAGE_EXTENSIONS.add("bmp");
		IMAGE_EXTENSIONS.add("gif");
		IMAGE_EXTENSIONS.add("jpg");
		IMAGE_EXTENSIONS.add("png");
	}
	private void maybeProcessImage(String filename, String binaryContent) {
		String fn = filename.toLowerCase();
		if (fn.equals("geogebra_thumbnail.png")) {
			return;			// Ignore thumbnail
		}
		
		int index = fn.lastIndexOf('.');
		if (index == -1) {
			return;			// Ignore files without extension
		}
		
		String ext = fn.substring(index + 1);
		if (! IMAGE_EXTENSIONS.contains(ext)) {
			return;			// Ignore non image files
		}
		
		String base64 = DataUtil.base64Encode(binaryContent);
		images.put(filename, createImage(ext, base64));
	}
	
	private ImageElement createImage(String ext, String base64) {
		String dataUrl = "data:image/" + ext + ";base64," + base64;
		ImageElement image = Document.get().createImageElement();
		image.setSrc(dataUrl);
		
		return image;
	}


	@Override
	public EuclidianView createEuclidianView() {
		return (EuclidianView) this.euclidianView;
	} 

	@Override
    public void updateConstructionProtocol() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public int getCurrentLabelingStyle() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public AbstractImageManager getImageManager() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String reverseGetColor(String colorName) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String getColor(String string) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public BufferedImageAdapter getExternalImageAdapter(String filename) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String getCommandSyntax(String cmd) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void showRelation(GeoElement geoElement, GeoElement geoElement2) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showError(MyError e) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showError(String string, String str) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public View getView(int id) {
	    // TODO Auto-generated method stub
	    return null;
    }


	
	@Override
    public DrawEquationInterface getDrawEquation() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void setShowConstructionProtocolNavigation(boolean show,
            boolean playButton, double playDelay, boolean showProtButton) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public double getWidth() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public double getHeight() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public Font getFontCanDisplay(String labelDesc, boolean serif, int style,
            int size) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public SpreadsheetTraceManager getTraceManager() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GuiManager getGuiManager() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractUndoManager getUndoManager(Construction cons) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractAnimationManager newAnimationManager(Kernel kernel2) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
	    return new geogebra.web.kernel.geos.GeoElementGraphicsAdapter();
    }

	
	@Override
    public GeoGebraCasInterface newGeoGebraCAS() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AlgoElement newAlgoShortestDistance(Construction cons, String label,
            GeoList list, GeoPointND start, GeoPointND end, GeoBoolean weighted) {
	    // TODO Auto-generated method stub
		return null;
    }

	@Override
    public void updateStyleBars() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setXML(String string, boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public GgbAPI getGgbApi() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public SoundManager getSoundManager() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public CommandProcessor newCmdBarCode() {
	    // TODO Auto-generated method stub
	    return null;
    }
	private boolean commandChanged = true;
	@Override
    protected boolean isCommandChanged() {
		return commandChanged;
    }

	@Override
    protected void setCommandChanged(boolean b) {
		commandChanged = b;
    }

	@Override
    protected boolean isCommandNull() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public void getCommandResourceBundle() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void initScriptingBundle() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public String getScriptingCommand(String internal) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    protected AbstractEuclidianView newEuclidianView(boolean[] showAxes,
            boolean showGrid) {
	    return euclidianView = new EuclidianView(canvas, euclidianController, showAxes, showGrid);
    }

	@Override
    protected AbstractEuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianController(kernel);
		
		
    }

	@Override
    public boolean showAlgebraInput() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public GlobalKeyDispatcher getGlobalKeyDispatcher() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractSpreadsheetTableModel getSpreadsheetTableModel() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public boolean isIniting() {
	    // TODO Auto-generated method stub
	    return false;
    }


	@Override
    public void evalPythonScript(AbstractApplication app, String string,
            String arg) {
	    debug("Python scripting not supported");
	    
    }

	@Override
    public void callAppletJavaScript(String fun, Object[] args) {
	    if (args == null || args.length == 0) {
	    	callNativeJavaScript(fun);
	    } else if (args.length == 1) {
	    	callNativeJavaScript(fun, args[0].toString());
	    } else {
	    	debug("callAppletJavaScript() not supported for more than 1 argument");
	    }
	    
    }

	@Override
    public boolean isRightClickEnabled() {
	    // TODO Auto-generated method stub
	    return false;
    }
	
	public static native void callNativeJavaScript(String fun, String arg) /*-{
	  eval(fun + "(" + arg + ");");
	}-*/;
	
	public static native void callNativeJavaScript(String fun) /*-{
	  eval(fun + "();");
	}-*/;

	@Override
    public void updateMenubar() {
	    // TODO Auto-generated method stub
	    
    }


}