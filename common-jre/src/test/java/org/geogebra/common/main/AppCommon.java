package org.geogebra.common.main;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewCommon;
import org.geogebra.common.factories.*;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.NormalizerMinimal;

/**
 * Common App class used for testing.
 */
public class AppCommon extends App {

    private LocalizationCommon localization;

    /**
     * Construct an AppCommon.
     */
    public AppCommon() {
        initFactories();
        initKernel();
        initLocalization();
        initEuclidianViews();
    }

    @Override
    protected void initLocalization() {
        localization = new LocalizationCommon(2);
        localization.setApp(this);
        super.initLocalization();
    }

    private void initFactories() {
        FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
        AwtFactory.setPrototypeIfNull(new AwtFactoryCommon());
    }

    @Override
    protected void showErrorDialog(String msg) {

    }

    @Override
    protected void initGuiManager() {

    }

    @Override
    protected EuclidianView newEuclidianView(boolean[] showAxes1, boolean showGrid1) {
        return new EuclidianViewCommon();
    }

    @Override
    protected FontManager getFontManager() {
        return null;
    }

    @Override
    protected int getWindowWidth() {
        return 0;
    }

    @Override
    protected int getWindowHeight() {
        return 0;
    }

    @Override
    protected void getLayoutXML(StringBuilder sb, boolean asPreference) {

    }

    @Override
    public CommandDispatcher getCommandDispatcher(Kernel k) {
        return null;
    }

    @Override
    public CommandDispatcher getCommand3DDispatcher(Kernel k) {
        return null;
    }

    @Override
    public void invokeLater(Runnable runnable) {

    }

    @Override
    public boolean isApplet() {
        return false;
    }

    @Override
    public void storeUndoInfo() {

    }

    @Override
    public void closePopups() {

    }

    @Override
    public GTimer newTimer(GTimerListener listener, int delay) {
        return null;
    }

    @Override
    public boolean isUsingFullGui() {
        return false;
    }

    @Override
    public boolean showView(int view) {
        return false;
    }

    @Override
    public void showError(String localizedError) {

    }

    @Override
    public void showError(String string, String str) {

    }

    @Override
    public AlgebraView getAlgebraView() {
        return null;
    }

    @Override
    public EuclidianView getActiveEuclidianView() {
        return null;
    }

    @Override
    public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
        return false;
    }

    @Override
    public boolean isShowingEuclidianView2(int idx) {
        return false;
    }

    @Override
    public ImageManager getImageManager() {
        return null;
    }

    @Override
    public GuiManagerInterface getGuiManager() {
        return null;
    }

    @Override
    public DialogManager getDialogManager() {
        return null;
    }

    @Override
    public void evalJavaScript(App app, String script, String arg) throws Exception {

    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public GFont getPlainFontCommon() {
        return null;
    }

    @Override
    public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
        return null;
    }

    @Override
    public void setWaitCursor() {

    }

    @Override
    public void updateStyleBars() {

    }

    @Override
    public void updateDynamicStyleBars() {

    }

    @Override
    public void set1rstMode() {

    }

    @Override
    public SpreadsheetTableModel getSpreadsheetTableModel() {
        return null;
    }

    @Override
    public void setXML(String string, boolean b) {

    }

    @Override
    public GgbAPI getGgbApi() {
        return null;
    }

    @Override
    public SoundManager getSoundManager() {
        return null;
    }

    @Override
    public boolean showAlgebraInput() {
        return false;
    }

    @Override
    public GlobalKeyDispatcher getGlobalKeyDispatcher() {
        return null;
    }

    @Override
    public void callAppletJavaScript(String string, String... args) {

    }

    @Override
    public void updateMenubar() {

    }

    @Override
    public void updateUI() {

    }

    @Override
    public void showURLinBrowser(String string) {

    }

    @Override
    public void uploadToGeoGebraTube() {

    }

    @Override
    public void updateApplicationLayout() {

    }

    @Override
    public boolean clearConstruction() {
        return false;
    }

    @Override
    public void fileNew() {

    }

    @Override
    public boolean loadXML(String xml) throws Exception {
        return false;
    }

    @Override
    public void copyGraphicsViewToClipboard() {

    }

    @Override
    public void exitAll() {

    }

    @Override
    public void runScripts(GeoElement geo1, String string) {

    }

    @Override
    public boolean freeMemoryIsCritical() {
        return false;
    }

    @Override
    public long freeMemory() {
        return 0;
    }

    @Override
    public EuclidianView createEuclidianView() {
        return null;
    }

    @Override
    public void setActiveView(int evID) {

    }

    @Override
    public UndoManager getUndoManager(Construction cons) {
        return null;
    }

    @Override
    public boolean isHTML5Applet() {
        return false;
    }

    @Override
    public CASFactory getCASFactory() {
        return null;
    }

    @Override
    public Factory getFactory() {
        return null;
    }

    @Override
    public NormalizerMinimal getNormalizer() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public EuclidianController newEuclidianController(Kernel kernel1) {
        return null;
    }

    @Override
    public DrawEquation getDrawEquation() {
        return null;
    }

    @Override
    public void resetUniqueId() {

    }

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public MyXMLio createXMLio(Construction cons) {
        return null;
    }

    @Override
    public double getMillisecondTime() {
        return 0;
    }

    @Override
    public void showCustomizeToolbarGUI() {

    }

    @Override
    public boolean isSelectionRectangleAllowed() {
        return false;
    }

    @Override
    public MyImage getExternalImageAdapter(String filename, int width, int height) {
        return null;
    }

    @Override
    public ScriptManager newScriptManager() {
        return null;
    }
}
