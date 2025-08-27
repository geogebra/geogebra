package org.geogebra.common.main;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.gui.Editing;
import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.gui.view.spreadsheet.MyTableInterface;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.AsyncOperation;

class GuiManagerMock extends GuiManager {

	private boolean hasAV = false;
	private boolean ev2 = false;
	private boolean sv = false;
	private boolean cv = false;
	private boolean cp = false;
	private boolean da = false;

	/**
	 * Abstract constructor
	 * @param app application
	 */
	public GuiManagerMock(App app) {
		super(app);
	}

	public GuiManagerMock withAlgebraView() {
		hasAV = true;
		return this;
	}

	public GuiManagerMock withSpreadsheetView() {
		sv = true;
		return this;
	}

	/**
	 * Activates the CAS view.
	 * @return this {@link GuiManagerMock} for call chaining.
	 */
	public GuiManagerMock withCasView() {
		cv = true;
		return this;
	}

	/**
	 * Activates the Construction Protocol view.
	 * @return this {@link GuiManagerMock} for call chaining.
	 */
	public GuiManagerMock withConstructionProtocol() {
		cp = true;
		return this;
	}

	/**
	 * Activates the Data Analysis view.
	 * @return this {@link GuiManagerMock} for call chaining.
	 */
	public GuiManagerMock withDataAnalysisView() {
		da = true;
		return this;
	}

	@Override
	public boolean hasAlgebraView() {
		return hasAV;
	}

	@Override
	public boolean hasCasView() {
		return cv;
	}

	@Override
	public boolean hasDataAnalysisView() {
		return da;
	}

	@Override
	public boolean hasSpreadsheetView() {
		return sv;
	}

	@Override
	public boolean isUsingConstructionProtocol() {
		return cp;
	}

	@Override
	protected ConstructionProtocolNavigation newConstructionProtocolNavigation(
			int viewID) {
		return null;
	}

	@Override
	public void updateCheckBoxesForShowConstructionProtocolNavigation(int id) {

	}

	@Override
	public String getToolbarDefinition() {
		return "";
	}

	@Override
	public void setToolBarDefinition(String toolBarDefinition) {

	}

	@Override
	public void updateMenubarSelection() {

	}

	@Override
	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon view, GPoint mouseLoc) {

	}

	@Override
	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view,
			GPoint p) {

	}

	@Override
	public boolean save() {
		return false;
	}

	@Override
	public void setFocusedPanel(AbstractEvent event, boolean updatePropertiesView) {

	}

	@Override
	public void loadImage(GeoPoint loc, Object object, boolean fromClipboard,
			EuclidianView view) {

	}

	@Override
	public void loadWebcam() {

	}

	@Override
	public boolean hasAlgebraViewShowing() {
		return true;
	}

	@Override
	public void updateFonts() {

	}

	@Override
	public void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
			GPoint mouseLoc) {

	}

	@Override
	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view,
			GPoint mouseLoc) {

	}

	@Override
	public void attachSpreadsheetView() {

	}

	@Override
	public void setShowView(boolean visible, int viewID) {

	}

	@Override
	public void setShowView(boolean visible, int viewID, boolean isPermanent) {

	}

	@Override
	public boolean showView(int viewID) {
		return false;
	}

	@Override
	public Editing getCasView() {
		return null;
	}

	@Override
	public SpreadsheetViewInterface getSpreadsheetView() {
		return new SpreadsheetViewInterface() {
			@Override
			public MyTableInterface getSpreadsheetTable() {
				return null;
			}

			@Override
			public void rowHeaderRevalidate() {

			}

			@Override
			public void updateCellFormat(String cellFormat) {

			}

			@Override
			public App getApplication() {
				return null;
			}

			@Override
			public void scrollIfNeeded(GeoElement geo, String labelNew) {

			}

			@Override
			public void showTraceDialog(GeoElement geo, TabularRange traceCell) {

			}

			@Override
			public void setKeyboardEnabled(boolean enable) {

			}

			@Override
			public boolean isShowing() {
				return sv;
			}

			@Override
			public void add(GeoElement geo) {

			}

			@Override
			public void remove(GeoElement geo) {

			}

			@Override
			public void rename(GeoElement geo) {

			}

			@Override
			public void update(GeoElement geo) {

			}

			@Override
			public void updateVisualStyle(GeoElement geo, GProperty prop) {

			}

			@Override
			public void updateHighlight(GeoElementND geo) {

			}

			@Override
			public void updateAuxiliaryObject(GeoElement geo) {

			}

			@Override
			public void repaintView() {

			}

			@Override
			public boolean suggestRepaint() {
				return false;
			}

			@Override
			public void reset() {

			}

			@Override
			public void clearView() {

			}

			@Override
			public void setMode(int mode, ModeSetter m) {

			}

			@Override
			public int getViewID() {
				return 0;
			}

			@Override
			public boolean hasFocus() {
				return false;
			}

			@Override
			public void startBatchUpdate() {

			}

			@Override
			public void endBatchUpdate() {

			}

			@Override
			public void updatePreviewFromInputBar(GeoElement[] geos) {

			}
		};
	}

	@Override
	public View getProbabilityCalculator() {
		return null;
	}

	@Override
	public View getDataAnalysisView() {
		return null;
	}

	@Override
	public View getPropertiesView() {
		return null;
	}

	@Override
	public void getAlgebraViewXML(StringBuilder sb, boolean asPreference) {

	}

	@Override
	public void updateSpreadsheetColumnWidths() {

	}

	@Override
	public void updateAlgebraInput() {

	}

	@Override
	public void setShowAuxiliaryObjects(boolean flag) {

	}

	@Override
	public void updatePropertiesView() {

	}

	@Override
	public void mousePressedForPropertiesView() {

	}

	@Override
	public void mouseReleasedForPropertiesView(boolean creatorMode) {

	}

	@Override
	public void updateGUIafterLoadFile(boolean success, boolean isMacroFile) {

	}

	@Override
	public void startEditing(GeoElement geoElement) {

	}

	@Override
	public boolean noMenusOpen() {
		return false;
	}

	@Override
	public void openFile() {

	}

	@Override
	public Layout getLayout() {
		return null;
	}

	@Override
	public void showGraphicExport() {

	}

	@Override
	public void showPSTricksExport() {

	}

	@Override
	public void showWebpageExport() {

	}

	@Override
	public void detachPropertiesView() {

	}

	@Override
	public boolean hasPropertiesView() {
		return false;
	}

	@Override
	public void attachPropertiesView() {

	}

	@Override
	public void attachAlgebraView() {

	}

	@Override
	public void attachCasView() {

	}

	@Override
	public void attachConstructionProtocolView() {

	}

	@Override
	public void attachProbabilityCalculatorView() {

	}

	@Override
	public void attachDataAnalysisView() {

	}

	@Override
	public void detachDataAnalysisView() {

	}

	@Override
	public EuclidianView getActiveEuclidianView() {
		return null;
	}

	@Override
	public void detachProbabilityCalculatorView() {

	}

	@Override
	public void detachCasView() {

	}

	@Override
	public void detachConstructionProtocolView() {

	}

	@Override
	public void detachSpreadsheetView() {

	}

	@Override
	public void detachAlgebraView() {

	}

	@Override
	public void setLayout(Layout layout) {

	}

	@Override
	public void initialize() {

	}

	@Override
	public void resetSpreadsheet() {

	}

	@Override
	public void setScrollToShow(boolean scrollToShow) {

	}

	@Override
	public void updateToolbar() {

	}

	@Override
	public boolean hasEuclidianView2(int idx) {
		return ev2;
	}

	@Override
	public EuclidianViewInterfaceCommon getEuclidianView2(int idx) {
		return null;
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		return false;
	}

	@Override
	public Editing getAlgebraView() {
		return new Editing() {
			@Override
			public void cancelEditItem() {

			}

			@Override
			public void resetItems(boolean unselectAll) {

			}

			@Override
			public boolean isShowing() {
				// we assume if it has av then it is showing by simplicity.
				return hasAV;
			}

			@Override
			public void add(GeoElement geo) {

			}

			@Override
			public void remove(GeoElement geo) {

			}

			@Override
			public void rename(GeoElement geo) {

			}

			@Override
			public void update(GeoElement geo) {

			}

			@Override
			public void updateVisualStyle(GeoElement geo, GProperty prop) {

			}

			@Override
			public void updateHighlight(GeoElementND geo) {

			}

			@Override
			public void updateAuxiliaryObject(GeoElement geo) {

			}

			@Override
			public void repaintView() {

			}

			@Override
			public boolean suggestRepaint() {
				return false;
			}

			@Override
			public void reset() {

			}

			@Override
			public void clearView() {

			}

			@Override
			public void setMode(int mode, ModeSetter m) {

			}

			@Override
			public int getViewID() {
				return 0;
			}

			@Override
			public boolean hasFocus() {
				return false;
			}

			@Override
			public void startBatchUpdate() {

			}

			@Override
			public void endBatchUpdate() {

			}

			@Override
			public void updatePreviewFromInputBar(GeoElement[] geos) {

			}
		} ;
	}

	@Override
	public void applyAlgebraViewSettings() {

	}

	@Override
	public void updateFrameSize() {

	}

	@Override
	public int getActiveToolbarId() {
		return 0;
	}

	@Override
	public ConstructionProtocolView getConstructionProtocolView() {
		return null;
	}

	@Override
	public boolean checkAutoCreateSliders(String string,
			AsyncOperation<String[]> callback) {
		return false;
	}

	@Override
	public void logout() {

	}

	@Override
	public int getEuclidianViewCount() {
		return 0;
	}

	@Override
	public void addToToolbarDefinition(int mode) {

	}

	@Override
	public void updatePropertiesViewStylebar() {

	}

	@Override
	public void getToolImageURL(int mode, GeoImage geoImage,
			AsyncOperation<String> onload) {

	}

	@Override
	public EuclidianViewInterfaceCommon getPlotPanelEuclidianView() {
		return null;
	}

	@Override
	public void openMenuInAVFor(GeoElement geo) {

	}

}
