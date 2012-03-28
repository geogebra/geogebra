package geogebra.web.main;

import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.spreadsheet.AbstractSpreadsheetTableModel;
import geogebra.common.gui.view.spreadsheet.SpreadsheetTraceManager;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.AbstractUndoManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Relation;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.AbstractFontManager;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.MyError;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.ScriptManagerCommon;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.Unicode;
import geogebra.web.css.GuiResources;
import geogebra.web.euclidian.EuclidianController;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.gui.DialogManagerWeb;
import geogebra.web.gui.GuiManager;
import geogebra.web.gui.SplashDialog;
import geogebra.web.gui.app.EuclidianPanel;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.html5.ArticleElement;
import geogebra.web.io.ConstructionException;
import geogebra.web.io.MyXMLio;
import geogebra.web.kernel.AnimationManager;
import geogebra.web.kernel.UndoManager;
import geogebra.web.properties.ColorsConstants;
import geogebra.web.properties.CommandConstants;
import geogebra.web.properties.PlainConstants;
import geogebra.web.util.DebugPrinterWeb;
import geogebra.web.util.ImageManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Application extends AbstractApplication {

	/**
	 * Constants related to internationalization
	 *  
	 */
	public final static String DEFAULT_LANGUAGE = "en";
	public final static String DEFAULT_LOCALE = "default";
	public final static String A_DOT = ".";
	public final static String AN_UNDERSCORE = "_";

	private FontManager fontManager;

	private boolean[] showAxes = { true, true };
	private boolean showGrid = false;

	protected ImageManager imageManager;

	/*
	 * Internationalization member variables
	 */
	private ColorsConstants colorConstants;
	private PlainConstants plainConstants;
	private CommandConstants commandConstants, commandConstantsOld = null;

	private AbsolutePanel euclidianViewPanel;
	private Canvas canvas;
	private geogebra.common.plugin.GgbAPI ggbapi;
	private HashMap<String, String> currentFile = null;
	private static LinkedList<Map<String, String>> fileList = new LinkedList<Map<String, String>>();

	private ArticleElement articleElement;
	private GeoGebraFrame frame;
	private GeoGebraAppFrame appFrame;
	// convenience method
	public Application(ArticleElement ae, GeoGebraFrame gf) {
		this(ae, gf, true);
	}

	/**
	 * @param useFullGui
	 *          if false only one EuclidianView
	 * @param undoActive
	 *          if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public Application(ArticleElement ae, GeoGebraFrame gf, final boolean undoActive) {
		this.articleElement = ae;
		this.frame = gf;
		createSplash();
		this.useFullGui = ae.getDataParamGui();
		dbg = new DebugPrinterWeb();
		initCommonObjects();
		
		this.canvas = Canvas.createIfSupported();
		euclidianViewPanel = new AbsolutePanel();	
		euclidianViewPanel.add(this.canvas); // canvas must be the 1rst widget in the euclidianViewPanel
		// because we will use euclidianViewPanel.getWidget(0) later
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceHeight(1);
		canvas.setCoordinateSpaceWidth(1);
		final Application this_app = this;
		
		//try to async loading of kernel, maybe we got quicker...
		GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
				initCoreObjects(undoActive, this_app);
				frame.finishAsyncLoading(articleElement, frame, this_app);
			}
			
			public void onFailure(Throwable reason) {
				AbstractApplication.debug(reason);
			}
		});
		
	}

	public Application(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame, boolean undoActive) {
		this.articleElement = article;
		this.appFrame = geoGebraAppFrame;
		createAppSplash();
		this.useFullAppGui  = true;
		appCanvasHeight = appFrame.getCanvasCountedHeight();
		appCanvasWidth = appFrame.getCanvasCountedWidth();
		dbg = new DebugPrinterWeb();
		initCommonObjects();
		
		this.canvas = appFrame.getEuclidianView1Canvas();
		this.euclidianViewPanel = appFrame.getEuclidianView1Panel();
		
		initCoreObjects(undoActive, this);
		getSettings().getEuclidian(1).setPreferredSize(geogebra.common.factories.AwtFactory.prototype
		.newDimension(appCanvasWidth, appCanvasHeight));
		getEuclidianView1().setDisableRepaint(false);
		getEuclidianView1().synCanvasSize();
		getEuclidianView1().repaintView();
		appFrame.finishAsyncLoading(article, geoGebraAppFrame, this);
    }

	public Application(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame) {
	   this(article, geoGebraAppFrame, true);
    }

	/**
	 * Inernationalization: instantiation using GWT.create() properties interfaces
	 * @author Rana
	 */
	private void initColorConstants() {
		colorConstants = GWT.create(ColorsConstants.class);
	}
	
	private void initPlainConstants() {
		plainConstants = GWT.create(PlainConstants.class);
	}
	
	private void initCommandConstants() {
		commandConstants = GWT.create(CommandConstants.class);
	}


	
	public void setUndoActive(boolean flag) {
		// don't allow undo when running with restricted permissions
		/*
		 * if (flag && !hasFullPermissions) {
		 * flag = false;
		 * }
		 */

		if (kernel.isUndoActive() == flag) {
			return;
		}

		kernel.setUndoActive(flag);
		if (flag) {
			kernel.initUndoInfo();
		}

		if (guiManager != null) {
			getGuiManager().updateActions();
		}

		// isSaved = true;
	}

	public ArticleElement getArticleElement() {
		return articleElement;
	}

	public GeoGebraFrame getGeoGebraFrame() {
		return frame;
	}

	/**
	 * Register file drop handlers for the canvas of this application
	 */
	native void registerFileDropHandlers(CanvasElement ce) /*-{

		var appl = this;
		var canvas = ce;

		if (canvas) {
			canvas.addEventListener("dragover", function(e) {
				e.preventDefault();
				e.stopPropagation();
				canvas.style.borderColor = "#ff0000";
			}, false);
			canvas.addEventListener("dragenter", function(e) {
				e.preventDefault();
				e.stopPropagation();
			}, false);
			canvas.addEventListener("drop", function(e) {
				e.preventDefault();
				e.stopPropagation();
				canvas.style.borderColor = "#000000";
				var dt = e.dataTransfer;
				if (dt.files.length) {
					var fileToHandle = dt.files[0];
					var imageRegEx = /\.(png|jpg|jpeg|gif)$/;
					var ggbRegEx = /\.(ggb|ggt)$/;
					if (fileToHandle.name.toLowerCase().match(imageRegEx)) {
						var reader = new FileReader();
						reader.onloadend = function(ev) {
							if (reader.readyState === reader.DONE) {
								var fileStr = reader.result;
								var fileName = fileToHandle.name;
								appl.@geogebra.web.main.Application::imageDropHappened(Ljava/lang/String;Ljava/lang/String;II)(fileName, fileStr, e.clientX, e.clientY);
							}
						};
						reader.readAsDataURL(fileToHandle);
					} else if (fileToHandle.name.toLowerCase().match(ggbRegEx)) {
						var reader = new FileReader();
						reader.onloadend = function(ev) {
							if (reader.readyState === reader.DONE) {
								var fileStr = reader.result;
								appl.@geogebra.web.main.Application::loadGgbFileAgain(Ljava/lang/String;)(fileStr);
							}
						};
						reader.readAsDataURL(fileToHandle);
					}
					//console.log(fileToHandle.name);
				}
			}, false);
		}
		$doc.body.addEventListener("dragover", function(e) {
			e.preventDefault();
			e.stopPropagation();
			if (canvas)
				canvas.style.borderColor = "#000000";
		}, false);
		$doc.body.addEventListener("drop", function(e) {
			e.preventDefault();
			e.stopPropagation();
		}, false);
	}-*/;

	/**
	 * Loads an image and puts it on the canvas (this happens by drag & drop)
	 * 
	 * @param imgFileName - the file name of the image
	 * @param fileStr - the image data url
	 * @param clientx - desired position on the canvas (x)
	 * @param clienty - desired position on the canvas (y)
	 */
	public void imageDropHappened(String imgFileName, String fileStr, int clientx, int clienty) {
		/*
		((ImageManager)getImageManager()).addExternalImage(imgFileName, fileStr);
		((ImageManager)getImageManager()).triggerSingleImageLoading(imgFileName);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		geoImage.setImageFileName(imgFileName);
		double cx = getActiveEuclidianView().toRealWorldCoordX(clientx);
		double cy = getActiveEuclidianView().toRealWorldCoordY(clienty);
		GeoPoint2 gsp = new GeoPoint2(getKernel().getConstruction(), cx, cy, 1);
		geoImage.setCorner(gsp, 0);
		geoImage.setLabel(null);
		GeoImage.updateInstances();
		*/
		//Application.debug("image dropped");
		//Window.alert("Image dropped at client position ("+clientx+","+clienty+")");
	}

	@Override
	public String getCommand(String key) {

		//TODO Implement tooltip internationalization and need to solve the initTranslatedCommands() and getCommandResourceBundle()
//		if (tooltipFlag) {
//			return getCommandTooltip(key);
//		}
		
		initTranslatedCommands();

		return commandConstants.getString(crossReferencingPropertiesKeys(key));
	}

	@Override
	public String getPlain(String key) {
		
		if (tooltipFlag) {
			return getPlainTooltip(key);
		}

		if (plainConstants == null) {
			initPlainConstants();
		}
		
		
		return plainConstants.getString(crossReferencingPropertiesKeys(key));
	}
	
	/**
	 * @author Rana
	 * Cross-Referencing properties keys: from old system of properties keys' 
		naming convention to new GWt compatible system
	 */
	private static String crossReferencingPropertiesKeys(String key) {
		
		if(key == null) {
			return "";
		}
		
		String aStr = null;
		if(key.equals("X->Y")) {
			aStr = "X_Y";
		} else if(key.equals("Y<-X")) {
			aStr = "Y_X";
		} else {
			aStr = key;
		}
		
		return aStr.replace(A_DOT, AN_UNDERSCORE);
	}
	
	
	@Override
	public String getMenu(String cmdName) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return cmdName;
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
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			// isSaved = false;
		}
	}

	public void restoreCurrentUndoInfo() {
		if (isUndoActive()) {
			kernel.restoreCurrentUndoInfo();
			// isSaved = false;
		}
	}

	@Override
	public boolean isUsingFullGui() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return guiManager != null;
	}

	@Override
	public boolean showView(int view) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	/**
	 * Following Java's convention, the return string should only include the language part of the
	 * local.
	 * The assumption here that the "default" locale is English (for now)
	 */
	@Override
	public String getLanguage() {
		return getLocaleStr().substring(0, 2);
	}

	@Override
	public String getLocaleStr() {
		String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
		if (localeName.toLowerCase().equals(Application.DEFAULT_LOCALE)) {
			return Application.DEFAULT_LANGUAGE;
		}
		return localeName.substring(0, 2);
	}

	public void setLanguage(String language) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implementation needed");

	}

	public void setLanguage(String language, String country) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implementation needed");
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

	public void showErrorDialog(final String msg) {
		// TODO: implement it better for GeoGebraWebGUI
		Window.alert(msg);
	}

	@Override
	public void showError(String s) {
		showErrorDialog(s);
	}

	@Override
	public String getTraceXML(GeoElement geoElement) {
		AbstractApplication.debug("implementation needed really"); // TODO Auto-generated
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
		return (EuclidianView) euclidianView;
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView() {
		// if (getGuiManager() == null) {
		return getEuclidianView1();
		// }
		// return getGuiManager().getActiveEuclidianView();
	}

	@Override
	public boolean isShowingEuclidianView2() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return false;
	}

	@Override
	public void evalJavaScript(AbstractApplication app, String script, String arg) {
		
		// TODO: maybe use sandbox?
		
		script = "ggbApplet = document.ggbApplet;"+script;
		
		// add eg arg="A"; to start
		if (arg != null) {
			script = "arg=\""+arg+"\";"+script;
		}

		evalScriptNative(script);
	}

	public native void evalScriptNative(String script) /*-{
		$wnd.eval(script);
	}-*/;

	/**
	 * Initializes the application, seeds factory prototypes, creates Kernel and MyXMLIO
	 * @param undoActive 
	 */
	public void init(final boolean undoAct) {
		initCommonObjects();
	}

	private void initCommonObjects() {
	    geogebra.common.factories.AwtFactory.prototype = new geogebra.web.factories.AwtFactory();
		geogebra.common.factories.FormatFactory.prototype = new geogebra.web.factories.FormatFactory();
		geogebra.common.factories.CASFactory.prototype = new geogebra.web.factories.CASFactory();
		geogebra.common.factories.SwingFactory.prototype = new geogebra.web.factories.SwingFactory();
		geogebra.common.factories.UtilFactory.prototype = new geogebra.web.factories.UtilFactory();
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
		geogebra.common.euclidian.clipping.DoubleArrayFactory.prototype = new geogebra.common.euclidian.clipping.DoubleArrayFactoryImpl();
    }
	
	

	private void showSplashImageOnCanvas() {
		if (this.canvas != null) {
			String geogebra = "GeoGebra";

			canvas.setWidth("427px");
			canvas.setHeight("120px");
			canvas.setCoordinateSpaceWidth(427);
			canvas.setCoordinateSpaceHeight(120);
			Context2d ctx = canvas.getContext2d();
			ctx.clearRect(0, 0, canvas.getCoordinateSpaceWidth(),
					canvas.getCoordinateSpaceHeight());
			ctx.setTextBaseline(TextBaseline.TOP);
			ctx.setTextAlign(TextAlign.START);
			ctx.setFont("50px Century Gothic, Helvetica, sans-serif");
			ctx.setFillStyle("#666666");
			ctx.fillText(geogebra, 33, 37);
			// TextMetrics txm = ctx.measureText(geogebra);
			// ctx.setFillStyle("#7e7eff");
			// ctx.setTextAlign(TextAlign.LEFT);
			// ctx.setFont("20px Century Gothic, Helvetica, sans-serif");
			// ctx.fillText("4",txm.getWidth(),37);
		}
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public AbsolutePanel getEuclidianViewpanel() {
		return euclidianViewPanel;
	}

	public void loadGgbFile(HashMap<String, String> archiveContent)
			throws Exception {
		loadFile(archiveContent);
	}

	public void loadGgbFileAgain(String dataUrl) {
		imageManager.reset();
		GeoGebraFrame.fileLoader.getView().processBase64String(dataUrl);
	}

	public static void log(String message) {
		GWT.log(message);
	}

	public void beforeLoadFile() {
		getEuclidianView1().setDisableRepaint(true);
		getEuclidianView1().setReIniting(true);
	}

	public void afterLoadFile() {
		kernel.initUndoInfo();
		getEuclidianView1().setDisableRepaint(false);
		getEuclidianView1().synCanvasSize();
		getEuclidianView1().repaintView();
		splash.canNowHide();
		getEuclidianView1().requestFocusInWindow();
	}
	
	/** Does some refining after file loaded in the App.
	 * Also note, that only one euclidianview is used now,
	 * later it must be retought.
	 * We save the original widht, height of the canvas,
	 * and restore it after file loading, as it needed to be fixed after all.
	 */
	public void afterLoadAppFile() {
		kernel.initUndoInfo();
		getEuclidianView1().setDisableRepaint(false);
		getEuclidianView1().synCanvasSize();
		splashDialog.canNowHide();
		getEuclidianView1().repaintView();
	}
	
	public void appSplashCanNowHide() {
		splashDialog.canNowHide();
	}

	private void loadFile(HashMap<String, String> archiveContent) throws Exception {

		beforeLoadFile();

		HashMap<String, String> archive = (HashMap<String, String>) archiveContent.clone();

		// Handling of construction and macro file
		String construction = archive.remove("geogebra.xml");
		String macros = archive.remove("geogebra_macro.xml");

		// Construction (required)
		if (construction == null) {
			throw new ConstructionException("File is corrupt: No GeoGebra data found");
		}

		// Macros (optional)
		if (macros != null) {
			//macros = DataUtil.utf8Decode(macros); //DataUtil.utf8Decode(macros);
			myXMLio.processXMLString(macros, true, true);
		}

		if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
				maybeProcessImage(entry.getKey(), entry.getValue());
			}
		}
		if (!imageManager.hasImages()) {
			// Process Construction
			//construction = DataUtil.utf8Decode(construction);//DataUtil.utf8Decode(construction);
			myXMLio.processXMLString(construction, true, false);
			setCurrentFile(archiveContent);
			if (!useFullAppGui) {
				afterLoadFile();
			} else {
				afterLoadAppFile();
			}
		} else {
			// on images do nothing here: wait for callback when images loaded.
			imageManager.triggerImageLoading(/*DataUtil.utf8Decode(*/construction/*)/*DataUtil.utf8Decode(construction)*/,
					(MyXMLio) myXMLio, this);
			setCurrentFile(archiveContent);
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
			return; // Ignore thumbnail
		}

		int index = fn.lastIndexOf('.');
		if (index == -1) {
			return; // Ignore files without extension
		}

		String ext = fn.substring(index + 1);
		if (!IMAGE_EXTENSIONS.contains(ext)) {
			return; // Ignore non image files
		}
		addExternalImage(filename, binaryContent);
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
	public AbstractImageManager getImageManager() {
		return imageManager;
	}

	@Override
	public String reverseGetColor(String colorName) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public String getColor(String key) {

		if (key == null) {
			return "";
		}

		if ((key.length() == 5)
				&& toLowerCase(key).startsWith("gray")) {
			switch (key.charAt(4)) {
			case '0':
				return getColor("white");
			case '1':
				return getPlain("AGray", Unicode.fraction1_8);
			case '2':
				return getPlain("AGray", Unicode.fraction1_4); // silver
			case '3':
				return getPlain("AGray", Unicode.fraction3_8);
			case '4':
				return getPlain("AGray", Unicode.fraction1_2);
			case '5':
				return getPlain("AGray", Unicode.fraction5_8);
			case '6':
				return getPlain("AGray", Unicode.fraction3_4);
			case '7':
				return getPlain("AGray", Unicode.fraction7_8);
			default:
				return getColor("black");
			}
		}

		if (colorConstants == null) {
			initColorConstants();
		}

		return colorConstants.getString(toLowerCase(key));
	}

	@Override
	public String getCommandSyntax(String cmd) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public void showRelation(GeoElement a, GeoElement b) {
		//TODO: implement it better for GeoGebraWebGUI
		Window.alert(new Relation(kernel).relation(a, b));
	}

	@Override
	public void showError(MyError e) {
		AbstractApplication.debug("implementation needed really"); // TODO Auto-generated

	}

	@Override
	public void showError(String key, String error) {
		showErrorDialog(getError(key) + ":\n" + error);
	}

	DrawEquationWeb drawEquation;
	private GuiManager guiManager;

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
		if (canvas == null)
			return 0;
		return canvas.getCanvasElement().getWidth();
	}

	@Override
	public double getHeight() {
		if (canvas == null)
			return 0;
		return canvas.getCanvasElement().getHeight();
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
		if (guiManager == null) {
			// TODO: add getGuiManager(), see #1783
			if (getUseFullGui()) {
				guiManager = new GuiManager(this);
			}
		}

		return guiManager;
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
		return new geogebra.common.kernel.discrete.AlgoShortestDistance(cons, label, list, start, end, weighted);
		
	}

	@Override
	public void updateStyleBars() {
		AbstractApplication.debug("implementation needed for GUI"); // TODO Auto-generated

	}

	@Override
	public void setXML(String xml, boolean clearAll) {
		if (clearAll) {
			setCurrentFile(null);
		}

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
		AbstractApplication.debug("implementation needed for GUI"); // TODO Auto-generated
		return null;
	}

	@Override
	public CommandProcessor newCmdBarCode() {
		AbstractApplication.debug("implementation needed really"); // TODO
		return null;
	}
	
	@Override
	protected boolean isCommandChanged() {
		return commandConstantsOld != commandConstants;
	}

	@Override
	protected void setCommandChanged(boolean b) {
		commandConstantsOld = commandConstants;
	}

	@Override
	protected boolean isCommandNull() {
		return commandConstants == null;
	}

	@Override
	public void initCommand() {
		if(commandConstants == null) {
			initCommandConstants();
		}
	}

	@Override
	public void initScriptingBundle() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	@Override
	public String getScriptingCommand(String internal) {
		AbstractApplication.debug("implementation needed really"); // TODO Auto-generated
		return null;
	}

	@Override
	protected AbstractEuclidianView newEuclidianView(boolean[] showAxes,
			boolean showGrid) {
		return euclidianView = new EuclidianView(euclidianViewPanel,
				euclidianController, showAxes, showGrid, getSettings().getEuclidian(1));
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

	private GlobalKeyDispatcher globalKeyDispatcher;

	@Override
	final public GlobalKeyDispatcher getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
		}
		return globalKeyDispatcher;
	}

	protected GlobalKeyDispatcher newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcher(this);
	}

	@Override
	public AbstractSpreadsheetTableModel getSpreadsheetTableModel() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	public boolean isIniting() {
		// this has no function in GeoGebraWeb currently
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
		// getGuiManager().updateMenubar(); // implementation is not needed for now
		// AbstractApplication.debug("implementation needed"); // TODO Auto-generated
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
		AbstractApplication.debug("implementation needed for GUI"); // TODO Auto-generated

	}

	@Override
	public String getTooltipLanguageString() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference) {
		AbstractApplication.debug("implementation needed for GUI"); // TODO Auto-generated

	}

	public Map<String, String> getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(HashMap<String, String> file) {
		if (currentFile == file) {
			return;
		}

		currentFile = file;
		if (currentFile != null) {
			addToFileList(currentFile);
		}

		// if (!isIniting() && isUsingFullGui()) {
		// updateTitle();
		// getGuiManager().updateMenuWindow();
		// }
	}

	public static void addToFileList(Map<String, String> file) {
		if (file == null) {
			return;
		}
		// add or move fileName to front of list
		fileList.remove(file);
		fileList.addFirst(file);
	}

	public static Map<String, String> getFromFileList(int i) {
		if (fileList.size() > i) {
			return fileList.get(i);
		}
		return null;
	}

	public static int getFileListSize() {
		return fileList.size();
	}

	@Override
	public void reset() {
		if (currentFile != null) {
			try {
				loadGgbFile(currentFile);
			} catch (Exception e) {
				clearConstruction();
			}
		} else {
			clearConstruction();
		}
	}

	public void clearConstruction() {
		// if (isSaved() || saveCurrentFile()) {
		kernel.clearConstruction();

		kernel.initUndoInfo();
		setCurrentFile(null);
		setMoveMode();
		// }
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
		return euclidianViewPanel;
	}

	public void showLoadingAnimation(boolean go) {
		// showSplashImageOnCanvas();

	}

	@Override
	public boolean isHTML5Applet() {
		return true;
	}

	protected void initImageManager() {
		imageManager = new ImageManager();
	}

	public void addExternalImage(String filename, String src) {
		imageManager.addExternalImage(filename, src);
	}

	@Override
	public BufferedImage getExternalImageAdapter(String fileName) {
		return new geogebra.web.awt.BufferedImage(
				ImageManager.getExternalImage(fileName));
	}

	// random id to identify ggb files
	// eg so that GeoGebraTube can notice it's a version of the same file
	private String uniqueId = null;// FIXME: generate new UUID: + UUID.randomUUID();
	private geogebra.web.gui.DialogManagerWeb dialogManager;
	public SplashDialog splash;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void resetUniqueId() {
		uniqueId = null;// FIXME: generate new UUID: + UUID.randomUUID();
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

	@Override
	public geogebra.common.gui.dialog.DialogManager getDialogManager() {
		if (dialogManager == null) {
			dialogManager = new DialogManagerWeb(this);
		}
		return dialogManager;
	}

	@Override
	public void showURLinBrowser(final String pageUrl) {
		// Window.open(pageUrl, "_blank", "");
		debug("opening: " + pageUrl);
		Button openWindow = new Button("Open Window");
		openWindow.addClickHandler(new ClickHandler() {

			public void onClick(final ClickEvent clickEvent) {
				Window.open(pageUrl, "_blank", null);
			}
		});
		RootPanel.get().add(openWindow);
	}

	@Override
	public void uploadToGeoGebraTube() {
		GeoGebraTubeExportWeb ggbtube = new GeoGebraTubeExportWeb(this);
		ggbtube.uploadWorksheet();
	}

	@Override
    public void setWaitCursor() {
		RootPanel.get().setStyleName("");
		RootPanel.get().addStyleName("cursor_wait");
    }

	public void resetCursor() {
		RootPanel.get().setStyleName("");
    }

	@Override
    protected void initGuiManager() {
	    // TODO Auto-generated method stub
	    
    }
	
	private void createSplash() {
		splash = new SplashDialog();
		int splashWidth = 427;
		int splashHeight = 120;
		int width = articleElement.getDataParamWidth();
		int height = articleElement.getDataParamHeight();
		if (width > 0 && height > 0) {
			frame.setWidth(width + "px");
			setDataParamWidth(width);
			setDataParamHeight(height);
			frame.setHeight(height + "px");
			splash.addStyleName("splash");
			splash.getElement().getStyle()
					.setTop((height / 2) - (splashHeight / 2), Unit.PX);
			splash.getElement().getStyle()
					.setLeft((width / 2) - (splashWidth / 2), Unit.PX);

		}
		frame.addStyleName("jsloaded");
		frame.add(splash);
	}
	
	private geogebra.web.gui.app.SplashDialog splashDialog = null;
	
	private void createAppSplash() {
		splashDialog = new geogebra.web.gui.app.SplashDialog();
	}

	/**
	 * @param undoActive
	 * @param this_app
	 * 
	 * Initializes Kernel, EuclidianView, EuclidianSettings, etc..
	 */
	void initCoreObjects(final boolean undoActive,
            final AbstractApplication this_app) {
	    kernel = new Kernel(this_app);

	    // init settings
	    settings = new Settings();

	    initEuclidianViews();

	    initImageManager();

	    myXMLio = new MyXMLio(kernel, kernel.getConstruction());
	    
	    fontManager = new FontManager();
	    setFontSize(12);
	    // setLabelDragsEnabled(false);
	    capturingThreshold = 20;

	    // make sure undo allowed
	    hasFullPermissions = true;

	    getScriptManager();// .ggbOnInit();//this is not called here because we have to delay it
	    										// until the canvas is first drawn

	    setUndoActive(undoActive);
	    registerFileDropHandlers((CanvasElement) canvas.getElement().cast());
    }

}