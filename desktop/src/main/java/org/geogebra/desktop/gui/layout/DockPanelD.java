package org.geogebra.desktop.gui.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.Border;

import org.geogebra.common.gui.layout.DockComponent;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GRectangleD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.gui.layout.panels.EuclidianDockPanelAbstract;
import org.geogebra.desktop.gui.toolbar.ToolbarContainer;
import org.geogebra.desktop.gui.toolbar.ToolbarD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

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
public abstract class DockPanelD extends JPanel implements ActionListener,
		WindowListener, MouseListener, DockPanel, DockComponent {
	private static final long serialVersionUID = 1L;

	protected DockManagerD dockManager;
	protected AppD app;
	protected LocalizationD loc;

	/**
	 * The ID of this dock panel.
	 */
	protected int id;

	/**
	 * The title of this dock panel.
	 */
	private String title;

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
	protected JComponent styleBar;

	/**
	 * Panel to contain a toggle button within the stylebar panel.
	 */
	private JPanel styleBarButtonPanel;

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
	 * The panel at the top where the title and the close button is displayed
	 * normally.
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
	private JButton unwindowButton, unwindowButton2;

	/**
	 * Button used to show / hide the style bar in the titlePanel.
	 */
	private JButton toggleStyleBarButton;

	/**
	 * Button used to show / hide the style bar when title panel is invisible.
	 */
	private JButton toggleStyleBarButton2;

	/**
	 * Button to maximize/unmaximize a panel.
	 */
	private JButton maximizeButton;

	/**
	 * Panel for the styling bar if one is available.
	 */
	private JPanel styleBarPanel;

	/**
	 * Panel used for the toolbar if this dock panel has one.
	 */
	private JPanel toolbarPanel;

	/**
	 * Toolbar container which is used if this dock panel is opened in its own
	 * frame.
	 */
	private ToolbarContainer toolbarContainer;

	/**
	 * Toolbar associated with this dock panel or null if this panel has no
	 * toolbar.
	 */
	private ToolbarD toolbar;

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
	 * The window which holds this DockPanel if the DockPanel is opened in an
	 * additional window. The window may become either a JFrame or JDialog.
	 */
	protected Window frame = null;

	/**
	 * The component used for this view.
	 */
	protected JComponent component;

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
	 * Flag to determine if a dialog is newly created
	 */
	private boolean isNewDialog = true;

	/**
	 * Flag to determine if the frame field will be created as a JDialog (true)
	 * or as a JFram (false). Default is false.
	 */
	private boolean isDialog = false;

	/**
	 * If the view needs a menu bar when undocked, its is kept here
	 */
	private JMenuBar menubar;

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
	public DockPanelD(int id, String title, String toolbar, boolean hasStyleBar,
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
	public DockPanelD(int id, String title, String toolbar, boolean hasStyleBar,
			int menuOrder, char menuShortcut) {
		this.id = id;
		this.title = title;
		this.defaultToolbarString = toolbar;
		this.menuOrder = menuOrder;
		this.menuShortcut = menuShortcut;
		this.hasStyleBar = hasStyleBar;
		this.isAlone = false;
		this.setMinimumSize(new Dimension(100, 100));
		setLayout(new BorderLayout());
	}

	/**
	 * @param app
	 *            application
	 */
	protected void setApp(AppD app) {
		this.app = app;
		this.loc = app.getLocalization();
	}

	/**
	 * @return The icon of the menu item, if this method was not overwritten it
	 *         will return the empty icon or null for Win Vista / 7 to prevent
	 *         the "checkbox bug"
	 */
	public ImageIcon getIcon() {
		if (AppD.WINDOWS_VISTA_OR_LATER) {
			return null;
		}
		return app.getEmptyIcon();
	}

	/**
	 * @return The style bar. Note: Unless this method is overridden a dummy
	 *         stylebar is returned.
	 */
	protected JComponent loadStyleBar() {
		return new JPanel();
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
	 * Method which is called if this dock panel gained focus. This happens if
	 * setFocus(true) was called and this panel had no focus before.
	 * 
	 * Remark: If GeoGebra is running as unsigned applet focus is just changed
	 *         between euclidian views (even if other views were selected in the
	 *         meantime).
	 */
	protected void focusGained() {
		// by default do nothing
	}

	/**
	 * Method which is called if this dock panel lost focus. This happens if
	 * setFocus(false) was called and this panel had focus before.
	 * 
	 * Remark: If GeoGebra is running as unsigned applet focus is just changed
	 *         between euclidian views (even if other views were selected in the
	 *         meantime).
	 */
	protected void focusLost() {
		// by default do nothing
	}

	/**
	 * create the focus panel (composed of titleLabel, and, for
	 * EuclidianDockPanels, focus icon)
	 * 
	 * @return the focus panel
	 */
	protected JComponent createFocusPanel() {
		titleLabel = new JLabel(loc.getMenu(title));
		titleLabel.setFont(app.getPlainFont());
		titleLabel.setForeground(Color.darkGray);

		JPanel p = new JPanel(new FlowLayout(app.flowLeft(), 2, 1));

		if (app.getLocalization().isRightToLeftReadingOrder()) {
			p.add(titleLabel);
			p.add(Box.createHorizontalStrut(2));
			if (this.hasStyleBar) {
				p.add(this.toggleStyleBarButton);
			}
		} else {
			if (this.hasStyleBar) {
				p.add(this.toggleStyleBarButton);
			}
			p.add(Box.createHorizontalStrut(2));
			p.add(titleLabel);
		}
		return p;
	}

	/**
	 * Bind this view to a dock manager. Also initializes the whole GUI as just
	 * at this point the application is available.
	 * 
	 * @param dockManager1
	 *            dock manager
	 */
	public void register(DockManagerD dockManager1) {
		this.dockManager = dockManager1;
		setApp(dockManager1.getLayout().getApplication());
		// create buttons for the panels
		createButtons();

		// create button panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(app.flowRight(), 0, 1));
		if (app.getLocalization().isRightToLeftReadingOrder()) {
			buttonPanel.add(closeButton);
			buttonPanel.add(Box.createHorizontalStrut(4));
			buttonPanel.add(windowButton);
			buttonPanel.add(unwindowButton);
			buttonPanel.add(Box.createHorizontalStrut(4));
			buttonPanel.add(maximizeButton);
		} else {
			buttonPanel.add(maximizeButton);
			buttonPanel.add(Box.createHorizontalStrut(4));
			buttonPanel.add(unwindowButton);
			buttonPanel.add(windowButton);
			buttonPanel.add(Box.createHorizontalStrut(4));
			buttonPanel.add(closeButton);
		}

		// Custom border for the major panels (title, stylebar and toolbar)
		Border panelBorder = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0,
						SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2));

		// create style bar panel
		styleBarPanel = new JPanel(new BorderLayout(1, 2));
		styleBarPanel.setBorder(panelBorder);
		styleBarPanel.addMouseListener(this);

		styleBarButtonPanel = new JPanel(new BorderLayout());
		JPanel p = new JPanel(new FlowLayout(0, 0, app.flowLeft()));
		if (this.hasStyleBar) {
			p.add(toggleStyleBarButton2);
		}
		p.add(Box.createHorizontalStrut(4));

		styleBarButtonPanel.add(p, BorderLayout.NORTH);
		styleBarPanel.add(styleBarButtonPanel, loc.borderWest());
		styleBarPanel.add(LayoutUtil.flowPanelRight(0, 0, 4, unwindowButton2),
				loc.borderEast());

		// construct the title panel and add all elements
		titlePanel = new JPanel();
		titlePanel.setBorder(panelBorder);
		titlePanel.setLayout(new BorderLayout());

		titlePanel.add(createFocusPanel(), loc.borderWest());
		titlePanel.add(buttonPanel, loc.borderEast());
		titlePanel.addMouseListener(this); // dragging to reconfigure
		titlePanel.addMouseListener(new MyButtonHider());

		// create toolbar panel
		if (hasToolbar()) {
			toolbarPanel = new JPanel(new BorderLayout());
			toolbarPanel.setBorder(panelBorder);
		}

		// construct a meta panel to hold the title, tool bar and style bar
		// panels

		JPanel titleBar = new JPanel(new BorderLayout());
		titleBar.add(styleBarPanel, BorderLayout.SOUTH);
		titleBar.add(titlePanel, BorderLayout.NORTH);

		JPanel metaPanel = new JPanel(new BorderLayout());
		metaPanel.add(titleBar, BorderLayout.SOUTH);
		if (hasToolbar()) {
			metaPanel.add(toolbarPanel, BorderLayout.CENTER);
		}

		// make titlebar visible if necessary
		updatePanel();

		add(metaPanel, BorderLayout.NORTH);
	}

	private void createButtons() {
		int iconSize = 16;// app.getScaledIconSize();

		int toggleSize = (int) Math.round(app.getScaledIconSize() * 0.75);
		// button to show/hide styling bar and the title panel buttons
		toggleStyleBarButton = new JButton();
		toggleStyleBarButton.addActionListener(this);
		toggleStyleBarButton.setFocusPainted(false);
		toggleStyleBarButton.setBorderPainted(false);
		toggleStyleBarButton.setContentAreaFilled(false);
		toggleStyleBarButton
				.setPreferredSize(new Dimension(toggleSize, toggleSize));
		toggleStyleBarButton.setRolloverEnabled(true);

		// button to show/hide styling bar if the title panel is invisible
		toggleStyleBarButton2 = new JButton();
		toggleStyleBarButton2.setFocusPainted(false);
		toggleStyleBarButton2.setBorderPainted(false);
		toggleStyleBarButton2.setContentAreaFilled(false);
		toggleStyleBarButton2
				.setPreferredSize(new Dimension(toggleSize, toggleSize));
		toggleStyleBarButton2.addActionListener(this);
		toggleStyleBarButton2.setRolloverEnabled(true);

		updateToggleStyleBarButtons();

		// button to insert the view in the main window
		unwindowButton = new JButton(
				app.getScaledIcon(GuiResourcesD.VIEW_UNWINDOW));
		unwindowButton.addActionListener(this);
		unwindowButton.setFocusPainted(false);
		unwindowButton.setContentAreaFilled(false);
		unwindowButton.setBorderPainted(false);
		unwindowButton.setPreferredSize(new Dimension(iconSize, iconSize));

		// button to insert the view in the main window
		unwindowButton2 = new JButton(
				app.getScaledIcon(GuiResourcesD.VIEW_UNWINDOW));
		unwindowButton2.addActionListener(this);
		unwindowButton2.setFocusPainted(false);
		unwindowButton2.setContentAreaFilled(false);
		unwindowButton2.setBorderPainted(false);
		unwindowButton2.setPreferredSize(new Dimension(iconSize, iconSize));

		// button to display the view in a separate window
		windowButton = new JButton(
				app.getScaledIcon(GuiResourcesD.VIEW_WINDOW));
		windowButton.addActionListener(this);
		windowButton.setFocusPainted(false);
		windowButton.setContentAreaFilled(false);
		windowButton.setBorderPainted(false);
		windowButton.setPreferredSize(new Dimension(iconSize, iconSize));

		// button to close the view
		closeButton = new JButton(app.getScaledIcon(GuiResourcesD.VIEW_CLOSE));
		closeButton.addActionListener(this);
		closeButton.setFocusPainted(false);
		closeButton.setPreferredSize(new Dimension(iconSize, iconSize));

		// button to toggle maximize/normal state
		maximizeButton = new JButton(
				app.getScaledIcon(GuiResourcesD.VIEW_MAXIMIZE));
		maximizeButton.addActionListener(this);
		maximizeButton.setFocusPainted(false);
		maximizeButton.setPreferredSize(new Dimension(iconSize, iconSize));

	}

	/**
	 * 
	 * @return title in plain style
	 */
	protected String getPlainTitle() {
		return loc.getMenu(title);
	}

	/**
	 * Create a frame for this DockPanel. The frame will either be a JFrame or a
	 * JDialog depending on the isDialog flag.
	 */
	public void createFrame() {

		if (isDialog) {
			frame = new JDialog(app.getFrame(), false) {
				private static final long serialVersionUID = 1L;

				// Send window closing event when dialog is set invisible.
				// This allows a dock panel view to close properly.
				@Override
				public void setVisible(boolean isVisible) {
					if (!isVisible && frame != null) {
						windowClosing(new WindowEvent(frame,
								WindowEvent.WINDOW_CLOSING));
					}
					super.setVisible(isVisible);
				}
			};
		} else {
			frame = new JFrame(getPlainTitle());
			// needs the higher res as used by Windows 7 for the Toolbar
			((JFrame) frame).setIconImage(
					app.getInternalImage(GuiResourcesD.GEOGEBRA64));
		}

		frame.addWindowListener(this);

		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent event) {
				setFrameBounds(event.getComponent().getBounds());
			}

			@Override
			public void componentMoved(ComponentEvent event) {
				setFrameBounds(event.getComponent().getBounds());
			}
		});

		if (isDialog) {
			(((JDialog) frame).getContentPane()).add(this);
		} else {
			(((JFrame) frame).getContentPane()).add(this);
			menubar = loadMenuBar();
			if (menubar != null) {
				((JFrame) frame).setJMenuBar(menubar);
			}
		}

		// TODO multimonitor supported?
		Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds();

		// Use the previous dimension of this view
		Rectangle windowBounds = getFrameBounds();

		// resize window if necessary
		if (windowBounds.width > screenSize.width) {
			windowBounds.width = screenSize.width - 50;
		}
		if (windowBounds.height > screenSize.height) {
			windowBounds.height = windowBounds.height - 50;
		}

		// center window if necessary
		if (isNewDialog) {
			// frame.pack();
			frame.setSize(windowBounds.getSize());
			frame.setLocationRelativeTo(app.getMainComponent());
			isNewDialog = false;
		} else if (windowBounds.x + windowBounds.width > screenSize.width
				|| windowBounds.y + windowBounds.height > screenSize.height) {
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
		if (frame == null) {
			closeButton.setVisible(!isMaximized());
			windowButton.setVisible(false); // !isMaximized());
			unwindowButton.setVisible(false);
			unwindowButton2.setVisible(false);
			maximizeButton.setVisible(isMaximized());
			titleLabel.setVisible(true);

		} else {
			closeButton.setVisible(false);
			unwindowButton.setVisible(true);
			unwindowButton2.setVisible(true);
			windowButton.setVisible(false);
			maximizeButton.setVisible(false);
			titleLabel.setVisible(false);

		}

		if (isMaximized()) {
			maximizeButton
					.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_UNMAXIMIZE));
		} else {
			maximizeButton
					.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_MAXIMIZE));
		}

		updateLabels();
	}

	/**
	 * A panel is 'alone' if no other panel is visible in the main frame. In
	 * this case no title bar is displayed, but just the style bar. Changing the
	 * value of the 'alone' state will cause the GUI to update automatically if
	 * this panel is visible.
	 * 
	 * @param isAlone
	 *            whether is only panel in the main frame
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
	 * 
	 * @param isHidden
	 *            true for hidden dock panel
	 */
	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	/**
	 * Update the panel.
	 */
	public void updatePanel() {

		// load content if panel was hidden till now
		if (component == null && isVisible()) {
			component = loadComponent();
			add(component, BorderLayout.CENTER);

			if (isStyleBarVisible()) {
				setStyleBar();
			}

			// load toolbar if this panel has one
			if (hasToolbar()) {
				toolbar = new ToolbarD(app, this);

				if (isOpenInFrame()) {
					toolbarContainer = new ToolbarContainer(app, false);
					toolbarContainer.addToolbar(toolbar);
					toolbarContainer.buildGui();
					toolbarContainer.setActiveToolbar(getViewId());
					toolbarPanel.add(toolbarContainer, BorderLayout.CENTER);
				}
			}

			// euclidian view uses the general toolbar
			if (this instanceof EuclidianDockPanelAbstract) {
				// TODO implement..
			}
		}

		// make panels visible if necessary
		if (isVisible()) {

			if (isStyleBarVisible()) {
				setStyleBar();
			}

			// display toolbar panel if the dock panel is open in a frame
			if (hasToolbar()) {
				toolbarPanel.setVisible(frame != null);
			}
		}

		// if this is the last dock panel don't display the title bar, otherwise
		// take the user's configuration into consideration

		titlePanel.setVisible(app.getSettings().getLayout().showTitleBar()
				&& !(isAlone && !isMaximized()) && !app.isApplet()
				&& (!isOpenInFrame()));

		// update stylebar visibility
		setShowStyleBar(isStyleBarVisible());
		updateStyleBarVisibility();

		// update the title bar if necessary
		updateTitleBarIfNecessary();

	}

	/**
	 * 
	 */
	protected void updateTitleBarIfNecessary() {
		if (titlePanel.isVisible()) {
			updateTitleBar();
		}
	}

	protected JMenuBar loadMenuBar() {
		return null;
	}

	/**
	 * Update the toolbar of this dock panel if it's open in its own toolbar
	 * container.
	 */
	public void updateToolbar() {
		if (isVisible() && isOpenInFrame() && hasToolbar()) {
			toolbarContainer.updateToolbarPanel();
		}
	}

	/**
	 * Change the toolbar mode for panels open in a separate frame.
	 * 
	 * @param mode
	 */
	public void setToolbarMode(int mode) {
		if (toolbarContainer != null && isVisible() && isOpenInFrame()
				&& hasToolbar()) {
			toolbarContainer.setMode(mode);
		}
	}

	/**
	 * Update the toolbar GUI.
	 */
	public void buildToolbarGui() {
		if (toolbarContainer != null) {
			toolbarContainer.buildGui();
			toolbarContainer.updateHelpText();

			if (isVisible() && isOpenInFrame()) {
				frame.validate();
			}
		}
	}

	/**
	 * Update all labels of this DockPanel. Called while initializing and if the
	 * language was changed.
	 */
	public void updateLabels() {
		closeButton.setToolTipText(loc.getMenuTooltip("Close"));
		windowButton.setToolTipText(loc.getPlainTooltip("ViewOpenExtraWindow"));
		unwindowButton
				.setToolTipText(loc.getPlainTooltip("ViewCloseExtraWindow"));
		unwindowButton2
				.setToolTipText(loc.getPlainTooltip("ViewCloseExtraWindow"));
		toggleStyleBarButton
				.setToolTipText(loc.getPlainTooltip("ToggleStyleBar"));
		toggleStyleBarButton2
				.setToolTipText(loc.getPlainTooltip("ToggleStyleBar"));

		if (frame == null) {
			titleLabel.setText(getPlainTitle());
		} else {
			updateTitle();
		}
	}

	/**
	 * Update fonts.
	 */
	public void updateFonts() {
		if (hasFocus && dockManager.hasFullFocusSystem()) {
			titleLabel.setFont(app.getBoldFont());
		} else {
			titleLabel.setFont(app.getPlainFont());
		}
		updateIcons();
	}

	/**
	 * Update the title of the frame. This is necessary if the language changed
	 * or if the title of the main window changed (e.g. because the file was
	 * saved under a different name).
	 */
	public void updateTitle() {
		if (isOpenInFrame()) {
			StringBuilder windowTitle = new StringBuilder();
			windowTitle.append(getPlainTitle());

			if (app.getCurrentFile() != null) {
				windowTitle.append(" - ");
				windowTitle.append(app.getCurrentFile().getName());
			} else {
				if (GeoGebraFrame.getInstanceCount() > 1) {
					int nr = ((GeoGebraFrame) app.getFrame())
							.getInstanceNumber();
					windowTitle.append(" - (");
					windowTitle.append(nr + 1);
					windowTitle.append(")");
				}
			}

			if (isDialog) {
				((JDialog) frame).setTitle(windowTitle.toString());
			} else {
				((JFrame) frame).setTitle(windowTitle.toString());
			}
		}
	}

	/**
	 * Close this panel permanently.
	 */
	@Override
	public void closePanel() {
		closePanel(true);
	}

	/**
	 * Close this panel.
	 * 
	 * @param isPermanent
	 *            true for permanent closing (also detach the view)
	 */
	protected void closePanel(boolean isPermanent) {
		dockManager.closePanel(this, isPermanent);
	}

	/**
	 * Display this panel in an external window.
	 */
	public void windowPanel() {

		// try to hide the panel
		if (dockManager.hide(this, false)) {

			// move the toolbar from the main window to the panel
			if (hasToolbar()) {
				if (toolbarContainer == null) {
					toolbarContainer = new ToolbarContainer(app, false);
				}

				toolbarContainer.addToolbar(toolbar);
				toolbarContainer.buildGui();
				toolbarContainer.setActiveToolbar(getViewId());
				toolbarPanel.add(toolbarContainer, BorderLayout.CENTER);

				ToolbarContainer mainContainer = ((GuiManagerD) app
						.getGuiManager()).getToolbarPanel();
				mainContainer.removeToolbar(toolbar);
				mainContainer.updateToolbarPanel();
			}

			setVisible(true);
			createFrame();
		}
	}

	/**
	 * Display this panel in the main window.
	 */
	public void unwindowPanel() {
		// hide the frame
		dockManager.hide(this, false);

		// don't display this panel in a frame the next time
		setOpenInFrame(false);

		// show the panel in the main window
		dockManager.show(this);

		// as this view already *had* focus and will retain focus
		// DockManager::show()
		// won't be able to update the active toolbar
		if (hasToolbar()) {
			((GuiManagerD) app.getGuiManager()).getToolbarPanel()
					.setActiveToolbar(toolbar);
		}

	}

	/** loads the styleBar and puts it into the stylBarPanel */
	protected void setStyleBar() {
		if (styleBar == null) {
			styleBar = loadStyleBar();
			styleBarPanel.add(styleBar, BorderLayout.CENTER);
		}
	}

	/**
	 * Toggle the style bar.
	 */
	public void toggleStyleBar() {
		setShowStyleBar(!showStyleBar);
		updateStyleBarVisibility();
	}

	/**
	 * Update the style bar visibility.
	 */
	public void updateStyleBarVisibility() {

		if (!isVisible()) {
			return;
		}

		styleBarPanel.setVisible(isStyleBarVisible());
		updateToggleStyleBarButtons();
		updateTitleBar();

		if (isStyleBarVisible()) {
			setStyleBar();
			styleBar.setVisible(showStyleBar);
			styleBarButtonPanel.setVisible(!titlePanel.isVisible());
		}
	}

	/**
	 * One of the buttons was pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeButton) {
			closePanel(false);
		} else if (e.getSource() == windowButton) {
			windowPanel();
		} else if (e.getSource() == unwindowButton
				|| e.getSource() == unwindowButton2) {
			unwindowPanel();
		} else if (e.getSource() == toggleStyleBarButton
				|| e.getSource() == toggleStyleBarButton2) {
			toggleStyleBar();

		} else if (e.getSource() == maximizeButton) {
			toggleMaximize();
		}
	}

	/**
	 * Hide the view if the window was closed or if the close button was
	 * pressed.
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		closePanel(false);
	}

	/**
	 * Start dragging if the mouse was pressed while it was on the title panel.
	 * Or toggle the stylebar on double-click.
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {

		// double-click opens the stylebar and shows the button panel
		if (arg0.getClickCount() == 2) {
			// toggleStyleBar();
			toggleMaximize();
		}

		// otherwise start drag if the view is in the main window
		else {
			if (frame == null) {
				dockManager.drag(this);
			}
		}
	}

	/**
	 * @return The parent DockSplitPane or null.
	 */
	public DockSplitPane getParentSplitPane() {
		if (isOpenInFrame()) {
			return null;
		}

		Container parent = getParent();

		if (parent == null || !(parent instanceof DockSplitPane)) {
			return null;
		}
		return (DockSplitPane) parent;
	}

	/**
	 * @return The embedded def string for this DockPanel.
	 */
	public String calculateEmbeddedDef() {
		StringBuilder def = new StringBuilder();

		Component current = this;
		Component parent = this.getParent();
		DockSplitPane parentDSP;

		while (parent instanceof DockSplitPane) {
			int defType = -1;

			parentDSP = (DockSplitPane) parent;

			if (parentDSP.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
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
				def.append("," + defType);
			}

			current = parent;
			parent = current.getParent();
		}

		return def.reverse().toString();
	}

	/**
	 * @return The XML container which stores all relevant information for this
	 *         panel.
	 */
	public DockPanelData createInfo() {
		return new DockPanelData(id, toolbarString, visible, openInFrame,
				showStyleBar, new GRectangleD(frameBounds), embeddedDef,
				embeddedSize);
	}

	/**
	 * @return If this DockPanel is in an extra frame / window.
	 */
	public boolean isInFrame() {
		return frame != null;
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
	@Override
	public boolean isOpenInFrame() {
		return openInFrame;
	}

	/**
	 * If the stylebar of this view should be visible. Has no immediate effect.
	 * 
	 * @param showStyleBar
	 */
	public void setShowStyleBar(boolean showStyleBar) {
		this.showStyleBar = showStyleBar;
	}

	private void updateToggleStyleBarButtons() {
		if (toggleStyleBarButton != null) {
			if (showStyleBar) {
				toggleStyleBarButton.setIcon(
						app.getScaledIcon(GuiResourcesD.TRIANGLE_DOWN));
			} else {
				toggleStyleBarButton.setIcon(
						app.getScaledIcon(GuiResourcesD.TRIANGLE_RIGHT));
			}
		}
		if (toggleStyleBarButton2 != null) {
			toggleStyleBarButton2.setIcon(toggleStyleBarButton.getIcon());
		}
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
		return (showStyleBar || !titlePanel.isVisible());
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
	public Window getFrame() {
		return frame;
	}

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
		if (this.visible != visible) {
			this.visible = visible;
			if (app.getGuiManager() != null) {
				app.getGuiManager().updatePropertiesViewStylebar();
			}
		}
	}

	@Override
	public boolean hasFocus() {
		return hasFocus;
	}

	/**
	 * Mark this panel as focused. When gaining focus the panel will
	 * automatically request focus for its parent frame.
	 * 
	 * Remark: The focus system implemented here has nothing to do with swings
	 *         focus system, therefore Swings focus methods won't work.
	 * 
	 * @param hasFocus
	 *            has the focus
	 * @param updatePropertiesView
	 *            update properties view
	 */
	public void setFocus(boolean hasFocus, boolean updatePropertiesView) {

		if (hasFocus && updatePropertiesView) {
			((GuiManagerD) app.getGuiManager()).updatePropertiesView();
		}

		setFocus(hasFocus);
	}

	/**
	 * Mark this panel as focused. When gaining focus the panel will
	 * automatically request focus for its parent frame.
	 * 
	 * Remark: The focus system implemented here has nothing to do with swings
	 *         focus system, therefore Swings focus methods won't work.
	 * 
	 * @param hasFocus
	 *            has the focus
	 */
	protected void setFocus(boolean hasFocus) {

		// don't change anything if it's not necessary
		if (this.hasFocus == hasFocus) {
			return;
		}

		this.hasFocus = hasFocus;

		if (hasFocus) {
			// request focus and change toolbar if necessary
			if (openInFrame) {
				frame.requestFocus();
			} else {
				if (!app.isApplet()) {
					JFrame frame1 = app.getFrame();

					if (frame1 != null) {
						frame1.toFront();
					}
				}

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
		int currentMode;
		if (hasToolbar()) {
			currentMode = ((GuiManagerD) app.getGuiManager()).getToolbarPanel()
					.setActiveToolbar(toolbar);
		} else {
			currentMode = ((GuiManagerD) app.getGuiManager()).getToolbarPanel()
					.setActiveToolbar(-1);
		}
		// switching the view may cause shrinking of help panel,
		// we need an update here
		((GuiManagerD) app.getGuiManager()).getToolbarPanel().validate();
		((GuiManagerD) app.getGuiManager()).getToolbarPanel()
				.updateHelpText(currentMode);
	}

	/**
	 * sets the title label when this has not the focus
	 */
	protected void setTitleLabelFocus() {
		if (dockManager.hasFullFocusSystem()) {
			if (titleIsBold()) {
				titleLabel.setFont(app.getBoldFont());
			} else {
				titleLabel.setFont(app.getPlainFont());
			}
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
	@Override
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
	 * @return The toolbar associated with this panel.
	 */
	public ToolbarD getToolbar() {
		return toolbar;
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
		if (toolbarString == null) {
			Log.warn("Toolbar not initialized");
			return defaultToolbarString;
		}
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
	@Override
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
	 * Helper class to compare dock panels for sorting in the menu.
	 * 
	 * @author Florian Sonner
	 */
	public static class MenuOrderComparator implements Comparator<DockPanelD> {
		@Override
		public int compare(DockPanelD a, DockPanelD b) {
			return a.getMenuOrder() - b.getMenuOrder();
		}
	}

	@Override
	public final void windowClosed(WindowEvent e) {
		// only handle windowClosing
	}

	@Override
	public final void windowActivated(WindowEvent e) {
		// only handle windowClosing
	}

	@Override
	public final void windowDeactivated(WindowEvent e) {
		// only handle windowClosing
	}

	@Override
	public final void windowDeiconified(WindowEvent e) {
		// only handle windowClosing
	}

	@Override
	public final void windowIconified(WindowEvent e) {
		// only handle windowClosing
	}

	@Override
	public final void windowOpened(WindowEvent e) {
		// only handle windowClosing
	}

	@Override
	public final void mouseClicked(MouseEvent e) {
		// only handle mousePressed
	}

	@Override
	public final void mouseEntered(MouseEvent e) {
		// only handle mousePressed
	}

	@Override
	public final void mouseExited(MouseEvent e) {
		// only handle mousePressed
	}

	@Override
	public final void mouseReleased(MouseEvent e) {
		// only handle mousePressed
	}

	public class MyButtonHider extends MouseAdapter {

		@Override
		public void mouseEntered(MouseEvent e) {
			// System.out.println("entered, not jpanel");
			if (e.getSource() != titlePanel) {
				e.consume();
			} else if (!windowButton.isVisible()
					&& (!isAlone() && !isInFrame() && !isMaximized())) {
				windowButton.setVisible(true);
			}

			// make sure tooltips from Tool Bar don't get in the way
			setToolTipText("");
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// System.out.println("exited:");
			if (!titlePanel.getVisibleRect().contains(e.getPoint())) {
				windowButton.setVisible(false);
			}
		}

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

		if (isMaximized()) {
			dockManager.undoMaximize(true);
		} else {
			dockManager.maximize(this);
		}

		updatePanel();
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

	public boolean isEuclidianDockPanel3D() {
		return false;
	}

	@Override
	public void deferredOnResize() {
		// used in Web only
	}

	public void addToToolbar(int mode) {
		this.toolbarString = ToolBar.addMode(toolbarString, mode);

	}

	public void updateIcons() {
		if (toggleStyleBarButton == null) {
			return;
		}
		int iconSize = app.getScaledIconSize();

		int toggleSize = (int) Math.round(app.getScaledIconSize() * 0.75);
		// button to show/hide styling bar and the title panel buttons
		toggleStyleBarButton
				.setPreferredSize(new Dimension(toggleSize, toggleSize));

		// button to show/hide styling bar if the title panel is invisible
		toggleStyleBarButton2
				.setPreferredSize(new Dimension(toggleSize, toggleSize));

		// button to insert the view in the main window
		unwindowButton.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_UNWINDOW));
		unwindowButton.setPreferredSize(new Dimension(iconSize, iconSize));

		// button to insert the view in the main window
		unwindowButton2.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_UNWINDOW));
		unwindowButton2.setPreferredSize(new Dimension(iconSize, iconSize));

		// button to display the view in a separate window
		windowButton.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_WINDOW));
		windowButton.setPreferredSize(new Dimension(iconSize, iconSize));

		// button to close the view
		closeButton.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_CLOSE));
		closeButton.setPreferredSize(new Dimension(iconSize, iconSize));

		// button to toggle maximize/normal state
		maximizeButton.setPreferredSize(new Dimension(iconSize, iconSize));

		if (isMaximized()) {
			maximizeButton
					.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_UNMAXIMIZE));
		} else {
			maximizeButton
					.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_MAXIMIZE));
		}
		updateToggleStyleBarButtons();
	}

	@Override
	public void updateNavigationBar() {
		// not needed in desktop
	}

	public boolean hasPlane() {
		return false;
	}

}
