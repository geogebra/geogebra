package org.geogebra.web.android;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.SwingFactory;
import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.AlgoCubicSwitchInterface;
import org.geogebra.common.main.AlgoCubicSwitchParams;
import org.geogebra.common.main.AlgoKimberlingWeightsInterface;
import org.geogebra.common.main.AlgoKimberlingWeightsParams;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCompanion;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.Language;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.DynamicScriptElement;
import org.geogebra.web.html5.util.ScriptLoadCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;

public class AppStub extends App {

	private LocalizationW localization;

	@Override
	public LocalizationW getLocalization() {
		if (localization == null) {
			localization = new LocalizationW(2);
		}
		return localization;
	}

	@Override
	public double getWidth() {
		return Window.getClientWidth();
	}

	public void setLanguage(String language, final ScriptLoadCallback callback) {
		final String lang = Language.getClosestGWTSupportedLanguage(language);
		// load keys (into a JavaScript <script> tag)
		DynamicScriptElement script = (DynamicScriptElement) Document.get()
				.createScriptElement();
		script.setSrc(GWT.getModuleBaseURL() + "js/properties_keys_" + lang
				+ ".js");
		script.addLoadHandler(new ScriptLoadCallback() {

			@Override
			public void onLoad() {
				getLocalization().setLanguage(lang);
				callback.onLoad();
			}
		});
		Document.get().getBody().appendChild(script);
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
	protected AppCompanion newAppCompanion() {
		// TODO Auto-generated method stub
		return null;
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
	public void showError(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void showErrorDialog(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public ScriptManager getScriptManager() {
		// TODO Auto-generated method stub
		return null;
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
	public AlgebraView getAlgebraView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShowingEuclidianView2(int idx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageManager getImageManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GuiManagerInterface getGuiManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DialogManager getDialogManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initGuiManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void evalJavaScript(App app, String script, String arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public EuclidianView createEuclidianView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyImage getExternalImageAdapter(String filename, int width,
			int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActiveView(int evID) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void showCommandError(String command, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showError(String string, String str) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUniqueId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUniqueId(String uniqueId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetUniqueId() {
		// TODO Auto-generated method stub

	}

	@Override
	public DrawEquation getDrawEquation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GFont getPlainFontCommon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1,
			boolean showGrid1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UndoManager getUndoManager(Construction cons) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnimationManager newAnimationManager(Kernel kernel2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWaitCursor() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStyleBars() {
		// TODO Auto-generated method stub

	}

	@Override
	public void set1rstMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		// TODO Auto-generated method stub
		return null;
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
	public void callAppletJavaScript(String string, Object[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMenubar() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUI() {
		// TODO Auto-generated method stub

	}

	@Override
	protected FontManager getFontManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getWindowWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getWindowHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void getLayoutXML(StringBuilder sb, boolean asPreference) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isHTML5Applet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showURLinBrowser(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uploadToGeoGebraTube() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateApplicationLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean clearConstruction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createNewWindow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fileNew() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean loadXML(String xml) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyBase64ToClipboard() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMenuItem(MenuInterface parentMenu, String filename,
			String name, boolean asHtml, MenuInterface subMenu) {
		// TODO Auto-generated method stub

	}

	@Override
	public NormalizerMinimal getNormalizer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void runScripts(GeoElement geo1, String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public CASFactory getCASFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SwingFactory getSwingFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Factory getFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyXMLio getXMLio() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyXMLio createXMLio(Construction cons) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTooltipHTML(int mode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getMillisecondTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AlgoKimberlingWeightsInterface getAlgoKimberlingWeights() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double kimberlingWeight(AlgoKimberlingWeightsParams kw) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AlgoCubicSwitchInterface getAlgoCubicSwitch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cubicSwitch(AlgoCubicSwitchParams kw) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandDispatcher getCommandDispatcher(Kernel k) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showCustomizeToolbarGUI() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSelectionRectangleAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getEnglishCommand(String command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void copyFullHTML5ExportToClipboard() {
		// TODO Auto-generated method stub

	}

}
