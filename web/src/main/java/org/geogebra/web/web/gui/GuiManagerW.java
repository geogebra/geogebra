package org.geogebra.web.web.gui;

import java.util.ArrayList;
import java.util.Iterator;

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
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Feature;
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
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.keyboard.HasKeyboard;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;
import org.geogebra.web.keyboard.KeyboardListener;
import org.geogebra.web.keyboard.OnScreenKeyBoard;
import org.geogebra.web.web.cas.view.CASTableW;
import org.geogebra.web.web.cas.view.CASViewW;
import org.geogebra.web.web.cas.view.RowHeaderPopupMenuW;
import org.geogebra.web.web.euclidian.EuclidianStyleBarW;
import org.geogebra.web.web.gui.app.GGWMenuBar;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.browser.BrowseGUI;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.dialog.options.OptionsTab.ColorPanel;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.inputbar.AlgebraInputW;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.LayoutW;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.web.gui.layout.panels.CASDockPanelW;
import org.geogebra.web.web.gui.layout.panels.ConstructionProtocolDockPanelW;
import org.geogebra.web.web.gui.layout.panels.DataAnalysisViewDockPanelW;
import org.geogebra.web.web.gui.layout.panels.DataCollectionDockPanelW;
import org.geogebra.web.web.gui.layout.panels.Euclidian2DockPanelW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.web.gui.layout.panels.FunctionInspectorDockPanelW;
import org.geogebra.web.web.gui.layout.panels.ProbabilityCalculatorDockPanelW;
import org.geogebra.web.web.gui.layout.panels.PropertiesDockPanelW;
import org.geogebra.web.web.gui.layout.panels.SpreadsheetDockPanelW;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.gui.properties.PropertiesViewW;
import org.geogebra.web.web.gui.toolbar.ToolBarW;
import org.geogebra.web.web.gui.util.ScriptArea;
import org.geogebra.web.web.gui.view.algebra.AlgebraControllerW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.web.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;
import org.geogebra.web.web.gui.view.data.DataAnalysisViewW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;
import org.geogebra.web.web.gui.view.probcalculator.ProbabilityCalculatorViewW;
import org.geogebra.web.web.gui.view.spreadsheet.CopyPasteCutW;
import org.geogebra.web.web.gui.view.spreadsheet.MyTableW;
import org.geogebra.web.web.gui.view.spreadsheet.SpreadsheetContextMenuW;
import org.geogebra.web.web.gui.view.spreadsheet.SpreadsheetViewW;
import org.geogebra.web.web.helper.ObjectPool;
import org.geogebra.web.web.html5.AttachedToDOM;
import org.geogebra.web.web.main.AppWFull;
import org.geogebra.web.web.main.AppWapplet;
import org.geogebra.web.web.main.GDevice;
import org.geogebra.web.web.util.keyboard.AutocompleteProcessing;
import org.geogebra.web.web.util.keyboard.GTextBoxProcessing;
import org.geogebra.web.web.util.keyboard.ScriptAreaProcessing;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("javadoc")
public class GuiManagerW extends GuiManager implements GuiManagerInterfaceW,
        EventRenderable {

	/**
	 * container for the Popup that only one exist for a given type
	 */
	public AttachedToDOM currentPopup;

	private AlgebraControllerW algebraController;
	private AlgebraViewW algebraView;
	private SpreadsheetViewW spreadsheetView;
	private final ArrayList<EuclidianViewW> euclidianView2 = new ArrayList<EuclidianViewW>();
	protected BrowseViewI browseGUI;
	protected LayoutW layout;
	protected boolean uploadWaiting;
	private CASViewW casView;
	private Euclidian2DockPanelW euclidianView2DockPanel;
	private String strCustomToolbarDefinition;
	private boolean draggingViews;
	private final ObjectPool objectPool;
	/** device: tbalet / browser */
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
	private ToolBarW updateToolBar = null;
	private DataCollectionView dataCollectionView;
	private OnScreenKeyBoard onScreenKeyboard;

	private int activeViewID;

	private boolean inputBarSetFocusScheduled = false;
	private boolean inputBarSetFocusAllowed = true;

	private GOptionPaneW optionPane;

	private ColorPanel colorPanel;

	private Localization loc;

	/**
	 * 
	 * @param app
	 *            app
	 * @param device
	 *            device (browser / tablet)
	 */
	public GuiManagerW(final AppW app, GDevice device) {
		this.app = app;
		this.loc = app.getLocalization();
		this.device = device;
		this.kernel = app.getKernel();
		this.objectPool = new ObjectPool();
		// AGdialogManagerFactory = new DialogManager.Factory();
	}

	@Override
	public void updateMenubarSelection() {
		final GGWMenuBar mb = getObjectPool().getGgwMenubar();
		if (mb != null && mb.getMenubar() != null) {
			mb.getMenubar().updateSelection();
		}
	}

	@Override
	public void updateMenubar() {
		final GGWMenuBar ggwMenuBar = getObjectPool().getGgwMenubar();
		if (ggwMenuBar != null) {
			final MainMenu menuBar = getObjectPool().getGgwMenubar()
			        .getMenubar();
			if (menuBar != null) {
				menuBar.updateMenubar();
			}
		}
	}

	public ObjectPool getObjectPool() {
		return this.objectPool;
	}

	@Override
	public void updateActions() {
		final GGWMenuBar ggwMenuBar = getObjectPool().getGgwMenubar();
		if (ggwMenuBar != null && ggwMenuBar.getMenubar() != null) {
			ggwMenuBar.getMenubar().updateSelection();
		}
		if (getToolbarPanel() != null) {
			getToolbarPanel().updateUndoActions();
		}
	}

	@Override
	public DialogManager getDialogManager() {
		return app.getDialogManager();
	}

	@Override
	public void showPopupMenu(final ArrayList<GeoElement> selectedGeos,
	        final EuclidianViewInterfaceCommon view, final GPoint mouseLoc) {
		showPopupMenu(selectedGeos, ((EuclidianViewW) view).g2p.getCanvas(),
		        mouseLoc);
	}

	private void showPopupMenu(final ArrayList<GeoElement> geos,
	        final Canvas invoker, final GPoint p) {
		if (geos == null || !app.letShowPopupMenu())
			return;
		if (app.getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			app.getActiveEuclidianView().resetMode();
			getPopupMenu(geos, p).show(invoker, p.x, p.y);
		}
	}

	@Override
	public void showPopupMenu(final ArrayList<GeoElement> geos,
	        final AlgebraView invoker, final GPoint p) {
		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		getPopupMenu(geos, p).show(p);
	}

	public SpreadsheetContextMenuW getSpreadsheetContextMenu(final MyTableW mt) {
		removePopup();
		final SpreadsheetContextMenuW contextMenu = new SpreadsheetContextMenuW(
		        mt);
		currentPopup = (AttachedToDOM) contextMenu.getMenuContainer();
		return contextMenu;
	}

	public RowHeaderPopupMenuW getCASContextMenu(final CASTableW table) {
		removePopup();
		currentPopup = new RowHeaderPopupMenuW(table, (AppW) app);
		return (RowHeaderPopupMenuW) currentPopup;
	}

	public ContextMenuGeoElementW getPopupMenu(
	        final ArrayList<GeoElement> geos, final GPoint location) {
		removePopup();
		currentPopup = new ContextMenuGeoElementW((AppW) app, geos, location);
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

		if (geos == null || !app.letShowPopupMenu())
			return;

		if (!geos.isEmpty() && app.getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(view, p);
		} else {

			final Canvas invoker = ((EuclidianViewWInterface) view).getCanvas();
			// clear highlighting and selections in views
			final GPoint screenPos = new GPoint(invoker.getAbsoluteLeft() + p.x,
			                invoker.getAbsoluteTop() + p.y);

			app.getActiveEuclidianView().resetMode();
			ContextMenuGeoElementW menu = getPopupMenu(view, selectedGeos,
					geos, screenPos, p);
			menu.show(invoker, p.x, p.y);
		}

	}

	private ContextMenuGeoElementW getPopupMenu(
	        final EuclidianView view, final ArrayList<GeoElement> selectedGeos,
	        final ArrayList<GeoElement> geos, final GPoint screenPos,
	        final GPoint p) {
		currentPopup = new ContextMenuChooseGeoW((AppW) app, view,
		        selectedGeos, geos, screenPos, p);
		return (ContextMenuGeoElementW) currentPopup;
	}

	@Override
	public void setFocusedPanel(final AbstractEvent event,
	        final boolean updatePropertiesView) {
		setFocusedPanel(((PointerEvent) event).getEvID(), updatePropertiesView);
	}

	@Override
	public void setFocusedPanel(final int evID,
	        final boolean updatePropertiesView) {

		if (!(((AppW) app).getEuclidianViewpanel() instanceof DockPanel)) {
			Log.debug("This part of the code should not have run!");
			return;
		}

		switch (evID) {
		case App.VIEW_EUCLIDIAN:
			setFocusedPanel((DockPanel) ((AppW) app).getEuclidianViewpanel(),
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

	public void setFocusedPanel(final DockPanel panel,
	        final boolean updatePropertiesView) {
		if (panel != null) {
			getLayout().getDockManager().setFocusedPanel(panel,
			        updatePropertiesView);

			// notify the properties view
			if (updatePropertiesView)
				updatePropertiesView();
		}
	}

	@Override
	public void loadImage(final GeoPoint imageLoc, final Object object,
			final boolean altDown, EuclidianView ev) {
		((AppW) app).getToolbar().closeAllSubmenu();

		if (altDown) {
			// AppW.nativeConsole("alt down");
			Log.debug("trying to paste image");

			// try to paste image in html format eg
			// http://jsfiddle.net/bvFNL/8/
			String html = CopyPasteCutW.getClipboardContents(null);
			// AppW.nativeConsole("from clipboard = " + html);

			// for testing
			// html = "<!--StartFragment--><img
			// src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAYAAACM/rhtAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAYRJREFUeNrslq9ywkAQxi+Zqip0FY+QyLriK1qDLbxBXwFbQ2trktqaVOAjkckbUFVVgcIeu3dfZpJMG/IXGGa/mQ2Bye3+2N3bi1IikUgkEl20nP5c6Qe6ePiSkuuvM/mPDKY3ZLpkG0CfDGxEFgDmF/fPsAC/adyPjlxiEzBGSddkn2S70kPXZFOyW1tyNaFw2yMAFuBCAFaJAWdtId0W6YsawCk8E2JN1DRYQ0DuJ3VH9lITLg/5btcaH0MAcvObUnE23lpkfoG1M/jqE9CMiyXm27zD2J2jF5d1R1ANQD2mC5fl2zZ5Z03gK4DvLoBmx3Jj8+djmzHxRxa31peC7+oZ6R7IXDZOqDRO2uMJm1qfxndclUm3oueSHFw4wGtAmINM/uvJEqDmMRCjrFwKfxi4AqSPWJGNzQwFQO3h/ExyJV0ALlWDy8TwETMreQImz7EHuhHvrA+y18ObQd/Q5b4hyYr8/tTYlDwjn8jG2Vmsz/mF9QqpFYlEIpFIdBrtBRgAu5eF1CVUIv4AAAAASUVORK5CYII=\"
			// style=\"text-rendering: optimizeLegibility; border: 0px; color:
			// rgb(0, 0, 0); font-family: 'Times New Roman'; font-size: medium;
			// font-style: normal; font-variant: normal; font-weight: normal;
			// letter-spacing: normal; line-height: normal; orphans: auto;
			// text-align: start; text-indent: 0px; text-transform: none;
			// white-space: normal; widows: 1; word-spacing: 0px;
			// -webkit-text-stroke-width: 0px;\"><!--EndFragment-->";

			Log.debug("trying to paste image " + html);

			String pngMarker = "data:image/png;base64,";

			int pngBase64index = html.indexOf(pngMarker);

			if (pngBase64index > -1) {
				int pngBase64end = html.indexOf("\"", pngBase64index);
				String base64 = html.substring(
						pngBase64index,
						pngBase64end);

				((AppW) app).imageDropHappened("pastedFromClipboard.png",
						base64, "", imageLoc, 0, 0);

				return;
			}

		}

		((DialogManagerW) getDialogManager()).showImageInputDialog(imageLoc,
				this.device);
	}

	/**
	 * It sometimes happens that a file changes the font size of GUI. The GUI is
	 * not ready for this in Web.
	 */
	@Override
	public void updateFonts() {
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

	private void updateFontSizeStyleElement() {

		final String fontsizeString = app.getGUIFontSize() + "px";
		final int imagesize = (int) Math
				.round(app.getGUIFontSize() * 4.0 / 3.0);
		int toolbariconSize = 2 * app.getGUIFontSize();

		// until we have no enough place for the big icons in the toolbar, don't
		// enable to increase too much the size of icons.
		if (toolbariconSize > 45)
			toolbariconSize = 45;

		// Build inner text for a style element that handles font size
		// =============================================================
		String innerText = ".GeoGebraMenuBar, .GeoGebraPopupMenu, .DialogBox, .gwt-PopupPanel, .ToolTip, .gwt-SuggestBoxPopup";
		innerText += "{font-size: " + fontsizeString + " !important}";

		innerText += ".GeoGebraMenuImage{height: " + imagesize + "px; width: "
		        + imagesize + "px;}";

		innerText += ".GeoGebraMenuBar input[type=\"checkbox\"], .GeogebraMenuBar input[type=\"radio\"], "
		        + ".GeoGebraPopupMenu input[type=\"checkbox\"], .GeogebraPopupMenu input[type=\"radio\"] ";
		innerText += "{height: " + fontsizeString + "; width: "
		        + fontsizeString + ";}";

		innerText += ".toolbar_menuitem{font-size: " + fontsizeString + ";}";
		innerText += ".toolbar_menuitem img{width: " + toolbariconSize + "px;}";

		// ============================================================

		// Create a new style element for font size changes, and remove the old
		// ones, if they already exist. Then add the new element for all
		// GeoGebraWeb applets or application.

		final NodeList<Element> fontsizeElements = Dom
		        .getElementsByClassName("GGWFontsize");
		for (int i = 0; i < fontsizeElements.getLength(); i++) {
			fontsizeElements.getItem(i).removeFromParent();
		}

		final Element fontsizeElement = DOM.createElement("style");
		fontsizeElement.addClassName("GGWFontsize");
		fontsizeElement.setInnerText(innerText);

		final NodeList<Element> geogebrawebElements = Dom
		        .getElementsByClassName("geogebraweb");
		for (int i = 0; i < geogebrawebElements.getLength(); i++) {
			geogebrawebElements.getItem(i).appendChild(fontsizeElement);
		}
	}

	@Override
	public boolean isInputFieldSelectionListener() {
		// TODO Auto-generated method stub
		// Log.debug("unimplemented method");
		return false;
	}

	@Override
	public void showDrawingPadPopup(final EuclidianViewInterfaceCommon view,
	        final GPoint mouseLoc) {
		showDrawingPadPopup(((EuclidianViewW) view).g2p.getCanvas(), mouseLoc);
	}

	@Override
	public void showDrawingPadPopup3D(final EuclidianViewInterfaceCommon view,
			GPoint mouseLoc) {
		// 3D stuff
	}

	private void showDrawingPadPopup(final Canvas invoker, final GPoint p) {
		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		getDrawingPadpopupMenu(p.x, p.y).show(invoker, p.x, p.y);
	}

	private ContextMenuGeoElementW getDrawingPadpopupMenu(final int x,
	        final int y) {
		currentPopup = new ContextMenuGraphicsWindowW((AppW) app, x, y);
		return (ContextMenuGeoElementW) currentPopup;
	}

	@Override
	public boolean hasSpreadsheetView() {
		if (spreadsheetView == null)
			return false;
		if (!spreadsheetView.isShowing())
			return false;
		return true;
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
	public void setShowView(final boolean flag, final int viewId,
	        final boolean isPermanent) {
		if (flag) {
			if (!showView(viewId))
				layout.getDockManager().show(viewId);

			if (viewId == App.VIEW_SPREADSHEET) {
				getSpreadsheetView().requestFocus();
			}
			if (viewId == App.VIEW_DATA_ANALYSIS) {
				getSpreadsheetView().requestFocus();
			}
		} else {
			if (showView(viewId))
				layout.getDockManager().hide(viewId, isPermanent);

			if (viewId == App.VIEW_SPREADSHEET) {
				(app).getActiveEuclidianView().requestFocus();
			}
		}
		((AppW) app).closePopups();
		app.dispatchEvent(new Event(EventType.PERSPECTIVE_CHANGE, null));
		// toolbarPanel.validate();
		// toolbarPanel.updateHelpText();
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
	public View getConstructionProtocolData() {
		// Log.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Editing getCasView() {
		if (casView == null)
			casView = new CASViewW((AppW) app);
		return casView;
	}

	@Override
	public boolean hasCasView() {
		return casView != null;
	}

	@Override
	public SpreadsheetViewW getSpreadsheetView() {
		// init spreadsheet view
		if (spreadsheetView == null) {
			spreadsheetView = new SpreadsheetViewW((AppW) app);
		}

		return spreadsheetView;
	}

	@Override
	public View getProbabilityCalculator() {
		if (probCalculator == null) {
			probCalculator = new ProbabilityCalculatorViewW((AppW) app);
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

		int widthChanged = 0;
		int heightChanged = 0;
		final Element geogebraFrame = ((AppW) app).getFrameElement();

		widthChanged = width - geogebraFrame.getOffsetWidth();
		heightChanged = height - geogebraFrame.getOffsetHeight();
		int borderThickness = ((AppW) app).getArticleElement()
				.getBorderThickness();
		if (getLayout() != null && getLayout().getRootComponent() != null) {
			final Widget root = getLayout().getRootComponent();
			root.setPixelSize(root.getOffsetWidth() + widthChanged,
			        root.getOffsetHeight() + heightChanged);
		} else {
			geogebraFrame.getStyle().setProperty("height",
					height - borderThickness + "px");
			geogebraFrame.getStyle().setProperty("width",
					width - borderThickness + "px");
			((AppW) app).getEuclidianViewpanel().setPixelSize(width,
					height);

			// maybe onResize is OK too
			((AppW) app).getEuclidianViewpanel().deferredOnResize();
		}
		if (this.algebraInput != null) {
			this.algebraInput.setWidth((width - borderThickness)
					+ "px");
			this.algebraInput.setInputFieldWidth(width - borderThickness);
		}
		if (this.toolbarPanel != null) {
			toolbarPanel.setToolbarWidth(width);
		}
		// update view sizes
		((AppW) app).updateViewSizes();
		((AppW) app).recalculateEnvironments();
		app.setPreferredSize(AwtFactory.getPrototype().newDimension(width, height));
	}

	public ToolBarW getGeneralToolbar() {
		return toolbarPanel.getToolBar();
	}

	@Override
	public String getToolbarDefinition() {
		if (strCustomToolbarDefinition == null && getToolbarPanel() != null)
			return getGeneralToolbar().getDefaultToolbarString();
		// return geogebra.web.gui.toolbar.ToolBarW.getAllTools(app);
		return strCustomToolbarDefinition;
	}

	@Override
	public String getToolbarDefinition(final Integer viewId) {
		if (viewId == App.VIEW_CAS)
			return CASView.TOOLBAR_DEFINITION;
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
		layout.initialize((AppW) app);
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
		if (((AppW) app).getEuclidianViewpanel() instanceof DockPanelW) {
			layout.registerPanel((DockPanelW) ((AppW) app)
			        .getEuclidianViewpanel());
		} else {
			Log.debug("This part of the code should not have been called!");
			return false;
		}

		// register spreadsheet view
		layout.registerPanel(new SpreadsheetDockPanelW(app));

		// register algebra view
		layout.registerPanel(new AlgebraDockPanelW());

		// register CAS view
		layout.registerPanel(new CASDockPanelW(app));

		// register EuclidianView2
		layout.registerPanel(getEuclidianView2DockPanel(1));

		// register ConstructionProtocol view
		layout.registerPanel(new ConstructionProtocolDockPanelW((AppW) app));

		// register ProbabilityCalculator view
		layout.registerPanel(new ProbabilityCalculatorDockPanelW(app));

		// register FunctionInspector view
		layout.registerPanel(new FunctionInspectorDockPanelW(app));

		// register Properties view

		layout.registerPanel(new PropertiesDockPanelW((AppW) app));

		// register data analysis view
		layout.registerPanel(new DataAnalysisViewDockPanelW((AppW) app));

		//register data collection view
		layout.registerPanel(new DataCollectionDockPanelW((AppW) app));
		


		return true;
	}

	@Override
	public void setLayout(final Layout layout) {
		this.layout = (LayoutW) layout;
	}

	@Override
	public LayoutW getLayout() {
		return layout;
	}

	public GGWToolBar getToolbarPanel() {
		if (toolbarPanel == null) {
			toolbarPanel = (GGWToolBar) ((AppW) app).getToolbar();
			if (toolbarPanel != null && !toolbarPanel.isInited()) {
				toolbarPanel.init((AppW) app);
			}
		}

		return toolbarPanel;
	}

	@Override
	public void updateToolbar() {
		// if (toolbarPanel != null) {
		// toolbarPanel.buildGui();
		// }

		if (layout != null) {
			// AG layout.getDockManager().updateToolbars();
			if (getToolbarPanel() != null) {
				getToolbarPanel().updateToolbarPanel();
			}
		}
	}

	public void updateToolbarActions() {
		if (getToolbarPanel() != null) {
			getToolbarPanel().updateActionPanel();
		}
	}

	@Override
	public void updateAlgebraInput() {
		Log.debug("Implementation needed...");
	}

	public boolean hasInputHelpPanel() {
		return inputHelpPanel != null;
	}
	@Override
	public InputBarHelpPanelW getInputHelpPanel() {
		if (inputHelpPanel == null) {
			if (app.showView(App.VIEW_CAS)) {
				app.getCommandDictionaryCAS();
			}
			inputHelpPanel = new InputBarHelpPanelW((AppW) app);
		}
		return inputHelpPanel;
	}

	public void reInitHelpPanel(boolean forCAS) {

		if (inputHelpPanel != null) {
			if (forCAS) {
				app.getCommandDictionaryCAS();
			}
			inputHelpPanel.setLabels();
		}
	}

	@Override
	public void setShowAuxiliaryObjects(final boolean flag) {
		if (!hasAlgebraViewShowing())
			return;
		getAlgebraView();
		algebraView.setShowAuxiliaryObjects(flag);
		app.getSettings().getAlgebra().setShowAuxiliaryObjects(flag);
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
			algebraController = new AlgebraControllerW(app.getKernel());
		}
	}

	/**
	 * 
	 * @param algc
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
		if (algebraView != null)
			algebraView.detachView();
	}

	@Override
	public void applyAlgebraViewSettings() {
		if (algebraView != null)
			algebraView.applySettings();
	}

	@Override
	public View getPropertiesView() {

		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = newPropertiesViewW((AppW) app,
					OptionType.EUCLIDIAN);
		}

		return propertiesView;
	}

	public PropertiesView getPropertiesView(OptionType ot) {

		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = newPropertiesViewW((AppW) app, ot);
		}

		return propertiesView;
	}

	/**
	 * 
	 * @param app1
	 *            application
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
		} else {
			// TODO: @Gabor: do we want this? I don't think we want to
			// initialize the view unnecessarily.
			// ((PropertiesViewW)
			// getPropertiesView()).mousePressedForPropertiesView();
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
		app.getLoginOperation().getView().add(this);
	}

	@Override
	public boolean save() {
		return ((AppW) app).getFileManager().save((AppW) app);
	}

	@Override
	public void showPropertiesViewSliderTab() {
		Log.debug("unimplemented");
	}

	@Override
	protected boolean loadURL_GGB(final String url) {
		((AppW) app).loadURL_GGB(url);
		return true;
	}

	@Override
	protected boolean loadURL_base64(final String url) {
		Log.debug("implementation needed");
		return true;
	}

	@Override
	protected boolean loadFromApplet(final String url) throws Exception {
		Log.debug("implementation needed");
		return false;
	}

	@Override
	public void updateGUIafterLoadFile(final boolean success,
	        final boolean isMacroFile) {
		if (success && !isMacroFile
		        && !app.getSettings().getLayout().isIgnoringDocumentLayout()) {

			getLayout().setPerspectives(app.getTmpPerspectives(), null);
			// SwingUtilities.updateComponentTreeUI(getLayout().getRootComponent());

			if (!app.isIniting()) {
				updateFrameSize(); // checks internally if frame is available
				if (app.needsSpreadsheetTableModel())
					(app).getSpreadsheetTableModel(); // ensure create one if
					                                  // not already done
			}
		} else if (isMacroFile && success) {

			refreshCustomToolsInToolBar();
			updateToolbar();
			((AppW) app).updateContentPane();

		}

		// #5320
		getApp().getEuclidianView1().updateFonts();
		if (hasEuclidianView2(1)) {
			((EuclidianView) getEuclidianView2(1)).updateFonts();
		}

		// if (getApp().getEuclidianView3D() != null) {
		// ((EuclidianView3DW) (getApp().getEuclidianView3D())).doRepaint2();
		//
		// }
		// force JavaScript ggbOnInit(); to be called
		if (!app.isApplet())
			app.getScriptManager().ggbOnInit();
	}

	@Override
	public void startEditing(final GeoElement geoElement) {
		Log.debug("unimplemented");

	}

	@Override
	public boolean noMenusOpen() {
		Log.debug("unimplemented");
		return true;
	}

	@Override
	public void openFile() {
		if (!app.isExam()) {
			app.openSearch("");
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
		if (propertiesView != null)
			propertiesView.detachView();
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
		Log.debug("unimplemented");
	}

	@Override
	public void attachProbabilityCalculatorView() {
		getProbabilityCalculator();
		probCalculator.attachView();
	}

	@Override
	public void attachAssignmentView() {
		Log.debug("unimplemented");
	}

	/**
	 * @return Data collection view
	 */
	public DataCollectionView getDataCollectionView() {
		if (dataCollectionView == null) {
			dataCollectionView = new DataCollectionView((AppW) app);
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
		if (layout == null)
			return app.getEuclidianView1();

		final EuclidianDockPanelWAbstract focusedEuclidianPanel = layout
		        .getDockManager().getFocusedEuclidianPanel();

		if (focusedEuclidianPanel != null) {
			return focusedEuclidianPanel.getEuclidianView();
		}
		if (layout.getDockManager().getPanel(App.VIEW_EUCLIDIAN).isVisible()) {
			return app.getEuclidianView1();
		}
		if (layout.getDockManager().getPanel(App.VIEW_EUCLIDIAN2).isVisible()
				&& app.hasEuclidianView2EitherShowingOrNot(1)) {
			return app.getEuclidianView2(1);
		}
		if (layout.getDockManager().getPanel(App.VIEW_EUCLIDIAN3D) != null
				&& layout.getDockManager().getPanel(App.VIEW_EUCLIDIAN3D)
						.isVisible()
				&& app.isEuclidianView3Dinited()) {
			return (EuclidianView) app.getEuclidianView3D();
		}
		return (app).getEuclidianView1();
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
			dataAnalysisView = new DataAnalysisViewW((AppW) app, app
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
		if (dataAnalysisView == null)
			return false;
		if (!dataAnalysisView.isShowing())
			return false;
		return true;
	}

	@Override
	public void detachAssignmentView() {
		Log.debug("unimplemented");
	}

	@Override
	public void detachProbabilityCalculatorView() {
		Log.debug("unimplemented");
	}

	@Override
	public void detachCasView() {
		Log.debug("unimplemented");
	}

	@Override
	public void detachConstructionProtocolView() {
		Log.debug("unimplemented");
	}

	@Override
	public void detachSpreadsheetView() {
		if (spreadsheetView != null)
			spreadsheetView.detachView();
	}

	@Override
	protected void openHelp(final String page, final Help type) {
		try {
			final String helpURL = getHelpURL(type, page);
			Window.open(helpURL, "_blank", "");
		} catch (final MyError e) {
			app.showError(e);
		} catch (final Exception e) {
			Log.debug("openHelp error: " + e.toString() + " " + e.getMessage()
			        + " " + page + " " + type);
			app.showError(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void resetSpreadsheet() {
		if (spreadsheetView != null)
			spreadsheetView.restart();
	}

	@Override
	public void setScrollToShow(final boolean b) {
		if (spreadsheetView != null)
			spreadsheetView.setScrollToShow(b);
	}

	@Override
	public void showURLinBrowser(final String strURL) {
		Log.debug("unimplemented");
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
	public int getInputHelpPanelMinimumWidth() {
		Log.debug("unimplemented");
		return 0;
	}

	@Override
	public void exitAll() {
		Log.debug("unimplemented");
		// TODO Auto-generated method stub

	}

	@Override
	public boolean saveCurrentFile() {
		// TODO Auto-generated method stub
		Log.debug("unimplemented");
		return false;
	}

	@Override
	public boolean hasEuclidianView2(final int idx) {
		if (!this.hasEuclidianView2EitherShowingOrNot(idx)) {
			return false;
		}
		if (idx == 1) {
			return showView(App.VIEW_EUCLIDIAN2);
		}
		if (!euclidianView2.get(idx).isShowing())
			return false;
		return true;
	}

	@Override
	public void allowGUIToRefresh() {
		Log.debug("unimplemented");
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFrameTitle() {
		// TODO Auto-generated method stub
		Log.debug("unimplemented");

	}

	@Override
	public void setLabels() {
		if (algebraInput != null)
			algebraInput.setLabels();

		if (toolbarPanel != null && toolbarPanel.getToolBar() != null) {
			toolbarPanel.getToolBar().buildGui();
			toolbarPanel.setLabels();
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
			for (int i = 0; i < panels.length; i++)
				panels[i].setLabels();
		}
		if (propertiesView != null) {
			((PropertiesViewW) propertiesView).setLabels();
		}
		if (this.dataCollectionView != null) {
			this.dataCollectionView.setLabels();
		}

		((DialogManagerW) app.getDialogManager()).setLabels();
		if (browseGUIwasLoaded()) {
			getBrowseView().setLabels();
		}
	}

	public void resetMenu() {
		final GGWMenuBar bar = getObjectPool().getGgwMenubar();
		if (bar != null && bar.getMenubar() != null) {
			bar.removeMenus();
			bar.init((AppW) app);
		}

	}

	public void updatePrintMenu() {
		// not implemented
	}

	@Override
	public void setShowToolBarHelp(final boolean showToolBarHelp) {
		Log.debug("unimplemented");
		// TODO Auto-generated method stub

	}

	@Override
	public View getEuclidianView2(final int idx) {
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

	public Euclidian2DockPanelW getEuclidianView2DockPanel(final int idx) {
		if (euclidianView2DockPanel == null) {
			euclidianView2DockPanel = new Euclidian2DockPanelW(
					((AppW) app).allowStylebar(), idx);
		}
		return euclidianView2DockPanel;
	}

	public DockPanelW getEuclidian3DPanel() {
		return null;
	}

	protected EuclidianViewW newEuclidianView(final boolean[] showAxis,
	        final boolean showGrid, final int id) {
		if (id == 2) {
			return ((AppW) app).newEuclidianView(getEuclidianView2DockPanel(1),
			        app.newEuclidianController(kernel), showAxis, showGrid, id,
			        app.getSettings().getEuclidian(id));
		}
		return ((AppW) app).newEuclidianView(((AppW) app)
		        .getEuclidianViewpanel(), app.newEuclidianController(kernel),
		        showAxis, showGrid, id, app.getSettings().getEuclidian(id));
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(final int idx) {
		if (euclidianView2 == null || euclidianView2.size() <= idx
		        || euclidianView2.get(idx) == null)
			return false;
		return true;
	}

	@Override
	public void updateFrameSize() {
		// TODO Auto-generated method stub
		Log.debug("unimplemented");

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
		if (!algebraView.isShowing()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasAlgebraView() {
		return algebraView != null;
	}

	@Override
	public void getAlgebraViewXML(final StringBuilder sb,
	        final boolean asPreference) {
		if (algebraView != null)
			algebraView.getXML(sb);
	}

	@Override
	public int getActiveToolbarId() {
		return toolbarID;
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
			getToolbarPanel().setActiveToolbar(Integer.valueOf(toolbarID));
			refreshCustomToolsInToolBar();
			if (toolbarID == App.VIEW_EUCLIDIAN
					|| toolbarID == App.VIEW_EUCLIDIAN2) {
				if (strCustomToolbarDefinition != null) {
					setToolBarDefinition(strCustomToolbarDefinition);
				}
			}

			updateToolbar();
		}

		// in theory, it should do not harm to also set mode here:
		// app.set1rstMode();
	}

	@Override
	public AppW getApp() {
		return (AppW) app;
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
			        .getConstructionProtocolView((AppW) app);
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
		} else
			return;

		if (ep == null)
			return;
		final Iterator<Widget> it = ep.iterator();
		while (it.hasNext()) {
			final Widget nextItem = it.next();
			if (!(nextItem instanceof Canvas))
				it.remove();
		}
	}

	@Override
	public boolean checkAutoCreateSliders(final String s,
			final AsyncOperation<String[]> callback) {
		if (!((AppW) app).enableGraphing()) {
			callback.callback(null);
			return false;
		}
		final String[] options = { loc.getMenu("Cancel"),
				loc.getPlain("CreateSliders") };

		final Image icon = new NoDragImage(
				ImgResourceHelper.safeURI(
						GGWToolBar
		        .getMyIconResourceBundle().mode_slider_32()), 32);
		icon.addStyleName("dialogToolIcon");
		// icon.getElement().getStyle()
		// .setProperty("border", "3px solid steelblue");

		getOptionPane().showOptionDialog(app,
		        loc.getPlain("CreateSlidersForA", s),
				loc.getPlain("CreateSliders"),
				Integer.parseInt(AlgebraProcessor.CREATE_SLIDER),
		        GOptionPane.INFORMATION_MESSAGE, icon, options, callback);

		return false;
	}


	public GOptionPaneW getOptionPane() {
		if (optionPane == null) {
			optionPane = new GOptionPaneW(((AppW) app).getPanel());
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
		// TODO Auto-generated method stub

	}

	/**
	 * @param show
	 *            whether to show the menubar or not
	 */
	@Override
	public void showMenuBar(final boolean show) {
		if (getObjectPool().getGgwMenubar() != null) {
			getObjectPool().getGgwMenubar().setVisible(show);
		} else {
			((AppWapplet) app).getAppletFrame().attachMenubar((AppW) app);
		}
		((AppW) app).closePopups();
	}

	@Override
	public void showToolBar(final boolean show) {
		if (((AppWFull) app).getToolbar() != null) {
			((AppWFull) app).getToolbar().setVisible(show);
		} else {
			((AppWapplet) app).getAppletFrame().attachToolbar((AppW) app);
		}
		((AppW) app).closePopups();
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
			((AppWapplet) app).attachAlgebraInput();
		}
		((AppW) app).closePopups();
	}

	@Override
	protected void setCallerApp() {
		this.caller_APP = WEB;
	}

	@Override
	public int setToolbarMode(final int mode, ModeSetter m) {
		if (toolbarPanel == null) {
			return mode;
		}

		final int ret = toolbarPanel.setMode(mode, m);
		if (this.updateToolBar != null) {
			this.updateToolBar.buildGui();
		}
		// layout.getDockManager().setToolbarMode(mode);
		return ret;
		// return mode;
	}

	private int getAlgebraInputHeight() {
		if (algebraInput != null) {
			return algebraInput.getOffsetHeight();
		}
		return 0;
	}

	private int getToolbarHeight() {
		if (toolbarPanel != null) {
			return toolbarPanel.getOffsetHeight();
		}
		return 0;
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

	public EuclidianViewW getPlotPanelEuclidanView() {
		return (EuclidianViewW) probCalculator.plotPanel;
	}

	public boolean isConsProtNavigationPlayButtonVisible() {
		return getConstructionProtocolNavigation().isPlayButtonVisible();
	}

	public boolean isConsProtNavigationProtButtonVisible() {
		return getConstructionProtocolNavigation().isConsProtButtonVisible();
	}

	@Override
	public void detachView(final int viewId) {
		switch (viewId) {
		case App.VIEW_FUNCTION_INSPECTOR:
			Log.debug("Detaching VIEW_FUNCTION_INSPECTOR");
			((DialogManagerW) app.getDialogManager()).getFunctionInspector()
			        .setInspectorVisible(false);
			break;
		default:
			super.detachView(viewId);
		}
	}

	/**
	 * @return {@link BrowseGUI}
	 */
	@Override
	public BrowseViewI getBrowseView(String query) {
		if (!browseGUIwasLoaded()) {
			this.browseGUI = this.device.createBrowseView((AppW) this.app);
			if (query != null && query.trim().length() > 0) {
				this.browseGUI.displaySearchResults(query);
			} else {
				this.browseGUI.loadAllMaterials();
			}
		}
 else if (query != null && query.trim().length() > 0) {
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
	public void invokeLater(final Runnable runnable) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				runnable.run();
			}
		});
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

	@Override
	public Widget getRootComponent() {
		return getLayout().getRootComponent();
	}

	@Override
	public EuclidianStyleBar newEuclidianStylebar(final EuclidianView ev,
	        int viewID) {
		return new EuclidianStyleBarW(ev, viewID);
	}

	@Override
	public String getMenuBarHtml(final ImageResource imgRes, String name,
	        final boolean b) {

		final String iconString = imgRes.getSafeUri().asString();
		return MainMenu.getMenuBarHtml(iconString, name, true);
	}

	@Override
	public void recalculateEnvironments() {
		for (int i = 0; i < getEuclidianViewCount(); i++) {
			if (hasEuclidianView2(i)) {
				((EuclidianView) getEuclidianView2(i)).getEuclidianController()
			        .calculateEnvironment();
			}
		}
		if (hasProbabilityCalculator()) {
			((ProbabilityCalculatorViewW) getProbabilityCalculator()).plotPanel
			        .getEuclidianController().calculateEnvironment();
		}
	}

	/**
	 * 
	 * @param toolBar
	 *            will be updated every time setMode(int) is called
	 */
	public void setToolBarForUpdate(final ToolBarW toolBar) {
		this.updateToolBar = toolBar;
	}

	@Override
	public void updateStyleBarPositions(boolean menuOpen) {
		for (DockPanelW panel : this.layout.getDockManager().getPanels()) {
			double panelLeftToAppRight = app.getWidth()
					- (panel.getAbsoluteLeft() - ((AppW) app)
					.getAbsLeft())
					/ ((AppW) app).getArticleElement().getScaleX();
			double panelRightToAppRight = panelLeftToAppRight
					- panel.getOffsetWidth();
			if (panel.isVisible()) {
				Log.debug("PANEL" + panelRightToAppRight + ","
					+ panelLeftToAppRight + "," + panel.getViewId());
			}
			if (menuOpen && panel.isVisible()
					&& panelRightToAppRight < GLookAndFeel.MENUBAR_WIDTH) {
				if (panelLeftToAppRight > GLookAndFeel.MENUBAR_WIDTH) {
					// -2 necessary because of style-settings for the StyleBar
					// and the Menu
					panel.showStyleBarPanel(true);
					panel.setStyleBarRightOffset(GLookAndFeel.MENUBAR_WIDTH
							- (int) panelRightToAppRight - 2);
				} else {
					panel.showStyleBarPanel(false);
				}
			} else {
				panel.showStyleBarPanel(true);
				panel.setStyleBarRightOffset(0);
			}
		}
	}

	/**
	 * shows the downloadDialog
	 */
	@Override
	public void exportGGB() {
		getOptionPane().showSaveDialog(app, loc.getMenu("Save"),
				app.getExportTitle() + ".ggb", null,
				new AsyncOperation<String[]>() {

					@Override
					public void callback(String[] obj) {
						getApp().dispatchEvent(
								new Event(EventType.EXPORT, null, "[\"ggb\"]"));
						if (Browser.isXWALK()) {
							getApp().getGgbApi().getBase64(true,
									getStringCallback(obj[1]));
						}

				else if (Integer.parseInt(obj[0]) == 0) {
							getApp().getGgbApi().getGGB(true,
									getDownloadCallback(obj[1]));
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
			a.href = "data:application/vnd.geogebra.file;base64," + base64;
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

	@Override
	public final void renderEvent(final BaseEvent event) {
		if (this.uploadWaiting && event instanceof LoginEvent
		        && ((LoginEvent) event).isSuccessful()) {
			this.uploadWaiting = false;
			save();
		} else if (this.uploadWaiting && event instanceof StayLoggedOutEvent) {
			this.uploadWaiting = false;
			((AppW) app).getFileManager().saveLoggedOut((AppW) app);
		}
	}

	@Override
	public BrowseViewI getBrowseView() {
		return getBrowseView(null);
	}

	public String getDefaultToolbarString() {
		if (toolbarPanel == null)
			return "";

		return getGeneralToolbar().getDefaultToolbarString();
	}

	@Override
	public OnScreenKeyBoard getOnScreenKeyboard(MathKeyboardListener textField,
			UpdateKeyBoardListener listener) {
		if (onScreenKeyboard == null) {

			boolean korean = app.has(Feature.KOREAN_KEYBOARD);
			onScreenKeyboard = new OnScreenKeyBoard((HasKeyboard) app, korean);
		}

		if (textField != null) {
				onScreenKeyboard.setProcessing(makeKeyboardListener(textField));
		}

		onScreenKeyboard.setListener(listener);
		return onScreenKeyboard;
	}

	public static KeyboardListener makeKeyboardListener(
			MathKeyboardListener textField) {
		if (textField instanceof RetexKeyboardListener) {
			return new MathFieldProcessing(
					((RetexKeyboardListener) textField).getMathField());
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
		if (textField instanceof EquationEditorListener) {
			return ((EquationEditorListener) textField).getKeyboardListener();
		}
		if (textField instanceof ScriptArea) {
			return new ScriptAreaProcessing((ScriptArea) textField);
		}

		return null;

	}

	@Override
	public void setOnScreenKeyboardTextField(MathKeyboardListener textField) {
		if (onScreenKeyboard != null) {
			onScreenKeyboard.setProcessing(makeKeyboardListener(textField));
		}
	}

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

	public String getTooltipURL(int mode) {
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			return getHelpURL(Help.GENERIC, "Custom_Tools");
		}

		return getHelpURL(Help.TOOL,
				EuclidianConstants.getModeTextSimple(mode));
	}

	public String getToolImageURL(int mode, GeoImage geoImage) {
		String url = GGWToolBar.getImageURL(mode, (AppW) app);
		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String fn = "geogebra_tool_" + mode;
		String zip_directory = md5e.encrypt(fn);
		fn = zip_directory + "/" + fn;
		((AppW) app).getImageManager().addExternalImage(fn, url);
		((AppW) app).getImageManager().triggerSingleImageLoading(fn, geoImage);
		return fn;
	}

	public static boolean mayForceKeyboard(AppW app) {
		return !app.isStartedWithFile()
				&& (app.getExam() == null || app.getExam().getStart() > 0);
	}

	public void handleFKeyForAlgebra(int fkey, GeoElement geo) {
		if (app.showView(App.VIEW_ALGEBRA)
				&& ((AlgebraViewW) app.getAlgebraView())
						.getInputTreeItem() != null) {
		((AlgebraViewW) app.getAlgebraView()).getInputTreeItem()
				.handleFKey(fkey, geo);
		}

	}

	@Override
	public void replaceInputSelection(String string) {
		if (app.showView(App.VIEW_ALGEBRA)
				&& ((AlgebraViewW) app.getAlgebraView())
						.getInputTreeItem() != null) {
			RadioTreeItem input = ((AlgebraViewW) app.getAlgebraView())
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
		if (app.showView(App.VIEW_ALGEBRA)
				&& ((AlgebraViewW) app.getAlgebraView())
						.getInputTreeItem() != null
				&& app.getInputPosition() == InputPosition.algebraView) {
			RadioTreeItem input = ((AlgebraViewW) app.getAlgebraView())
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

}
