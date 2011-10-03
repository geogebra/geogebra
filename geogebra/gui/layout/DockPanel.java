package geogebra.gui.layout;

import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.gui.toolbar.Toolbar;
import geogebra.gui.toolbar.ToolbarContainer;
import geogebra.io.layout.DockPanelXml;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Every object which should be dragged needs to be of type DockPanel.
 * A DockPanel will wrap around the component with the real contents
 * (e.g. the EuclidianView) and will add a title bar if the user is not in
 * the "layout fixed" mode. The user can move the DockPanel by dragging the
 * title bar.
 * 
 * To add a new dock panel one has to subclass DockPanel, implement the abstract
 * method DockPanel::loadComponent() and maybe replace DockPanel::getIcon() 
 * and DockPanel::getStyleBar().
 * 
 * One can add a panel using Layout::registerPanel(), the GuiManager also provides
 * GuiManager()::initLayoutPanels() as an easy access point to add new panels. This
 * is also important because it matters at which point of execution a panel is added,
 * see Layout::registerPanel() for further information.  
 * 
 * @author Florian Sonner
 */
public abstract class DockPanel extends JPanel implements ActionListener, WindowListener, MouseListener {
	private static final long serialVersionUID = 1L;
	
	protected DockManager dockManager;
	protected Application app;
	
	/**
	 * The ID of this dock panel.
	 */
	private int id;
	
	/**
	 * The title of this dock panel.
	 */
	private String title;
	
	/**
	 * If this panel is visible.
	 */
	private boolean visible = false;
	
	/**
	 * If this panel has focus.
	 */
	protected boolean hasFocus = false;
	
	/**
	 * The dimensions of the external window of this panel.
	 */
	private Rectangle frameBounds = new Rectangle(50, 50, 500, 500);
	
	/**
	 * If this panel should be opened in a frame the next time it's visible.
	 */
	private boolean openInFrame = true;
	
	/**
	 * If there is a style bar associated with this panel.
	 */
	private boolean hasStyleBar = false;
	
	/**
	 * Style bar component.
	 */
	private JComponent styleBar;
	
	/**
	 * If the style bar is visible.
	 */
	private boolean showStyleBar = false;
	
	/**
	 * String which stores the position of the panel in the layout.
	 */
	private String embeddedDef = "1";
	
	/**
	 * The size of the panel in the layout, may be either the width or height depending upon
	 * embeddedDef. 
	 */
	private int embeddedSize = 150;
	
	/**
	 * The panel at the top where the title and the close button
	 * is displayed normally.
	 */
	protected JPanel titlePanel;
	
	/**
	 * The label with the view title.
	 */
	protected JLabel titleLabel;
	
	/**
	 * The panel which holds all buttons.
	 */
	protected JPanel buttonPanel;
	
	/**
	 * The close button.
	 */
	protected JButton closeButton;
	
	/**
	 * Button which opens the panel in a new window.
	 */
	private JButton windowButton;
	
	/**
	 * A button which brings the panel back to the main window.
	 */
	private JButton unwindowButton;
	
	/**
	 * Button used to show / hide the style bar.
	 */
	private JButton toggleStyleBarButton;
	
	/**
	 * Panel for the styling bar if one is available.
	 */
	private JPanel styleBarPanel;
	
	/**
	 * Panel used for the toolbar if this dock panel has one.
	 */
	private JPanel toolbarPanel;
	
	/**
	 * Toolbar container which is used if this dock panel is opened in its own frame.
	 */
	private ToolbarContainer toolbarContainer;
	
	/**
	 * Toolbar associated with this dock panel or null if this panel has no toolbar.
	 */
	private Toolbar toolbar;
	
	/**
	 * Toolbar definition string associated with this panel or null if this panel has no
	 * toolbar. Always contains the string of the perspective loaded last.
	 */
	private String toolbarString;
	
	/**
	 * Default toolbar definition string associated with this panel or null
	 * if this panel has no toolbar. This string is specified in the constructor and won't
	 * change. 
	 */
	private String defaultToolbarString;
	
	/**
	 * The frame which holds this DockPanel if the DockPanel is opened in
	 * an additional window.
	 */
	private JFrame frame = null;
	
	/**
	 * The component used for this view.
	 */
	protected JComponent component;
	
	/**
	 * The location of this panel in the view menu. If -1 this panel won't appear there at all.
	 */
	private int menuOrder;
	
	/**
	 * Shortcut to show this panel, SHIFT is automatically used as modifier, \u0000 is the default value.
	 */
	private char menuShortcut;
	
	/**
	 * Indicator whether this panel is the last one in the main frame. In this case no title bar will be visible,
	 * but just the stylebar.
	 */
	private boolean isAlone;
	
	/**
	 * Prepare dock panel. DockPanel::register() has to be called to make this panel fully functional!
	 * No shortcut is assigned to the view in this construtor.
	 * 
	 * @param id 			The id of the panel
	 * @param title			The title phrase of the view located in plain.properties
	 * @param toolbar		The default toolbar string (or null if this view has none)
	 * @param hasStyleBar	If a style bar exists
	 * @param menuOrder		The location of this view in the view menu, -1 if the view should not appear at all
	 */
	public DockPanel(int id, String title, String toolbar, boolean hasStyleBar, int menuOrder) {
		this(id, title, toolbar, hasStyleBar, menuOrder, '\u0000');
	}
	
	/**
	 * Prepare dock panel. DockPanel::register() has to be called to make this panel fully functional!
	 * 
	 * @param id 			The id of the panel
	 * @param title			The title phrase of the view located in plain.properties
	 * @param toolbar		The default toolbar string (or null if this view has none)
	 * @param hasStyleBar	If a style bar exists
	 * @param menuOrder		The location of this view in the view menu, -1 if the view should not appear at all
	 * @param menuShortcut	The shortcut character which can be used to make this view visible
	 */
	public DockPanel(int id, String title, String toolbar, boolean hasStyleBar, int menuOrder, char menuShortcut) {
		this.id = id;
		this.title = title;
		this.defaultToolbarString = toolbar;
		this.menuOrder = menuOrder;
		this.menuShortcut = menuShortcut;
		this.hasStyleBar = hasStyleBar;
		this.isAlone = false;
		this.setMinimumSize(new Dimension(5,5));
		setLayout(new BorderLayout());
	}
	
	/**
	 * @return The icon of the menu item, if this method
	 * 		was not overwritten it will return the empty icon or 
	 * 		null for Win Vista / 7 to prevent the "checkbox bug" 
	 */
	public ImageIcon getIcon() { 
		if(Application.WINDOWS_VISTA_OR_LATER) {
			return null; 
		} else {
			return app.getEmptyIcon();
		}
	}
	
	/**
	 * @return The style bar if one exists
	 */
	protected JComponent loadStyleBar() {
		return null; 
	}
	
	/**
	 * @return The main panel of this view.
	 */
	protected abstract JComponent loadComponent();
	
	/**
	 * @return The main panel of this view (null if none was loaded yet).
	 */
	public JComponent getComponent() {
		return component;
	}
	
	/**
	 * Method which is called if this dock panel gained focus. This happens
	 * if setFocus(true) was called and this panel had no focus before.
	 * 
	 * @remark If GeoGebra is running as unsigned applet focus is just changed between
	 * euclidian views (even if other views were selected in the meantime).
	 */
	protected void focusGained() {
	}
	
	/**
	 * Method which is called if this dock panel lost focus. This happens
	 * if setFocus(false) was called and this panel had focus before.
	 * 
	 * @remark If GeoGebra is running as unsigned applet focus is just changed between
	 * euclidian views (even if other views were selected in the meantime).
	 */
	protected void focusLost() {
	}
	
	
	/**
	 * create the focus panel (composed of titleLabel, and, for EuclidianDockPanels, focus icon)
	 * @return the focus panel
	 */
	protected JComponent createFocusPanel(){
		titleLabel = new JLabel(app.getPlain(title));
		titleLabel.setFont(app.getPlainFont());
		titleLabel.setForeground(Color.darkGray);
		return titleLabel;
	}
	
	/**
	 * Bind this view to a dock manager. Also initializes the whole GUI as just
	 * at this point the application is available.
	 * 
	 * @param dockManager
	 */
	public void register(DockManager dockManager) {
		this.dockManager = dockManager;
		this.app = dockManager.getLayout().getApplication();
		
		// the meta panel holds both title and style bar panel
		JPanel metaPanel = new JPanel(new BorderLayout());
		
		// Construct title bar and all elements
		titlePanel = new JPanel();
		titlePanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		titlePanel.setLayout(new BorderLayout());
		
		titlePanel.add(createFocusPanel(), BorderLayout.WEST);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		titlePanel.add(buttonPanel, BorderLayout.EAST);
		
		// Show / hide styling bar if one exists
		if(hasStyleBar) {
			toggleStyleBarButton = new JButton(app.getImageIcon("triangle-down.png"));
			toggleStyleBarButton.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
			toggleStyleBarButton.addActionListener(this);
			
			if(Application.MAC_OS) {
				toggleStyleBarButton.setUI(new TitleBarButtonUI());
			} else {
				toggleStyleBarButton.setFocusPainted(false);
			}
			
			toggleStyleBarButton.setPreferredSize(new Dimension(16,16));
			buttonPanel.add(toggleStyleBarButton);
		}
		
		// Insert the view in the main window
		unwindowButton = new JButton(app.getImageIcon("view-unwindow.png"));
		unwindowButton.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
		unwindowButton.addActionListener(this);

		if(Application.MAC_OS) {
			unwindowButton.setUI(new TitleBarButtonUI());
		} else {
			unwindowButton.setFocusPainted(false);
		}
		
		unwindowButton.setPreferredSize(new Dimension(16,16));
		buttonPanel.add(unwindowButton);
		
		// Display the view in a separate window
		windowButton = new JButton(app.getImageIcon("view-window.png"));
		windowButton.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
		windowButton.addActionListener(this);

		if(Application.MAC_OS) {
			windowButton.setUI(new TitleBarButtonUI());
		} else {
			windowButton.setFocusPainted(false);
		}
		
		windowButton.setPreferredSize(new Dimension(16,16));
		buttonPanel.add(windowButton);
		
		// Close the title bar
		closeButton = new JButton(app.getImageIcon("view-close.png"));
		closeButton.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
		closeButton.addActionListener(this);

		if(Application.MAC_OS) {
			closeButton.setUI(new TitleBarButtonUI());
		} else {
			closeButton.setFocusPainted(false);
		}
		
		closeButton.setPreferredSize(new Dimension(16,16));
		buttonPanel.add(closeButton);
		
		metaPanel.add(titlePanel, BorderLayout.NORTH);
		
		// Style bar panel
		if(hasStyleBar) {
			styleBarPanel = new JPanel(new BorderLayout());

			styleBarPanel.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow),
					BorderFactory.createEmptyBorder(0, 2, 0, 2)));
			
			metaPanel.add(styleBarPanel, BorderLayout.SOUTH);
		}
		
		// toolbar panel
		if(hasToolbar()) {
			toolbarPanel = new JPanel(new BorderLayout());

			toolbarPanel.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow),
					BorderFactory.createEmptyBorder(0, 2, 0, 2)));
			
			metaPanel.add(toolbarPanel, BorderLayout.CENTER);
		}

	   	// make titlebar visible if necessary
		updatePanel();
		
		add(metaPanel, BorderLayout.NORTH);
	}
	

	
	/**
	 * 
	 * @return title in plain style
	 */
	protected String getPlainTitle(){
		return app.getPlain(title);
	}
	
	/**
	 * Create a frame for this DockPanel.
	 */
	public void createFrame() {
		frame = new JFrame(getPlainTitle());
		
		// needs the higher res as used by Windows 7 for the Toolbar
   	frame.setIconImage(app.getInternalImage("geogebra64.png"));  
   	frame.addWindowListener(this);	
   	
   	frame.addComponentListener(new ComponentAdapter() {
          public void componentResized(ComponentEvent event) {
          	setFrameBounds(event.getComponent().getBounds());
          }
          
          public void componentMoved(ComponentEvent event) {
          	setFrameBounds(event.getComponent().getBounds());
          }
      });
   	
   	frame.getContentPane().add(this);
   	
   	
   	// TODO multimonitor supported?
   	Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
   	
   	// Use the previous dimension of this view
   	Rectangle windowBounds = getFrameBounds();
   	
   	// resize window if necessary
   	if(windowBounds.width > screenSize.width)
   		windowBounds.width = screenSize.width - 50;
   	if(windowBounds.height > screenSize.height)
   		windowBounds.height = windowBounds.height - 50;
   	
   	// center window if necessary
   	if(windowBounds.x + windowBounds.width > screenSize.width ||
   		windowBounds.y + windowBounds.height > screenSize.height) {
   		frame.setLocationRelativeTo(null);
   	} else {
   		frame.setLocation(windowBounds.getLocation());
   	}
   	setOpenInFrame(true);
   	
   	frame.setSize(windowBounds.getSize());
   	frame.setVisible(true);
	
   	// make titlebar visible if necessary
		updatePanel();
		
		frame.repaint();
	}
	
	/**
	 * Remove the frame.
	 */
	public void removeFrame() {
		frame.removeAll();
		frame.setVisible(false);
		frame = null;
	}
	
	/**
	 * Update all elements in the title bar.
	 */
	public void updateTitleBar() {
		// The view is in the main window
		if(frame == null) {
			closeButton.setVisible(true);
			windowButton.setVisible(true);
			titleLabel.setVisible(true);
			unwindowButton.setVisible(false);
			
			if(titlePanel.getMouseListeners().length == 0) {
				titlePanel.addMouseListener(this);
			}
		} else {
			closeButton.setVisible(true);
			unwindowButton.setVisible(true);
			
			windowButton.setVisible(false);
			titleLabel.setVisible(false);
			
			titlePanel.removeMouseListener(this);
		}
		
		updateLabels();
	}
	
	/**
	 * A panel is 'alone' if no other panel is visible in the main frame. In this
	 * case no title bar is displayed, but just the style bar. Changing the 
	 * value of the 'alone' state will cause the GUI to update automatically if this
	 * panel is visible. 
	 * 
	 * @param isAlone
	 */
	public void setAlone(boolean isAlone) {
		if(this.isAlone == isAlone) {
			return;
		}
		
		this.isAlone = isAlone;
		
		if(isVisible()) {
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
	 * Update the panel.
	 */
	public void updatePanel() {
		
		// load content if panel was hidden till now
		if(component == null && isVisible()) {
			component = loadComponent();
			add(component, BorderLayout.CENTER);
			
			if(hasStyleBar && isStyleBarVisible()) {
				styleBar = loadStyleBar();
				styleBarPanel.add(styleBar, BorderLayout.CENTER);
			}
			
			// load toolbar if this panel has one
			if(hasToolbar()) {
				toolbar = new Toolbar(app, this);
				
				if(isOpenInFrame()) {
					toolbarContainer = new ToolbarContainer(app, false);
					toolbarContainer.addToolbar(toolbar);
					toolbarContainer.buildGui();
					toolbarContainer.setActiveToolbar(getViewId());
					toolbarPanel.add(toolbarContainer, BorderLayout.CENTER);
				}
			} 
			
			// euclidian view uses the general toolbar
			if(this instanceof EuclidianDockPanelAbstract) {
				// TODO implement..
			}
		}
		
		// make panels visible if necessary
		if(isVisible()) {
			if(hasStyleBar) {
				if(isStyleBarVisible() && styleBar == null) {
					styleBar = loadStyleBar();
					styleBarPanel.add(styleBar, BorderLayout.CENTER);
				}
				
				styleBarPanel.setVisible(isStyleBarVisible());
				toggleStyleBarButton.setVisible(app.getSettings().getLayout().isAllowingStyleBar());
			} 
			
			// display toolbar panel if the dock panel is open in a frame
			if(hasToolbar()) {
				toolbarPanel.setVisible(frame != null);
			}
		}
		
		// if this is the last dock panel don't display the title bar, otherwise
		// take the user's configuration into consideration
		titlePanel.setVisible(!isAlone && !app.isApplet() && app.getSettings().getLayout().showTitleBar());
		
		// update the title bar if necessary
		if(titlePanel.isVisible()) {
			updateTitleBar();
		}
	}
	
	/**
	 * Update the toolbar of this dock panel if it's open in its own toolbar
	 * container. 
	 */
	public void updateToolbar() {
		if(isVisible() && isOpenInFrame() && hasToolbar()) {
			toolbarContainer.updateToolbarPanel();
		}
	}
	
	/**
	 * Change the toolbar mode for panels open in a separate frame.
	 * 
	 * @param mode
	 */
	public void setToolbarMode(int mode) {
		if(isVisible() && isOpenInFrame() && hasToolbar()) {
			toolbarContainer.setMode(mode);
		}
	}
	
	/**
	 * Update the toolbar GUI.
	 */
	public void buildToolbarGui() {
		if(toolbarContainer != null) {
			toolbarContainer.buildGui();
			toolbarContainer.updateHelpText();
			
			if(isVisible() && isOpenInFrame()) {
				frame.validate();
			}
		}
	}
	
	/**
	 * Update all labels of this DockPanel. Called while initializing and if
	 * the language was changed.
	 */
	public void updateLabels() {		
		closeButton.setToolTipText(app.getMenuTooltip("Close"));
		windowButton.setToolTipText(app.getPlainTooltip("ViewOpenExtraWindow"));
		unwindowButton.setToolTipText(app.getPlainTooltip("ViewCloseExtraWindow"));
		
		if(hasStyleBar) {
			toggleStyleBarButton.setToolTipText(app.getPlainTooltip("ToggleStyleBar"));
		}
		
		if(frame == null) {
			titleLabel.setText(getPlainTitle());
		} else {
			updateTitle();
		}
	}
	
	/**
	 * Update fonts.
	 */
	public void updateFonts() {
		if(hasFocus && dockManager.hasFullFocusSystem()) {
			titleLabel.setFont(app.getBoldFont());
		} else {
			titleLabel.setFont(app.getPlainFont());
		}
	}
	
	/**
	 * Update the title of the frame. This is necessary if the language changed
	 * or if the title of the main window changed (e.g. because the file was saved
	 * under a different name).
	 */
	public void updateTitle() {
		if(isOpenInFrame()) {
			StringBuilder windowTitle = new StringBuilder();
			windowTitle.append(getPlainTitle());
			
	        if (app.getCurrentFile() != null) {
	        	windowTitle.append(" - ");
	            windowTitle.append(app.getCurrentFile().getName());
	        } else {
	        	if (GeoGebraFrame.getInstanceCount() > 1) {
	        		int nr = ((GeoGebraFrame)app.getFrame()).getInstanceNumber();        	
	        		windowTitle.append(" - (");
	        		windowTitle.append(nr+1);
	        		windowTitle.append(")");
	        	}
	        }
			
			frame.setTitle(windowTitle.toString());
		}
	}
	
	/**
	 * Close this panel.
	 */
	protected void closePanel() {
		dockManager.hide(this);
		dockManager.getLayout().getApplication().updateMenubar();
		
		if(dockManager.getFocusedPanel() == this) {
			dockManager.setFocusedPanel(null);
		}
	}
	
	/**
	 * Display this panel in an external window.
	 */
	private void windowPanel() {
		// move the toolbar from the main window to the panel
		if(hasToolbar()) {
			if(toolbarContainer == null) {
				toolbarContainer = new ToolbarContainer(app, false);
			}
			
			toolbarContainer.addToolbar(toolbar);
			toolbarContainer.buildGui();
			toolbarContainer.setActiveToolbar(getViewId());
			toolbarPanel.add(toolbarContainer, BorderLayout.CENTER);
			
			ToolbarContainer mainContainer = app.getGuiManager().getToolbarPanel();
			mainContainer.removeToolbar(toolbar);
			mainContainer.updateToolbarPanel();
		}
		
		dockManager.hide(this, false);
		setVisible(true);		
		createFrame();
	}
	
	/**
	 * Display this panel in the main window.
	 */
	private void unwindowPanel() {
		// hide the frame
		dockManager.hide(this, false);
		
		// don't display this panel in a frame the next time
		setOpenInFrame(false);
		
		// show the panel in the main window
		dockManager.show(this);
		
		// as this view already *had* focus and will retain focus DockManager::show()
		// won't be able to update the active toolbar
		if(hasToolbar()) {
			app.getGuiManager().getToolbarPanel().setActiveToolbar(toolbar);
		}
	}
	
	/**
	 * Toggle the style bar.
	 */
	public void toggleStyleBar() {
		if(!this.hasStyleBar) return;
		
		if(!showStyleBar && styleBar == null) {
			styleBar = loadStyleBar();
			styleBarPanel.add(styleBar, BorderLayout.CENTER);
		}
		
		styleBarPanel.setVisible(!showStyleBar);
		setShowStyleBar(!showStyleBar);
	}

	/**
	 * One of the buttons were pressed.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == closeButton) {
			closePanel();
		} else if(e.getSource() == windowButton) {
			windowPanel();
		} else if(e.getSource() == unwindowButton) {
			unwindowPanel();
		} else if(e.getSource() == toggleStyleBarButton) {
			toggleStyleBar();
		}
	}

	/**
	 * Hide the view if the window was closed or if the close button was
	 * pressed. 
	 */
	public void windowClosing(WindowEvent e) {
		closePanel();
	}
	
	/**
	 * Start dragging if the mouse was pressed while it was on the
	 * title panel.
	 */
	public void mousePressed(MouseEvent arg0) {
		if(arg0.getClickCount()==2)
			toggleStyleBar();
		else
			dockManager.drag(this);
	}
	
	/**
	 * @return The parent DockSplitPane or null.
	 */
	public DockSplitPane getParentSplitPane() {
		if(isOpenInFrame())
			return null;
		
		Container parent = getParent();
		
		if(parent == null || !(parent instanceof DockSplitPane))
			return null;
		else
			return (DockSplitPane)parent;
	}

	/**
	 * @return The embedded def string for this DockPanel.
	 */
	public String calculateEmbeddedDef() {
		StringBuilder def = new StringBuilder();

		Component current = this;
		Component parent = this.getParent();
		DockSplitPane parentDSP;
		
		while(parent instanceof DockSplitPane) {
			int defType = -1;
			
			parentDSP = (DockSplitPane)parent;
			
			if(parentDSP.getOrientation() == DockSplitPane.HORIZONTAL_SPLIT) {
				if(current == parentDSP.getLeftComponent()) // left
					defType = 3;
				else // right
					defType = 1;
			} else {
				if(current == parentDSP.getLeftComponent()) // top
					defType = 0;
				else // bottom
					defType = 2;
			}
			
			if(def.length() == 0) {
				def.append(defType);
			} else {
				def.append(","+defType);
			}
			
			current = parent;
			parent = current.getParent();
		}
		
		return def.reverse().toString();
	}
	
	/**
	 * @return The XML container which stores all relevant information for this
	 *			panel.
	 */
	public DockPanelXml createInfo() {
		return new DockPanelXml(
			id, 
			toolbarString,
			visible,
			openInFrame, 
			showStyleBar, 
			frameBounds, 
			embeddedDef, 
			embeddedSize
		);
	}
	
	/**
	 * @return If this DockPanel is in an extra frame / window.
	 */
	public boolean isInFrame() {
		return frame != null;
	}
	
	/**
	 * If this view should open in a frame. Has no immediate effect.
	 * @param openInFrame
	 */
	public void setOpenInFrame(boolean openInFrame) {
		this.openInFrame = openInFrame;
	}
	
	/**
	 * @return Whether this view should open in frame.
	 */
	public boolean isOpenInFrame() {
		return openInFrame;
	}
	
	/**
	 * If the stylebar of this view should be visible. Has no immediate effect.
	 * @param showStyleBar
	 */
	public void setShowStyleBar(boolean showStyleBar) {
		this.showStyleBar = showStyleBar;
		
		if(toggleStyleBarButton != null) {
			if(showStyleBar) {
				toggleStyleBarButton.setIcon(app.getImageIcon("triangle-up.png"));
			} else {
				toggleStyleBarButton.setIcon(app.getImageIcon("triangle-down.png"));
			}
		}
	}
	
	/**
	 * @return If the style bar should be visible.
	 */
	private boolean isStyleBarVisible() {
		return (isAlone || showStyleBar)  && app.getSettings().getLayout().isAllowingStyleBar();
	}
	
	public void setFrameBounds(Rectangle frameBounds) {
		this.frameBounds = frameBounds;
	}
	
	public Rectangle getFrameBounds() {
		return this.frameBounds;
	}

	/**
	 * @param embeddedDef the embeddedDef to set
	 */
	public void setEmbeddedDef(String embeddedDef) {
		this.embeddedDef = embeddedDef;
	}
	
	public String getEmbeddedDef() {
		return embeddedDef;
	}

	/**
	 * @param embeddedSize the embeddedSize to set
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
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean hasFocus() {
		return hasFocus;
	}
	
	/**
	 * Mark this panel as focused. When gaining focus the panel will
	 * automatically request focus for its parent frame.
	 * 
	 * @remark The focus system implemented here has nothing to do with
	 * swings focus system, therefore Swings focus methods won't work.
	 * 
	 * @param hasFocus
	 */
	public void setFocus(boolean hasFocus) {
		// don't change anything if it's not necessary
		if(this.hasFocus == hasFocus)
			return;
		
		this.hasFocus = hasFocus;
		
		if(hasFocus) {
			// request focus and change toolbar if necessary
			if(openInFrame) {
				frame.requestFocus();
			} else {
				if(!app.isApplet()) {
					JFrame frame = app.getFrame();
					
					if(frame != null) {
						frame.toFront();
					}
				}
				
				
				setActiveToolBar();
			}
		}
		
		else {
			
		}
		
		// call callback methods for focus changes 
		if(hasFocus) {
			focusGained();
		} else {
			focusLost();
		}
		
		/*
		 * Mark the focused view in bold if the focus system is available. If 
		 * this isn't the case we always stick with the normal font as it would
		 * confuse the users that the focus "indicator" just changes if we switch
		 * between EVs. 
		 */
		if(dockManager.hasFullFocusSystem()) {
			setTitleLabelFocus();
		}
	}
	
	/**
	 * sets the active toolbar
	 */
	protected void setActiveToolBar(){
		if(hasToolbar()) {
			app.getGuiManager().getToolbarPanel().setActiveToolbar(toolbar);
		} else {
			app.getGuiManager().getToolbarPanel().setActiveToolbar(-1);
		}
		//switching the view may cause shrinking of help panel, 
		//we need an update here
		app.getGuiManager().getToolbarPanel().validate();
		app.getGuiManager().getToolbarPanel().updateHelpText();
	}
	
	
	/**
	 * sets the title label when this has not the focus
	 */	
	protected void setTitleLabelFocus(){
		if(hasFocus) 
			titleLabel.setFont(app.getBoldFont());
		else
			titleLabel.setFont(app.getPlainFont());
	}	
	
	/**
	 * @return An unique ID for this DockPanel.
	 */
	public int getViewId() {
		return id;
	}
	
	/**
	 * @return The title of this view. The String returned has to be the key of a value
	 * in plain.properties
	 */
	public String getViewTitle() {
		return title;
	}
	
	/**
	 * @return The order of this panel in the view menu, with 0 being "highest". Will be
	 * -1 if this view does not appear in the menu at all.
	 */
	public int getMenuOrder() {
		return menuOrder;
	}
	
	/**
	 * @return Whether the current view has a menu shortcut to toggle its visibility.
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
	 * @return The toolbar associated with this panel.
	 */
	public Toolbar getToolbar() {
		return toolbar;
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
	 * Set the toolbar string of this view. If the toolbar string is null but this 
	 * panel has a panel normally the default toolbar string is used. This is used for
	 * backward compability. Has no visible effect.
	 * 
	 * @param toolbarString
	 */
	public void setToolbarString(String toolbarString) {
		if(toolbarString == null && hasToolbar()) {
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
	 * Helper class to compare dock panels for sorting in the menu. 
	 * 
	 * @author Florian Sonner
	 */
	public static class MenuOrderComparator implements Comparator<DockPanel>  {
		public int compare(DockPanel a, DockPanel b) {
			return a.getMenuOrder() - b.getMenuOrder();
		}
	}

	public void windowClosed(WindowEvent e) { }
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) {	}
	public void mouseExited(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	
	/**
	 * UI for the buttons in the title panel. Used for Mac as the normal
	 * buttons are not displayed correclty as they are too small.
	 * 
	 * @author Florian Sonner
	 */
	private static class TitleBarButtonUI extends BasicButtonUI
	{ 
		@Override
		public void paint(Graphics g, JComponent component) {
			JButton button = (JButton)component;
			
			// TODO implement drawing...
			
			super.paint(g, component);
		}
	}
}
