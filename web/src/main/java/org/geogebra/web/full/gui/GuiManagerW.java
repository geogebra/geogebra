package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.Iterator;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.Editing;
import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.javax.swing.GOptionPane;
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
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.full.cas.view.CASTableW;
import org.geogebra.web.full.cas.view.CASViewW;
import org.geogebra.web.full.cas.view.RowHeaderPopupMenuW;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.euclidian.DynamicStyleBar;
import org.geogebra.web.full.euclidian.EuclidianStyleBarW;
import org.geogebra.web.full.gui.app.GGWMenuBar;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.dialog.options.OptionsTab.ColorPanel;
import org.geogebra.web.full.gui.inputbar.AlgebraInputW;
import org.geogebra.web.full.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.full.gui.keyboard.OnscreenTabbedKeyboard;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.gui.layout.panels.CASDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ConstructionProtocolDockPanelW;
import org.geogebra.web.full.gui.layout.panels.DataAnalysisViewDockPanelW;
import org.geogebra.web.full.gui.layout.panels.DataCollectionDockPanelW;
import org.geogebra.web.full.gui.layout.panels.Euclidian2DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.full.gui.layout.panels.FunctionInspectorDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ProbabilityCalculatorDockPanelW;
import org.geogebra.web.full.gui.layout.panels.PropertiesDockPanelW;
import org.geogebra.web.full.gui.layout.panels.SpreadsheetDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.layout.scientific.ScientificSettingsView;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.toolbar.ToolBarW;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.util.PopupBlockAvoider;
import org.geogebra.web.full.gui.util.ScriptArea;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.full.gui.view.algebra.AlgebraControllerW;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolNavigationW;
import org.geogebra.web.full.gui.view.data.DataAnalysisViewW;
import org.geogebra.web.full.gui.view.dataCollection.DataCollectionView;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;
import org.geogebra.web.full.gui.view.spreadsheet.CopyPasteCutW;
import org.geogebra.web.full.gui.view.spreadsheet.MyTableW;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetContextMenuW;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetViewW;
import org.geogebra.web.full.html5.AttachedToDOM;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.full.util.keyboard.AutocompleteProcessing;
import org.geogebra.web.full.util.keyboard.GTextBoxProcessing;
import org.geogebra.web.full.util.keyboard.ScriptAreaProcessing;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.util.HasResource;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Visibility;
import org.geogebra.web.shared.GlobalHeader;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class GuiManagerW extends GuiManager
		implements GuiManagerInterfaceW, EventRenderable, SetLabels {

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
	protected boolean uploadWaiting;
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
	private DataCollectionView dataCollectionView;
	private VirtualKeyboardGUI onScreenKeyboard;
	private GeoGebraFrameFull frame;

	private int activeViewID;

	private boolean inputBarSetFocusScheduled = false;
	private boolean inputBarSetFocusAllowed = true;

	private GOptionPaneW optionPane;

	private ColorPanel colorPanel;

	private Localization loc;

	private GGWMenuBar mainMenuBar;

	private AnimatingPanel sciSettingsView;

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
		if (getApp().getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			getApp().getActiveEuclidianView().resetMode();
			getPopupMenu(geos).showScaled(invoker, p.x, p.y);
		}
	}

	@Override
	public void showPopupMenu(final ArrayList<GeoElement> geos,
			final AlgebraView invoker, final GPoint p) {
		// clear highlighting and selections in views
		getApp().getActiveEuclidianView().resetMode();
		getPopupMenu(geos).show(p);
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
		currentPopup = (AttachedToDOM) contextMenu.getMenuContainer();
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
		currentPopup = new ContextMenuGeoElementW(getApp(), geos);
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
				selectedGeos, geos, p);
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

		if (altDown) {
			// AppW.nativeConsole("alt down");
			Log.debug("trying to paste image");

			// try to paste image in html format eg
			// http://jsfiddle.net/bvFNL/8/
			String html = CopyPasteCutW.getClipboardContents(null);
			// AppW.nativeConsole("from clipboard = " + html);

			Log.debug("trying to paste image " + html);

			int pngBase64index = html.indexOf(StringUtil.pngMarker);

			if (pngBase64index > -1) {
				int pngBase64end = html.indexOf("\"", pngBase64index);
				String base64 = html.substring(
						pngBase64index,
						pngBase64end);

				getApp().imageDropHappened("pastedFromClipboard.png",
						base64);

				return;
			}
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
		if (Browser.isiOS()) {
			dialogManager.showImageInputDialog(null, device);
		} else {
			dialogManager.showWebcamInputDialog(device);
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
		/*
		 * ((AppW)
		 * app).getFrameElement().getStyle().setFontSize(app.getFontSize(),
		 * Unit.PX);
		 *
		 * // if (((AppW) app).getObjectPool().getGgwMenubar() != null){ //
		 * GeoGebraMenubarW menubar = ((AppW)
		 * app).getObjectPool().getGgwMenubar().getMenubar(); // if (menubar !=
		 * null) menubar.updateFonts(); // }
		 *
		 * updateFontSizeStyleElement();
		 *
		 * if(hasPropertiesView()){
		 * ((PropertiesViewW)getPropertiesView()).updateFonts(); }
		 *
		 * if(hasSpreadsheetView()){ getSpreadsheetView().updateFonts(); }
		 */
	}

	@Override
	public boolean isInputFieldSelectionListener() {
		return false;
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
		currentPopup = new ContextMenuGraphicsWindowW(getApp(), x, y);
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
	public void setShowView(final boolean flag, final int viewId) {
		setShowView(flag, viewId, true);
	}

	@Override
	public void setShowView(final boolean flag, final int viewId, final boolean isPermanent) {
		ToolbarPanel sidePanel = getUnbundledToolbar();
		ToolbarPanel.ToolbarTab sidePanelTab = sidePanel != null ? sidePanel.getTab(viewId) : null;
		if (sidePanelTab != null) {
			if (flag) {
				sidePanelTab.open();
			} else {
				sidePanelTab.close();
			}
		} else {
			if (flag) {
				showViewWithId(viewId);
			} else {
				hideViewWith(viewId, isPermanent);
			}
			getApp().dispatchEvent(new Event(EventType.PERSPECTIVE_CHANGE, null));
		}

		layout.getDockManager().updateVoiceover();
		getApp().closePopups();

		if (sidePanel != null) {
			sidePanel.updateUndoRedoPosition();
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
			e.printStackTrace();
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
		frame.forceHeaderVisibility(Visibility.HIDDEN);
		getApp().setCloseBrowserCallback(new Runnable() {
			@Override
			public void run() {
				frame.forceHeaderVisibility(Visibility.NOT_SET);
			}
		});
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
			setProbCalculator(new ProbabilityCalculatorViewW(getApp()));
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
		// TODO Auto-generated method stub
		Log.debug("unimplemented");
		// if (spreadsheetView != null) {
		// spreadsheetView.updateColumnWidths();
		// }
	}

	@Override
	public void resize(final int width, final int height) {
		final Element geogebraFrame = getApp().getFrameElement();

		int widthChanged = width - geogebraFrame.getOffsetWidth();
		int heightChanged = height - geogebraFrame.getOffsetHeight();
		int borderThickness = getApp().getArticleElement()
				.getBorderThickness();
		if (getLayout() != null && getLayout().getRootComponent() != null) {
			final DockSplitPaneW root = getLayout().getRootComponent();
			root.setPixelSize(root.getOffsetWidth() + widthChanged,
					root.getOffsetHeight() + heightChanged);
			root.onResize();
		} else {
			geogebraFrame.getStyle().setProperty("height",
					height - borderThickness + "px");
			geogebraFrame.getStyle().setProperty("width",
					width - borderThickness + "px");
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
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				getApp().centerAndResizeViews();
				resizeKeyboard();
			}

		});
	}

	/**
	 * Update keyboard size.
	 */
	protected void resizeKeyboard() {
		if (onScreenKeyboard != null) {
			onScreenKeyboard.updateSize();
			onScreenKeyboard.setStyleName();
		}
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
	public boolean moveMoveFloatingButtonUp(int left, int width,
			boolean isSmall) {
		if (getUnbundledToolbar() != null) {
			return getUnbundledToolbar()
					.moveMoveFloatingButtonUpWithTooltip(left,
					width, isSmall);
		}
		return false;
	}

	@Override
	public void moveMoveFloatingButtonDown(boolean isSmall, boolean wasMoved) {
		if (getUnbundledToolbar() != null) {
			getUnbundledToolbar().moveMoveFloatingButtonDownWithTooltip(isSmall,
				wasMoved);
		}
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
			// Application.debug("before: " + strCustomToolbarDefinition +
			// ",  delete " + mode);
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

			// Application.debug("after: " + strCustomToolbarDefinition);
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

		//register data collection view
		layout.registerPanel(new DataCollectionDockPanelW());
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
			if (getApp().showView(App.VIEW_CAS)) {
				getApp().getCommandDictionaryCAS();
			}
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
			algebraView = newAlgebraView(algebraController);
			// if (!app.isApplet()) {
			// allow drag & drop of files on algebraView
			// algebraView.setDropTarget(new DropTarget(algebraView,
			// new FileDropTargetListener(app)));
			// }
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

	/**
	 *
	 * @param algc
	 *            algebra controller
	 * @return new algebra view
	 */
	protected AlgebraViewW newAlgebraView(final AlgebraControllerW algc) {
		// if (USE_COMPRESSED_VIEW) {
		// return new CompressedAlgebraView(algc, CV_UPDATES_PER_SECOND);
		// }
		return new AlgebraViewW(algc);
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
	 * close properties view
	 *
	 */
	public void closePropertiesView() {
		if (propertiesView != null) {
			getLayout().getDockManager().closePanel(App.VIEW_PROPERTIES, false);
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
	public void listenToLogin() {
		uploadWaiting = true;
		if (listeningToLogin) {
			return;
		}
		listeningToLogin = true;
		getApp().getLoginOperation().getView().add(this);
	}

	@Override
	public boolean save() {
		return getApp().getFileManager().save(getApp());
	}

	@Override
	public void showPropertiesViewSliderTab() {
		Log.debug("unimplemented");
	}

	@Override
	public void updateGUIafterLoadFile(final boolean success,
			final boolean isMacroFile) {
		if (success && !isMacroFile
				&& !getApp().getSettings().getLayout().isIgnoringDocumentLayout()) {

			getLayout().setPerspectives(getApp().getTmpPerspectives(), null);
			// SwingUtilities.updateComponentTreeUI(getLayout().getRootComponent());

			if (!getApp().isIniting()) {
				updateFrameSize(); // checks internally if frame is available
				if (getApp().needsSpreadsheetTableModel()) {
					(getApp()).getSpreadsheetTableModel(); // ensure create one if
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

		// if (getApp().getEuclidianView3D() != null) {
		// ((EuclidianView3DW) (getApp().getEuclidianView3D())).doRepaint2();
		//
		// }
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
		Log.debug("unimplemented");
		return true;
	}

	@Override
	public void openFile() {
		if (!getApp().isExam()) {
			getApp().openSearch("");
		}
	}

	@Override
	public void showGraphicExport() {
		Log.debug("unimplemented");

	}

	@Override
	public void showPSTricksExport() {
		Log.debug("unimplemented");

	}

	@Override
	public void showWebpageExport() {
		Log.debug("unimplemented");

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

	/**
	 * @return Data collection view
	 */
	public DataCollectionView getDataCollectionView() {
		if (dataCollectionView == null) {
			dataCollectionView = new DataCollectionView(getApp());
			dataCollectionView.attachView();
		}
		return dataCollectionView;
	}

	/**
	 * Update lists in data collection view
	 */
	public void updateDataCollectionView() {
		this.dataCollectionView.updateGeoList();
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

	@Override
	public Command getShowAxesAction() {
		return new Command() {

			@Override
			public void execute() {
				showAxesCmd();
			}
		};
	}

	@Override
	public Command getShowGridAction() {
		return new Command() {

			@Override
			public void execute() {
				showGridCmd();
			}
		};
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
		Log.debug("DAMODE attachDataAnalysisView");
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
	protected void openHelp(final String page, final Help type) {
		try {
			final String helpURL = getHelpURL(type, page);
			getApp().getFileManager().open(helpURL);
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
	public void showURLinBrowser(final String strURL) {
		final PopupBlockAvoider popupBlockAvoider = new PopupBlockAvoider();
		popupBlockAvoider.openURL(strURL);
	}

	@Override
	public void clearInputbar() {
		Log.debug("unimplemented");
	}

	@Override
	public Object createFrame() {
		return null;
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
		SetLabels toolbarMow = (((AppWFull) app).getAppletFrame())
				.getToolbarMow();
		if (toolbarMow != null) {
			toolbarMow.setLabels();
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
		if (this.dataCollectionView != null) {
			this.dataCollectionView.setLabels();
		}

		getApp().getDialogManager().setLabels();
		if (browseGUIwasLoaded()) {
			getBrowseView().setLabels();
		}
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
			Log.debug("Creating 2nd Euclidian View");
			final EuclidianViewW ev = newEuclidianView(showAxis, showGrid, 2);
			euclidianView2.set(idx, ev);
			// euclidianView2.setEuclidianViewNo(2);
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

	protected EuclidianViewW newEuclidianView(final boolean[] showAxis,
			final boolean showGrid, final int id) {
		if (id == 2) {
			return getApp().newEuclidianView(getEuclidianView2DockPanel(1),
					getApp().newEuclidianController(kernel), showAxis, showGrid, id,
					getApp().getSettings().getEuclidian(id));
		}
		return getApp().newEuclidianView(getApp()
				.getEuclidianViewpanel(), getApp().newEuclidianController(kernel),
				showAxis, showGrid, id, getApp().getSettings().getEuclidian(id));
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(final int idx) {
		return euclidianView2 != null && euclidianView2.size() > idx
				&& euclidianView2.get(idx) != null;
	}

	@Override
	public void updateFrameSize() {
		if (!getApp().getArticleElement().getDataParamApp()) {
			return;
		}
		// get frame size from layout manager
		GDimension size = getApp().getPreferredSize();
		int width = size.getWidth();
		int height = size.getHeight();
		// check if frame fits on screen
		Log.debug("Window resize: " + width + "," + height);

		if (getApp().getDevice() != null) {
			getApp().getDevice().resizeView(width, height);
		}

	}

	@Override
	public void getSpreadsheetViewXML(final StringBuilder sb,
			final boolean asPreference) {
		if (spreadsheetView != null) {
			spreadsheetView.getXML(sb, asPreference);
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
		if (!getApp().enableGraphing()) {
			callback.callback(null);
			return false;
		}
		final String[] options = { loc.getMenu("Cancel"),
				loc.getMenu("CreateSliders") };

		final Image icon = new NoDragImage(
				ImgResourceHelper.safeURI(
						ToolbarSvgResourcesSync.INSTANCE.mode_slider_32()),
				32);
		icon.addStyleName("dialogToolIcon");
		// icon.getElement().getStyle()
		// .setProperty("border", "3px solid steelblue");

		getOptionPane().showOptionDialog(
				loc.getPlain("CreateSlidersForA", s),
				loc.getMenu("CreateSliders"),
				Integer.parseInt(AlgebraProcessor.CREATE_SLIDER),
				GOptionPane.INFORMATION_MESSAGE, icon, options, callback);

		return false;
	}

	@Override
	public GOptionPaneW getOptionPane() {
		if (optionPane == null) {
			optionPane = new GOptionPaneW(getApp().getPanel(), getApp());
		}
		return optionPane;
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
		getApp().getArticleElement().attr("showMenuBar", show + "");
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
		ToolBarInterface tb = getApp().getToolbar();
		boolean currentlyVisible = tb != null && tb
				.isShown();
		if (!show) {
			showMenuBar(false);
		}
		if (currentlyVisible != show) {
			getApp().setShowToolBar(show);
			getApp().getArticleElement()
			.removeAttribute("data-param-showToolBar");
			getApp().persistWidthAndHeight();
			getApp()
			.addToHeight(show ? -GLookAndFeel.TOOLBAR_HEIGHT
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

		if (getApp().isWhiteboardActive()) {
			(getApp().getAppletFrame()).setToorbarMowMode(mode);
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
		this.activeViewID = evID;
		if (layout == null || layout.getDockManager() == null) {
			return;
		}
		layout.getDockManager().setFocusedPanel(evID);
	}

	/**
	 *
	 * @return ID of the active view
	 * @see #setActiveView(int)
	 */
	public int getActiveViewID() {
		return this.activeViewID;
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
			Log.debug("Detaching VIEW_FUNCTION_INSPECTOR");
			getApp().getDialogManager().getFunctionInspector()
			.setInspectorVisible(false);
		} else {
			super.detachView(viewId);
		}
	}

	/**
	 * @return {@link BrowseGUI}
	 */
	@Override
	public BrowseViewI getBrowseView(String query) {
		if (!browseGUIwasLoaded()) {
			this.browseGUI = this.device.createBrowseView(this.getApp());
			if (!StringUtil.emptyTrim(query)) {
				this.browseGUI.displaySearchResults(query);
			} else {
				this.browseGUI.loadAllMaterials();
			}
		}
		else if (!StringUtil.emptyTrim(query)) {
			this.browseGUI.displaySearchResults(query);
		}
		return this.browseGUI;
	}

	/**
	 * @return true if {@link BrowseGUI} is not null
	 */
	public boolean browseGUIwasLoaded() {
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
		// ((AppW) app).getEuclidianViewpanel().updateNavigationBar();
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
		((EuclidianDockPanelWAbstract) dp).getAbsolutePanel()
		.add((DynamicStyleBar) dynamicStylebar);
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
	 * shows the downloadDialog
	 */
	@Override
	public void exportGGB() {
		final String extension = ((AppW) app).getFileExtension();
		getOptionPane().showSaveDialog(loc.getMenu("Save"),
				getApp().getExportTitle() + extension, null,
				new AsyncOperation<String[]>() {

					@Override
					public void callback(String[] obj) {
						getApp().dispatchEvent(
								new Event(EventType.EXPORT, null, "[\""
										+ extension.substring(1) + "\"]"));
						if (Browser.isXWALK()) {
							getApp().getGgbApi().getBase64(true,
									getStringCallback(obj[1]));
						} else if (Integer.parseInt(obj[0]) == 0) {

							String filename = obj[1];

							if (filename == null || filename.trim().isEmpty()) {
								filename = getApp().getExportTitle();
							}

							// in case user removes extension
							if (!filename.endsWith(extension)) {
								filename += extension;
							}
							if (Browser.isFirefox()) {
								getApp().getGgbApi().getBase64(true,
										getBase64DownloadCallback(filename));
							} else {
								getApp().getGgbApi().getGGBfile(true,
									getDownloadCallback(filename));
							}
						}
					}
				}, loc.getMenu("Save"));
	}

	/**
	 * @param title
	 *            construction title
	 * @return local file saving callback for base64
	 */
	native JavaScriptObject getStringCallback(String title) /*-{

		return function(base64) {
			var a = $doc.createElement("a");
			$doc.body.appendChild(a);
			a.style = "display: none";
			a.href = @org.geogebra.common.util.StringUtil::ggbMarker + base64;
			a.download = title;
			a.click();
		}

	}-*/;

	/**
	 * @param title
	 *            construction title
	 * @return local file saving callback for binary file
	 */
	native JavaScriptObject getDownloadCallback(String title) /*-{
		var _this = this;
		return function(ggbZip) {
			var URL = $wnd.URL || $wnd.webkitURL;
			var ggburl = URL.createObjectURL(ggbZip);
			//global function in Chrome Kiosk App
			if (typeof $wnd.ggbExportFile == "function") {
				$wnd.ggbExportFile(ggburl, title);
				return;
			}
			if ($wnd.navigator.msSaveBlob) {
				//works for chrome and internet explorer
				$wnd.navigator.msSaveBlob(ggbZip, title);
			} else {
				//works for firefox
				var a = $doc.createElement("a");
				$doc.body.appendChild(a);
				a.style = "display: none";
				a.href = ggburl;
				a.download = title;
				a.click();
				//		        window.URL.revokeObjectURL(url);
			}
		}
	}-*/;

	/**
	 * @param title
	 *            export title
	 * @return callback for base64 encoded download
	 */
	native JavaScriptObject getBase64DownloadCallback(String title) /*-{
		return function(base64) {
			@org.geogebra.web.html5.Browser::downloadDataURL(Ljava/lang/String;Ljava/lang/String;)("data:application/vnd.geogebra.file;base64,"+base64, title);
		}
	}-*/;

	@Override
	public final void renderEvent(final BaseEvent event) {
		if (this.uploadWaiting && event instanceof LoginEvent
				&& ((LoginEvent) event).isSuccessful()) {
			this.uploadWaiting = false;
			save();
		} else if (this.uploadWaiting && event instanceof StayLoggedOutEvent) {
			this.uploadWaiting = false;
			getApp().getFileManager().saveLoggedOut(getApp());
		}
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
	public boolean getKeyboardShouldBeShownFlag() {
		return onScreenKeyboard != null && onScreenKeyboard.shouldBeShown();
	}

	/**
	 * Return a keyboard and connected to given textfield.
	 *
	 * @param textField
	 *            textfield adapter
	 * @param listener
	 *            open/close listener
	 * @return keyboard
	 */
	public VirtualKeyboardGUI getOnScreenKeyboard(
			MathKeyboardListener textField,
			UpdateKeyBoardListener listener) {
		if (onScreenKeyboard == null) {
			onScreenKeyboard = new OnscreenTabbedKeyboard(getApp(),
					app.getConfig().hasScientificKeyboard(),
					app.getConfig().showKeyboardHelpButton());
		}

		if (textField != null) {
			setOnScreenKeyboardTextField(textField);
		}

		onScreenKeyboard.setListener(listener);
		return onScreenKeyboard;
	}

	@Override
	public void updateKeyboardLanguage() {
		if (onScreenKeyboard != null) {
			onScreenKeyboard.checkLanguage();
		}
	}

	/**
	 * Create keyboard adapter for text editing object.
	 *
	 * @param textField
	 *            text / math editor
	 * @return keyboard adapter
	 */
	public static KeyboardListener makeKeyboardListener(
			MathKeyboardListener textField, HasLastItem lastItemProvider) {
		if (textField instanceof RetexKeyboardListener) {
			return new MathFieldProcessing(
					((RetexKeyboardListener) textField).getMathField(),
					lastItemProvider);
		}
		if (textField instanceof RadioTreeItem) {
			return new MathFieldProcessing(
					((RadioTreeItem) textField).getMathField(),
					lastItemProvider);
		}
		if (textField instanceof KeyboardListener) {
			return (KeyboardListener) textField;
		}
		if (textField instanceof GTextBox) {
			return new GTextBoxProcessing((GTextBox) textField);
		}
		if (textField instanceof AutoCompleteTextFieldW) {
			return new AutocompleteProcessing(
					(AutoCompleteTextFieldW) textField);
		}

		if (textField instanceof ScriptArea) {
			return new ScriptAreaProcessing((ScriptArea) textField);
		}

		return null;
	}

	@Override
	public void setOnScreenKeyboardTextField(MathKeyboardListener textField) {
		if (onScreenKeyboard != null) {
			onScreenKeyboard
				.setProcessing(
							makeKeyboardListener(textField,
									AlgebraItem.getLastItemProvider(app)));
		}
	}

	@Override
	public void onScreenEditingEnded() {
		if (onScreenKeyboard != null) {
			onScreenKeyboard.endEditing();
		}
	}

	@Override
	public boolean hasDataCollectionView() {
		return dataCollectionView != null;
	}

	@Override
	public void getDataCollectionViewXML(StringBuilder sb, boolean asPreference) {
		if (hasDataCollectionView()) {
			dataCollectionView.getXML(sb, asPreference);
		}
	}

	/**
	 * This is just a method for implementing the logic in
	 * InputTreeItem.setFocus, because it might not be accessible at early a
	 * time... I tried to do everything in one method to spare
	 *
	 * Comment copied from there (as earlier):
	 *
	 * This method should tell the Input Bar that a focus is scheduled in a
	 * timeout or invokelater or some other method, this is important because
	 * any intentional blur should cancel the schedule (hopefully), so:
	 *
	 * - setFocus shall set setFocusScheduled to false AND call the focus, in
	 * case setFocusAllowed was true but do not call it if setFocusAllowed was
	 * false AND setFocusScheduled was true at the same time
	 *
	 * - any blur event shall set setFocusAllowed to false, in case
	 * setFocusScheduled was true (at least, it can have effect only in this
	 * case)
	 */
	@Override
	public boolean focusScheduled(boolean setNotGet,
			boolean setOrGetScheduledPrioritized, boolean setOrGetAllowed) {
		if (setNotGet) {
			inputBarSetFocusScheduled = setOrGetScheduledPrioritized;
			inputBarSetFocusAllowed = setOrGetAllowed;

			// shall not be used:
			return true;
		} else if (setOrGetScheduledPrioritized) {
			return inputBarSetFocusScheduled;
		} else if (setOrGetAllowed) {
			return inputBarSetFocusAllowed;
		} else {
			// strange, but we need another option of just setting
			// one of them at once, so that focusScheduled can be called
			// many times after one another, with also onBlur being called
			// meanwhile, where the inputBarSetFocusAllowed shall be
			// collected and summed all along the way, while still being
			// in the same scheduled mode! In theory, the allowed
			// property is set to true when the previous setFocus returns
			inputBarSetFocusScheduled = true;
		}

		// shall not be used:
		return true;
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
			return getHelpURL(Help.GENERIC, "Custom_Tools");
		}

		return getHelpURL(Help.TOOL,
				EuclidianConstants.getModeTextSimple(mode));
	}

	@Override
	public void getToolImageURL(final int mode, final GeoImage geoImage,
			final AsyncOperation<String> s) {

		GGWToolBar.getImageResource(mode, getApp(), new HasResource() {

			@Override
			public void setResource(ResourcePrototype mode_tool_32) {
				String url = NoDragImage.safeURI(mode_tool_32);
				String fn = "geogebra_tool_" + mode;
				String zipDirectory = getApp().md5Encrypt(fn);
				fn = zipDirectory + "/" + fn;
				getApp().getImageManager().addExternalImage(fn, url);
				getApp().getImageManager().triggerSingleImageLoading(fn,
						geoImage);
				s.callback(fn);
			}
		});

	}

	/**
	 * @param app
	 *            application
	 * @return whether keyboard may be shown at startup
	 */
	public static boolean mayForceKeyboard(AppW app) {
		return !app.isStartedWithFile()
				&& !app.getArticleElement().preventFocus()
				&& (app.getExam() == null || app.getExam().getStart() > 0);
	}

	@Override
	public void replaceInputSelection(String string) {
		if (getApp().showView(App.VIEW_ALGEBRA)
				&& getApp().getAlgebraView()
				.getInputTreeItem() != null) {
			RadioTreeItem input = getApp().getAlgebraView()
					.getInputTreeItem();
			input.autocomplete(string);
			input.setFocus(true, true);
			input.ensureEditing();
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
			input.setText(string);
			input.setFocus(true, true);
			input.ensureEditing();
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
				return ((ToolbarDockPanelW) (avPanel)).getToolbar();
			}
		}
		return null;
	}

	@Override
	public void switchToolsToAV() {
		getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA)
				.setToolMode(false);
	}

	@Override
	public void addKeyboardAutoHidePartner(GPopupPanel popup) {
		if (onScreenKeyboard != null) {
			onScreenKeyboard.addAutoHidePartner(popup);
		}
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
	public boolean isKeyboardClosedByUser() {
		return this.onScreenKeyboard != null
				&& !this.onScreenKeyboard.shouldBeShown();
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
		getTableValuesView().add(geo);
		getTableValuesView().showColumn((GeoEvaluatable) geo);
		getUnbundledToolbar().openTableView((GeoEvaluatable) geo, true);
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
			getUnbundledToolbar().updateTabs();
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
}
