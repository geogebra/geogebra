package geogebra.mobile;

import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.spreadsheet.SpreadsheetTableModel;
import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.UndoManager;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.main.GlobalKeyDispatcher;
import geogebra.common.main.MyError;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.plugin.ScriptManagerCommon;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.NormalizerMinimal;
import geogebra.mobile.gui.GeoGebraMobileGUI;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * @deprecated Dummy App for the Kernel, will be removed as soon as App is
 *             separated from the Kernel
 * @author Matthias Meisinger
 * 
 */
public class MobileApp extends App
{

	public MobileApp(GeoGebraMobileGUI mobileGUI)
	{
		initing = true;

		// try to async loading of kernel, maybe we got quicker...
		GWT.runAsync(new RunAsyncCallback()
		{

			public void onSuccess()
			{
				// kernel = new Kernel(MobileApp.this);

				settings = new Settings();

				// initEuclidianViews();

				// setUndoActive(true);

				initing = false;
			}

			public void onFailure(Throwable reason)
			{
				App.debug("onFailure " + reason);
			}
		});
	}

	@Override
	protected boolean isCommandChanged()
	{

		return false;
	}

	@Override
	protected void setCommandChanged(boolean b)
	{

	}

	@Override
	protected boolean isCommandNull()
	{

		return false;
	}

	@Override
	public void initCommand()
	{

	}

	@Override
	public void initScriptingBundle()
	{

	}

	@Override
	public String getScriptingCommand(String internal)
	{

		return null;
	}

	@Override
	public String getCommand(String key)
	{

		return null;
	}

	@Override
	public String getPlain(String key)
	{

		return null;
	}

	@Override
	public String getMenu(String key)
	{

		return null;
	}

	@Override
	public String getError(String key)
	{

		return null;
	}

	@Override
	public String getSymbol(int key)
	{

		return null;
	}

	@Override
	public String getSymbolTooltip(int key)
	{

		return null;
	}

	@Override
	public void setTooltipFlag()
	{

	}

	@Override
	public boolean isApplet()
	{

		return false;
	}

	@Override
	public void storeUndoInfo()
	{

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
	public String getLanguage()
	{

		return null;
	}

	@Override
	public String getInternalCommand(String s)
	{

		return null;
	}

	@Override
	public void showError(String s)
	{

	}

	@Override
	public ScriptManagerCommon getScriptManager()
	{

		return null;
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

		return null;
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView()
	{

		return null;
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
	public GuiManager getGuiManager()
	{

		return null;
	}

	@Override
	public DialogManager getDialogManager()
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
	public EuclidianView createEuclidianView()
	{

		return null;
	}

	@Override
	public String reverseGetColor(String colorName)
	{

		return null;
	}

	@Override
	public String getColor(String key)
	{

		return null;
	}

	@Override
	public GBufferedImage getExternalImageAdapter(String filename)
	{

		return null;
	}

	@Override
	protected String getSyntaxString()
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
	public DrawEquationInterface getDrawEquation()
	{

		return null;
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

		return null;
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1, boolean showGrid1)
	{

		return null;
	}

	@Override
	protected EuclidianController newEuclidianController(Kernel kernel1)
	{

		return null;
	}

	@Override
	public UndoManager getUndoManager(Construction cons)
	{

		return null;
	}

	@Override
	public AnimationManager newAnimationManager(Kernel kernel2)
	{

		return null;
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter()
	{

		return null;
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
	public SpreadsheetTableModel getSpreadsheetTableModel()
	{

		return null;
	}

	@Override
	public void setXML(String string, boolean b)
	{

	}

	@Override
	public GgbAPI getGgbApi()
	{

		return null;
	}

	@Override
	public SoundManager getSoundManager()
	{

		return null;
	}

	@Override
	public CommandProcessor newCmdBarCode()
	{

		return null;
	}

	@Override
	public boolean showAlgebraInput()
	{

		return false;
	}

	@Override
	public GlobalKeyDispatcher getGlobalKeyDispatcher()
	{

		return null;
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
	public String getTooltipLanguageString()
	{

		return null;
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

		return false;
	}

	@Override
	public StringType getFormulaRenderingType()
	{

		return null;
	}

	@Override
	public String getLocaleStr()
	{

		return null;
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
	public void clearConstruction()
	{

	}

	@Override
	public void fileNew()
	{

	}

	@Override
	public String getCountryFromGeoIP() throws Exception
	{

		return null;
	}

	@Override
	public boolean loadXML(String xml) throws Exception
	{

		return false;
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
	public NormalizerMinimal getNormalizer()
	{

		return null;
	}

	@Override
	public void runScripts(GeoElement geo1, String string)
	{

	}

	@Override
	public AlgoElement newAlgoShortestDistance(Construction cons, String label, GeoList list, GeoPointND start, GeoPointND end, GeoBoolean weighted)
	{
		return null;
	}

}
