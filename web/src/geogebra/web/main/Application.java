package geogebra.web.main;

import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.spreadsheet.AbstractSpreadsheetTableModel;
import geogebra.common.gui.view.spreadsheet.SpreadsheetTraceManager;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.AbstractUndoManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
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
import geogebra.common.plugin.ScriptManagerCommon;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.web.css.GuiResources;
import geogebra.web.euclidian.EuclidianController;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.io.ConstructionException;
import geogebra.web.io.MyXMLio;
import geogebra.web.kernel.AnimationManager;
import geogebra.web.kernel.UndoManager;
import geogebra.web.util.DataUtil;
import geogebra.web.util.DebugPrinterWeb;
import geogebra.web.util.ImageManager;

import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Widget;


public class Application extends AbstractApplication {
	
	
	//Internationalization constants
	public final static String DEFAULT_LANGUAGE = "en";
	public final static String DEFAULT_LOCALE = "default";

	private FontManager fontManager;

	private boolean[] showAxes = {true,true};
	private boolean showGrid = false;
	
	protected ImageManager imageManager;

	private Canvas canvas;
	private geogebra.common.plugin.GgbAPI ggbapi;
	
	public Application(){
		dbg = new DebugPrinterWeb();
		this.init(Canvas.createIfSupported());
		fontManager = new FontManager();
		setFontSize(12);
		setLabelDragsEnabled(false);
		AbstractEuclidianView.setCapturingThreshold(20);


		getScriptManager();//.ggbOnInit();//this is not called here because we have to delay it until the canvas is first drawn
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

	
	/**
	 * Following Java's convention, the return string should only include the language part of the local.
	 * The assumption here that the "default" locale is English (for now) 
	 */
	@Override
	public String getLanguage() {
		
		String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
		if(localeName.toLowerCase().equals(Application.DEFAULT_LOCALE)) {
			return Application.DEFAULT_LANGUAGE;
		}
		return  localeName.substring(0,2);
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
	public String getTraceXML(GeoElement geoElement) {
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
	public EuclidianView getEuclidianView1() {
		return (EuclidianView)euclidianView;
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView() {
//		if (getGuiManager() == null) {
			return getEuclidianView1();
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

	    registerCanvasHelpers();

		kernel = new Kernel(this);

		// init settings
		settings = new Settings();
		
		initEuclidianViews();
		
		initImageManager();
		
		myXMLio = new MyXMLio(kernel, kernel.getConstruction());
	}

	private void showSplashImageOnCanvas() {
	    if (this.canvas != null) {
	    	String geogebra = "GeoGebra";
	    	
	    	canvas.setWidth("427px");
	    	canvas.setHeight("120px");
	    	canvas.setCoordinateSpaceWidth(427);
	    	canvas.setCoordinateSpaceHeight(120);
	    	Context2d ctx = canvas.getContext2d();
	    	ctx.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
	    	ctx.setTextBaseline(TextBaseline.TOP);
	    	ctx.setTextAlign(TextAlign.START);	
	    	ctx.setFont("50px Century Gothic, Helvetica, sans-serif");
	    	ctx.setFillStyle("#666666");
	    	ctx.fillText(geogebra, 33, 37);
	    	//TextMetrics txm = ctx.measureText(geogebra);
	    	//ctx.setFillStyle("#7e7eff");
	    	//ctx.setTextAlign(TextAlign.LEFT);
	    	//ctx.setFont("20px Century Gothic, Helvetica, sans-serif");
	    	//ctx.fillText("4",txm.getWidth(),37);
	    }
    }

	public Canvas getCanvas() {
		return canvas;
	}

	public native void registerCanvasHelpers() /*-{

		$wnd.canvasHelpers = {};
		$wnd.canvasHelpers.imageData = {};
		$wnd.canvasHelpers.canvas = this.@geogebra.web.main.Application::getCanvas()().@com.google.gwt.canvas.client.Canvas::getElement()();
		$wnd.canvasHelpers.context = $wnd.canvasHelpers.canvas.getContext("2d");

	}-*/;

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
			myXMLio.processXMLString(macros,true,true);
		}	
		
		if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
				maybeProcessImage(entry.getKey(), entry.getValue());
			}
		}
		if (!imageManager.hasImages()) {
			// Process Construction
			construction = DataUtil.utf8Decode(construction);
			myXMLio.processXMLString(construction, true, false);		
		} else {
		//on images do nothing here: wait for callback when images loaded.
			imageManager.triggerImageLoading(construction,(MyXMLio) myXMLio);
		}
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
		addExternalImage(filename,createImageSrc(ext,base64));
	}
	
	private String createImageSrc(String ext, String base64) {
		String dataUrl = "data:image/" + ext + ";base64," + base64;
		return dataUrl;
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
	    return imageManager;
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
	
	DrawEquationWeb drawEquation;

	@Override
    public DrawEquationInterface getDrawEquation() {
	    if (drawEquation == null) {
	    	drawEquation = new DrawEquationWeb(this);
	    }
	    
	    return drawEquation;
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

	public Font getFontCanDisplay(String testString, boolean serif, int style,
            int size) {
	    return fontManager.getFontCanDisplay(testString, serif, style, size);
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
	    return new UndoManager(cons);
    }

	@Override
    public AbstractAnimationManager newAnimationManager(Kernel kernel2) {
	    return new AnimationManager(kernel2);
    }

	@Override
    public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
	    return new geogebra.web.kernel.geos.GeoElementGraphicsAdapter(this);
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
	public void setXML(String xml, boolean clearAll) {
		//AR if (clearAll) {
			//AR setCurrentFile(null);
		//AR }

		try {
			// make sure objects are displayed in the correct View
			setActiveView(AbstractApplication.VIEW_EUCLIDIAN);
			myXMLio.processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
		} catch (Exception e) {
			e.printStackTrace();
			showError("LoadFileFailed");
		}
	}

	@Override
	public geogebra.common.plugin.GgbAPI getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new geogebra.web.main.GgbAPI(this);
		}

		return ggbapi;
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
	
	public ScriptManagerCommon getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManager(this);
		}
		return scriptManager;
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
	    return true;
    }
	
	public native void callNativeJavaScript(String funcname) /*-{
	if ($wnd[funcname]) {
		$wnd[funcname]();
	}
}-*/;

	public native void callNativeJavaScript(String funcname, String arg) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg);
		}
	}-*/;

	public static native void ggbOnInit() /*-{
	if (typeof $wnd.ggbOnInit === 'function')
		$wnd.ggbOnInit();
	}-*/;

	public static native void ggbOnInit(String arg) /*-{
	if (typeof $wnd.ggbOnInit === 'function')
		$wnd.ggbOnInit(arg);
	}-*/;


	@Override
    public void updateMenubar() {
		//getGuiManager().updateMenubar(); // implementation is not needed for now
		//AbstractApplication.debug("implementation needed"); // TODO Auto-generated
    }

	@Override
    public Font getPlainFontCommon() {
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
	    clearConstruction();
    }

	public void clearConstruction() {
	//if (isSaved() || saveCurrentFile()) {
		kernel.clearConstruction();

		kernel.initUndoInfo();
	//	setCurrentFile(null);
		setMoveMode();
	//}
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
		showSplashImageOnCanvas();
    }

	public static native void alert(String string) /*-{
	   $wnd.alert(string);
    }-*/;


	@Override
    public boolean isHTML5Applet() {
	    return true;
    }
	
	protected void initImageManager() {
		imageManager = new ImageManager();
	}
	
	public void addExternalImage(String filename, String src) {
		imageManager.addExternalImage(filename,src);
	}

	@Override
    public BufferedImage getExternalImageAdapter(String fileName) {
		return new geogebra.web.awt.BufferedImage(ImageManager.getExternalImage(fileName));
    }


	// random id to identify ggb files
	// eg so that GeoGebraTube can notice it's a version of the same file
	private String uniqueId = null;//FIXME: generate new UUID: + UUID.randomUUID();

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void resetUniqueId() {
		uniqueId = null;//FIXME: generate new UUID: + UUID.randomUUID();
	}
	
	public ImageElement getRefreshViewImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResources.INSTANCE.viewRefresh());
	}

	public ImageElement getPlayImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResources.INSTANCE.navPlay());
	}

	public ImageElement getPauseImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResources.INSTANCE.navPause());
	}
	@Override
    public boolean hasEuclidianView2EitherShowingOrNot() {
	    // TODO Auto-generated method stub
	    return false;
    }
	@Override
    public StringType getFormulaRenderingType() {
	    return StringType.MATHML;
    }

	public static native void console(JavaScriptObject dataAsJSO) /*-{
		$wnd.console.log(dataAsJSO);
    }-*/;
	


}