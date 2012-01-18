package geogebra.web.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.view.algebra.AlgebraView;
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
import geogebra.common.main.AbstractFontManager;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.GlobalKeyDispatcher;
import geogebra.common.main.MyError;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.ResourceBundleAdapter;
import geogebra.web.euclidian.EuclidianController;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.io.ConstructionException;
import geogebra.web.io.MyXMLio;
import geogebra.web.kernel.UndoManager;
import geogebra.web.util.DataUtil;


public class Application extends AbstractApplication {
	
	
	private FontManager fontManager;
	MyXMLio myXMLio;
	
	private boolean[] showAxes = {true,true};
	private boolean showGrid = false;
	
	private Map<String, ImageElement> images = new HashMap<String, ImageElement>();

	private Canvas canvas;
	
	public Application(){
		this.init(Canvas.createIfSupported());
		fontManager = new FontManager();
		setFontSize(12);
		setLabelDragsEnabled(false);
		getEuclidianView().setCapturingThreshold(20);
		
	}

	@Override
	public String getCommand(String cmdName) {
		if(cmdName.equals("CurveCartesian"))
			return "Curve";
		return cmdName;
	}

	@Override
	public String getPlain(String cmdName) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2,
	        String param3) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2,
	        String param3, String param4) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2,
	        String param3, String param4, String param5) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public String getMenu(String cmdName) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public String getError(String cmdName) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return cmdName;
	}

	

	@Override
	public void setTooltipFlag() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	@Override
	public boolean isApplet() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	@Override
	public void storeUndoInfo() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	@Override
	public boolean isUsingFullGui() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	@Override
	public boolean showView(int view) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	@Override
	public String getLanguage() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return "en";
	}

	@Override
	public boolean letRedefine() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	@Override
	public void traceToSpreadsheet(GeoElement o) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	@Override
	public void resetTraceColumn(GeoElement o) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	

	@Override
	public String getInternalCommand(String s) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public void showError(String s) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	@Override
	public void initJavaScriptViewWithoutJavascript() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	@Override
	public Object getTraceXML(GeoElement geoElement) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	

	

	@Override
    public boolean freeMemoryIsCritical() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	@Override
	public long freeMemory() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return 0;
	}

	@Override
	public AlgebraView getAlgebraView() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public EuclidianView getEuclidianView() {
		return (EuclidianView)euclidianView;
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView() {
//		if (getGuiManager() == null) {
			return getEuclidianView();
//		}
//		return getGuiManager().getActiveEuclidianView();
	}

	
	@Override
	public boolean isShowingEuclidianView2() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	@Override
	public void evalScript(AbstractApplication app, String script, String arg) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	/**
	 * @param canvas Canvas
	 * Initializes the application, seeds factory prototypes, creates Kernel and MyXXMLIO
	 */
	public void init(Canvas canvas) {
		geogebra.common.factories.AwtFactory.prototype = new geogebra.web.factories.AwtFactory();
		geogebra.common.factories.FormatFactory.prototype = new geogebra.web.factories.FormatFactory();
		geogebra.common.factories.CASFactory.prototype = new geogebra.web.factories.CASFactory();
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

		// init settings
		settings = new Settings();
		
		initEuclidianViews();
		
		myXMLio = new MyXMLio(kernel, kernel.getConstruction());
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
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
		
		if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
				maybeProcessImage(entry.getKey(), entry.getValue());
				//GWT.log(entry.getKey()+" "+entry.getValue());
			}
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
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public AbstractImageManager getImageManager() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public String reverseGetColor(String colorName) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public String getColor(String string) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public BufferedImage getExternalImageAdapter(String filename) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public String getCommandSyntax(String cmd) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public void showRelation(GeoElement geoElement, GeoElement geoElement2) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void showError(MyError e) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void showError(String string, String str) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public DrawEquationInterface getDrawEquation() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public void setShowConstructionProtocolNavigation(boolean show,
            boolean playButton, double playDelay, boolean showProtButton) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public double getWidth() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return 0;
    }

	@Override
    public double getHeight() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return 0;
    }

	public Font getFontCanDisplay(String labelDesc, boolean serif, int style,
            int size) {
	    return new geogebra.web.awt.Font("normal");
    }

	@Override
    public SpreadsheetTraceManager getTraceManager() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public GuiManager getGuiManager() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public AbstractUndoManager getUndoManager(Construction cons) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return new UndoManager(cons);
    }

	@Override
    public AbstractAnimationManager newAnimationManager(Kernel kernel2) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
	    return new geogebra.web.kernel.geos.GeoElementGraphicsAdapter();
    }

	@Override
    public AlgoElement newAlgoShortestDistance(Construction cons, String label,
            GeoList list, GeoPointND start, GeoPointND end, GeoBoolean weighted) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
    }

	@Override
    public void updateStyleBars() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void setXML(String string, boolean b) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public GgbAPI getGgbApi() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public SoundManager getSoundManager() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public CommandProcessor newCmdBarCode() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
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
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return false;
    }

	@Override
    public void getCommandResourceBundle() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void initScriptingBundle() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public String getScriptingCommand(String internal) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    protected AbstractEuclidianView newEuclidianView(boolean[] showAxes,
            boolean showGrid) {
	    return euclidianView = new EuclidianView(
	    		canvas, euclidianController, showAxes, showGrid, getSettings().getEuclidian(1));
    }

	@Override
    protected AbstractEuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianController(kernel);
		
		
    }

	@Override
    public boolean showAlgebraInput() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return false;
    }

	@Override
    public GlobalKeyDispatcher getGlobalKeyDispatcher() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public AbstractSpreadsheetTableModel getSpreadsheetTableModel() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public boolean isIniting() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
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
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
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
		//getGuiManager().updateMenubar(); // implementation is not needed for now
		//AbstractApplication.debug("implementation needed"); // TODO Auto-generated
    }

	@Override
    public Font getPlainFontCommon() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return new geogebra.web.awt.Font("normal");
    }

	@Override
    public AbstractFontManager getFontManager() {
	    return fontManager;
    }

	@Override
    public void updateUI() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public String getTooltipLanguageString() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void reset() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public boolean hasPythonBridge() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public PythonBridge getPythonBridge() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String getPlainTooltip(String string) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElementSelectionListener getCurrentSelectionListener() {
	    // TODO Auto-generated method stub
	    return null;
    }

	

	public Widget buildApplicationPanel() {
	    return canvas;
    }

	public void showLoadingAnimation(boolean go) {
		if (canvas != null) {
			Context2d ctx = canvas.getContext2d();
			ctx.fillText("Loading...", 22, 22);
		}
    }

	public static native void console(String str) /*-{
		if ($wnd && $wnd.console) {
			$wnd.console.log("no"+str);
		}
    }-*/;


}