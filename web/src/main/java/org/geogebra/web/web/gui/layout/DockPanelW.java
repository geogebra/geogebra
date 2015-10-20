package org.geogebra.web.web.gui.layout;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.gui.layout.DockComponent;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.cas.view.CASStylebarW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.app.ShowKeyboardButton;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.util.StandardButton;
import org.geogebra.web.web.gui.util.StyleBarW;
import org.geogebra.web.web.gui.view.spreadsheet.SpreadsheetStyleBarW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
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
public abstract class DockPanelW extends ResizeComposite implements
        DockPanel, DockComponent, MouseDownHandler {
	protected DockManagerW dockManager;

	protected AppW app;

	/**
	 * The ID of this dock panel.
	 */
	protected int id;

	/**
	 * The title of this dock panel.
	 */
	private String title = " no title";

	/**
	 * If this panel is visible.
	 */
	protected boolean visible = false;

	/**
	 * If this panel has focus.
	 */
	protected boolean hasFocus = false;

	/**
	 * The dimensions of the external window of this panel.
	 */
	protected Rectangle frameBounds = new Rectangle(50, 50, 500, 500);

	/**
	 * If this panel should be opened in a frame the next time it's visible.
	 */
	protected boolean openInFrame = false;

	/**
	 * If there is a style bar associated with this panel.
	 */
	private boolean hasStyleBar = false;

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
	private String defaultToolbarString;

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
	private boolean isAlone;

	/**
	 * Indicator whether this panel is hidden. A hidden panel is not visible,
	 * but it's View component is still attached to the kernel.
	 */
	private boolean isHidden;

	/**
	 * Flag to determine if the frame field will be created as a JDialog (true)
	 * or as a JFram (false). Default is false.
	 */
	private boolean isDialog = false;

	/**
	 * Images for Stylingbar
	 */
	private Image triangleRight, triangleLeft, dragIcon, closeIcon;

	/**
	 * For calling the onResize method in a deferred way
	 */
	Scheduler.ScheduledCommand deferredOnRes = new Scheduler.ScheduledCommand() {
		public void execute() {
			onResize();
		}
	};

	/**
	 * For calling the onResize method in a deferred way
	 */
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	/**
	 * @return true if this dock panel frame will be created as a JDialog. If
	 *         false then it will be created as a JFrame
	 * 
	 */
	public boolean isDialog() {
		return isDialog;
	}

	/**
	 * Sets the isDialog flag.
	 * 
	 * @param isDialog
	 *            true if this dock panel frame will be created as a JDialog. If
	 *            false then it will be created as a JFrame
	 */
	public void setDialog(boolean isDialog) {
		this.isDialog = isDialog;
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
	public DockPanelW(int id, String title, String toolbar,
	        boolean hasStyleBar, int menuOrder) {
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
	public DockPanelW(int id, String title, String toolbar,
	        boolean hasStyleBar, int menuOrder, char menuShortcut) {
		this.id = id;
		this.title = title;
		this.defaultToolbarString = toolbar;

		// this is different in Web and Desktop!
		setToolbarString(defaultToolbarString);

		this.menuOrder = menuOrder;
		this.menuShortcut = menuShortcut;
		this.hasStyleBar = hasStyleBar;
		this.isAlone = false;
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
	 * Method which is called if this dock panel gained focus. This happens if
	 * setFocus(true) was called and this panel had no focus before.
	 * 
	 * @remark If GeoGebra is running as unsigned applet focus is just changed
	 *         between euclidian views (even if other views were selected in the
	 *         meantime).
	 */
	protected void focusGained() {
	}

	/**
	 * Method which is called if this dock panel lost focus. This happens if
	 * setFocus(false) was called and this panel had focus before.
	 * 
	 * @remark If GeoGebra is running as unsigned applet focus is just changed
	 *         between euclidian views (even if other views were selected in the
	 *         meantime).
	 */
	protected void focusLost() {
		// empty by default
	}

	/**
	 * Bind this view to a dock manager. Also initializes the whole GUI as just
	 * at this point the application is available.
	 * 
	 * @param dockManager
	 */
	public void register(DockManagerW dockManager1) {
		this.dockManager = dockManager1;
		app = dockManager1.getLayout().getApplication();

		buildDockPanel();

		// buildGUI should be called in a lazy way!
		// buildGUI();
	}

	MyDockLayoutPanel dockPanel;
	PushButton toglStyleBtn;

	PushButton toglStyleBtn2;
	FlowPanel titleBarPanel;
	private FlowPanel titleBarPanelContent;

	Label titleBarLabel;
	private PushButton closeButton;
	private FlowPanel dragPanel;
	private FlowPanel closeButtonPanel;

	private VerticalPanel componentPanel;

	protected StandardButton toggleStyleBarButton;

	private ResourcePrototype viewImage;

	// needs to be initialized here, because the button might be added in
	// adSouth(), before setLayout() is called
	private final SimplePanel kbButtonSpace = new SimplePanel();

	public int getHeight() {
		return dockPanel.getOffsetHeight();
	}

	public int getWidth() {
		return dockPanel.getOffsetWidth();
	}

	public void buildDockPanel() {

		// guard against repeated call
		// while creating DockPanel based GUI (problem with early init of EV)
		if (dockPanel != null) {
			return;
		}

		dockPanel = new MyDockLayoutPanel(Style.Unit.PX);
		initWidget(dockPanel);
	}

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
		titleBarPanel.addStyleName("cursor_drag");
		
//		kbButtonSpace = new SimplePanel();
		
		titleBarPanelContent = new FlowPanel();
		titleBarPanelContent.setStyleName("TitleBarPanelContent");
		titleBarPanel.add(titleBarPanelContent);

		dragPanel = new FlowPanel();
		dragPanel.setStyleName("dragPanel");
		dragPanel.addDomHandler(this, MouseDownEvent.getType());
		dragPanel.setVisible(false);
		if(dragIcon == null){
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
		
		if(closeIcon == null){
			closeIcon = new Image(GuiResources.INSTANCE.dockbar_close());
		}
		closeButton = new PushButton(closeIcon);
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
		titleBarPanelContent.add(dragPanel);

		if (!this.isStyleBarEmpty()) {
			addToggleButton();
		}

		titleBarPanelContent.setVisible(!isStyleBarEmpty());

		if (this.isStyleBarEmpty()) {
			titleBarPanel.add(closeButtonPanel);
		}

		if (app.getGuiManager().isDraggingViews()) {
			enableDragging(true);
		}

		if (setlayout) {
			setLayout(false);
		}
	}

	private void addToggleButton() {
		// always show the view-icon; otherwise use showStylebar as parameter
		toggleStyleBarButton = new StandardButton(getToggleImage(false),null, 32);
		toggleStyleBarButton.addStyleName("toggleStyleBar");

		if(!showStyleBar && viewImage != null){
			toggleStyleBarButton.addStyleName("toggleStyleBarViewIcon");
		}

		FastClickHandler toggleStyleBarHandler = new FastClickHandler() {

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

	public final void setLabels() {
		if (toggleStyleBarButton != null) {
			toggleStyleBarButton.setTitle(app.getPlain("ToggleStyleBar"));
		}
	}

	/**
	 * sets the layout of the stylebar and title panel
	 */
	protected void setLayout(boolean deferred) {

		if (!isVisible())
			return;

		buildGUIIfNecessary(false);

		dockPanel.clear();

		if (hasStyleBar()) {

			if (app.getSettings().getLayout().showTitleBar()
			        /* && !(isAlone && !isMaximized()) */&& (!app.isApplet()
			                || app.getArticleElement().getDataParamShowMenuBar(
			                        false) || app.getArticleElement()
			                .getDataParamAllowStyleBar(false))
			        && (!isOpenInFrame())) {
				
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

	public int getComponentInteriorHeight() {

		if (dockPanel != null) {
			return (int) dockPanel.getCenterHeight();
		}
		return 0;
	}

	public int getComponentInteriorWidth() {
		if (dockPanel != null) {
			return (int) dockPanel.getCenterWidth();
		}
		return 0;
	}

	/**
	 * extends DockLayoutPanel to expose getCenterHeight() and getCenterWidth()
	 * TODO: move some code above into this class, e.g. setLayout(), or possibly
	 * extend DockPanelW itself
	 */
	public class MyDockLayoutPanel extends DockLayoutPanel {
		public MyDockLayoutPanel(Unit unit) {
			super(unit);
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
        public void add(Widget w){
			if(this.getCenter()!=null){
				Log.error("TODO: dock panel filled twice");
				this.remove(this.getCenter());
			}
			super.add(w);
		}

	}

	/**
	 * 
	 * @return title in plain style
	 */
	protected String getPlainTitle() {
		return app.getPlain(title);
	}

	/**
	 * 
	 * @return toolTip text as HTML string with image and title
	 */
	protected String getToolTip() {
		FlowPanel p = new FlowPanel();
		String caption;
		if (!this.isStyleBarEmpty()) {
			Image img = new NoDragImage(GGWToolBar.safeURI(getIcon()),24);
			img.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
			img.getElement().getStyle().setMarginRight(4, Unit.PX);
			p.add(img);
			caption = app.getPlain(title);
		} else {
			caption = app.getMenu("Close");
		}

		p.add(new InlineLabel(caption));

		return p.getElement().getInnerHTML();
	}

	private AsyncOperation toolTipHandler = new AsyncOperation() {
		@Override
		public void callback(Object obj) {
			setData(getToolTip());
		}
	};

	/**
	 * Update all elements in the title bar.
	 */
	public void updateTitleBar() {

		if (componentPanel == null)
			return;

		updateLabels();
	}

	public void updateLabels() {
		//TODO implement or delete
	}

	/**
	 * A panel is 'alone' if no other panel is visible in the main frame. In
	 * this case no title bar is displayed, but just the style bar. Changing the
	 * value of the 'alone' state will cause the GUI to update automatically if
	 * this panel is visible.
	 * 
	 * @param isAlone
	 */
	public void setAlone(boolean isAlone) {
		if (this.isAlone == isAlone) {
			return;
		}

		this.isAlone = isAlone;

		if (isVisible()) {
			updatePanel(true);
		}
	}

	/**
	 * @return If this panel thinks it's the last visible one in the main frame.
	 */
	public boolean isAlone() {
		return isAlone;
	}

	/**
	 * @return If this panel is hidden but not permanently removed.
	 */
	public boolean isHidden() {
		return isHidden;
	}

	/**
	 * Sets the the isHidden flag (no other action)
	 */
	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	/**
	 * Update the panel.
	 */
	public final void updatePanel(boolean deferred) {

		if (!isVisible())
			return;

		if (component == null) {
			component = loadComponent();
		}
		setLayout(deferred);
			//ignore
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
		app.getGuiManager().setActiveToolbarId(id);
	}

	/**
	 * Update fonts.
	 */
	public void updateFonts() {
		//TODO implement or delete
	}

	/**
	 * Close this panel.
	 * 
	 * @param isPermanent
	 */
	protected void closePanel(boolean isPermanent) {
		dockManager.closePanel(this, isPermanent);
	}
	
	public void closePanel() {
		closePanel(true);
	}

	/** loads the styleBar and puts it into the styleBarPanel */
	private void setStyleBar() {
		if (styleBar == null) {
			buildGUIIfNecessary(false);
			styleBar = loadStyleBar();
			styleBarPanel.add(styleBar);
		}
	}

	/**
	 * Update the style bar visibility.
	 */
	public void updateStyleBarVisibility() {
		if (!isVisible())
			return;

		buildGUIIfNecessary(true);

		styleBarPanel.setVisible(isStyleBarVisible());
		if (isStyleBarVisible()) {
			setStyleBar();
			styleBar.setVisible(showStyleBar && !app.getGuiManager().isDraggingViews());
		}
		if (styleBar instanceof SpreadsheetStyleBarW || styleBar instanceof CASStylebarW) {
			setStyleBarLongVisibility(isStyleBarVisible());
		}
	}
	
	/**
	 * Sets style bar visibility to true and false.
	 * When visible, style bar occupies a space of a full row (instead of floating).
	 * E.g. in CASView and SpreadsheetView
	 * @param value true to show style bar
	 */
	private void setStyleBarLongVisibility(boolean value) {
		if ((!app.isApplet()
				|| app.getArticleElement().getDataParamShowMenuBar(false) || app
				.getArticleElement().getDataParamAllowStyleBar(false))) {
			dockPanel.setWidgetSize(titleBarPanel, value ? 44 : 0);
			titleBarPanel.setStyleName("TitleBarPanel-open", value);
			deferredOnResize();
		}
	}

	/**
	 * @return The parent DockSplitPane or null.
	 */
	public DockSplitPaneW getParentSplitPane() {
		if (isOpenInFrame())
			return null;

		Widget parent = getParent();

		if (parent == null || !(parent instanceof DockSplitPaneW))
			return null;

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
			int defType = -1;

			parentDSP = (DockSplitPaneW) parent;

			if (parentDSP.getOrientation() == DockSplitPaneW.HORIZONTAL_SPLIT) {
				if (current == parentDSP.getLeftComponent()) // left
					defType = 3;
				else
					// right
					defType = 1;
			} else {
				if (current == parentDSP.getLeftComponent()) // top
					defType = 0;
				else
					// bottom
					defType = 2;
			}

			if (def.length() == 0) {
				def.append(defType);
			} else {
				def.append("," + defType);
			}

			current = parent;
			parent = current.getParent();
		}

		// gwt does not support reverse() ??
		// return def.reverse().toString();

		String s = new String();
		for (int i = def.length() - 1; i >= 0; i--) {
			s += def.charAt(i);
		}
		return s;
	}

	/**
	 * @return The XML container which stores all relevant information for this
	 *         panel.
	 */
	public DockPanelData createInfo() {
		return new DockPanelData(id, getToolbarString(), visible, openInFrame,
		        showStyleBar, new Rectangle(frameBounds),
		        embeddedDef, embeddedSize);
	}

	/**
	 * If this view should open in a frame. Has no immediate effect.
	 * 
	 * @param openInFrame
	 */
	public void setOpenInFrame(boolean openInFrame) {
		this.openInFrame = openInFrame;
	}

	/**
	 * @return Whether this view should open in frame.
	 */
	public boolean isOpenInFrame() {
		// TODO: return openInFrame;
		// currently opening in an own frame is not implemented on web,
		// so temporarily it will return false all time (see #3468)
		return false;
		// return openInFrame;
	}

	/**
	 * If the stylebar of this view should be visible. Has no immediate effect.
	 * 
	 * @param showStyleBar
	 */
	public void setShowStyleBar(boolean showStyleBar) {
		this.showStyleBar = showStyleBar;
		if (app != null) {
			app.dispatchEvent(new Event(EventType.SHOW_STYLE_BAR, null, "["
				+ showStyleBar + "," + getViewId() + "]"));
		}
//		if (this.toggleStyleBarButton != null) {
//			this.toggleStyleBarButton.getElement().removeAllChildren();
//			this.toggleStyleBarButton.getElement().appendChild(getToggleImage(showStyleBar).getElement());
//			if(!showStyleBar && viewImage != null){
//				toggleStyleBarButton.addStyleName("toggleStyleBarViewIcon");
//			} else {
//				toggleStyleBarButton.removeStyleName("toggleStyleBarViewIcon");
//			}
//		}
	}

	/**
	 * @return If the style bar should be visible.
	 */
	protected boolean isStyleBarVisible() {
		if (id == App.VIEW_EUCLIDIAN || id == App.VIEW_EUCLIDIAN2
		        || id == App.VIEW_ALGEBRA) {
			if (!app.getSettings().getLayout().isAllowingStyleBar()) {
				return false;
			}
		}
		return (showStyleBar /*
							 * || !(theRealTitleBarPanel.isVisible() &&
							 * theRealTitleBarPanel.isAttached())
							 */);
	}

	/**
	 * just return hasStyleBar - overridden for spreadsheet
	 * 
	 * @return hasStyleBar
	 */
	protected boolean hasStyleBar() {
		return hasStyleBar;
	}

	public void setFrameBounds(Rectangle frameBounds) {
		this.frameBounds = frameBounds;
	}

	public Rectangle getFrameBounds() {
		return this.frameBounds;
	}

	/**
	 * @return return the Window
	 */
	/*
	 * public Window getFrame() { return frame; }
	 */

	/**
	 * @param embeddedDef
	 *            the embeddedDef to set
	 */
	public void setEmbeddedDef(String embeddedDef) {
		this.embeddedDef = embeddedDef;
	}

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


	// @Override
	public boolean hasFocus() {
		return hasFocus;
	}

	/**
	 * Mark this panel as focused. When gaining focus the panel will
	 * automatically request focus for its parent frame.
	 * 
	 * @remark The focus system implemented here has nothing to do with swings
	 *         focus system, therefore Swings focus methods won't work.
	 * 
	 * @param hasFocus
	 *            has the focus
	 * @param updatePropertiesView
	 *            update properties view
	 */
	public void setFocus(boolean hasFocus, boolean updatePropertiesView) {

		if (hasFocus && updatePropertiesView) {
			app.getGuiManager().updatePropertiesView();
		}

		setFocus(hasFocus);
	}

	/**
	 * Mark this panel as focused. When gaining focus the panel will
	 * automatically request focus for its parent frame.
	 * 
	 * @remark The focus system implemented here has nothing to do with swings
	 *         focus system, therefore Swings focus methods won't work.
	 * 
	 * @param hasFocus
	 *            has the focus
	 */
	protected void setFocus(boolean hasFocus) {

		// don't change anything if it's not necessary
		if (this.hasFocus == hasFocus)
			return;

		this.hasFocus = hasFocus;

		if (hasFocus) {
			// request focus and change toolbar if necessary
			if (isOpenInFrame()) {
				// TODO frame.requestFocus();
			} else {
				/*
				 * TODO if (!app.isApplet()) { JFrame frame = app.getFrame();
				 * 
				 * if (frame != null) { frame.toFront(); } }
				 */

				setActiveToolBar();
			}
		}

		// call callback methods for focus changes
		if (hasFocus) {
			focusGained();
		} else {
			focusLost();
		}

		/*
		 * Mark the focused view in bold if the focus system is available. If
		 * this isn't the case we always stick with the normal font as it would
		 * confuse the users that the focus "indicator" just changes if we
		 * switch between EVs.
		 */
		setTitleLabelFocus();
	}

	/**
	 * sets the active toolbar
	 */
	protected void setActiveToolBar() {
		if (hasToolbar()) {
			((GuiManagerW) app.getGuiManager()).setActiveToolbarId(getViewId());
		} 
	}

	/**
	 * Set the title bar focus style
	 * 
	 * TODO: Focus is indicated by change in title bar style instead of bold
	 * text, so refactor to express this correctly
	 * 
	 */
	protected void setTitleLabelFocus() {

		if (titleIsBold()) {
			titleBarPanel.addStyleName("TitleBarPanel-focus");

		} else {
			titleBarPanel.removeStyleName("TitleBarPanel-focus");

		}

	}

	/**
	 * 
	 * @return true if title has to be in bold
	 */
	protected boolean titleIsBold() {
		return hasFocus;
	}

	/**
	 * @return An unique ID for this DockPanel.
	 */
	public int getViewId() {
		return id;
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
	public boolean canCustomizeToolbar(){
		return hasToolbar();
	}


	/**
	 * @return The definition string associated with this toolbar.
	 */
	public String getToolbarString() {
		return toolbarString;
	}

	/**
	 * Set the toolbar string of this view. If the toolbar string is null but
	 * this panel has a panel normally the default toolbar string is used. This
	 * is used for backward compability. Has no visible effect.
	 * 
	 * @param toolbarString
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
	public String getDefaultToolbarString() {
		return defaultToolbarString;
	}

	/**
	 * @return dock panel information as string for debugging.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[DockPanel,id=");
		sb.append(getViewId());
		sb.append(",toolbar=");
		sb.append(getToolbarString());
		sb.append(",visible=");
		sb.append(isVisible());
		sb.append(",inframe=");
		sb.append(isOpenInFrame());
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @return true if the layout has been maximized
	 */
	public boolean isMaximized() {
		return dockManager.isMaximized();
	}

	/**
	 * Toggles the panel between maximized and normal state
	 */
	public void toggleMaximize() {

		if (isMaximized())
			dockManager.undoMaximize(true);
		else
			dockManager.maximize(this);

		updatePanel(true);
	}

	public String toString(String prefix) {
		return "\n" + prefix + this.toString();
	}

	public boolean updateResizeWeight() {
		return false;
	}

	public void saveDividerLocation() {
		// no divider here
	}

	public void updateDividerLocation(int size, int orientation1) {
		// no divider here
	}

	public void setDockPanelsVisible(boolean visible) {
		setVisible(visible);
	}


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
		//TODO view menu?

		if(drag){
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

	public boolean isStyleBarEmpty() {
		return false;
	}

	public void onMouseDown(MouseDownEvent event) {

		// No, we don't need this, but do nothing instead if building GUI is
		// necessary
		// buildGUIIfNecessary();
		App.debug("PANEL MOUSE DOWN" + (componentPanel == null));
		if (componentPanel == null)
			return;

		dockManager.drag(this);
	}

	public GDimension getEstimatedSize() {
		switch (getViewId()) {
		case App.VIEW_EUCLIDIAN:
			return new GDimensionW(app.getSettings().getEuclidian(1)
			        .getPreferredSize().getWidth(), app.getSettings()
			        .getEuclidian(1).getPreferredSize().getHeight());
		case App.VIEW_EUCLIDIAN2:
			return new GDimensionW(app.getSettings().getEuclidian(2)
			        .getPreferredSize().getWidth(), app.getSettings()
			        .getEuclidian(2).getPreferredSize().getHeight());
		case App.VIEW_SPREADSHEET:
			return new GDimensionW(app.getSettings().getSpreadsheet()
			        .preferredSize().getWidth(), app.getSettings()
			        .getSpreadsheet().preferredSize().getHeight());
		}

		// probably won't work
		return new GDimensionW(getOffsetWidth(), getOffsetHeight());
	}

	private int embeddedDimWidth, embeddedDimHeight;

	private ShowKeyboardButton keyboardButton;

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
	 * @param imageResource: the icon the be shown
	 */
	public void setViewImage(ResourcePrototype imageResource){
		this.viewImage = imageResource;
	}

	/**
	 * show/hide the whole Panel
	 */
	public void setPanelVisible(boolean visible){
		this.titleBarPanel.setVisible(visible);
	}
	
	private ResourcePrototype getToggleImage(boolean showing){
		if(showing){
			if(triangleRight == null){
				triangleRight = new Image(
				        GuiResources.INSTANCE.dockbar_triangle_right());
			}
			return GuiResources.INSTANCE.dockbar_triangle_right();
		}
		if(viewImage != null){
			return viewImage;
		}
		if(triangleLeft == null){
			triangleLeft = new Image(
			        GuiResources.INSTANCE.dockbar_triangle_left());
		}
        return GuiResources.INSTANCE.dockbar_triangle_left();
	}

	public void setStyleBarRightOffset(int offset){
		if(this.titleBarPanel != null){
			this.titleBarPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
			this.titleBarPanel.getElement().getStyle().setRight(offset, Unit.PX);
		}
	}

	public void showStyleBarPanel(boolean show){
		if(this.titleBarPanel != null){
			this.titleBarPanel.setVisible(show);
		}
	}
	
	protected PerspectiveResources getResources(){
		return ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
	}

	public void addToToolbar(int mode) {
		this.toolbarString = ToolBar.addMode(toolbarString, mode);
	    
    }


	public void setCloseButtonVisible(boolean isVisible) {
		if (closeButtonPanel == null) {
			return;
		}
		closeButtonPanel.setVisible(isVisible);
	}

	public void addSouth(ShowKeyboardButton showKeyboardButton) {
		if (this.kbButtonSpace == null) {
			return;
		}
		this.kbButtonSpace.setWidget(showKeyboardButton);
	}

	/**
	 * set a ShowKeyBoardButton that will be updated, if this panel is resized
	 * 
	 * @param button
	 *            the button to be updated
	 */
	public void setKeyBoardButton(ShowKeyboardButton button) {
		this.keyboardButton = button;
	}

	@Override
	public void setVisible(boolean visible) {
		if (this.visible != visible) {
			this.visible = visible;
			if (app.getGuiManager() != null) {
				app.getGuiManager().updatePropertiesViewStylebar();
			}
		}

		// hide the keyboard-button, when the view is closed
		if (keyboardButton != null && !visible) {
			keyboardButton.hide();
		}
	}

	public MathKeyboardListener getKeyboardListener() {
		return null;
	}

	public void updateNavigationBar() {
		// TODO Auto-generated method stub
	}

	public boolean hasPlane() {
		// TODO Auto-generated method stub
		return false;
	}

	public int navHeight() {
		return 0;
	}
}
