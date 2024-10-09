package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.Editing;
import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.InputKeyboardButton;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.cas.view.CASTableW;
import org.geogebra.web.full.cas.view.CASViewW;
import org.geogebra.web.full.cas.view.RowHeaderPopupMenuW;
import org.geogebra.web.full.euclidian.DynamicStyleBar;
import org.geogebra.web.full.euclidian.EuclidianStyleBarW;
import org.geogebra.web.full.euclidian.SymbolicEditorW;
import org.geogebra.web.full.gui.app.GGWMenuBar;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.dialog.options.OptionsTab.ColorPanel;
import org.geogebra.web.full.gui.dialog.template.TemplateChooserController;
import org.geogebra.web.full.gui.inputbar.AlgebraInputW;
import org.geogebra.web.full.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.gui.layout.panels.CASDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ConstructionProtocolDockPanelW;
import org.geogebra.web.full.gui.layout.panels.DataAnalysisViewDockPanelW;
import org.geogebra.web.full.gui.layout.panels.Euclidian2DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.full.gui.layout.panels.FunctionInspectorDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ProbabilityCalculatorDockPanelW;
import org.geogebra.web.full.gui.layout.panels.PropertiesDockPanelW;
import org.geogebra.web.full.gui.layout.panels.SpreadsheetDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.layout.scientific.ScientificSettingsView;
import org.geogebra.web.full.gui.menubar.FileMenuW;
import org.geogebra.web.full.gui.menubar.action.SaveExamAction;
import org.geogebra.web.full.gui.openfileview.OpenFileView;
import org.geogebra.web.full.gui.openfileview.OpenFileViewMebis;
import org.geogebra.web.full.gui.openfileview.OpenTemporaryFileView;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.toolbar.ToolBarW;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.full.gui.toolbarpanel.ShowableTab;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.util.DateTimeFormat;
import org.geogebra.web.full.gui.util.InputKeyboardButtonW;
import org.geogebra.web.full.gui.view.algebra.AlgebraControllerW;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolNavigationW;
import org.geogebra.web.full.gui.view.data.DataAnalysisViewW;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;
import org.geogebra.web.full.gui.view.probcalculator.TabbedProbCalcView;
import org.geogebra.web.full.gui.view.spreadsheet.MyTableW;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetContextMenuW;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetViewW;
import org.geogebra.web.full.html5.AttachedToDOM;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.FileConsumer;
import org.geogebra.web.html5.util.StringConsumer;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

import elemental2.core.JsDate;
import elemental2.dom.DomGlobal;
import elemental2.dom.URL;

public class GuiManagerW extends GuiManager
		implements GuiManagerInterfaceW, SetLabels {

	/**
	 * container for the Popup that only one exist for a given type
	 */
	public AttachedToDOM currentPopup;

	private AlgebraControllerW algebraController;
	private AlgebraViewW algebraView;
	private SpreadsheetViewW spreadsheetView;
	private final ArrayList<EuclidianViewW> euclidianView2 = new ArrayList<>();
	protected BrowseViewI browseGUI;
	protected LayoutW layout;
	private CASViewW casView;
	private Euclidian2DockPanelW euclidianView2DockPanel;
	private String strCustomToolbarDefinition;
	private boolean draggingViews;
	/** device: tablet / browser */
	protected final GDevice device;
	private int toolbarID = App.VIEW_EUCLIDIAN;
	private ConstructionProtocolView constructionProtocolView;
	private boolean oldDraggingViews;
	private String generalToolbarDefinition;
	private GGWToolBar toolbarPanel = null;
	private InputBarHelpPanelW inputHelpPanel;
	private AlgebraInputW algebraInput;
	private PropertiesView propertiesView;
	private DataAnalysisViewW dataAnalysisView = null;
	private boolean listeningToLogin = false;
	private ToolBarW toolbarForUpdate = null;
	private GeoGebraFrameFull frame;

	private ColorPanel colorPanel;

	private Localization loc;

	private GGWMenuBar mainMenuBar;

	private AnimatingPanel sciSettingsView;
	private TemplateChooserController templateController;

	private Runnable runAfterLogin;
	private InputKeyboardButtonW inputKeyboardButton = null;
	private static final ExamController examController = GlobalScope.examController;

	/**
	 *
	 * @param app
	 *            app
	 * @param device
	 *            device (browser / tablet)
	 */
	public GuiManagerW(final AppW app, GDevice device) {
		super(app);

		this.loc = app.getLocalization();
		this.device = device;
		frame = getApp().getAppletFrame();
	}

	@Override
	public void updateMenubarSelection() {
		if (mainMenuBar != null && mainMenuBar.getMenubar() != null) {
			mainMenuBar.getMenubar().updateSelection();
		}
	}

	@Override
	public void updateMenubar() {
		if (mainMenuBar != null && mainMenuBar.getMenubar() != null) {
			mainMenuBar.getMenubar().updateMenubar();
		}
	}

	@Override
	public void updateActions() {
		updateMenubarSelection();

		if (getApp().isWhiteboardActive()) {
			(getApp().getAppletFrame()).updateUndoRedoMOW();
			return;
		}

		if (getToolbarPanel() != null) {
			getToolbarPanel().updateUndoActions();
		}

		if (getUnbundledToolbar() != null) {
			getUnbundledToolbar().updateUndoRedoActions();
		}
	}

	@Override
	public void showPopupMenu(final ArrayList<GeoElement> selectedGeos,
			final EuclidianViewInterfaceCommon view, final GPoint mouseLoc) {
		showPopupMenu(selectedGeos,
				((EuclidianViewWInterface) view).getG2P().getElement(),
				mouseLoc);
	}

	private void showPopupMenu(final ArrayList<GeoElement> geos,
			final Element invoker, final GPoint p) {
		if (geos == null || !getApp().letShowPopupMenu()) {
			return;
		}
		if (!geos.isEmpty() && hasNoContextMenu(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			getApp().getActiveEuclidianView().resetMode();
			getPopupMenu(geos).showScaled(invoker, p.x, p.y);
		}
	}

	private boolean hasNoContextMenu(GeoElement geoElement) {
		return getApp().getKernel().isAxis(geoElement) || geoElement.isSpotlight();
	}

	@Override
	public void showPopupMenu(final ArrayList<GeoElement> geos,
			final Widget invoker, final int x, int y) {
		// clear highlighting and selections in views
		getApp().getActiveEuclidianView().resetMode();
		getPopupMenu(geos).showScaled(invoker.getElement(), x, y);
	}

	/**
	 * @param mt
	 *            spreadsheet table
	 * @return spreadsheet context menu
	 */
	public SpreadsheetContextMenuW getSpreadsheetContextMenu(final MyTableW mt) {
		removePopup();
		final SpreadsheetContextMenuW contextMenu = new SpreadsheetContextMenuW(
				mt);
		currentPopup = contextMenu.getMenuContainer();
		return contextMenu;
	}

	/**
	 * @param table
	 *            CAS table
	 * @return context menu for CAS
	 */
	public RowHeaderPopupMenuW getCASContextMenu(final CASTableW table) {
		removePopup();
		currentPopup = new RowHeaderPopupMenuW(table, getApp());
		return (RowHeaderPopupMenuW) currentPopup;
	}

	/**
	 * @param geos
	 *            selected geos
	 * @return context popup menu
	 */
	public ContextMenuGeoElementW getPopupMenu(
			final ArrayList<GeoElement> geos) {
		removePopup();
		currentPopup = new ContextMenuGeoElementW(getApp(), geos,
				new ContextMenuFactory());
		((ContextMenuGeoElementW) currentPopup).addOtherItems();
		return (ContextMenuGeoElementW) currentPopup;
	}

	@Override
	public void showPopupChooseGeo(final ArrayList<GeoElement> selectedGeos,
			final ArrayList<GeoElement> geos,
			final EuclidianViewInterfaceCommon view, final GPoint p) {
		showPopupChooseGeo(selectedGeos, geos, (EuclidianView) view, p);
	}

	private void showPopupChooseGeo(final ArrayList<GeoElement> selectedGeos,
			final ArrayList<GeoElement> geos, final EuclidianView view,
			final GPoint p) {

		if (geos == null || !getApp().letShowPopupMenu()) {
			return;
		}

		if (!geos.isEmpty() && getApp().getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(view, p);
		} else {

			final Element invoker = ((EuclidianViewWInterface) view)
					.getCanvasElement();
			// clear highlighting and selections in views
			getApp().getActiveEuclidianView().resetMode();
			ContextMenuGeoElementW menu = getPopupMenu(view, selectedGeos,
					geos, p);
			menu.showScaled(invoker, p.x, p.y);
		}
	}

	private ContextMenuGeoElementW getPopupMenu(
			final EuclidianView view, final ArrayList<GeoElement> selectedGeos,
			final ArrayList<GeoElement> geos, final GPoint p) {
		currentPopup = new ContextMenuChooseGeoW(getApp(), view,
				selectedGeos, geos, p, new ContextMenuFactory());
		return (ContextMenuGeoElementW) currentPopup;
	}

	@Override
	public void setFocusedPanel(final AbstractEvent event,
			final boolean updatePropertiesView) {

		if (event instanceof PointerEvent) {
			setFocusedPanel(((PointerEvent) event).getEvID(),
					updatePropertiesView);
		}
	}

	@Override
	public void setFocusedPanel(final int evID,
			final boolean updatePropertiesView) {

		if (!(getApp().getEuclidianViewpanel() instanceof DockPanel)) {
			Log.debug("This part of the code should not have run!");
			return;
		}

		switch (evID) {
		case App.VIEW_EUCLIDIAN:
			setFocusedPanel((DockPanel) getApp().getEuclidianViewpanel(),
					updatePropertiesView);
			break;
		case App.VIEW_EUCLIDIAN2:
			setFocusedPanel(getEuclidianView2DockPanel(1), updatePropertiesView);
			break;
		case App.VIEW_EUCLIDIAN3D:
			setFocusedPanel(getEuclidian3DPanel(), updatePropertiesView);
			break;
		default:
			if (evID >= App.VIEW_EUCLIDIAN_FOR_PLANE_START
			&& evID <= App.VIEW_EUCLIDIAN_FOR_PLANE_END) {
				setFocusedPanel(getLayout().getDockManager().getPanel(evID),
						updatePropertiesView);
			}
			break;
		}
	}

	/**
	 * Notify dock manager about focused panel.
	 *
	 * @param panel
	 *            focused panel
	 * @param updatePropertiesView
	 *            whether to switch tab in properties view
	 */
	public void setFocusedPanel(final DockPanel panel,
			final boolean updatePropertiesView) {
		if (panel != null) {
			getLayout().getDockManager().setFocusedPanel(panel,
					updatePropertiesView);

			// notify the properties view
			if (updatePropertiesView) {
				updatePropertiesView();
			}
		}
	}

	@Override
	public void loadImage(final GeoPoint imageLoc, final Object object,
			final boolean altDown, EuclidianView ev) {
		if (getApp().getToolbar() != null) {
			getApp().getToolbar().closeAllSubmenu();
		}

		((DialogManagerW) app.getDialogManager()).showImageInputDialog(imageLoc,
				this.device);
	}

	@Override
	public void loadWebcam() {
		if (getApp().getToolbar() != null) {
			getApp().getToolbar().closeAllSubmenu();
		}
		DialogManagerW dialogManager = (DialogManagerW) app.getDialogManager();
		if (NavigatorUtil.isiOS()) {
			dialogManager.showImageInputDialog(null, device);
		} else {
			dialogManager.showWebcamInputDialog();
		}
	}

	/**
	 * It sometimes happens that a file changes the font size of GUI. The GUI is
	 * not ready for this in Web.
	 */
	@Override
	public void updateFonts() {
		if (hasCasView()) {
			((CASViewW) getCasView()).updateFonts();
		}
	}

	@Override
	public void showDrawingPadPopup(final EuclidianViewInterfaceCommon view,
			final GPoint mouseLoc) {
		showDrawingPadPopup(((EuclidianViewW) view).getG2P().getElement(),
				mouseLoc);
	}

	@Override
	public void showDrawingPadPopup3D(final EuclidianViewInterfaceCommon view,
			GPoint mouseLoc) {
		// 3D stuff
	}

	private void showDrawingPadPopup(final Element invoker, final GPoint p) {
		// clear highlighting and selections in views
		getApp().getActiveEuclidianView().resetMode();
		getDrawingPadpopupMenu(p.x, p.y).showScaled(invoker, p.x, p.y);
	}

	private ContextMenuGeoElementW getDrawingPadpopupMenu(final int x,
			final int y) {
		currentPopup = new ContextMenuGraphicsWindowW(getApp(), x, y, true);
		return (ContextMenuGeoElementW) currentPopup;
	}

	@Override
	public boolean hasSpreadsheetView() {
		return spreadsheetView != null && spreadsheetView.isShowing();
	}

	@Override
	public void attachSpreadsheetView() {
		getSpreadsheetView();
		spreadsheetView.attachView();
	}

	@Override
	public void setShowView(final boolean visible, final int viewId) {
		setShowView(visible, viewId, true);
	}

	@Override
	public void setShowView(final boolean visible, final int viewId, final boolean isPermanent) {
		ToolbarPanel sidePanel = getUnbundledToolbar();
		ShowableTab sidePanelTab = sidePanel != null ? sidePanel.getTab(viewId) : null;
		if (sidePanelTab != null) {
			if (visible) {
				sidePanelTab.open();
			} else {
				sidePanelTab.close();
			}
			onToolbarVisibilityChanged(viewId, visible);
		} else {
			if (visible) {
				showViewWithId(viewId);
			} else {
				hideViewWith(viewId, isPermanent);
			}
			getApp().dispatchEvent(new Event(EventType.PERSPECTIVE_CHANGE));
		}

		getApp().updateVoiceover();
		getApp().closePopups();

		if (sidePanel != null) {
			sidePanel.updateUndoRedoPosition();
		}
	}

	@Override
	public void onToolbarVisibilityChanged(int viewId, boolean isVisible) {
		DockPanel panel = layout.getDockManager().getPanel(viewId);
		if (panel != null) {
			panel.setVisible(isVisible);
		}
	}

	private void showViewWithId(int viewId) {
		if (!showView(viewId)) {
			layout.getDockManager().show(viewId);
		}

		if (viewId == App.VIEW_SPREADSHEET) {
			getSpreadsheetView().requestFocus();
		}
		if (viewId == App.VIEW_DATA_ANALYSIS) {
			getSpreadsheetView().requestFocus();
		}
	}

	private void hideViewWith(int viewId, boolean isPermanent) {
		if (showView(viewId)) {
			layout.getDockManager().hide(viewId, isPermanent);
		}

		if (viewId == App.VIEW_SPREADSHEET) {
			(getApp()).getActiveEuclidianView().requestFocus();
		}
	}

	@Override
	public boolean showView(final int viewId) {
		try {
			return layout.getDockManager().getPanel(viewId).isVisible();
		} catch (final Exception e) {
			Log.debug(e);
			return false;
		}
	}

	@Override
	public Editing getCasView() {
		if (casView == null) {
			casView = new CASViewW(getApp());
		}
		return casView;
	}

	@Override
	public boolean hasCasView() {
		return casView != null;
	}

	@Override
	public void showSciSettingsView() {
		if (sciSettingsView == null) {
			sciSettingsView = new ScientificSettingsView(getApp());
			getApp().getLocalization().registerLocalizedUI(sciSettingsView);
		}
		frame.forceHeaderHidden(true);
		getApp().setCloseBrowserCallback(() -> frame.forceHeaderHidden(false));
		frame.showPanel(sciSettingsView);
	}

	@Override
	public SpreadsheetViewW getSpreadsheetView() {
		// init spreadsheet view
		if (spreadsheetView == null) {
			spreadsheetView = new SpreadsheetViewW(getApp());
		}

		return spreadsheetView;
	}

	@Override
	public View getProbabilityCalculator() {
		if (probCalculator == null) {
			setProbCalculator(app.isSuite() ? ProbabilityCalculatorViewW.create(getApp())
					: new TabbedProbCalcView(getApp()));
		}

		return probCalculator;
	}

	/**
	 * @return whether it has probability calculator or not
	 */
	@Override
	public boolean hasProbabilityCalculator() {
		return probCalculator != null;
	}

	public boolean hasPlotPanelEuclidianView() {
		return hasProbabilityCalculator();
	}

	@Override
	public void updateSpreadsheetColumnWidths() {
		// unimplemented in web
	}

	@Override
	public void resize(final int width, final int height) {
		final Element geogebraFrame = getApp().getFrameElement();
		int borderThickness = getApp().getAppletParameters()
				.getBorderThickness();
		if (getLayout() != null && getLayout().getRootComponent() != null) {
			if (geogebraFrame.getOffsetHeight() <= 0) {
				return; // not in DOM yet => no reliable size
			}
			final DockSplitPaneW root = getLayout().getRootComponent();
			int verticalSpace = borderThickness + getApp().getToolbarAndInputBarHeight();
			int horizontalSpace = borderThickness;
			if (mainMenuBar != null) {
				horizontalSpace += mainMenuBar.getOffsetWidth();
			}
			root.setPixelSize(width - horizontalSpace, height - verticalSpace);
			root.onResize();
		} else {
			geogebraFrame.getStyle().setHeight(height, Unit.PX);
			geogebraFrame.getStyle().setWidth(width, Unit.PX);
			getApp().getEuclidianViewpanel().setPixelSize(width, height);

			// maybe onResize is OK too
			getApp().getEuclidianViewpanel().deferredOnResize();
		}
		if (this.algebraInput != null) {
			this.algebraInput.setWidth((width - borderThickness) + "px");
			this.algebraInput.setInputFieldWidth(width - borderThickness);
		}
		if (this.toolbarPanel != null) {
			toolbarPanel.setToolbarWidth(width);
		}
		getApp().updateMenuHeight();
		// NB updateViewSizes not needed after root.onResize
		getApp().recalculateEnvironments();
		getApp().setPreferredSize(
				AwtFactory.getPrototype().newDimension(width, height));
		Scheduler.get().scheduleDeferred(() -> {
			getApp().centerAndResizeViews();
			getApp().getKeyboardManager().resizeKeyboard();
		});
	}

	private ToolBarW getGeneralToolbar() {
		return toolbarPanel.getToolBar();
	}

	@Override
	public String getToolbarDefinition() {
		if (strCustomToolbarDefinition == null && getToolbarPanel() != null) {
			return getGeneralToolbar().getDefaultToolbarString();
		}
		// return geogebra.web.gui.toolbar.ToolBarW.getAllTools(app);
		return strCustomToolbarDefinition;
	}

	@Override
	public int getMoveTopBelowSnackbar(int snackbarRight) {
		if (getUnbundledToolbar() != null) {
			return getUnbundledToolbar().getMoveTopBelowSnackbar(snackbarRight);
		}
		return 0;
	}

	@Override
	public String getToolbarDefinition(final Integer viewId) {
		if (viewId == App.VIEW_CAS) {
			return CASView.TOOLBAR_DEFINITION;
		}
		return getToolbarDefinition();
	}

	@Override
	public void removeFromToolbarDefinition(final int mode) {
		if (strCustomToolbarDefinition != null) {
			strCustomToolbarDefinition = strCustomToolbarDefinition.replaceAll(
					Integer.toString(mode), "");

			if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
				// if a macro mode is removed all higher macros get a new id
				// (i.e. id-1)
				final int lastID = kernel.getMacroNumber()
						+ EuclidianConstants.MACRO_MODE_ID_OFFSET - 1;
				for (int id = mode + 1; id <= lastID; id++) {
					strCustomToolbarDefinition = strCustomToolbarDefinition
							.replaceAll(Integer.toString(id),
									Integer.toString(id - 1));
				}
			}
		}
	}

	@Override
	public void addToToolbarDefinition(final int mode) {
		if (this.getActiveEuclidianView().getDimension() > 2) {
			DockPanelW panel = this.getLayout().getDockManager()
					.getPanel(this.getActiveEuclidianView().getViewID());
			panel.addToToolbar(mode);
			panel.updateToolbar();

			return;
		}

		if (generalToolbarDefinition != null) {
			generalToolbarDefinition = ToolBar.addMode(
					generalToolbarDefinition, mode);
		}
		strCustomToolbarDefinition = generalToolbarDefinition;
	}

	@Override
	public final String getCustomToolbarDefinition() {
		return strCustomToolbarDefinition;
	}

	/**
	 * Initializes GuiManager for web
	 */
	@Override
	public void initialize() {
		initAlgebraController(); // ? needed for keyboard input in EuclidianView
		// in Desktop
		layout.initialize(getApp());
		initLayoutPanels();
	}

	/**
	 * Register panels for the layout manager.
	 *
	 * @return whether it was successful
	 */
	protected boolean initLayoutPanels() {

		// register euclidian view
		// this is done earlier
		if (getApp().getEuclidianViewpanel() instanceof DockPanelW) {
			layout.registerPanel((DockPanelW) getApp()
					.getEuclidianViewpanel());
		} else {
			Log.debug("This part of the code should not have been called!");
			return false;
		}

		// register spreadsheet view
		layout.registerPanel(new SpreadsheetDockPanelW(getApp()));

		layout.getDockManager().swapAlgebraPanel();

		// register CAS view
		layout.registerPanel(new CASDockPanelW(getApp()));

		// register EuclidianView2
		layout.registerPanel(getEuclidianView2DockPanel(1));

		// register ConstructionProtocol view
		layout.registerPanel(new ConstructionProtocolDockPanelW(getApp()));

		// register ProbabilityCalculator view
		layout.registerPanel(new ProbabilityCalculatorDockPanelW(getApp()));

		// register FunctionInspector view
		layout.registerPanel(new FunctionInspectorDockPanelW(getApp()));

		// register Properties view

		layout.registerPanel(new PropertiesDockPanelW(getApp()));

		// register data analysis view
		layout.registerPanel(new DataAnalysisViewDockPanelW(getApp()));

		return true;
	}

	/**
	 * Reset dock panels for current app.
	 */
	public void resetPanels() {
		layout.getDockManager().swapAlgebraPanel();
		layout.getDockManager().reset();
	}

	@Override
	public void setLayout(final Layout layout) {
		this.layout = (LayoutW) layout;
	}

	@Override
	public LayoutW getLayout() {
		return layout;
	}

	/**
	 * Get the toolbar, never null (lazy loading).
	 *
	 * @return the toolbar
	 */
	public GGWToolBar getToolbarPanel() {
		if (toolbarPanel == null) {
			toolbarPanel = (GGWToolBar) getApp().getToolbar();
			if (toolbarPanel != null && !toolbarPanel.isInited()) {
				toolbarPanel.init(getApp());
			}
		}

		return toolbarPanel;
	}

	@Override
	public void updateToolbar() {
		if (layout != null) {
			if (getToolbarPanel() != null) {
				getToolbarPanel().updateToolbarPanel();
			}
		}
	}

	@Override
	public void updateUnbundledToolbarContent() {
		if (getUnbundledToolbar() != null) {
			getUnbundledToolbar().updateContent();
		}
	}

	@Override
	public void updateToolbarActions() {
		if (getToolbarPanel() != null) {
			getToolbarPanel().updateActionPanel();
		}
	}

	@Override
	public void updateAlgebraInput() {
		if (algebraInput != null) {
			algebraInput.initGUI();
		}
	}

	/**
	 * @return whether input help panel is initialized
	 */
	public boolean hasInputHelpPanel() {
		return inputHelpPanel != null;
	}

	@Override
	public InputBarHelpPanelW getInputHelpPanel() {
		if (inputHelpPanel == null) {
			inputHelpPanel = new InputBarHelpPanelW(getApp());
		}
		return inputHelpPanel;
	}

	/**
	 * Update help panel if exists
	 *
	 * @param forCAS
	 *            whether to include CAS commands
	 */
	public void reInitHelpPanel(boolean forCAS) {
		if (inputHelpPanel != null) {
			if (forCAS) {
				getApp().getCommandDictionaryCAS();
			}
			inputHelpPanel.setLabels();
		}
	}

	@Override
	public void setShowAuxiliaryObjects(final boolean flag) {
		if (!hasAlgebraViewShowing()) {
			return;
		}
		getAlgebraView();
		algebraView.setShowAuxiliaryObjects(flag);
		getApp().getSettings().getAlgebra().setShowAuxiliaryObjects(flag);
	}

	@Override
	public AlgebraViewW getAlgebraView() {
		if (algebraView == null) {
			initAlgebraController();
			algebraView = new AlgebraViewW(algebraController);
		}

		return algebraView;
	}

	/**
	 * Make sure algebra controller exists
	 */
	protected void initAlgebraController() {
		if (algebraController == null) {
			algebraController = new AlgebraControllerW(getApp().getKernel());
		}
	}

	@Override
	public void attachAlgebraView() {
		getAlgebraView();
		algebraView.attachView();
	}

	@Override
	public void detachAlgebraView() {
		if (algebraView != null) {
			algebraView.detachView();
		}
	}

	@Override
	public void applyAlgebraViewSettings() {
		if (algebraView != null) {
			algebraView.applySettings();
		}
	}

	@Override
	public View getPropertiesView() {
		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = newPropertiesViewW(getApp(),
					OptionType.EUCLIDIAN);
		}

		return propertiesView;
	}

	/**
	 * Get the properties view and initilize the right tab.
	 *
	 * @param ot
	 *            initial tab
	 * @return properties view
	 */
	public PropertiesView getPropertiesView(OptionType ot) {
		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = newPropertiesViewW(getApp(), ot);
		}
		return propertiesView;
	}

	/**
	 *
	 * @param app1
	 *            application
	 * @param ot
	 *            option type
	 * @return new properties view
	 */
	protected PropertiesViewW newPropertiesViewW(final AppW app1,
			OptionType ot) {
		return new PropertiesViewW(app1, ot);
	}

	@Override
	public void updatePropertiesView() {
		if (propertiesView != null) {
			propertiesView.updatePropertiesView();
		}
	}

	@Override
	public void updatePropertiesViewStylebar() {
		if (propertiesView != null) {
			propertiesView.updateStyleBar();
		}
	}

	/**
	 * Close properties view.
	 */
	public void closePropertiesView() {
		if (propertiesView != null) {
			((PropertiesViewW) propertiesView).close();
		}
	}

	@Override
	public void mousePressedForPropertiesView() {
		if (propertiesView != null) {
			propertiesView.mousePressedForPropertiesView();
		}
	}

	@Override
	public void mouseReleasedForPropertiesView(final boolean creatorMode) {
		if (propertiesView != null) {
			propertiesView.mouseReleasedForPropertiesView(creatorMode);
		}
	}

	@Override
	public void addAlgebraInput(final AlgebraInput ai) {
		this.algebraInput = (AlgebraInputW) ai;
	}

	@Override
	public AlgebraInputW getAlgebraInput() {
		return algebraInput;
	}

	@Override
	public void listenToLogin(Runnable onSuccess) {
		runAfterLogin = onSuccess;
		if (listeningToLogin) {
			return;
		}
		listeningToLogin = true;
		getApp().getLoginOperation().getView().add(this::uploadCallback);
	}

	@Override
	public boolean save() {
		if (!examController.isIdle()) {
			SaveExamAction.showExamSaveDialog(getApp());
		} else {
			getApp().getFileManager().save(getApp());
		}
		return true;
	}

	@Override
	public void updateGUIafterLoadFile(final boolean success,
			final boolean isMacroFile) {
		if (success && !isMacroFile
				&& !getApp().getSettings().getLayout().isIgnoringDocumentLayout()) {

			getLayout().setPerspectiveOrDefault(getApp().getTmpPerspective());

			if (!getApp().isIniting()) {
				updateFrameSize(); // checks internally if frame is available
				if (getApp().needsSpreadsheetTableModel()) {
					getApp().getSpreadsheetTableModel(); // ensure create one if
					// not already done
				}
			}
		} else if (isMacroFile && success) {

			refreshCustomToolsInToolBar();
			updateToolbar();
			getApp().updateContentPane();

		}

		// #5320
		getApp().getEuclidianView1().updateFonts();
		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).updateFonts();
		}

		// force JavaScript ggbOnInit(); to be called
		if (!getApp().isApplet()) {
			getApp().getScriptManager().ggbOnInit();
		}
	}

	@Override
	public void startEditing(final GeoElement geoElement) {
		switchToolsToAV();
		if (this.algebraView != null) {
			algebraView.startEditItem(geoElement);
		}
	}

	@Override
	public boolean noMenusOpen() {
		// unimplemented
		return true;
	}

	@Override
	public void openFile() {
		if (examController.isIdle()) {
			getApp().openSearch("");
		}
	}

	@Override
	public void showGraphicExport() {
		// unimplemented
	}

	@Override
	public void showPSTricksExport() {
		// unimplemented
	}

	@Override
	public void showWebpageExport() {
		// unimplemented
	}

	@Override
	public void detachPropertiesView() {
		if (propertiesView != null) {
			propertiesView.detachView();
		}
	}

	@Override
	public boolean hasPropertiesView() {
		return propertiesView != null;
	}

	public PropertiesView getCurrentPropertiesView() {
		return propertiesView;
	}

	@Override
	public void attachPropertiesView() {
		getPropertiesView();
		propertiesView.attachView();
	}

	@Override
	public void attachCasView() {
		getCasView();
		casView.attachView();
	}

	@Override
	public void attachConstructionProtocolView() {
		getConstructionProtocolView();
		constructionProtocolView.getData().attachView();
	}

	@Override
	public void attachProbabilityCalculatorView() {
		getProbabilityCalculator();
		probCalculator.attachView();
	}

	@Override
	public EuclidianView getActiveEuclidianView() {
		if (layout == null) {
			return getApp().getEuclidianView1();
		}

		final EuclidianDockPanelWAbstract focusedEuclidianPanel = layout
				.getDockManager().getFocusedEuclidianPanel();

		if (focusedEuclidianPanel != null) {
			return focusedEuclidianPanel.getEuclidianView();
		}
		if (layout.getDockManager().getPanel(App.VIEW_EUCLIDIAN).isVisible()) {
			return getApp().getEuclidianView1();
		}
		if (layout.getDockManager().getPanel(App.VIEW_EUCLIDIAN2).isVisible()
				&& getApp().hasEuclidianView2EitherShowingOrNot(1)) {
			return getApp().getEuclidianView2(1);
		}
		if (layout.getDockManager().getPanel(App.VIEW_EUCLIDIAN3D) != null
				&& layout.getDockManager().getPanel(App.VIEW_EUCLIDIAN3D)
				.isVisible()
				&& getApp().isEuclidianView3Dinited()) {
			return (EuclidianView) getApp().getEuclidianView3D();
		}
		return (getApp()).getEuclidianView1();
		// return app.getEuclidianView1();
	}

	/**
	 * Clear data analysis
	 */
	public void clearDataAnalysisView() {
		dataAnalysisView = null;
	}

	@Override
	public View getDataAnalysisView() {
		if (dataAnalysisView == null) {
			dataAnalysisView = new DataAnalysisViewW(getApp(), getApp()
					.getSettings().getDataAnalysis().getMode());
		}
		return dataAnalysisView;
	}

	@Override
	public void attachDataAnalysisView() {
		getDataAnalysisView();
		dataAnalysisView.attachView();
	}

	@Override
	public void detachDataAnalysisView() {
		if (dataAnalysisView != null) {
			dataAnalysisView.detachView();
		}
	}

	@Override
	public boolean hasDataAnalysisView() {
		return dataAnalysisView != null && dataAnalysisView.isShowing();
	}

	@Override
	public void detachProbabilityCalculatorView() {
		getProbabilityCalculator();
		probCalculator.detachView();
	}

	@Override
	public void detachCasView() {
		if (casView != null) {
			casView.detachView();
		}
	}

	@Override
	public void detachConstructionProtocolView() {
		if (constructionProtocolView != null) {
			constructionProtocolView.getData().detachView();
		}
	}

	@Override
	public void detachSpreadsheetView() {
		if (spreadsheetView != null) {
			spreadsheetView.detachView();
		}
	}

	@Override
	public void openHelp(final String page, final Help type) {
		try {
			final String helpURL = getHelpURL(type, page);
			getApp().getFileManager().open(helpURL);
			getInputHelpPanel().getInputHelpPanel().logHelpIconEvent(page, false);
		} catch (final MyError e) {
			getApp().showError(e);
		} catch (final Exception e) {
			Log.debug("openHelp error: " + e.toString() + " " + e.getMessage()
			+ " " + page + " " + type);
			getApp().showGenericError(e);
		}
	}

	@Override
	public void resetSpreadsheet() {
		if (spreadsheetView != null) {
			spreadsheetView.restart();
		}
	}

	@Override
	public void setScrollToShow(final boolean b) {
		if (spreadsheetView != null) {
			spreadsheetView.setScrollToShow(b);
		}
	}

	@Override
	public boolean hasEuclidianView2(final int idx) {
		if (!this.hasEuclidianView2EitherShowingOrNot(idx)) {
			return false;
		}
		if (idx == 1) {
			return showView(App.VIEW_EUCLIDIAN2);
		}
		return euclidianView2.get(idx).isShowing();
	}

	@Override
	public void setLabels() {
		if (algebraInput != null) {
			algebraInput.setLabels();
		}

		if (toolbarPanel != null && toolbarPanel.getToolBar() != null) {
			toolbarPanel.getToolBar().buildGui();
			toolbarPanel.setLabels();
		}
		SetLabels notesLayout = (((AppWFull) app).getAppletFrame())
				.getNotesLayout();
		if (notesLayout != null) {
			notesLayout.setLabels();
		}
		resetMenu();

		if (constProtocolNavigationMap != null) {
			for (ConstructionProtocolNavigation constProtocolNavigation : constProtocolNavigationMap
					.values()) {
				constProtocolNavigation.setLabels();
			}
		}

		// set the labelling of the panels
		// titles on the top of their style bars
		if (getLayout() != null && getLayout().getDockManager() != null) {
			final DockPanelW[] panels = getLayout().getDockManager()
					.getPanels();
			for (DockPanelW panel : panels) {
				panel.setLabels();
			}
		}
		if (propertiesView != null) {
			((PropertiesViewW) propertiesView).setLabels();
		}

		getApp().getDialogManager().setLabels();
		if (isOpenFileViewLoaded()) {
			getBrowseView().setLabels();
		}

		GlobalHeader.INSTANCE.setLabels();
	}

	@Override
	public void resetMenu() {
		if (mainMenuBar != null && mainMenuBar.getMenubar() != null) {
			mainMenuBar.removeMenus();
			mainMenuBar.init(getApp());
		}
		updateGlobalOptions();
	}

	@Override
	public void resetMenuIfScreenChanged() {
		if (mainMenuBar != null && mainMenuBar.getMenubar() != null
				&& mainMenuBar.getMenubar().smallScreen != frame.shouldHaveSmallScreenLayout()) {
			mainMenuBar.removeMenus();
			mainMenuBar.init(getApp());
			updateGlobalOptions();
		}
	}

	@Override
	public void updateGlobalOptions() {
		if (propertiesView != null) {
			((PropertiesViewW) this.getPropertiesView())
					.getOptionPanel(OptionType.GLOBAL, 0).updateGUI();
		}
	}

	@Override
	public EuclidianView getEuclidianView2(final int idx) {
		for (int i = euclidianView2.size(); i <= idx; i++) {
			euclidianView2.add(null);
		}
		if (euclidianView2.get(idx) == null) {
			final boolean[] showAxis = { true, true };
			final boolean showGrid = false;
			final EuclidianViewW ev = newEuclidianView2(showAxis, showGrid);
			euclidianView2.set(idx, ev);
			ev.updateFonts();
		}
		return euclidianView2.get(idx);
	}

	/**
	 * Get panel for secondary graphics view
	 *
	 * @param idx
	 *            EV2 index
	 * @return EV2 panel
	 */
	public Euclidian2DockPanelW getEuclidianView2DockPanel(final int idx) {
		if (euclidianView2DockPanel == null) {
			euclidianView2DockPanel = new Euclidian2DockPanelW(
					getApp().allowStylebar(), idx);
		}
		return euclidianView2DockPanel;
	}

	public DockPanelW getEuclidian3DPanel() {
		return null;
	}

	protected EuclidianViewW newEuclidianView2(final boolean[] showAxis,
			final boolean showGrid) {
		return getApp().newEuclidianView(getEuclidianView2DockPanel(1),
				getApp().newEuclidianController(kernel), showAxis, showGrid, 2,
				getApp().getSettings().getEuclidian(2));
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(final int idx) {
		return euclidianView2 != null && euclidianView2.size() > idx
				&& euclidianView2.get(idx) != null;
	}

	@Override
	public void updateFrameSize() {
		if (!getApp().getAppletParameters().getDataParamApp() || !examController.isIdle()) {
			return;
		}
		// get frame size from layout manager
		GDimension size = getApp().getPreferredSize();
		int width = size.getWidth();
		int height = size.getHeight();
		// check if frame fits on screen

		if (getApp().getDevice() != null) {
			getApp().getDevice().resizeView(width, height);
		}
	}

	@Override
	public void getSpreadsheetViewXML(final StringBuilder sb,
			final boolean asPreference) {
		if (spreadsheetView != null) {
			spreadsheetView.getXML(sb, asPreference);
		} else {
			super.getSpreadsheetViewXML(sb, asPreference);
		}
	}

	@Override
	public boolean hasAlgebraViewShowing() {
		if (algebraView == null) {
			return false;
		}
		// get from model, not DOM because it may be hidden by tool panel
		return showView(App.VIEW_ALGEBRA);
	}

	@Override
	public boolean hasAlgebraView() {
		return algebraView != null;
	}

	@Override
	public void getAlgebraViewXML(final StringBuilder sb,
			final boolean asPreference) {
		if (algebraView != null) {
			algebraView.getXML(sb);
		}
	}

	@Override
	public int getActiveToolbarId() {
		return toolbarID;
	}

	@Override
	public void setActivePanelAndToolbar(final int viewID) {
		getLayout().getDockManager().setFocusedPanel(viewID);
		setActiveToolbarId(viewID);
	}

	@Override
	public void setActiveToolbarId(final int toolbarID) {

		// set the toolbar string directly from the panels
		// after closing some panels, this may need to be done
		// even if the following need not
		// only do this if toolbar string not null, otherwise this may
		DockPanel dp = layout.getDockManager().getPanel(toolbarID);
		String def = dp == null ? null : dp.getToolbarString();
		if ((def == null || "".equals(def))
				&& this.generalToolbarDefinition != null) {
			def = this.generalToolbarDefinition;
		}
		setToolBarDefinition(def);
		boolean changed = this.toolbarID != toolbarID && toolbarPanel != null;
		this.toolbarID = toolbarID;

		if (changed) {
			getToolbarPanel().setActiveToolbar(toolbarID);
			refreshCustomToolsInToolBar();
			if (toolbarID == App.VIEW_EUCLIDIAN
					|| toolbarID == App.VIEW_EUCLIDIAN2) {
				if (strCustomToolbarDefinition != null) {
					setToolBarDefinition(strCustomToolbarDefinition);
				}
			}

			updateToolbar();
		}
	}

	@Override
	public AppWFull getApp() {
		return (AppWFull) app;
	}

	@Override
	public void removePopup() {
		if (currentPopup != null) {
			currentPopup.removeFromDOM();
			currentPopup = null;
		}
	}

	@Override
	public void setGeneralToolBarDefinition(final String toolBarDefinition) {
		if (toolBarDefinition == null) {
			return;
		}
		generalToolbarDefinition = toolBarDefinition;
		strCustomToolbarDefinition = toolBarDefinition;
	}

	/**
	 * @return general toolbar definition
	 */
	public String getGeneralToolbarDefinition() {
		// General definition is set to not null by applyPerspective, but anyway
		if (this.generalToolbarDefinition == null) {
			return this.getToolbarDefinition();
		}
		return this.generalToolbarDefinition;
	}

	@Override
	public void setToolBarDefinition(final String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
	}

	@Override
	public ConstructionProtocolView getConstructionProtocolView() {
		if (constructionProtocolView == null) {
			constructionProtocolView = this.device
					.getConstructionProtocolView(getApp());
		}
		return constructionProtocolView;
	}

	@Override
	public boolean isUsingConstructionProtocol() {
		return constructionProtocolView != null;
	}

	@Override
	public void clearAbsolutePanels() {
		clearAbsolutePanel(App.VIEW_EUCLIDIAN);
		clearAbsolutePanel(App.VIEW_EUCLIDIAN2);
	}

	private void clearAbsolutePanel(final int viewid) {
		AbsolutePanel ep;
		if (viewid == App.VIEW_EUCLIDIAN) {
			ep = ((EuclidianDockPanelW) getLayout().getDockManager().getPanel(
					viewid)).getAbsolutePanel();
		} else if (viewid == App.VIEW_EUCLIDIAN2) {
			ep = ((Euclidian2DockPanelW) getLayout().getDockManager().getPanel(
					viewid)).getAbsolutePanel();
		} else {
			return;
		}

		if (ep == null) {
			return;
		}
		final Iterator<Widget> it = ep.iterator();
		while (it.hasNext()) {
			final Widget nextItem = it.next();
			if (!(nextItem instanceof Canvas)) {
				it.remove();
			}
		}
	}

	@Override
	public boolean checkAutoCreateSliders(final String s,
			final AsyncOperation<String[]> callback) {
		DialogData data = new DialogData("CreateSliders", "Cancel",
				"CreateSliders");
		ComponentDialog createSlider = new ComponentDialog((AppW) app, data, false, true);
		Label message = new Label(loc.getPlain("CreateSlidersForA", s));
		createSlider.addDialogContent(message);
		createSlider.setOnPositiveAction(() ->
				callback.callback(new String[] { AlgebraProcessor.CREATE_SLIDER }));
		createSlider.show();
		return false;
	}

	@Override
	protected ConstructionProtocolNavigation newConstructionProtocolNavigation(
			int id) {
		ConstructionProtocolNavigationW cpn = new ConstructionProtocolNavigationW(
				this.getApp(), id);
		if (constructionProtocolView != null) {
			cpn.register(constructionProtocolView);
		}
		return cpn;
	}

	@Override
	public void logout() {
		if (app.isWhiteboardActive()) {
			this.browseGUI = null;
		}
	}

	/**
	 * @param show
	 *            whether to show the menubar or not
	 */
	@Override
	public void showMenuBar(final boolean show) {
		getApp().getAppletParameters().setAttribute("showMenuBar", show + "");
		if (show) {
			showToolBar(true);
		}
		GGWToolBar tb = getApp().getAppletFrame().getToolbar();
		if (tb != null) {
			tb.onResize();
			tb.updateActionPanel();
		}

		getApp().closePopups();
	}

	@Override
	public void showToolBar(final boolean show) {
		if (app.isWhiteboardActive()) {
			if (show) {
				frame.attachNotesUI(getApp());
			} else {
				frame.detachNotesToolbar(getApp());
			}
			return;
		}
		ToolBarInterface tb = getApp().getToolbar();
		boolean currentlyVisible = tb != null && tb
				.isShown();
		if (!show) {
			showMenuBar(false);
		}
		if (currentlyVisible != show) {
			getApp().setShowToolBar(show);
			getApp().getAppletParameters().removeAttribute("showToolBar");
			getApp().persistWidthAndHeight();
			getApp().addToHeight(show ? -GLookAndFeel.TOOLBAR_HEIGHT
					: GLookAndFeel.TOOLBAR_HEIGHT);
			getApp().updateCenterPanelAndViews();
			getApp().getAppletFrame().refreshKeyboard();
			if (show) {
				updateToolbar();
			}
		}
	}

	/**
	 * @param show
	 *            whether to show algebra input or not
	 */
	@Override
	public void showAlgebraInput(final boolean show) {
		if (algebraInput != null) {
			algebraInput.setVisible(show);
		} else {
			getApp().attachAlgebraInput();
		}

		getApp().setShowAlgebraInput(show, false);
		getApp().updateSplitPanelHeight();

		getApp().closePopups();
	}

	@Override
	public int setToolbarMode(final int mode, ModeSetter m) {
		if (getApp().isUnbundled() && getUnbundledToolbar() != null) {
			getUnbundledToolbar().setMode(mode);

			// close dynamic stylebar at changing mode

			if (this.getActiveEuclidianView().hasDynamicStyleBar()) {
				this.getActiveEuclidianView().getDynamicStyleBar()
				.setVisible(false);
			}

			return mode;
		}

		if (toolbarPanel == null) {
			return mode;
		}

		final int ret = toolbarPanel.setMode(mode, m);
		if (this.toolbarForUpdate != null) {
			this.toolbarForUpdate.buildGui();
		}
		// layout.getDockManager().setToolbarMode(mode);
		return ret;
	}

	@Override
	public void setActiveView(final int evID) {
		if (layout == null || layout.getDockManager() == null) {
			return;
		}
		layout.getDockManager().setFocusedPanel(evID);
	}

	@Override
	public boolean isDraggingViews() {
		return draggingViews;
	}

	@Override
	public void setDraggingViews(final boolean draggingViews,
			final boolean temporary) {
		if (!temporary) {
			this.oldDraggingViews = draggingViews;
		}
		this.draggingViews = draggingViews;
		if (layout != null) {
			layout.getDockManager().enableDragging(draggingViews);
		}
	}

	@Override
	public void refreshDraggingViews() {
		layout.getDockManager().enableDragging(oldDraggingViews);
	}

	@Override
	public EuclidianViewW getPlotPanelEuclidanView() {
		return (EuclidianViewW) probCalculator.getPlotPanel();
	}

	public boolean isConsProtNavigationPlayButtonVisible() {
		return getConstructionProtocolNavigation().isPlayButtonVisible();
	}

	public boolean isConsProtNavigationProtButtonVisible() {
		return getConstructionProtocolNavigation().isConsProtButtonVisible();
	}

	@Override
	public void detachView(final int viewId) {
		if (viewId == App.VIEW_FUNCTION_INSPECTOR) {
			getApp().getDialogManager().getFunctionInspector()
			.setInspectorVisible(false);
		} else {
			super.detachView(viewId);
		}
	}

	/**
	 * @return {@link BrowseViewI}
	 */
	@Override
	public BrowseViewI getBrowseView(String query) {
		if (!isOpenFileViewLoaded()) {
			this.browseGUI = createBrowseView(this.getApp());
			if (!StringUtil.emptyTrim(query)) {
				this.browseGUI.displaySearchResults(query);
			} else {
				this.browseGUI.loadAllMaterials(0);
			}
		}
		else if (!StringUtil.emptyTrim(query)) {
			this.browseGUI.displaySearchResults(query);
		}
		return this.browseGUI;
	}

	private BrowseViewI createBrowseView(AppWFull app) {
		if (!examController.isIdle()) {
			return new OpenTemporaryFileView(app);
		} else {
			BrowserDevice.FileOpenButton fileOpenButton =
					new BrowserDevice.FileOpenButton("containedButton", app);
			BrowseViewI openFileView;
			if (app.isMebis()) {
				openFileView = new OpenFileViewMebis(app, fileOpenButton);
			} else {
				openFileView = new OpenFileView(app, fileOpenButton);
			}
			fileOpenButton.setOpenFileView(openFileView);
			return openFileView;
		}
	}

	@Override
	public boolean isOpenFileViewLoaded() {
		return this.browseGUI != null;
	}

	@Override
	public int getEuclidianViewCount() {
		return euclidianView2 == null ? 0 : euclidianView2.size();
	}

	@Override
	public void updateCheckBoxesForShowConstructinProtocolNavigation(int id) {
		getLayout().getDockManager().getPanel(id)
		.updateNavigationBar();
	}

	public DockSplitPaneW getRootComponent() {
		return getLayout().getRootComponent();
	}

	@Override
	public EuclidianStyleBar newEuclidianStylebar(final EuclidianView ev,
			int viewID) {
		return new EuclidianStyleBarW(ev, viewID);
	}

	@Override
	public EuclidianStyleBar newDynamicStylebar(final EuclidianView ev) {
		return new DynamicStyleBar(ev);
	}

	@Override
	public void addStylebar(EuclidianView ev,
			EuclidianStyleBar dynamicStylebar) {
		DockPanelW dp = getLayout().getDockManager().getPanel(ev.getViewID());
		AbsolutePanel absolutePanel = ((EuclidianDockPanelWAbstract) dp).getAbsolutePanel();
		if (absolutePanel != null) {
			absolutePanel.add((DynamicStyleBar) dynamicStylebar);
		}
	}

	@Override
	public void recalculateEnvironments() {
		for (int i = 0; i < getEuclidianViewCount(); i++) {
			if (hasEuclidianView2(i)) {
				getEuclidianView2(i).getEuclidianController()
				.calculateEnvironment();
			}
		}
		if (hasProbabilityCalculator()) {
			((ProbabilityCalculatorViewW) getProbabilityCalculator()).getPlotPanel()
			.getEuclidianController().calculateEnvironment();
		}
	}

	/**
	 *
	 * @param toolBar
	 *            will be updated every time setMode(int) is called
	 */
	public void setToolBarForUpdate(final ToolBarW toolBar) {
		this.toolbarForUpdate = toolBar;
	}

	/**
	 *
	 */
	@Override
	public void exportGGB() {
		String extension = ((AppW) app).getFileExtension();
		String currentDate = DateTimeFormat.format(new JsDate())
				+ extension;
		String filename = getApp().isMebis() ? currentDate : getApp().getExportTitle() + extension;
		exportGgb(filename, extension);
	}

	private void exportGgb(String filename, String extension) {
		getApp().dispatchEvent(
				new Event(EventType.EXPORT, null, "[\""
						+ extension.substring(1) + "\"]"));

		if (NavigatorUtil.isFirefox()) {
			getApp().getGgbApi().getBase64(true,
					getBase64DownloadCallback(filename));
		} else {
			getApp().getGgbApi().getZippedGgbAsync(true,
					getDownloadCallback(filename));
		}
	}

	/**
	 * Download macro file (.ggt)
	 * @param filename file name
	 */
	public void exportMacros(String filename) {
		getApp().dispatchEvent(
				new Event(EventType.EXPORT, null, "[\"ggt\"]"));

		if (NavigatorUtil.isFirefox()) {
			getApp().getGgbApi().getAllMacrosBase64(true,
					getBase64DownloadCallback(filename));
		} else {
			getApp().getGgbApi().getZippedMacrosAsync(
					getDownloadCallback(filename));
		}
	}

	/**
	 * @param title
	 *            construction title
	 * @return local file saving callback for binary file
	 */
	private FileConsumer getDownloadCallback(String title) {
		return blob -> {
			//global function in Chrome Kiosk App
			String ggburl = URL.createObjectURL(blob);
			if (GeoGebraGlobal.getGgbExportFile() != null) {
				GeoGebraGlobal.getGgbExportFile().call(DomGlobal.window, ggburl, title);
				return;
			}
			Browser.downloadURL(ggburl, title);
		};
	}

	/**
	 * @param title
	 *            export title
	 * @return callback for base64 encoded download
	 */
	protected StringConsumer getBase64DownloadCallback(String title) {
		return base64 ->
			Browser.downloadURL("data:application/vnd.geogebra.file;base64," + base64,
					title);
	}

	private void uploadCallback(final BaseEvent event) {
		if (runAfterLogin != null) {
			if (event instanceof LoginEvent
					&& ((LoginEvent) event).isSuccessful()) {
				runAfterLogin.run();
				this.runAfterLogin = null;
			} else if (event instanceof StayLoggedOutEvent || (event instanceof LoginEvent
					&& !((LoginEvent) event).isSuccessful())) {
				runAfterLogin = null;
				getApp().getFileManager().showOfflineErrorTooltip(getApp());
			}
		}
	}

	@Override
	public void setRunAfterLogin(Runnable runAfterLogin) {
		this.runAfterLogin = runAfterLogin;
	}

	@Override
	public BrowseViewI getBrowseView() {
		return getBrowseView(null);
	}

	/**
	 * @return default definition for general toolbar
	 */
	public String getDefaultToolbarString() {
		if (getUnbundledToolbar() != null) {
			return ToolBar.getAllToolsNoMacros(true, false, getApp());
		}
		if (toolbarPanel != null) {
			return getGeneralToolbar().getDefaultToolbarString();
		}
		return "";
	}

	@Override
	public void setPixelRatio(double ratio) {
		if (hasAlgebraView()) {
			this.getAlgebraView().setPixelRatio(ratio);
		}
		if (hasCasView()) {
			((CASViewW) getCasView()).setPixelRatio(ratio);
		}
		if (hasSpreadsheetView()) {
			getSpreadsheetView().setPixelRatio(ratio);
		}
	}

	@Override
	public String getTooltipURL(int mode) {
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			return getHelpURL(Help.TOOL, "Custom_Tools");
		}

		return getHelpURL(Help.TOOL,
				EuclidianConstants.getModeHelpPage(mode));
	}

	@Override
	public void getToolImageURL(final int mode, final GeoImage geoImage,
			final AsyncOperation<String> s) {

		GGWToolBar.getImageResource(mode, getApp(), mode_tool_32 -> {
			String url = NoDragImage.safeURI(mode_tool_32);
			String fn = "geogebra_tool_" + mode;
			String zipDirectory = getApp().md5Encrypt(fn);
			fn = zipDirectory + "/" + fn;
			getApp().getImageManager().addExternalImage(fn, url);
			getApp().getImageManager().triggerSingleImageLoading(fn,
					geoImage);
			s.callback(fn);
		});
	}

	/**
	 * @param app
	 *            application
	 * @return whether keyboard may be shown at startup
	 */
	public static boolean mayForceKeyboard(AppW app) {
		return !app.isStartedWithFile()
				&& !app.getAppletParameters().preventFocus()
				&& examController.getState() != ExamState.PREPARING;
	}

	@Override
	public void replaceInputSelection(String string) {
		if (getApp().showView(App.VIEW_ALGEBRA)
				&& getApp().getAlgebraView()
				.getInputTreeItem() != null) {
			RadioTreeItem input = getApp().getAlgebraView()
					.getInputTreeItem();
			if (input != null) {
				input.insertMath(string);
				input.setFocus(true);
				input.ensureEditing();
			}
		} else if (getAlgebraInput() != null) {
			getAlgebraInput().getTextField().autocomplete(string);
			getAlgebraInput().getTextField().setFocus(true);
		}
	}

	@Override
	public void setInputText(String string) {
		if (getApp().showView(App.VIEW_ALGEBRA)
				&& getApp().getAlgebraView()
				.getInputTreeItem() != null
				&& getApp().getInputPosition() == InputPosition.algebraView) {
			RadioTreeItem input = getApp().getAlgebraView()
					.getInputTreeItem();
			if (input != null) {
				input.setText(string);
				input.setFocus(true);
				input.ensureEditing();
			}
		} else if (getAlgebraInput() != null) {
			getAlgebraInput().setText(string);
			getAlgebraInput().getTextField().setFocus(true);
		}
	}

	public void setColorTab(ColorPanel colorPanel) {
		this.colorPanel = colorPanel;
	}

	public ColorPanel getColorPanel() {
		return colorPanel;
	}

	/**
	 * @return toolbar panel for unbundled apps
	 */
	public ToolbarPanel getUnbundledToolbar() {
		if (getApp().isUnbundled()) {
			DockPanel avPanel = getLayout().getDockManager()
					.getPanel(App.VIEW_ALGEBRA);
			if (avPanel instanceof ToolbarDockPanelW) {
				return ((ToolbarDockPanelW) avPanel).getToolbar();
			}
		}
		return null;
	}

	@Override
	public TemplateChooserController getTemplateController() {
		if (templateController == null) {
			templateController = new TemplateChooserController();
		}
		return templateController;
	}

	@Override
	public void switchToolsToAV() {
		getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA)
				.setTabId(DockPanelData.TabIds.ALGEBRA);
	}

	/**
	 * Open settings menu for geo element in AV.
	 */
	@Override
	public void openMenuInAVFor(GeoElement geo) {
		if (getApp().isUnbundled() && hasAlgebraView()) {
			getAlgebraView().openMenuFor(geo);
		}
	}

	@Override
	public MathKeyboardListener getKeyboardListener(DockPanel panel) {
		MathKeyboardListener ml = null;
		if (panel instanceof DockPanelW) {
			ml = ((DockPanelW) panel).getKeyboardListener();
		}
		if (ml == null && getApp().showAlgebraInput()
				&& getApp().getInputPosition() != InputPosition.algebraView) {
			return getAlgebraInput().getTextField();
		}
		return ml;
	}

	@Override
	public boolean isVerticalSplit(boolean fallback) {
		return getLayout().getDockManager().getRoot() == null ? fallback
				: getLayout().getDockManager().getRoot()
				.getOrientation() == SwingConstants.VERTICAL_SPLIT;
	}

	@Override
	public void setUnbundledHeaderStyle(String style) {
		if (getUnbundledToolbar() != null) {
			getUnbundledToolbar().setHeaderStyle(style);
		}
	}

	/**
	 * init on click for exam info button
	 */
	@Override
	public void initInfoBtnAction() {
		if (getUnbundledToolbar() != null) {
			getUnbundledToolbar().initInfoBtnAction();
		}
	}

	/**
	 * @param ggwMenuBar
	 *            menu
	 */
	public void setGgwMenubar(GGWMenuBar ggwMenuBar) {
		mainMenuBar = ggwMenuBar;
	}

	/**
	 * @param geo
	 *            function/lie to be added
	 */
	public void addGeoToTableValuesView(GeoElement geo) {
		app.getEventDispatcher()
				.dispatchEvent(EventType.ADD_TV, geo);
		addGeoToTV(geo);
		getUnbundledToolbar().openTableView((GeoEvaluatable) geo, true);
	}

	@Override
	public void toggleTableValuesView() {
		getUnbundledToolbar().toggleTableView();
	}

	@Override
	public void removeGeoFromTV(String label) {
		GeoElement geo = app.getKernel().lookupLabel(label);
		if (getTableValuesView() != null && geo instanceof GeoEvaluatable) {
			getTableValuesView().hideColumn((GeoEvaluatable) geo);
		}
	}

	@Override
	public void setValues(double min, double max, double step) throws InvalidValuesException {
		if (getTableValuesView() != null) {
			getTableValuesView().setValues(min, max, step);
		}
	}

	@Override
	public void showPointsTV(int column, boolean show) {
		if (getTableValuesPoints() != null) {
			getTableValuesPoints().setPointsVisible(column, show);
		}
	}

	@Override
	public void addGeoToTV(GeoElement geo) {
		getTableValuesView().addAndShow(geo);
	}

	@Override
	public void showTableValuesView(GeoElement geo) {
		if (getTableValuesView().isEmpty()) {
			app.getDialogManager().openTableViewDialog(geo);
		} else {
			addGeoToTableValuesView(geo);
		}
	}

	@Override
	public void updateUnbundledToolbar() {
		if (getUnbundledToolbar() != null) {
			getUnbundledToolbar().resizeTabs();
		}
	}

	@Override
	public void menuToGlobalHeader() {
		if (GlobalHeader.isInDOM()) {
			MenuToggleButton btn = new MenuToggleButton((AppW) app);
			btn.setExternal(true);
			btn.addToGlobalHeader();
		}
	}

	@Override
	public void initShareActionInGlobalHeader() {
		GlobalHeader.INSTANCE.initShareButton(share -> {
			getApp().hideMenu();
			FileMenuW.share(getApp(), share);
		}, (AppW) app);
	}

	@Override
	public SymbolicEditor createSymbolicEditor(EuclidianViewW view, TextRendererSettings settings) {
		return new SymbolicEditorW(app, view, settings);
	}

	/**
	 * @return with keyboard listener
	 */
	public MathKeyboardListener getKeyboardListener() {
		DockPanelW dockPanelForKeyboard = layout.getDockManager().getPanelForKeyboard();
		return getKeyboardListener(dockPanelForKeyboard);
	}

	/**
	 * Clears browser GUI to enable to build a new one
	 * (when starting or ending an exam)
	 */
	public void resetBrowserGUI() {
		browseGUI = null;
	}

	@Override
	public void closeFullscreenView() {
		if (getUnbundledToolbar() != null) {
			getUnbundledToolbar().showOppositeView();
			getUnbundledToolbar().resizeTabs();
			getUnbundledToolbar().closeAnimation();
		}
	}

	@Override
	public boolean isAlgebraViewActive() {
		return getUnbundledToolbar().getTab(DockPanelData.TabIds.ALGEBRA).isActive();
	}

	@Override
	public InputKeyboardButton getInputKeyboardButton() {
		if (inputKeyboardButton == null) {
			inputKeyboardButton = new InputKeyboardButtonW(getApp());
		}
		return inputKeyboardButton;
	}

	@Override
	public boolean isTableViewShowing() {
		if (!app.getConfig().hasTableView() || !app.isUnbundled() || !showView(App.VIEW_ALGEBRA)) {
			return false;
		}
		ToolbarPanel toolbar = getUnbundledToolbar();
		return toolbar.getSelectedTabId() == DockPanelData.TabIds.TABLE;
	}

	@Override
	public boolean toolbarHasImageMode() {
		if (!app.showToolBar()) {
			return false;
		} else if (getApp().isWhiteboardActive()) {
			return true;
		} else if (app.getConfig().getToolbarType().equals(AppType.CLASSIC)) {
			Vector<ToolbarItem> toolbarItems =
					ToolBar.parseToolbarString(app.getGuiManager().getToolbarDefinition());

			for (ToolbarItem toolbarItem : toolbarItems) {
				if (toolbarItem.getMode() == null) {
					if (toolbarItem.getMenu().contains(EuclidianConstants.MODE_IMAGE)) {
						return true;
					}
				} else if (toolbarItem.getMode() == EuclidianConstants.MODE_IMAGE) {
					return true;
				}
			}
		} else {
			return app.getAvailableTools().contains(EuclidianConstants.MODE_IMAGE);
		}

		return false;
	}
}
