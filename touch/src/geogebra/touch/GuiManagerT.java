package geogebra.touch;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.Layout;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.common.javax.swing.GTextComponent;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;

import java.util.ArrayList;

public class GuiManagerT extends GuiManager {

    @Override
    public void allowGUIToRefresh() {
	// TODO Auto-generated method stub

    }

    @Override
    public void applyAlgebraViewSettings() {
	// TODO Auto-generated method stub

    }

    @Override
    public void attachAlgebraView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void attachAssignmentView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void attachCasView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void attachConstructionProtocolView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void attachDataAnalysisView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void attachProbabilityCalculatorView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void attachPropertiesView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void attachSpreadsheetView() {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean checkAutoCreateSliders(String string) {
	// TODO #3490
	return true;
    }

    @Override
    public void clearAbsolutePanels() {
	// TODO Auto-generated method stub

    }

    @Override
    public void clearInputbar() {
	// TODO Auto-generated method stub

    }

    @Override
    public Object createFrame() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void detachAlgebraView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void detachAssignmentView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void detachCasView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void detachConstructionProtocolView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void detachDataAnalysisView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void detachProbabilityCalculatorView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void detachPropertiesView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void detachSpreadsheetView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void exitAll() {
	// TODO Auto-generated method stub

    }

    @Override
    public EuclidianView getActiveEuclidianView() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getActiveToolbarId() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public GTextComponent getAlgebraInputTextField() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public View getAlgebraView() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void getAlgebraViewXML(StringBuilder sb, boolean asPreference) {
	// TODO Auto-generated method stub

    }

    @Override
    protected App getApp() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public View getCasView() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public View getConstructionProtocolData() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ConstructionProtocolView getConstructionProtocolView() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public View getDataAnalysisView() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public DialogManager getDialogManager() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public View getEuclidianView2() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Object getInputHelpPanel() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getInputHelpPanelMinimumWidth() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public Layout getLayout() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public View getPlotPanelView(int id) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public View getProbabilityCalculator() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public View getPropertiesView() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public View getSpreadsheetView() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean hasAlgebraViewShowing() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean hasDataAnalysisView() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean hasEuclidianView2() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean hasEuclidianView2EitherShowingOrNot() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean hasPropertiesView() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean hasSpreadsheetView() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void initialize() {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean isInputFieldSelectionListener() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    protected boolean loadFromApplet(String url) throws Exception {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void loadImage(GeoPoint loc, Object object, boolean altDown) {
	// TODO Auto-generated method stub

    }

    @Override
    protected boolean loadURL_base64(String url) throws Exception {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    protected boolean loadURL_GGB(String url) throws Exception {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void mousePressedForPropertiesView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void mouseReleasedForPropertiesView(boolean creatorMode) {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean noMenusOpen() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void openFile() {
	// TODO Auto-generated method stub

    }

    @Override
    protected void openHelp(String internalCmd, Help command) {
	// TODO Auto-generated method stub

    }

    @Override
    public void openURL() {
	// TODO Auto-generated method stub

    }

    @Override
    public void redo() {
	// TODO Auto-generated method stub

    }

    @Override
    public void resetSpreadsheet() {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean save() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean saveCurrentFile() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void setFocusedPanel(AbstractEvent event, boolean updatePropertiesView) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setLabels() {
	// TODO Auto-generated method stub

    }

    @Override
    public void setLayout(Layout layout) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setMode(int mode, ModeSetter m) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setScrollToShow(boolean b) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setShowAuxiliaryObjects(boolean flag) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setShowToolBarHelp(boolean showToolBarHelp) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setShowView(boolean b, int viewSpreadsheet) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setShowView(boolean b, int viewSpreadsheet, boolean isPermanent) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showDrawingPadPopup(EuclidianViewInterfaceCommon view, GPoint mouseLoc) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showGraphicExport() {
	// TODO Auto-generated method stub

    }

    @Override
    public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos, ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view, GPoint p) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showPopupMenu(ArrayList<GeoElement> selectedGeos, EuclidianViewInterfaceCommon euclidianViewInterfaceCommon, GPoint mouseLoc) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showPropertiesViewSliderTab() {
	// TODO Auto-generated method stub

    }

    @Override
    public void showPSTricksExport() {
	// TODO Auto-generated method stub

    }

    @Override
    public void showURLinBrowser(String strURL) {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean showView(int viewSpreadsheet) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void showWebpageExport() {
	// TODO Auto-generated method stub

    }

    @Override
    public void startEditing(GeoElement geoElement) {
	// TODO Auto-generated method stub

    }

    @Override
    public void undo() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateAlgebraInput() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateFonts() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateFrameSize() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateFrameTitle() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateGUIafterLoadFile(boolean success, boolean isMacroFile) {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateMenubarSelection() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateMenuFile() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateMenuWindow() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updatePropertiesView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateSpreadsheetColumnWidths() {
	// TODO Auto-generated method stub

    }

    @Override
    public void updateToolbar() {
	// TODO Auto-generated method stub

    }

}
