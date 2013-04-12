package geogebra.web.main;

import geogebra.common.euclidian.DrawEquation;
import geogebra.common.factories.SwingFactory;
import geogebra.common.gui.SetLabels;
import geogebra.common.io.MyXMLio;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.main.App;
import geogebra.common.main.CasType;
import geogebra.common.main.Localization;
import geogebra.common.main.MyError;
import geogebra.common.plugin.ScriptManager;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.NormalizerMinimal;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.euclidian.EuclidianViewWeb;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.helper.ScriptLoadCallback;
import geogebra.web.html5.DynamicScriptElement;
import geogebra.web.io.ConstructionException;
import geogebra.web.io.MyXMLioW;
import geogebra.web.sound.SoundManagerW;
import geogebra.web.util.ImageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;

public abstract class AppWeb extends App implements SetLabels{
	
	public static final String DEFAULT_APPLET_ID = "ggbApplet";
	private DrawEquationWeb drawEquation;
	private SoundManager soundManager;
	private NormalizerMinimal normalizerMinimal;
	private GgbAPI ggbapi;
	private final LocalizationW loc;
	private ImageManager imageManager;
	private HashMap<String, String> currentFile = null;
	private static LinkedList<Map<String, String>> fileList = new LinkedList<Map<String, String>>();
	
	protected AppWeb(){
		loc = new LocalizationW();
	}
	
	@Override
	public final DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationWeb();
		}

		return drawEquation;
	}
	
	@Override
	public final SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManagerW(this);
		}
		return soundManager;
	}
	
	@Override
	public geogebra.web.main.GgbAPI getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new geogebra.web.main.GgbAPI(this);
		}
		return ggbapi;
	}
	
	public abstract Canvas getCanvas();
	
	@Override
	public final StringType getPreferredFormulaRenderingType() {
		return StringType.LATEX;
	}
	
	@Override
	public final NormalizerMinimal getNormalizer() {
		if (normalizerMinimal == null) {
			normalizerMinimal = new NormalizerMinimal();
		}

		return normalizerMinimal;
	}
	
	@Override
    public final SwingFactory getSwingFactory() {
	    return SwingFactory.getPrototype();
    }
	
	protected static void initFactories()
	{
		geogebra.common.factories.FormatFactory.prototype = new geogebra.web.factories.FormatFactoryW();
		geogebra.common.factories.AwtFactory.prototype = new geogebra.web.factories.AwtFactoryW();
		geogebra.common.euclidian.EuclidianStatic.prototype = new geogebra.web.euclidian.EuclidianStaticW();
		geogebra.common.factories.SwingFactory.setPrototype(new geogebra.web.factories.SwingFactoryW());
		geogebra.common.util.StringUtil.prototype = new geogebra.common.util.StringUtil();
		geogebra.common.euclidian.clipping.DoubleArrayFactory.prototype = new geogebra.common.euclidian.clipping.DoubleArrayFactoryImpl();

	}
	
	private GlobalKeyDispatcherW globalKeyDispatcher;

	@Override
	final public GlobalKeyDispatcherW getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
		}
		return globalKeyDispatcher;
	}

	protected GlobalKeyDispatcherW newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcherW(this);
	}
	
	@Override
	public EuclidianViewWeb getEuclidianView1() {
		return (EuclidianViewWeb) euclidianView;
	}
	private TimerSystemW timers;
	public TimerSystemW getTimerSystem() {
		if (timers == null) {
			timers = new TimerSystemW(this);
		}
		return timers;
	}

	public abstract void showMessage(String error);
	
	public abstract ViewManager getViewManager();

	public void syncAppletPanelSize(int width, int height, int evNo) {
	    // TODO Auto-generated method stub
	    
    }
	@Override
	public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManagerW(this);
		}
		return scriptManager;
	}

	@Override
	public CasType getCASType() {
		return CasType.GIAC;
	}
	
	// ================================================
		// NATIVE JS
		// ================================================

		

		public native void evalScriptNative(String script) /*-{
			$wnd.eval(script);
		}-*/;

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
		public void callAppletJavaScript(String fun, Object[] args) {
			if (args == null || args.length == 0) {
				callNativeJavaScript(fun);
			} else if (args.length == 1) {
				App.debug("calling function: " + fun + "(" + args[0].toString()
				        + ")");
				callNativeJavaScript(fun, args[0].toString());
			} else {
				debug("callAppletJavaScript() not supported for more than 1 argument");
			}

		}

		public String getDataParamId() {
	        return DEFAULT_APPLET_ID;
        }
		
		private MyXMLioW xmlio;

		@Override
		public boolean loadXML(String xml) throws Exception {
			getXMLio().processXMLString(xml, true, false);
			return true;
		}

		@Override
		public MyXMLioW getXMLio() {
			if (xmlio == null) {
				xmlio = createXMLio(kernel.getConstruction());
			}
			return xmlio;
		}

		@Override
		public MyXMLioW createXMLio(Construction cons) {
			return new MyXMLioW(cons.getKernel(), cons);
		}
		
		public void setLanguage(final String lang) {

			if (lang != null && lang.equals(loc.getLanguage())) {
				setLabels();
				return;
			}

			if (lang == null || "".equals(lang)) {

				App.error("language being set to empty string");
				setLanguage("en");
				return;
			}

			App.debug("setting language to:" + lang);

			// load keys (into a JavaScript <script> tag)
			DynamicScriptElement script = (DynamicScriptElement) Document.get()
			        .createScriptElement();
			script.setSrc(GWT.getModuleBaseURL() + "js/properties_keys_" + lang
			        + ".js");
			script.addLoadHandler(new ScriptLoadCallback() {

				public void onLoad() {
					// force reload
					resetCommandDictionary();

					loc.setLanguage(lang);

					// make sure digits are updated in all numbers
					getKernel().updateConstruction();
					setUnsaved();

					// update display & Input Bar Dictionary etc
					setLabels();

					// inputField.setDictionary(getCommandDictionary());

				}

				
			});
			Document.get().getBody().appendChild(script);
		}
		
		protected abstract void resetCommandDictionary();

		public void setLanguage(String language, String country) {

			if (language == null || "".equals(language)) {
				App.warn("error calling setLanguage(), setting to English (US): "
				        + language + "_" + country);
				setLanguage("en");
				return;
			}

			if (country == null || "".equals(country)) {
				setLanguage(language);
			}
			this.

			setLanguage(language + "_" + country);
		}
		
		@Override
		public Localization getLocalization() {
			return loc;
		}
		
		/**
		 * This method checks if the command is stored in the command properties
		 * file as a key or a value.
		 * 
		 * @param command
		 *            : a value that should be in the command properties files (part
		 *            of Internationalization)
		 * @return the value "command" after verifying its existence.
		 */
		@Override
		final public String getReverseCommand(String command) {

			if (loc.getLanguage() == null) {
				// keys not loaded yet
				return command;
			}

			return super.getReverseCommand(command);
		}
		
		public String getEnglishCommand(String pageName) {
			loc.initCommand();
			// String ret = commandConstants
			// .getString(crossReferencingPropertiesKeys(pageName));
			// if (ret != null)
			// return ret;
			return pageName;
		}

		public abstract void appSplashCanNowHide();

		public abstract String getLanguageFromCookie();

		public abstract void showLoadingAnimation(boolean b);
		
		public void loadGgbFile(HashMap<String, String> archiveContent)
		        throws Exception {
			loadFile(archiveContent);
		}

		public void loadGgbFileAgain(String dataUrl) {

			((DrawEquationWeb) getDrawEquation())
			        .deleteLaTeXes((EuclidianViewWeb) getActiveEuclidianView());
			getImageManager().reset();
			if (useFullAppGui)
				GeoGebraAppFrame.fileLoader.getView().processBase64String(dataUrl);
			else
				GeoGebraFrame.fileLoader.getView().processBase64String(dataUrl);
		}
		
		private void loadFile(HashMap<String, String> archiveContent)
		        throws Exception {

			beforeLoadFile();

			HashMap<String, String> archive = (HashMap<String, String>) archiveContent
			        .clone();

			// Handling of construction and macro file
			String construction = archive.remove(MyXMLio.XML_FILE);
			String macros = archive.remove(MyXMLio.XML_FILE_MACRO);
			String libraryJS = archive.remove(MyXMLio.JAVASCRIPT_FILE);

			// Construction (required)
			if (construction == null) {
				throw new ConstructionException(
				        "File is corrupt: No GeoGebra data found");
			}

			// Macros (optional)
			if (macros != null) {
				// macros = DataUtil.utf8Decode(macros);
				// //DataUtil.utf8Decode(macros);
				getXMLio().processXMLString(macros, true, true);
			}
			
			// Library JavaScript (optional)
			if (libraryJS == null) { //TODO: && !isGGTfile)
				kernel.resetLibraryJavaScript();
			} else {
				kernel.setLibraryJavaScript(libraryJS);
			}


			if (archive.entrySet() != null) {
				for (Entry<String, String> entry : archive.entrySet()) {
					maybeProcessImage(entry.getKey(), entry.getValue());
				}
			}
			if (!getImageManager().hasImages()) {
				// Process Construction
				// construction =
				// DataUtil.utf8Decode(construction);//DataUtil.utf8Decode(construction);
				getXMLio().processXMLString(construction, true, false);
				setCurrentFile(archiveContent);
				afterLoadFileAppOrNot();
			} else {
				// on images do nothing here: wait for callback when images loaded.
				getImageManager().triggerImageLoading(
				/* DataUtil.utf8Decode( */construction/*
													 * )/*DataUtil.utf8Decode
													 * (construction)
													 */, getXMLio(), this);
				setCurrentFile(archiveContent);
				

			}
		}
		
		public abstract void afterLoadFileAppOrNot();

		public void beforeLoadFile() {
			startCollectingRepaints();
			getEuclidianView1().setReIniting(true);
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
		
		public Map<String, String> getCurrentFile() {
			return currentFile;
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
		
		private static final ArrayList<String> IMAGE_EXTENSIONS = new ArrayList<String>();
		static {
			IMAGE_EXTENSIONS.add("bmp");
			IMAGE_EXTENSIONS.add("gif");
			IMAGE_EXTENSIONS.add("jpg");
			IMAGE_EXTENSIONS.add("jpeg");
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

			String ext = fn.substring(index + 1).toLowerCase();
			if (!IMAGE_EXTENSIONS.contains(ext)) {
				return; // Ignore non image files
			}

			// for file names e.g. /geogebra/main/nav_play.png in GeoButtons
			if (filename != null && filename.length() != 0
			        && filename.charAt(0) == '/')
				addExternalImage(filename.substring(1), binaryContent);
			else
				addExternalImage(filename, binaryContent);
		}
		
		public void addExternalImage(String filename, String src) {
			getImageManager().addExternalImage(filename, src);
		}
		
		@Override
		public ImageManager getImageManager() {
			return imageManager;
		}
		
		protected void initImageManager() {
			imageManager = new ImageManager();
		}
		
		@Override
		public final void setXML(String xml, boolean clearAll) {
			if (clearAll) {
				setCurrentFile(null);
			}

			try {
				// make sure objects are displayed in the correct View
				setActiveView(App.VIEW_EUCLIDIAN);
				getXMLio().processXMLString(xml, clearAll, false);
			} catch (MyError err) {
				err.printStackTrace();
				showError(err);
			} catch (Exception e) {
				e.printStackTrace();
				showError("LoadFileFailed");
			}
		}
		
		@Override
		public boolean clearConstruction() {
			// if (isSaved() || saveCurrentFile()) {
			kernel.clearConstruction(true);

			kernel.initUndoInfo();
			setCurrentFile(null);
			setMoveMode();

			((DrawEquationWeb) getDrawEquation())
			        .deleteLaTeXes((EuclidianViewW) getActiveEuclidianView());
			return true;

			// }
			// return false;
		}

		public abstract void tubeSearch(String query);
		
}
