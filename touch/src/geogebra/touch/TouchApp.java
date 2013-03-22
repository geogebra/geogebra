package geogebra.touch;

import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.factories.CASFactory;
import geogebra.common.factories.Factory;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.io.MyXMLio;
import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.UndoManager;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.main.FontManager;
import geogebra.common.main.Localization;
import geogebra.common.main.MyError;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.ScriptManager;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.GeoGebraLogger.LogDestination;
import geogebra.touch.gui.GeoGebraTouchGUI;
import geogebra.touch.gui.elements.header.XMLBuilder;
import geogebra.touch.gui.euclidian.EuclidianViewM;
import geogebra.touch.utils.GeoGebraLoggerM;
import geogebra.web.io.MyXMLioW;
import geogebra.web.kernel.UndoManagerW;
import geogebra.web.main.AppWeb;
import geogebra.web.main.FontManagerW;
import geogebra.web.main.LocalizationW;
import geogebra.web.main.ViewManager;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.i18n.client.LocaleInfo;

/**
 * Extends from class {@link App}.
 * 
 * @author Matthias Meisinger
 * 
 */
public class TouchApp extends AppWeb
{
	private GeoGebraTouchGUI mobileGUI;
	private FontManagerW fontManager;
	/**
	 * static because it gets from server side, either "" or the set filename
	 */
	public static String currentFileId = null;
	private XMLBuilder mobileXML;
	private LocalizationW loc;

	// accepting range for hitting Geos (except for Points) is multiplied with
	// this factor
	// (for Points see EuclidianView)
	private int selectionFactor = 3;

	/**
	 * Initializes the factories, {@link FontManagerW} and {@link Settings}.
	 * 
	 * @param mobileGUI
	 *          graphic user interface
	 * @see geogebra.common.factories.FormatFactory FormatFactory
	 * @see geogebra.common.factories.AwtFactory AwtFactory
	 */
	public TouchApp(GeoGebraTouchGUI mobileGUI)
	{
		super.initing = true;

		setLabelDragsEnabled(false);

		initFactories();

		this.loc = new LocalizationW();

		this.fontManager = new FontManagerW();
		this.mobileGUI = mobileGUI;
		this.settings = new Settings();

		setFontSize(12);

		this.capturingThreshold *= this.selectionFactor;
		
		logger = new GeoGebraLoggerM(mobileGUI);
		logger.setLogDestination(LogDestination.CONSOLES);
		logger.setLogLevel("DEBUG");
	}

	// not needed yet, because we use xml-Strings
	// private static native void setCurrentFileId() /*-{
	// @geogebra.touch.TouchApp::currentFileId = $wnd.GGW_appengine.FILE_IDS[0];
	// }-*/;

	/**
	 * Creates a new {@link Kernel}, a new instance of {@link MyXMLio} and
	 * initializes the components of the {@link GeoGebraTouchGUI}.
	 */
	public void start()
	{
		initKernel();

		this.mobileGUI.initComponents(this.kernel);
		super.euclidianView = this.mobileGUI.getEuclidianViewPanel().getEuclidianView();

		hasFullPermissions = true;
		setUndoActive(true);

		super.initing = false;
	}

	public GeoGebraTouchGUI getMobileGui()
	{
		return this.mobileGUI;
	}

	@Override
	protected FontManager getFontManager()
	{
		return this.fontManager;
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1, boolean showGrid1)
	{
		// MobileEuclidianController ec = new MobileEuclidianController();
		// ec.setKernel(this.kernel);
		// return new EuclidianViewM(ec, showAxes1, showGrid1,
		// getSettings().getEuclidian(1));
		return null;
	}

	@Override
	public boolean isApplet()
	{

		return false;
	}

	@Override
	public void storeUndoInfo()
	{
		if (isUndoActive())
		{
			this.kernel.storeUndoInfo();
		}
	}

	@Override
	public boolean isUsingFullGui()
	{

		return false;
	}

	@Override
	public boolean showView(int view)
	{

		return false;
	}

	@Override
	public void showError(String s)
	{

	}

	@Override
	public boolean freeMemoryIsCritical()
	{

		return false;
	}

	@Override
	public long freeMemory()
	{

		return 0;
	}

	@Override
	public AlgebraView getAlgebraView()
	{
		// TODO: will not be used!
		return null;
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView()
	{
		// TODO
		return this.mobileGUI.getEuclidianViewPanel().getEuclidianView();
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot()
	{

		return false;
	}

	@Override
	public boolean isShowingEuclidianView2()
	{

		return false;
	}

	@Override
	public AbstractImageManager getImageManager()
	{

		return null;
	}

	@Override
	protected void initGuiManager()
	{

	}

	@Override
	public void evalJavaScript(App app, String script, String arg)
	{

	}

	@Override
	public GBufferedImage getExternalImageAdapter(String filename)
	{

		return null;
	}

	@Override
	public void showRelation(GeoElement geoElement, GeoElement geoElement2)
	{

	}

	@Override
	public void showError(MyError e)
	{

	}

	@Override
	public void showError(String string, String str)
	{

	}

	@Override
	public String getUniqueId()
	{

		return null;
	}

	@Override
	public void setUniqueId(String uniqueId)
	{

	}

	@Override
	public void resetUniqueId()
	{

	}

	@Override
	public void setShowConstructionProtocolNavigation(boolean show, boolean playButton, double playDelay, boolean showProtButton)
	{

	}

	@Override
	public double getWidth()
	{

		return 0;
	}

	@Override
	public double getHeight()
	{

		return 0;
	}

	@Override
	public GFont getPlainFontCommon()
	{
		// TODO
		return new geogebra.web.awt.GFontW("normal");
	}

	@Override
	protected EuclidianController newEuclidianController(geogebra.common.kernel.Kernel kernel1)
	{

		return null;
	}

	@Override
	public UndoManager getUndoManager(Construction cons)
	{
		// TODO
		return new UndoManagerW(cons);
	}

	@Override
	public AnimationManager newAnimationManager(geogebra.common.kernel.Kernel kernel2)
	{

		return null;
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter()
	{
		// TODO
		return new geogebra.web.kernel.geos.GeoElementGraphicsAdapter(this);
	}

	@Override
	public void setWaitCursor()
	{

	}

	@Override
	public void updateStyleBars()
	{

	}

	@Override
	public void setXML(String string, boolean b)
	{

	}

	@Override
	public geogebra.common.plugin.GgbAPI getGgbApi()
	{
		// not needed yet, because now we use xml-strings
		// if (ggbapi == null) {
		// ggbapi = new geogebra.touch.gui.elements.header.GgbAPI(this);
		// }
		//
		// return ggbapi;
		return null;
	}

	@Override
	public boolean showAlgebraInput()
	{

		return false;
	}

	@Override
	public void evalPythonScript(App app, String string, String arg)
	{

	}

	@Override
	public void callAppletJavaScript(String string, Object[] args)
	{

	}

	@Override
	public void updateMenubar()
	{

	}

	@Override
	public void updateUI()
	{

	}

	@Override
	protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference)
	{

	}

	@Override
	public void reset()
	{

	}

	@Override
	public PythonBridge getPythonBridge()
	{

		return null;
	}

	@Override
	public String getPlainTooltip(String string)
	{

		return null;
	}

	@Override
	public boolean isHTML5Applet()
	{
		// TODO
		return true;
	}

	@Override
	public String getLocaleStr()
	{
		// TODO
		String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
		App.debug("Current Locale: " + localeName);

		if (localeName.toLowerCase().equals(LocalizationW.DEFAULT_LOCALE))
		{
			return LocalizationW.DEFAULT_LANGUAGE;
		}
		return localeName.substring(0, 2);
	}

	@Override
	public void showURLinBrowser(String string)
	{

	}

	@Override
	public void uploadToGeoGebraTube()
	{

	}

	@Override
	public void updateApplicationLayout()
	{

	}

	@Override
	public boolean clearConstruction()
	{
		return false;

	}

	@Override
	public void fileNew()
	{

	}

	@Override
	public boolean loadXML(String xml) throws Exception
	{
		this.mobileXML = createXMLBuilder(this.kernel.getConstruction());
		this.mobileXML.processXMLString(xml, true, false);
		return true;
	}

	@Override
	public void exportToLMS(boolean b)
	{
	}

	@Override
	public void copyGraphicsViewToClipboard()
	{
	}

	@Override
	public void exitAll()
	{
	}

	@Override
	public void addMenuItem(MenuInterface parentMenu, String filename, String name, boolean asHtml, MenuInterface subMenu)
	{
	}

	@Override
	public void runScripts(GeoElement geo1, String string)
	{
	}

	@Override
	protected Object getMainComponent()
	{
		return null;
	}

	@Override
	public GuiManager getGuiManager()
	{
		// TODO
		return null;
	}

	@Override
	public EuclidianView createEuclidianView()
	{
		// TODO
		if (this.euclidianView == null)
		{
			// initEuclidianViews();
			this.euclidianView = (EuclidianView) getActiveEuclidianView();
		}
		return this.euclidianView;
	}

	@Override
	public String getCountryFromGeoIP() throws Exception
	{
		return null;
	}

	@Override
	public void showErrorDialog(String s)
	{
	}

	@Override
	public String getReverseCommand(String command)
	{
		return null;
	}

	@Override
	public DialogManager getDialogManager()
	{
		return null;
	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel()
	{
		return null;
	}

	@Override
	public CASFactory getCASFactory()
	{
		return null;
	}

	@Override
	public Factory getFactory()
	{
		return null;
	}

	// needed, to save xml-Strings(constructions)
	public XMLBuilder getXMLBuilder()
	{
		if (this.mobileXML == null)
		{
			this.mobileXML = createXMLBuilder(this.kernel.getConstruction());
		}
		return this.mobileXML;
	}

	public static XMLBuilder createXMLBuilder(Construction cons)
	{
		return new XMLBuilder(cons);
	}

	@Override
	public String getXML()
	{
		return getXMLBuilder().getFullXML();
	}

	@Override
	public ScriptManager getScriptManager()
	{
		// TODO
		if (this.scriptManager == null)
		{
			this.scriptManager = new ScriptManagerM(this);
		}
		return this.scriptManager;
	}

	@Override
	public geogebra.common.io.MyXMLio getXMLio()
	{
		// if (this.myXMLio == null) {
		// this.myXMLio = createXMLio(this.kernel.getConstruction());
		// }
		// return this.myXMLio;
		return null;
	}

	@Override
	public geogebra.common.io.MyXMLio createXMLio(Construction cons)
	{
		return new MyXMLioW(cons.getKernel(), cons);
	}

	@Override
	public void createNewWindow()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Localization getLocalization()
	{
		return this.loc;
	}

	@Override
	public Canvas getCanvas()
	{
		return ((EuclidianViewM) getActiveEuclidianView()).getCanvas();
	}

	@Override
	public void showMessage(String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ViewManager getViewManager() {
		return new ViewManagerM();
	}

}
