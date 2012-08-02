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
 * @deprecated Dummy App for the Kernel, will be removed as soon as App is separated from the Kernel
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
				kernel = new Kernel(MobileApp.this);

				settings = new Settings();

				initEuclidianViews();

				setUndoActive(true);

				initing = false;
			}

			public void onFailure(Throwable reason)
			{
				App.debug("onFailure " + reason);
			}
		});
	}

	@Override
	public void addMenuItem(MenuInterface parentMenu, String filename, String name, boolean asHtml, MenuInterface subMenu)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void callAppletJavaScript(String string, Object[] args)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearConstruction()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void copyGraphicsViewToClipboard()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public EuclidianView createEuclidianView()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void evalJavaScript(App app, String script, String arg)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void evalPythonScript(App app, String string, String arg)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void exitAll()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void exportToLMS(boolean b)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void fileNew()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public long freeMemory()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean freeMemoryIsCritical()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public AlgebraView getAlgebraView()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getColor(String string)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCommand(String cmdName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCountryFromGeoIP() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public DialogManager getDialogManager()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public DrawEquationInterface getDrawEquation()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getError(String cmdName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public GBufferedImage getExternalImageAdapter(String filename)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public StringType getFormulaRenderingType()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public GgbAPI getGgbApi()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public GlobalKeyDispatcher getGlobalKeyDispatcher()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public GuiManager getGuiManager()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public double getHeight()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public AbstractImageManager getImageManager()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInternalCommand(String s)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLanguage()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLocaleStr()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMenu(String cmdName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public NormalizerMinimal getNormalizer()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPlain(String cmdName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public GFont getPlainFontCommon()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPlainTooltip(String string)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public PythonBridge getPythonBridge()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScriptingCommand(String internal)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ScriptManagerCommon getScriptManager()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SoundManager getSoundManager()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSymbol(int key)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSymbolTooltip(int key)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected String getSyntaxString()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTooltipLanguageString()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public UndoManager getUndoManager(Construction cons)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUniqueId()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public double getWidth()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void initCommand()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	protected void initGuiManager()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void initScriptingBundle()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isApplet()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean isCommandChanged()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean isCommandNull()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isHTML5Applet()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isShowingEuclidianView2()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUsingFullGui()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean loadXML(String xml) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public AnimationManager newAnimationManager(Kernel kernel2)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public CommandProcessor newCmdBarCode()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected EuclidianController newEuclidianController(Kernel kernel1)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1, boolean showGrid1)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void resetUniqueId()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public String reverseGetColor(String colorName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void runScripts(GeoElement geo1, String string)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	protected void setCommandChanged(boolean b)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void setShowConstructionProtocolNavigation(boolean show, boolean playButton, double playDelay, boolean showProtButton)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void setTooltipFlag()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void setUniqueId(String uniqueId)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void setWaitCursor()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void setXML(String string, boolean b)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean showAlgebraInput()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void showError(MyError e)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void showError(String s)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void showError(String string, String str)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void showRelation(GeoElement geoElement, GeoElement geoElement2)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void showURLinBrowser(String string)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean showView(int view)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void storeUndoInfo()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void updateApplicationLayout()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void updateMenubar()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void updateStyleBars()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void updateUI()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void uploadToGeoGebraTube()
	{
		throw new UnsupportedOperationException();

	}
}
