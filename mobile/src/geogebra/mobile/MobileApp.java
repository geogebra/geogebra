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
import geogebra.common.plugin.GgbAPI;
import geogebra.common.plugin.ScriptManagerCommon;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.NormalizerMinimal;

public class MobileApp extends App
{

	public MobileApp()
	{
		//TODO Implement our own constructor without loading all stuff at once.

	}

	@Override
  protected boolean isCommandChanged()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  protected void setCommandChanged(boolean b)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  protected boolean isCommandNull()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public void initCommand()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void initScriptingBundle()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public String getScriptingCommand(String internal)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getCommand(String cmdName)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getPlain(String cmdName)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getMenu(String cmdName)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getError(String cmdName)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getSymbol(int key)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getSymbolTooltip(int key)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void setTooltipFlag()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public boolean isApplet()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public void storeUndoInfo()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public boolean isUsingFullGui()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public boolean showView(int view)
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public String getLanguage()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getInternalCommand(String s)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void showError(String s)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public ScriptManagerCommon getScriptManager()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public boolean freeMemoryIsCritical()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public long freeMemory()
  {
	  // TODO Auto-generated method stub
	  return 0;
  }

	@Override
  public AlgebraView getAlgebraView()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public EuclidianViewInterfaceCommon getActiveEuclidianView()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public boolean hasEuclidianView2EitherShowingOrNot()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public boolean isShowingEuclidianView2()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public AbstractImageManager getImageManager()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public GuiManager getGuiManager()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public DialogManager getDialogManager()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  protected void initGuiManager()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void evalJavaScript(App app, String script, String arg)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public EuclidianView createEuclidianView()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String reverseGetColor(String colorName)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getColor(String string)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public GBufferedImage getExternalImageAdapter(String filename)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  protected String getSyntaxString()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void showRelation(GeoElement geoElement, GeoElement geoElement2)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void showError(MyError e)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void showError(String string, String str)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public String getUniqueId()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void setUniqueId(String uniqueId)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void resetUniqueId()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public DrawEquationInterface getDrawEquation()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void setShowConstructionProtocolNavigation(boolean show, boolean playButton, double playDelay, boolean showProtButton)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public double getWidth()
  {
	  // TODO Auto-generated method stub
	  return 0;
  }

	@Override
  public double getHeight()
  {
	  // TODO Auto-generated method stub
	  return 0;
  }

	@Override
  public GFont getPlainFontCommon()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  protected EuclidianView newEuclidianView(boolean[] showAxes1, boolean showGrid1)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  protected EuclidianController newEuclidianController(Kernel kernel1)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public UndoManager getUndoManager(Construction cons)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public AnimationManager newAnimationManager(Kernel kernel2)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void setWaitCursor()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public AlgoElement newAlgoShortestDistance(Construction cons, String label, GeoList list, GeoPointND start, GeoPointND end, GeoBoolean weighted)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void updateStyleBars()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public SpreadsheetTableModel getSpreadsheetTableModel()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void setXML(String string, boolean b)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public GgbAPI getGgbApi()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public SoundManager getSoundManager()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public CommandProcessor newCmdBarCode()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public boolean showAlgebraInput()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public GlobalKeyDispatcher getGlobalKeyDispatcher()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void evalPythonScript(App app, String string, String arg)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void callAppletJavaScript(String string, Object[] args)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public boolean isRightClickEnabled()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public void updateMenubar()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void updateUI()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public String getTooltipLanguageString()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void reset()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public PythonBridge getPythonBridge()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getPlainTooltip(String string)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public boolean isHTML5Applet()
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public StringType getFormulaRenderingType()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public String getLocaleStr()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void showURLinBrowser(String string)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void uploadToGeoGebraTube()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void updateApplicationLayout()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void clearConstruction()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void fileNew()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public String getCountryFromGeoIP() throws Exception
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public boolean loadXML(String xml) throws Exception
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	@Override
  public void exportToLMS(boolean b)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void copyGraphicsViewToClipboard()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void exitAll()
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void addMenuItem(MenuInterface parentMenu, String filename, String name, boolean asHtml, MenuInterface subMenu)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public NormalizerMinimal getNormalizer()
  {
	  // TODO Auto-generated method stub
	  return null;
  }
}
