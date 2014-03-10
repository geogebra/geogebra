package geogebra.web.gui.layout;

import geogebra.common.awt.GDimension;
import geogebra.common.gui.layout.DockComponent;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.main.App;
import geogebra.common.util.AsyncOperation;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.awt.GRectangleW;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.StyleBarW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetStyleBarW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ImageResource;
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
public abstract    class DockPanelW extends ResizeComposite implements
		geogebra.common.gui.layout.DockPanel, DockComponent, MouseDownHandler {
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
	protected GRectangleW frameBounds = new GRectangleW(50, 50, 500, 500);

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
	private Widget styleBar;
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
	private Image triangleRight = new Image(GuiResources.INSTANCE.dockbar_triangle_right());
	private Image triangleLeft = new Image(GuiResources.INSTANCE.dockbar_triangle_left());
	private Image dragIcon = new Image(GuiResources.INSTANCE.dockbar_drag());
	private Image closeIcon = new Image(GuiResources.INSTANCE.dockbar_close());

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
	public ImageResource getIcon() {
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
	}

	

	/**
	 * Bind this view to a dock manager. Also initializes the whole GUI as just
	 * at this point the application is available.
	 * 
	 * @param dockManager
	 */
	public void register(DockManagerW dockManager) {
		this.dockManager = dockManager;
		app = dockManager.getLayout().getApplication();

		buildDockPanel();

		// buildGUI should be called in a lazy way!
		// buildGUI();
	}

	MyDockLayoutPanel dockPanel;
	PushButton toglStyleBtn;

	PushButton toglStyleBtn2;
	FlowPanel titleBarPanel;
	
	Label titleBarLabel;
	private PushButton dragButton;

	private VerticalPanel componentPanel;

	private PushButton toggleStyleBarButton;
	
	public int getHeight(){
		return dockPanel.getOffsetHeight();	
	}
	
	public int getWidth(){
		return dockPanel.getOffsetWidth();	
	}

	public void buildDockPanel() {

		// guard against repeated call 
		// while creating DockPanel based GUI (problem with early init of EV)
		if(dockPanel != null){
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

		styleBarPanel = new FlowPanel();	
		styleBarPanel.setStyleName("StyleBarPanel");

		titleBarPanel = new FlowPanel();
		titleBarPanel.setStyleName("TitleBarPanel");
		titleBarPanel.addStyleName("cursor_drag");

		ToolTipManagerW.sharedInstance().registerWidget(titleBarPanel, toolTipHandler, false, true);
		
		toggleStyleBarButton = new PushButton(triangleRight);
		toggleStyleBarButton.addStyleName("toggleStyleBar");
		
		ClickHandler toggleStyleBarHandler = new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				if (showStyleBar) {
					showStyleBar = false;
					toggleStyleBarButton.getElement().removeAllChildren();
					toggleStyleBarButton.getElement().appendChild(triangleRight.getElement());
				} else {
					showStyleBar = true;
					toggleStyleBarButton.getElement().removeAllChildren();
					toggleStyleBarButton.getElement().appendChild(triangleLeft.getElement());
				}
				updateStyleBarVisibility();
				if(styleBar instanceof StyleBarW){
					((StyleBarW)styleBar).setOpen(showStyleBar);
				}
			}
		};
		toggleStyleBarButton.addClickHandler(toggleStyleBarHandler);
		
		dragButton = new PushButton(dragIcon);
		dragButton.addDomHandler(this,MouseDownEvent.getType());
		dragButton.setVisible(false);

		titleBarPanel.add(toggleStyleBarButton);
		
		titleBarPanel.add(styleBarPanel);
		titleBarPanel.add(dragButton);
		
		if(app.getGuiManager().isDraggingViews()){
			enableDragging(true);
		}

		if (setlayout) {
			setLayout(false);
		}
	}

	public void setLabels() {
		if (titleBarLabel != null) {
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
				/* && !(isAlone && !isMaximized())*/ && !app.isApplet()
				&& (!isOpenInFrame())) {
				dockPanel.addNorth(titleBarPanel, 0);
			}

			
			if (isStyleBarVisible()) {
				setStyleBar();
				updateStyleBarVisibility();
			}
			if(styleBar instanceof StyleBarW) {
				((StyleBarW)styleBar).setOpen(showStyleBar);
			}
			updateStyleBarVisibility();
		}

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
	public class MyDockLayoutPanel extends DockLayoutPanel{
	public MyDockLayoutPanel(Unit unit) {
	        super(unit);
			addStyleName("ggbdockpanelhack");
        }		
	
	@Override
    public double getCenterHeight(){
		return super.getCenterHeight();
	}

	@Override
    public double getCenterWidth(){
		return super.getCenterWidth();
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
		
		Image img = new Image(getIcon());
		img.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		img.getElement().getStyle().setMarginRight(4, Unit.PX);
		
		FlowPanel p = new FlowPanel();
		p.add(img);
		p.add(new InlineLabel(app.getPlain(title)));
		
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
	    App.debug("why is it here");
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
			updatePanel();
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
	public void updatePanel() {

		if (!isVisible())
			return;

		if (component == null) {
			component = loadComponent();
		}

		setLayout(false);
	}

	/**
	 * Update the toolbar GUI.
	 */
	public void buildToolbarGui() {
			App.debug("TODO why is it here....");
	}



	/**
	 * Update fonts.
	 */
	public void updateFonts() {
		App.debug("Why is it here....");
	}

	/**
	 * Close this panel.
	 * 
	 * @param isPermanent
	 */
	protected void closePanel(boolean isPermanent) {
		dockManager.closePanel(this, isPermanent);
	}

	/** loads the styleBar and puts it into the stylBarPanel */
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
			styleBar.setVisible(showStyleBar);
			if (styleBar instanceof SpreadsheetStyleBarW) {	
				dockPanel.setWidgetSize(titleBarPanel, 50);		
			}
		} else if (styleBar instanceof SpreadsheetStyleBarW) {
			dockPanel.setWidgetSize(titleBarPanel, 0);
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
		//return def.reverse().toString();
			
		String s = new String();
		for (int i = def.length()-1; i >=0; i--){
			s += def.charAt(i);
		}
		return s;
	}

	/**
	 * @return The XML container which stores all relevant information for this
	 *         panel.
	 */
	public DockPanelData createInfo() {
		return new DockPanelData(id, toolbarString, visible, openInFrame,
				showStyleBar, new geogebra.html5.awt.GRectangleW(frameBounds),
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
		//TODO: return openInFrame;
		//currently opening in an own frame is not implemented on web,
		//so temporarily it will return false all time (see #3468)
		return false;
		//return openInFrame;
	}

	/**
	 * If the stylebar of this view should be visible. Has no immediate effect.
	 * 
	 * @param showStyleBar
	 */
	public void setShowStyleBar(boolean showStyleBar) {
		this.showStyleBar = showStyleBar;
	}
	/**
	 * @return If the style bar should be visible.
	 */
	protected boolean isStyleBarVisible() {
		if (id == App.VIEW_EUCLIDIAN || id == App.VIEW_EUCLIDIAN2 || id == App.VIEW_ALGEBRA) {
			if (!app.getSettings().getLayout().isAllowingStyleBar()) {
				return false;
			}
		}
		return (showStyleBar /*|| !(theRealTitleBarPanel.isVisible() && theRealTitleBarPanel.isAttached())*/);
	}

	/**
	 * just return hasStyleBar - overridden for spreadsheet
	 * @return hasStyleBar
	 */
	protected boolean hasStyleBar() {
		return hasStyleBar;
	}

	public void setFrameBounds(GRectangleW frameBounds) {
		this.frameBounds = frameBounds;
	}

	public GRectangleW getFrameBounds() {
		return this.frameBounds;
	}

	/**
	 * @return return the Window
	 */
	/*public Window getFrame() {
		return frame;
	}*/

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

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	//@Override
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
			if (openInFrame) {
				//TODO frame.requestFocus();
			} else {
				/*TODO if (!app.isApplet()) {
					JFrame frame = app.getFrame();

					if (frame != null) {
						frame.toFront();
					}
				}*/

				setActiveToolBar();
			}
		}

		else {

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
		App.debug("unimplemented");
	}

	/**
	 * Set the title bar focus style
	 * 
	 * TODO: Focus is indicated by change in title bar style instead of bold
	 * text, so refactor to express this correctly
	 * 
	 */
	protected void setTitleLabelFocus() {
		
		App.debug(this.getPlainTitle() + " title is bold? " + titleIsBold());
		
	
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
	 * @return The title of this view. The String returned has to be the key of
	 *         a value in plain.properties
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
			toolbarString = defaultToolbarString;
		}

		this.toolbarString = toolbarString;
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

		updatePanel();
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
	
	public void setDockPanelsVisible(boolean visible){
		setVisible(visible);
	}

	public abstract void showView(boolean b);

	public void enableDragging(boolean drag){
		if(dragButton==null){
			return;
		}
		dragButton.setVisible(drag);
		this.toggleStyleBarButton.setVisible(!drag);
		if(drag){
			this.styleBarPanel.setVisible(false);
		}else{
			updateStyleBarVisibility();
			if(styleBar instanceof StyleBarW){
				((StyleBarW)styleBar).setOpen(showStyleBar);
			}
		}
	}
	
	public void onMouseDown(MouseDownEvent event) {

		// No, we don't need this, but do nothing instead if building GUI is necessary
		// buildGUIIfNecessary();

		if (componentPanel == null)
			return;

		dockManager.drag(this);
    }

	public GDimension getEstimatedSize() {
		switch (getViewId()) {
			case App.VIEW_EUCLIDIAN:
				return new GDimensionW(
					app.getSettings().getEuclidian(1).getPreferredSize().getWidth(),
					app.getSettings().getEuclidian(1).getPreferredSize().getHeight());
			case App.VIEW_EUCLIDIAN2:
				return new GDimensionW(
					app.getSettings().getEuclidian(2).getPreferredSize().getWidth(),
					app.getSettings().getEuclidian(2).getPreferredSize().getHeight());
			case App.VIEW_SPREADSHEET:
				return new GDimensionW(
					app.getSettings().getSpreadsheet().preferredSize().getWidth(),
					app.getSettings().getSpreadsheet().preferredSize().getHeight());
		}

		// probably won't work
		return new GDimensionW(getOffsetWidth(), getOffsetHeight());
	}
}
