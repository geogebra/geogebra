package org.geogebra.web.full.gui.layout;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.layout.DockComponent;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.web.full.cas.view.CASStylebarW;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ContextMenuGraphicsWindowW;
import org.geogebra.web.full.gui.app.ShowKeyboardButton;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetStyleBarW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.GPushButton;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Every object which should be dragged needs to be of type DockPanel. A
 * DockPanel will wrap around the component with the real contents (e.g. the
 * EuclidianView) and will add a title bar if the user is not in the
 * "layout fixed" mode. The user can move the DockPanel by dragging the title
 * bar.
 * 
 * To add a new dock panel one has to subclass DockPanel, implement the abstract
 * method DockPanel::loadComponent() and maybe replace DockPanel::getIcon() and
 * DockPanel::getStyleBar().
 * 
 * One can add a panel using Layout::registerPanel(), the GuiManager also
 * provides GuiManager()::initLayoutPanels() as an easy access point to add new
 * panels. This is also important because it matters at which point of execution
 * a panel is added, see Layout::registerPanel() for further information.
 * 
 * @author Florian Sonner
 */
public abstract class DockPanelW extends ResizeComposite
		implements DockPanel, DockComponent {
	/** Dock manager */
	protected DockManagerW dockManager;
	/** app */
	protected AppW app;
	private boolean longStyleBar = false;

	/**
	 * The ID of this dock panel.
	 */
	protected int id;

	/**
	 * The title of this dock panel.
	 */
	private final String title;

	/**
	 * If this panel is visible.
	 */
	protected boolean visible = false;

	/**
	 * The dimensions of the external window of this panel.
	 */
	protected Rectangle frameBounds = new Rectangle(50, 50, 500, 500);

	/**
	 * If there is a style bar associated with this panel.
	 */
	private final boolean hasStyleBar;

	private int embeddedDimWidth;
	private int embeddedDimHeight;

	/**
	 * Style bar component.
	 */
	Widget styleBar;
	/**
	 * If the style bar is visible.
	 */
	protected boolean showStyleBar = false;

	/**
	 * String which stores the position of the panel in the layout.
	 */
	protected String embeddedDef = "1";

	/**
	 * The size of the panel in the layout, may be either the width or height
	 * depending upon embeddedDef.
	 */
	protected int embeddedSize = 150;

	/**
	 * Panel for the styling bar if one is available.
	 */
	private FlowPanel styleBarPanel;

	/**
	 * Toolbar definition string associated with this panel or null if this
	 * panel has no toolbar. Always contains the string of the perspective
	 * loaded last.
	 */
	protected String toolbarString;

	/**
	 * Default toolbar definition string associated with this panel or null if
	 * this panel has no toolbar. This string is specified in the constructor
	 * and won't change.
	 */
	private final String defaultToolbarString;

	/**
	 * The component used for this view.
	 */
	protected Widget component;

	/**
	 * The location of this panel in the view menu. If -1 this panel won't
	 * appear there at all.
	 */
	private int menuOrder;

	/**
	 * Shortcut to show this panel, SHIFT is automatically used as modifier,
	 * \u0000 is the default value.
	 */
	private char menuShortcut;

	/**
	 * Indicator whether this panel is the last one in the main frame. In this
	 * case no title bar will be visible, but just the stylebar.
	 */
	private boolean alone;

	/**
	 * Indicator whether this panel is hidden. A hidden panel is not visible,
	 * but it's View component is still attached to the kernel.
	 */
	private boolean hidden;

	/**
	 * Flag to determine if the frame field will be created as a JDialog (true)
	 * or as a JFram (false). Default is false.
	 */
	private boolean dialog = false;

	/**
	 * Images for Stylingbar
	 */
	private Image dragIcon;
	private Image closeIcon;

	/** dock panel */
	MyDockLayoutPanel dockPanel;
	/** the main panel of this stylebar */
	FlowPanel titleBarPanel;
	private FlowPanel titleBarPanelContent;

	private GPushButton closeButton;
	private FlowPanel dragPanel;
	private FlowPanel closeButtonPanel;

	private VerticalPanel componentPanel;
	/** button to collapse / expand stylebar */
	protected StandardButton toggleStyleBarButton;
	/**
	 * button to open graphics context menu
	 */
	protected StandardButton graphicsContextMenuBtn;

	private ResourcePrototype viewImage;

	// needs to be initialized here, because the button might be added in
	// adSouth(), before setLayout() is called
	private final SimplePanel kbButtonSpace = new SimplePanel();

	/**
	 * For calling the onResize method in a deferred way
	 */
	Scheduler.ScheduledCommand deferredOnRes = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			onResize();
		}
	};

	/**
	 * For calling the onResize method in a deferred way
	 */
	@Override
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	/**
	 * @return true if this dock panel frame will be created as a JDialog. If
	 *         false then it will be created as a JFrame
	 * 
	 */
	public boolean isDialog() {
		return dialog;
	}

	/**
	 * Sets the isDialog flag.
	 * 
	 * @param isDialog
	 *            true if this dock panel frame will be created as a JDialog. If
	 *            false then it will be created as a JFrame
	 */
	public void setDialog(boolean isDialog) {
		this.dialog = isDialog;
	}

	/**
	 * Prepare dock panel. DockPanel::register() has to be called to make this
	 * panel fully functional! No shortcut is assigned to the view in this
	 * construtor.
	 * 
	 * @param id
	 *            The id of the panel
	 * @param title
	 *            The title phrase of the view located in plain.properties
	 * @param toolbar
	 *            The default toolbar string (or null if this view has none)
	 * @param hasStyleBar
	 *            If a style bar exists
	 * @param menuOrder
	 *            The location of this view in the view menu, -1 if the view
	 *            should not appear at all
	 */
	public DockPanelW(int id, String title, String toolbar, boolean hasStyleBar,
			int menuOrder) {
		this(id, title, toolbar, hasStyleBar, menuOrder, '\u0000');
	}

	/**
	 * Prepare dock panel. DockPanel::register() has to be called to make this
	 * panel fully functional!
	 * 
	 * @param id
	 *            The id of the panel
	 * @param title
	 *            The title phrase of the view located in plain.properties
	 * @param toolbar
	 *            The default toolbar string (or null if this view has none)
	 * @param hasStyleBar
	 *            If a style bar exists
	 * @param menuOrder
	 *            The location of this view in the view menu, -1 if the view
	 *            should not appear at all
	 * @param menuShortcut
	 *            The shortcut character which can be used to make this view
	 *            visible
	 */
	public DockPanelW(int id, String title, String toolbar, boolean hasStyleBar,
			int menuOrder, char menuShortcut) {
		this.id = id;
		this.title = title;
		this.defaultToolbarString = toolbar;

		// this is different in Web and Desktop!
		setToolbarString(defaultToolbarString);

		this.menuOrder = menuOrder;
		this.menuShortcut = menuShortcut;
		this.hasStyleBar = hasStyleBar;
		this.alone = false;
	}

	/**
	 * @return The icon of the menu item, if this method was not overwritten it
	 *         will return the empty icon or null for Win Vista / 7 to prevent
	 *         the "checkbox bug"
	 */
	public ResourcePrototype getIcon() {
		return AppResources.INSTANCE.empty();
	}

	/**
	 * @return The style bar. Note: Unless this method is overridden a dummy
	 *         stylebar is returned.
	 */
	protected Widget loadStyleBar() {
		return new SimplePanel();
	}

	/**
	 * @return The main panel of this view.
	 */
	protected abstract Widget loadComponent();

	/**
	 * @return The main panel of this view (null if none was loaded yet).
	 */
	public Widget getComponent() {
		return component;
	}

	/**
	 * Bind this view to a dock manager. Also initializes the whole GUI as just
	 * at this point the application is available.
	 * 
	 * @param dockManager1
	 *            dock manager
	 */
	public void register(DockManagerW dockManager1) {
		this.dockManager = dockManager1;
		app = dockManager1.getLayout().getApplication();

		buildDockPanel();

		// buildGUI should be called in a lazy way!
		// buildGUI();
	}

	/**
	 * @return height in pixels
	 */
	public int getHeight() {
		return dockPanel.getOffsetHeight();
	}

	/**
	 * @return width in pixels
	 */
	public int getWidth() {
		return dockPanel.getOffsetWidth();
	}

	/**
	 * Create the UI
	 */
	protected void buildDockPanel() {
		// guard against repeated call
		// while creating DockPanel based GUI (problem with early init of EV)
		if (dockPanel != null) {
			return;
		}

		dockPanel = new MyDockLayoutPanel();
		initWidget(dockPanel);
	}

	/**
	 * Build gui, has no effect when called the second time
	 * 
	 * @param setlayout
	 *            whether to also set layout
	 */
	public void buildGUIIfNecessary(boolean setlayout) {

		// This way it is safe to call buildGUI multiple times
		if (componentPanel != null) {
			return;
		}
		// This also acts as a boolean to show whether this
		// method has already been called
		componentPanel = new VerticalPanel();
		componentPanel.setStyleName("ComponentPanel");

		styleBarPanel = new FlowPanel();
		styleBarPanel.setStyleName("StyleBarPanel_");

		titleBarPanel = new FlowPanel();
		titleBarPanel.setStyleName("TitleBarPanel");
		if (!app.isUnbundledOrWhiteboard()) {
			titleBarPanel.addStyleName("TitleBarClassic");
		}
		titleBarPanel.addStyleName("cursor_drag");

		// kbButtonSpace = new SimplePanel();

		titleBarPanelContent = new FlowPanel();

		updateStyles();

		titleBarPanel.add(titleBarPanelContent);

		if (closeIcon == null) {
			closeIcon = new Image(GuiResources.INSTANCE.dockbar_close());
		}
		closeButton = new GPushButton(closeIcon);
		closeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				app.getGuiManager().setShowView(false, DockPanelW.this.id);

			}
		});
		closeButtonPanel = new FlowPanel();
		closeButtonPanel.setStyleName("closeButtonPanel");
		closeButtonPanel.setVisible(isStyleBarEmpty());
		closeButtonPanel.add(closeButton);

		titleBarPanelContent.add(styleBarPanel);
		addDragPanel();

		if (!this.isStyleBarEmpty()) {
			addToggleButton();
		}

		titleBarPanelContent.setVisible(!isStyleBarEmpty());

		if (this.isStyleBarEmpty()) {
			titleBarPanel.add(closeButtonPanel);
		}
		tryBuildZoomPanel();

		if (app.getGuiManager().isDraggingViews()) {
			enableDragging(true);
		}

		if (setlayout) {
			setLayout(false);
		}
	}

	private void updateStyles() {
		if (titleBarPanelContent != null) {
			titleBarPanelContent.setStyleName("MatTitleBarPanelContent",
					app.isUnbundledOrWhiteboard());
			titleBarPanelContent.setStyleName("TitleBarPanelContent",
					!app.isUnbundledOrWhiteboard());
		}
	}

	private void addDragPanel() {
		if (titleBarPanelContent == null) {
			return;
		}
		boolean dragNeeded = !app.isUnbundled();
		if (dragNeeded && dragPanel == null) {
			dragPanel = new FlowPanel();
			dragPanel.setStyleName("dragPanel");
			ClickStartHandler.init(dragPanel,
					new ClickStartHandler(true, false) {
						@Override
						public void onClickStart(int x, int y,
								PointerEventType type) {
							startDragging();
						}
					});
			dragPanel.setVisible(false);
			if (dragIcon == null) {
				dragIcon = new Image(GuiResources.INSTANCE.dockbar_drag());
				/*
				 * Prevent default image drag from interfering with view drag --
				 * needed for IE
				 */
				dragIcon.addDragHandler(new DragHandler() {
					@Override
					public void onDrag(DragEvent event) {
						event.preventDefault();
					}
				});
			}
			dragPanel.add(dragIcon);
			titleBarPanelContent.add(dragPanel);
		} else if (!dragNeeded && dragPanel != null) {
			titleBarPanelContent.remove(dragPanel);
			dragPanel = null;
		}
	}

	/**
	 * Reset stylebar
	 */
	public void resetStylebar() {
		updateStyles();
		addDragPanel();
		if (!this.isStyleBarEmpty()) {
			addToggleButton();
		}
	}

	/** Builds zoom panel */
	public void tryBuildZoomPanel() {
		// overridden in EV
	}

	/**
	 * Switch panel to drag mode
	 */
	protected void startDragging() {
		if (componentPanel == null) {
			return;
		}

		dockManager.drag(this);

	}

	private void addToggleButton() {
		if (titleBarPanelContent == null) {
			return;
		}

		((AppWFull) app).getActivity().initStylebar(this);
	}

	/**
	 * Initialize stylebar
	 */
	public void initToggleButton() {
		if (graphicsContextMenuBtn != null) {
			graphicsContextMenuBtn.removeFromParent();
			graphicsContextMenuBtn = null;
		}
		// always show the view-icon; othrwise use showStylebar as parameter
		toggleStyleBarButton = new StandardButton(getToggleImage(false), null,
				32, 24, app);
		toggleStyleBarButton.addStyleName("toggleStyleBar");

		if (!showStyleBar && viewImage != null) {
			toggleStyleBarButton.addStyleName("toggleStyleBarViewIcon");
		}

		FastClickHandler toggleStyleBarHandler = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				setShowStyleBar(!showStyleBar);

				updateStyleBarVisibility();
				if (styleBar instanceof StyleBarW) {
					((StyleBarW) styleBar).setOpen(showStyleBar);
				}
			}

		};
		toggleStyleBarButton.addFastClickHandler(toggleStyleBarHandler);
		titleBarPanelContent.add(toggleStyleBarButton);
	}

	/**
	 * Initialize stylebar with a gear icon for graphics settings
	 */
	public void initGraphicsSettingsButton() {
		if (graphicsContextMenuBtn != null) {
			return;
		}
		graphicsContextMenuBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.settings_border(), null, 24,
				app);
		graphicsContextMenuBtn
				.setTitle(app.getLocalization().getMenu("Settings"));
		final FocusableWidget focusableWidget = new FocusableWidget(
				AccessibilityGroup.getViewGroup(getViewId()),
				AccessibilityGroup.ViewControlId.SETTINGS_BUTTON,  graphicsContextMenuBtn);
		if (getViewId() == App.VIEW_EUCLIDIAN) {
			focusableWidget.attachTo(app);
		}
		FastClickHandler graphicsContextMenuHandler = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				app.getAccessibilityManager().setAnchor(focusableWidget);
				onGraphicsSettingsPressed();
			}

		};
		graphicsContextMenuBtn.addFastClickHandler(graphicsContextMenuHandler);
		graphicsContextMenuBtn.addStyleName("flatButton");
		graphicsContextMenuBtn.addStyleName(app.isWhiteboardActive()
				? "graphicsContextMenuBtn mow" : "graphicsContextMenuBtn");
		titleBarPanelContent.add(graphicsContextMenuBtn);
		graphicsContextMenuBtn.getElement().setTabIndex(0);
		TestHarness.setAttr(graphicsContextMenuBtn, "graphicsViewContextMenu");
		if (toggleStyleBarButton != null) {
			toggleStyleBarButton.removeFromParent();
			toggleStyleBarButton = null;
		}
	}

	/** Graphics Settings button handler */
	protected void onGraphicsSettingsPressed() {
		app.hideMenu();
		if (app.isWhiteboardActive() && app.getAppletFrame() != null
				&& app.getAppletFrame() instanceof GeoGebraFrameFull) {
			((GeoGebraFrameFull) app.getAppletFrame()).deselectDragBtn();
		}
		int x = graphicsContextMenuBtn.getAbsoluteLeft();
		final int y = 8;
		final ContextMenuGraphicsWindowW contextMenu = new ContextMenuGraphicsWindowW(
				app, x, y, false);
		final GPopupPanel popup = contextMenu.getWrappedPopup().getPopupPanel();
		popup.setPopupPositionAndShow(new GPopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				popup.setPopupPosition((int) app.getWidth() - offsetWidth, y);
				contextMenu.getWrappedPopup().getPopupMenu().focusDeferred();
			}
		});
		popup.addCloseHandler(new CloseHandler<GPopupPanel>() {

			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				if (event.isAutoClosed()) {
					app.getEuclidianView1().getEuclidianController()
							.setPopupJustClosed(true);
				}
			}
		});
	}

	/**
	 * Update localization
	 */
	public void setLabels() {
		if (toggleStyleBarButton != null) {
			toggleStyleBarButton
					.setTitle(app.getLocalization().getMenu("ToggleStyleBar"));
		}
	}

	/**
	 * sets the layout of the stylebar and title panel
	 * 
	 * @param deferred
	 *            whether to set the layout in deferred call
	 */
	protected void setLayout(boolean deferred) {

		if (!isVisible()) {
			return;
		}

		buildGUIIfNecessary(false);

		dockPanel.clear();
		if (hasStyleBar()) {

			if (app.getSettings().getLayout().showTitleBar()
					&& (app.allowStylebar() || needsResetIcon()
							|| forceCloseButton())) {

				dockPanel.addNorth(titleBarPanel, 0);
			}

			if (isStyleBarVisible()) {
				setStyleBar();
				updateStyleBarVisibility();
			}
			if (styleBar instanceof StyleBarW) {
				((StyleBarW) styleBar).setOpen(showStyleBar);
			}
			updateStyleBarVisibility();
		}

		addZoomPanel(dockPanel);

		if (!app.allowStylebar() && needsResetIcon()) {
			showResetIcon();
		}
		dockPanel.addSouth(kbButtonSpace, 0);

		if (component != null) {
			dockPanel.add(component);
		} else {
			dockPanel.add(componentPanel);
		}

		if (deferred) {
			deferredOnResize();
		} else {
			onResize();
		}
	}

	/**
	 * Adds a panel zoom buttons on it.
	 * 
	 * @param dockPanel2
	 *            panel to add to.
	 */
	protected void addZoomPanel(MyDockLayoutPanel dockPanel2) {
		// TODO Auto-generated method stub

	}

	private boolean forceCloseButton() {
		return getViewId() == App.VIEW_PROPERTIES
				&& app.getAppletParameters().getDataParamEnableRightClick();
	}

	/**
	 * @return whether reset icon should be shown in this panel (only EV1/2, 3D)
	 */
	protected boolean needsResetIcon() {
		return false;
	}

	/**
	 * @return pixel height; physical or preferred when not visible
	 */
	public int getComponentInteriorHeight() {
		if (dockPanel != null) {
			int h = (int) dockPanel.getCenterHeight();
			if (h == 0 && this.getParentSplitPane() != null) {
				return this.getParentSplitPane().getPreferredHeight(this);
			}
			return h;
		}
		return 0;
	}

	/**
	 * @return pixel width; physical or preferred when not visible
	 */
	public int getComponentInteriorWidth() {
		if (dockPanel != null) {
			int w = (int) dockPanel.getCenterWidth();
			if (w == 0 && this.getParentSplitPane() != null) {
				return this.getParentSplitPane().getPreferredWidth(this);
			}
			return w;
		}
		return 0;
	}

	/**
	 * extends DockLayoutPanel to expose getCenterHeight() and getCenterWidth()
	 * TODO: move some code above into this class, e.g. setLayout(), or possibly
	 * extend DockPanelW itself
	 */
	public static class MyDockLayoutPanel extends DockLayoutPanel {
		/**
		 * Create new dock panel
		 */
		public MyDockLayoutPanel() {
			super(Style.Unit.PX);
			addStyleName("ggbdockpanelhack");
		}

		@Override
		public double getCenterHeight() {
			return super.getCenterHeight();
		}

		@Override
		public double getCenterWidth() {
			return super.getCenterWidth();
		}

		@Override
		public void add(Widget w) {
			if (this.getCenter() != null) {
				Log.error("TODO: dock panel filled twice");
				this.remove(this.getCenter());
			}
			super.add(w);
		}
	}

	/**
	 * A panel is 'alone' if no other panel is visible in the main frame. In
	 * this case no title bar is displayed, but just the style bar. Changing the
	 * value of the 'alone' state will cause the GUI to update automatically if
	 * this panel is visible.
	 * 
	 * @param isAlone
	 *            whether this is in own window
	 */
	public void setAlone(boolean isAlone) {
		if (this.alone == isAlone) {
			return;
		}

		this.alone = isAlone;

		if (isVisible()) {
			updatePanel(true);
		}
	}

	/**
	 * @return If this panel thinks it's the last visible one in the main frame.
	 */
	public boolean isAlone() {
		return alone;
	}

	/**
	 * @return If this panel is hidden but not permanently removed.
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Sets the the isHidden flag (no other action)
	 * 
	 * @param isHidden
	 *            hidden flag
	 */
	public void setHidden(boolean isHidden) {
		this.hidden = isHidden;
	}

	/**
	 * Update the panel.
	 * 
	 * @param deferred
	 *            whether to update it from a timer
	 */
	public final void updatePanel(boolean deferred) {

		if (!isVisible()) {
			return;
		}

		if (component == null) {
			component = loadComponent();
		}
		setLayout(deferred);
		// ignore
	}

	/**
	 * Build the toolbar GUI.
	 */
	public void buildToolbarGui() {
		// TODO implement or delete
	}

	/**
	 * Update the toolbar GUI.
	 */
	public void updateToolbar() {
		app.getGuiManager().setActivePanelAndToolbar(id);
	}

	/**
	 * Update fonts.
	 */
	public void updateFonts() {
		// TODO implement or delete
	}

	/**
	 * Close this panel.
	 * 
	 * @param isPermanent
	 *            whether it should be also detached from kernel
	 */
	protected void closePanel(boolean isPermanent) {
		dockManager.closePanel(this, isPermanent);
	}

	@Override
	public void closePanel() {
		closePanel(true);
	}

	/** loads the styleBar and puts it into the styleBarPanel */
	private void setStyleBar() {
		if (styleBar == null && !app.isUnbundled()) {
			buildGUIIfNecessary(false);
			styleBar = loadStyleBar();
			styleBarPanel.add(styleBar);
		}
	}

	/**
	 * Update the style bar visibility.
	 */
	public void updateStyleBarVisibility() {
		if (!isVisible()) {
			return;
		}

		buildGUIIfNecessary(true);

		if (app.isUnbundledOrWhiteboard() && toggleStyleBarButton != null) {
			if (!showStyleBar) {
				toggleStyleBarButton.setIcon(viewImage);
			}
		}

		styleBarPanel.setVisible(isStyleBarVisible());
		if (isStyleBarVisible()) {
			setStyleBar();
			if (styleBar != null) {
				styleBar.setVisible(
					showStyleBar && !app.getGuiManager().isDraggingViews());
			}
		}
		if (styleBar instanceof SpreadsheetStyleBarW
				|| styleBar instanceof CASStylebarW
				|| styleBar instanceof AlgebraStyleBarW) {
			setStyleBarLongVisibility(isStyleBarVisible());
		}
	}

	/**
	 * Sets style bar visibility to true and false. When visible, style bar
	 * occupies a space of a full row (instead of floating). E.g. in CASView and
	 * SpreadsheetView
	 * 
	 * @param value
	 *            true to show style bar
	 */
	private void setStyleBarLongVisibility(boolean value) {
		// in applets title bar may be null and view menu still enabled
		if (app.allowStylebar() && titleBarPanel != null
				&& titleBarPanel.getLayoutData() != null) {
			dockPanel.setWidgetSize(titleBarPanel, value ? 44 : 0);
			titleBarPanel.setStyleName("TitleBarPanel-open", value);
			setLongStyleBar(value);
			deferredOnResize();
		}
	}

	/**
	 * @return The parent DockSplitPane or null.
	 */
	public DockSplitPaneW getParentSplitPane() {
		Widget parent = getParent();

		if (!(parent instanceof DockSplitPaneW)) {
			return null;
		}

		return (DockSplitPaneW) parent;
	}

	/**
	 * @return The embedded def string for this DockPanel.
	 */
	public String calculateEmbeddedDef() {
		StringBuilder def = new StringBuilder();

		Widget current = this;
		Widget parent = this.getParent();
		DockSplitPaneW parentDSP;

		while (parent instanceof DockSplitPaneW) {
			int defType;

			parentDSP = (DockSplitPaneW) parent;

			if (parentDSP.getOrientation() == SwingConstants.HORIZONTAL_SPLIT) {
				if (current == parentDSP.getLeftComponent()) {
					defType = 3;
				} else {
					// right
					defType = 1;
				}
			} else {
				if (current == parentDSP.getLeftComponent()) {
					defType = 0;
				} else {
					// bottom
					defType = 2;
				}
			}

			if (def.length() == 0) {
				def.append(defType);
			} else {
				def.append(",");
				def.append(defType);
			}

			current = parent;
			parent = current.getParent();
		}

		// gwt does not support reverse() ??
		// return def.reverse().toString();

		// reverse is OK for GWT 2.8
		return def.reverse().toString();
	}

	/**
	 * @return The XML container which stores all relevant information for this
	 *         panel.
	 */
	public DockPanelData createInfo() {
		return new DockPanelData(id, getToolbarString(), visible, false,
				showStyleBar, new Rectangle(frameBounds), embeddedDef,
				embeddedSize);
	}

	/**
	 * If the stylebar of this view should be visible. Has no immediate effect.
	 * 
	 * @param showStyleBar
	 *            whether to show stylebar
	 */
	public void setShowStyleBar(boolean showStyleBar) {
		this.showStyleBar = showStyleBar;
		if (app != null) {
			app.dispatchEvent(new Event(EventType.SHOW_STYLE_BAR, null,
					"[" + showStyleBar + "," + getViewId() + "]"));
		}
	}

	/**
	 * @return If the style bar should be visible.
	 */
	public boolean isStyleBarVisible() {
		if (id == App.VIEW_EUCLIDIAN || id == App.VIEW_EUCLIDIAN2
				|| id == App.VIEW_ALGEBRA) {
			if (!app.getSettings().getLayout().isAllowingStyleBar()) {
				return false;
			}
		}
		return showStyleBar;
	}

	/**
	 * just return hasStyleBar - overridden for spreadsheet
	 * 
	 * @return hasStyleBar
	 */
	protected boolean hasStyleBar() {
		return hasStyleBar;
	}

	/**
	 * @param frameBounds
	 *            frame bounds
	 */
	public void setFrameBounds(Rectangle frameBounds) {
		this.frameBounds = frameBounds;
	}

	/**
	 * @return frame bounds
	 */
	public Rectangle getFrameBounds() {
		return this.frameBounds;
	}

	/**
	 * @param embeddedDef
	 *            the embeddedDef to set
	 */
	public void setEmbeddedDef(String embeddedDef) {
		this.embeddedDef = embeddedDef;
	}

	/**
	 * @return psosition in the UI tree of the splitpanes
	 */
	public String getEmbeddedDef() {
		return embeddedDef;
	}

	/**
	 * @param embeddedSize
	 *            the embeddedSize to set
	 */
	public void setEmbeddedSize(int embeddedSize) {
		this.embeddedSize = embeddedSize;
	}

	/**
	 * @return the embeddedSize
	 */
	public int getEmbeddedSize() {
		return embeddedSize;
	}

	/**
	 * @return If this DockPanel is visible.
	 */
	@Override
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Mark this panel as focused. When gaining focus the panel will
	 * automatically request focus for its parent frame.
	 *
	 * @param updatePropertiesView
	 *            update properties view
	 */
	public void setFocus(boolean updatePropertiesView) {
		if (updatePropertiesView) {
			app.getGuiManager().updatePropertiesView();
		}

		setActiveToolBar();
	}

	/**
	 * sets the active toolbar
	 */
	protected void setActiveToolBar() {
		if (hasToolbar()) {
			app.getGuiManager().setActivePanelAndToolbar(getViewId());
		}
	}

	/**
	 * @return An unique ID for this DockPanel.
	 */
	@Override
	public int getViewId() {
		return id;
	}

	/**
	 * @return The title of this view.
	 */
	public String getViewTitle() {
		return title;
	}

	/**
	 * @return The order of this panel in the view menu, with 0 being "highest".
	 *         Will be -1 if this view does not appear in the menu at all.
	 */
	public int getMenuOrder() {
		return menuOrder;
	}

	/**
	 * @return Whether the current view has a menu shortcut to toggle its
	 *         visibility.
	 */
	public boolean hasMenuShortcut() {
		return menuShortcut != '\u0000';
	}

	/**
	 * @return The menu shortcut of this view.
	 */
	public char getMenuShortcut() {
		return menuShortcut;
	}

	/**
	 * @return If this panel has a toolbar.
	 */
	public boolean hasToolbar() {
		return defaultToolbarString != null;
	}

	/**
	 * @return If this panel can customize its toolbar.
	 */
	public boolean canCustomizeToolbar() {
		return hasToolbar();
	}

	/**
	 * @return The definition string associated with this toolbar.
	 */
	@Override
	public String getToolbarString() {
		return toolbarString;
	}

	/**
	 * Set the toolbar string of this view. If the toolbar string is null but
	 * this panel has a panel normally the default toolbar string is used. This
	 * is used for backward compability. Has no visible effect.
	 * 
	 * @param toolbarString
	 *            toolbar definition
	 */
	public void setToolbarString(String toolbarString) {
		if (toolbarString == null && hasToolbar()) {
			this.toolbarString = defaultToolbarString;
		} else {
			this.toolbarString = toolbarString;
		}
	}

	/**
	 * @return The default toolbar string of this panel (or null).
	 */
	@Override
	public String getDefaultToolbarString() {
		return defaultToolbarString;
	}

	@Override
	public String toString(String prefix) {
		return "\n" + prefix + this.toString();
	}

	@Override
	public boolean updateResizeWeight() {
		return false;
	}

	@Override
	public void saveDividerLocation() {
		// no divider here
	}

	@Override
	public void updateDividerLocation(int size, int orientation1) {
		// no divider here
	}

	@Override
	public void setDockPanelsVisible(boolean visible) {
		setVisible(visible);
	}

	/**
	 * Change dragging state.
	 * 
	 * @param drag
	 *            whether to enable drag
	 */
	public void enableDragging(boolean drag) {
		if (dragPanel == null) {
			return;
		}

		// titleBarPanelContent is unvisible, when it's empty
		// so is has to be shown/hidden when dragmode changes
		titleBarPanelContent.setVisible(drag || !isStyleBarEmpty());
		dragPanel.setVisible(drag);

		// hide close button when in dragmode
		closeButtonPanel.setVisible(!drag);
		// TODO view menu?

		if (drag) {
			titleBarPanelContent.removeStyleName("TitleBarPanelContent");
			titleBarPanelContent.addStyleName("DragPanel");
		} else {
			titleBarPanelContent.removeStyleName("DragPanel");
			titleBarPanelContent.addStyleName("TitleBarPanelContent");
		}

		if (this.toggleStyleBarButton != null) {
			this.toggleStyleBarButton.setVisible(!drag);
		}
		if (drag) {
			this.styleBarPanel.setVisible(false);
		} else {
			updateStyleBarVisibility();
			if (styleBar instanceof StyleBarW) {
				((StyleBarW) styleBar).setOpen(showStyleBar);
			}
		}
	}

	/**
	 * @return whether stylebar is empty
	 */
	public boolean isStyleBarEmpty() {
		return false;
	}

	/**
	 * @return estimated size based on prefered width of views
	 */
	public GDimension getEstimatedSize() {
		switch (getViewId()) {
		case App.VIEW_EUCLIDIAN:
			return new GDimensionW(
					app.getSettings().getEuclidian(1).getPreferredSize()
							.getWidth(),
					app.getSettings().getEuclidian(1).getPreferredSize()
							.getHeight());
		case App.VIEW_EUCLIDIAN2:
			return new GDimensionW(
					app.getSettings().getEuclidian(2).getPreferredSize()
							.getWidth(),
					app.getSettings().getEuclidian(2).getPreferredSize()
							.getHeight());
		case App.VIEW_SPREADSHEET:
			return new GDimensionW(
					app.getSettings().getSpreadsheet().preferredSize()
							.getWidth(),
					app.getSettings().getSpreadsheet().preferredSize()
							.getHeight());
		}

		// probably won't work
		return new GDimensionW(getOffsetWidth(), getOffsetHeight());
	}

	/**
	 * embedded dimensions (calculated when perspective is set)
	 * 
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void setEmbeddedDim(int w, int h) {
		embeddedDimWidth = w;
		embeddedDimHeight = h;
	}

	/**
	 * 
	 * @return embedded width
	 */
	public int getEmbeddedDimWidth() {
		return embeddedDimWidth;
	}

	/**
	 * 
	 * @return embedded height
	 */
	public int getEmbeddedDimHeight() {
		return embeddedDimHeight;
	}

	/**
	 * Initializes the view-specific icon of the DockPanel
	 * 
	 * @param imageResource
	 *            the icon the be shown
	 */
	public void setViewImage(ResourcePrototype imageResource) {
		this.viewImage = imageResource;
	}

	private ResourcePrototype getToggleImage(boolean showing) {
		if (showing) {
			return GuiResources.INSTANCE.dockbar_triangle_right();
		}
		if (getViewIcon() != null) {
			return getViewIcon();
		}
		return GuiResources.INSTANCE.dockbar_triangle_left();
	}

	/**
	 * @return image for stylebar toggle
	 */
	protected abstract ResourcePrototype getViewIcon();

	/**
	 * @param offset
	 *            stylebar offset from the right
	 */
	public void setStyleBarRightOffset(int offset) {
		if (this.titleBarPanel != null) {
			this.titleBarPanel.getElement().getStyle()
					.setPosition(Position.ABSOLUTE);
			this.titleBarPanel.getElement().getStyle().setRight(offset,
					Unit.PX);
		}
	}

	/**
	 * Show or hide stylebar if it exists.
	 * 
	 * @param show
	 *            whether to show stylebar
	 */
	public void showStyleBarPanel(boolean show) {
		if (this.titleBarPanel != null) {
			this.titleBarPanel.setVisible(show);
		}
	}

	/**
	 * @return whether stylebar is visible
	 */
	public boolean isStyleBarPanelShown() {
		if (titleBarPanel != null) {
			return this.titleBarPanel.isVisible()
					&& titleBarPanel.getParent() != null;
		}
		return false;
	}

	/**
	 * @return resource bundle for icons
	 */
	protected SvgPerspectiveResources getResources() {
		return SvgPerspectiveResources.INSTANCE;
	}

	/**
	 * Add mode to toolbar definition.
	 * 
	 * @param mode
	 *            app mode
	 */
	public void addToToolbar(int mode) {
		this.toolbarString = ToolBar.addMode(toolbarString, mode);
	}

	/**
	 * Show/hide close button.
	 * 
	 * @param isVisible
	 *            whether to show close button
	 */
	public void setCloseButtonVisible(boolean isVisible) {
		if (closeButtonPanel == null) {
			return;
		}
		closeButtonPanel.setVisible(isVisible);
	}

	/**
	 * @param showKeyboardButton
	 *            keyboard button
	 */
	public void addSouth(ShowKeyboardButton showKeyboardButton) {
		this.kbButtonSpace.setWidget(showKeyboardButton);
	}

	@Override
	public void setVisible(boolean visible) {
		if (this.visible != visible) {
			this.visible = visible;
			if (app.getGuiManager() != null) {
				app.getGuiManager().updatePropertiesViewStylebar();
			}
		}
	}

	/**
	 * @return keyboard listener
	 */
	public MathKeyboardListener getKeyboardListener() {
		return null;
	}

	@Override
	public void updateNavigationBar() {
		// implemented in subclasses
	}

	/**
	 * @return whether this is a view for plane
	 */
	public boolean hasPlane() {
		return false;
	}

	/**
	 * @return navigation bar height
	 */
	protected int navHeight() {
		return 0;
	}

	/**
	 * @return whether to use whole view width
	 */
	public boolean hasLongStyleBar() {
		return longStyleBar;
	}

	/**
	 * @param longStyleBar
	 *            whether to use whole view width
	 */
	public void setLongStyleBar(boolean longStyleBar) {
		this.longStyleBar = longStyleBar;
	}

	/**
	 * @return whether navigation bar is shown here
	 */
	public int navHeightIfShown() {
		return app.showConsProtNavigation(getViewId()) ? navHeight() : 0;
	}

	/**
	 * Add reset icon to the stylebar
	 */
	public void showResetIcon() {
		StandardButton resetIcon = new StandardButton(
				GuiResourcesSimple.INSTANCE.viewRefresh(), null, 24, app);
		resetIcon.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				app.reset();

			}
		});
		if (!app.allowStylebar()) {
			titleBarPanel.clear();
			titleBarPanel.add(resetIcon);
		} else {
			titleBarPanelContent.add(resetIcon);
		}
	}

	/**
	 * @param toolMode
	 *            whether unbundled toolbar is shown in this panel
	 */
	public void setToolMode(boolean toolMode) {
		// do nothing by default
	}

	/**
	 * @param content
	 *            content panel
	 */
	public void resizeContent(Panel content) {
		int height = getComponentInteriorHeight() - navHeightIfShown();
		if (height > 0) {
			content.setHeight(height + "px");
		}
	}
}
