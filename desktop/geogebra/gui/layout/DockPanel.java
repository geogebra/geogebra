package geogebra.gui.layout;

import geogebra.common.gui.layout.DockComponent;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.gui.toolbar.ToolbarD;
import geogebra.gui.toolbar.ToolbarContainer;
import geogebra.gui.util.LayoutUtil;
import geogebra.main.AppD;

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
import javax.swing.plaf.basic.BasicButtonUI;

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
public abstract class DockPanel extends JPanel implements ActionListener,
		WindowListener, MouseListener, geogebra.common.gui.layout.DockPanel,
		DockComponent {
	private static final long serialVersionUID = 1L;

	protected DockManager dockManager;
	protected AppD app;

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
	private JComponent styleBar;

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
	public DockPanel(int id, String title, String toolbar, boolean hasStyleBar,
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
	public DockPanel(int id, String title, String toolbar, boolean hasStyleBar,
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
	 * @return The icon of the menu item, if this method was not overwritten it
	 *         will return the empty icon or null for Win Vista / 7 to prevent
	 *         the "checkbox bug"
	 */
	public ImageIcon getIcon() {
		if (AppD.WINDOWS_VISTA_OR_LATER) {
			return null;
		} else {
			return app.getEmptyIcon();
		}
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
	 * create the focus panel (composed of titleLabel, and, for
	 * EuclidianDockPanels, focus icon)
	 * 
	 * @return the focus panel
	 */
	protected JComponent createFocusPanel() {
		titleLabel = new JLabel(app.getPlain(title));
		titleLabel.setFont(app.getPlainFont());
		titleLabel.setForeground(Color.darkGray);

		JPanel p = new JPanel(new FlowLayout(app.flowLeft(), 2, 1));

		if (app.isRightToLeftReadingOrder()) {
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
	 * @param dockManager
	 */
	public void register(DockManager dockManager) {
		this.dockManager = dockManager;
		this.app = dockManager.getLayout().getApplication();

		// create buttons for the panels
		createButtons();

		// create button panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(app.flowRight(), 0, 1));
		if (app.isRightToLeftReadingOrder()) {
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
		Border panelBorder = BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow),
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
		styleBarPanel.add(styleBarButtonPanel, app.borderWest());
		styleBarPanel.add(LayoutUtil.flowPanelRight(0, 0, 4, unwindowButton2),
				app.borderEast());

		// construct the title panel and add all elements
		titlePanel = new JPanel();
		titlePanel.setBorder(panelBorder);
		titlePanel.setLayout(new BorderLayout());

		titlePanel.add(createFocusPanel(), app.borderWest());
		titlePanel.add(buttonPanel, app.borderEast());
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
		if (hasToolbar())
			metaPanel.add(toolbarPanel, BorderLayout.CENTER);

		// make titlebar visible if necessary
		updatePanel();

		add(metaPanel, BorderLayout.NORTH);
	}

	private void createButtons() {

		// button to show/hide styling bar and the title panel buttons
		toggleStyleBarButton = new JButton();
		toggleStyleBarButton.addActionListener(this);
		toggleStyleBarButton.setFocusPainted(false);
		toggleStyleBarButton.setBorderPainted(false);
		toggleStyleBarButton.setContentAreaFilled(false);
		toggleStyleBarButton.setPreferredSize(new Dimension(12, 12));
		toggleStyleBarButton.setRolloverEnabled(true);

		// button to show/hide styling bar if the title panel is invisible
		toggleStyleBarButton2 = new JButton();
		toggleStyleBarButton2.setFocusPainted(false);
		toggleStyleBarButton2.setBorderPainted(false);
		toggleStyleBarButton2.setContentAreaFilled(false);
		toggleStyleBarButton2.setPreferredSize(new Dimension(12, 12));
		toggleStyleBarButton2.addActionListener(this);
		toggleStyleBarButton2.setRolloverEnabled(true);

		updateToggleStyleBarButtons();

		// button to insert the view in the main window
		unwindowButton = new JButton(app.getImageIcon("view-unwindow.png"));
		unwindowButton.addActionListener(this);
		unwindowButton.setFocusPainted(false);
		unwindowButton.setContentAreaFilled(false);
		unwindowButton.setBorderPainted(false);
		unwindowButton.setPreferredSize(new Dimension(16, 16));

		// button to insert the view in the main window
		unwindowButton2 = new JButton(app.getImageIcon("view-unwindow.png"));
		unwindowButton2.addActionListener(this);
		unwindowButton2.setFocusPainted(false);
		unwindowButton2.setContentAreaFilled(false);
		unwindowButton2.setBorderPainted(false);
		unwindowButton2.setPreferredSize(new Dimension(16, 16));

		// button to display the view in a separate window
		windowButton = new JButton(app.getImageIcon("view-window.png"));
		windowButton.addActionListener(this);
		windowButton.setFocusPainted(false);
		windowButton.setContentAreaFilled(false);
		windowButton.setBorderPainted(false);
		windowButton.setPreferredSize(new Dimension(16, 16));

		// button to close the view
		closeButton = new JButton(app.getImageIcon("view-close.png"));
		closeButton.addActionListener(this);
		closeButton.setFocusPainted(false);
		closeButton.setPreferredSize(new Dimension(16, 16));

		// button to toggle maximize/normal state
		maximizeButton = new JButton(app.getImageIcon("view-maximize.png"));
		maximizeButton.addActionListener(this);
		maximizeButton.setFocusPainted(false);
		maximizeButton.setPreferredSize(new Dimension(16, 16));

	}

	/**
	 * 
	 * @return title in plain style
	 */
	protected String getPlainTitle() {
		return app.getPlain(title);
	}

	/**
	 * Create a frame for this DockPanel. The frame will either be a JFrame or a
	 * JDialog depending on the isDialog flag.
	 */
	public void createFrame() {

		if (isDialog) {
			frame = new JDialog(app.getFrame(), false) {
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
			((JFrame) frame).setIconImage(app
					.getInternalImage("geogebra64.png"));
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
		Rectangle screenSize = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getMaximumWindowBounds();

		// Use the previous dimension of this view
		Rectangle windowBounds = getFrameBounds();

		// resize window if necessary
		if (windowBounds.width > screenSize.width)
			windowBounds.width = screenSize.width - 50;
		if (windowBounds.height > screenSize.height)
			windowBounds.height = windowBounds.height - 50;

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
			maximizeButton.setIcon(app.getImageIcon("view-unmaximize.png"));
		} else {
			maximizeButton.setIcon(app.getImageIcon("view-maximize.png"));
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
		if (isVisible() && isOpenInFrame() && hasToolbar()) {
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
		closeButton.setToolTipText(app.getMenuTooltip("Close"));
		windowButton.setToolTipText(app.getPlainTooltip("ViewOpenExtraWindow"));
		unwindowButton.setToolTipText(app
				.getPlainTooltip("ViewCloseExtraWindow"));
		unwindowButton2.setToolTipText(app
				.getPlainTooltip("ViewCloseExtraWindow"));
		toggleStyleBarButton.setToolTipText(app
				.getPlainTooltip("ToggleStyleBar"));
		toggleStyleBarButton2.setToolTipText(app
				.getPlainTooltip("ToggleStyleBar"));

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
	public void closePanel() {
		closePanel(true);
	}

	/**
	 * Close this panel.
	 * 
	 * @param isPermanent
	 */
	protected void closePanel(boolean isPermanent) {
		dockManager.closePanel(this, isPermanent);
	}

	/**
	 * Display this panel in an external window.
	 */
	protected void windowPanel() {

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
	protected void unwindowPanel() {
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
	private void setStyleBar() {
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

		if (!isVisible())
			return;

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
	public void windowClosing(WindowEvent e) {
		closePanel(false);
	}

	/**
	 * Start dragging if the mouse was pressed while it was on the title panel.
	 * Or toggle the stylebar on double-click.
	 */
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
		if (isOpenInFrame())
			return null;

		Container parent = getParent();

		if (parent == null || !(parent instanceof DockSplitPane))
			return null;
		else
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

		return def.reverse().toString();
	}

	/**
	 * @return The XML container which stores all relevant information for this
	 *         panel.
	 */
	public DockPanelData createInfo() {
		return new DockPanelData(id, toolbarString, visible, openInFrame,
				showStyleBar, new geogebra.awt.GRectangleD(frameBounds),
				embeddedDef, embeddedSize);
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
				toggleStyleBarButton.setIcon(app
						.getImageIcon("triangle-down.png"));
				// toggleStyleBarButton.setRolloverIcon(app.getImageIcon("triangle-down-rollover.png"));
			} else {
				toggleStyleBarButton.setIcon(app
						.getImageIcon("triangle-right.png"));
				// toggleStyleBarButton.setRolloverIcon(app.getImageIcon("triangle-right-rollover.png"));
			}
		}
		if (toggleStyleBarButton2 != null) {
			toggleStyleBarButton2.setIcon(toggleStyleBarButton.getIcon());
			// toggleStyleBarButton2.setRolloverIcon(toggleStyleBarButton.getRolloverIcon());
		}
	}

	/**
	 * @return If the style bar should be visible.
	 */
	private boolean isStyleBarVisible() {
		if (id == App.VIEW_EUCLIDIAN || id == App.VIEW_EUCLIDIAN2 || id == App.VIEW_ALGEBRA) {
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
		this.visible = visible;
	}

	@Override
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
			((GuiManagerD) app.getGuiManager()).updatePropertiesView();
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
				frame.requestFocus();
			} else {
				if (!app.isApplet()) {
					JFrame frame = app.getFrame();

					if (frame != null) {
						frame.toFront();
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
		if (hasToolbar()) {
			((GuiManagerD) app.getGuiManager()).getToolbarPanel()
					.setActiveToolbar(toolbar);
		} else {
			((GuiManagerD) app.getGuiManager()).getToolbarPanel()
					.setActiveToolbar(-1);
		}
		// switching the view may cause shrinking of help panel,
		// we need an update here
		((GuiManagerD) app.getGuiManager()).getToolbarPanel().validate();
		((GuiManagerD) app.getGuiManager()).getToolbarPanel().updateHelpText();
	}

	/**
	 * sets the title label when this has not the focus
	 */
	protected void setTitleLabelFocus() {
		if (dockManager.hasFullFocusSystem()) {
			if (titleIsBold())
				titleLabel.setFont(app.getBoldFont());
			else
				titleLabel.setFont(app.getPlainFont());
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
	 * Helper class to compare dock panels for sorting in the menu.
	 * 
	 * @author Florian Sonner
	 */
	public static class MenuOrderComparator implements Comparator<DockPanel> {
		public int compare(DockPanel a, DockPanel b) {
			return a.getMenuOrder() - b.getMenuOrder();
		}
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * UI for the buttons in the title panel. Used for Mac as the normal buttons
	 * are not displayed correctly as they are too small.
	 * 
	 * @author Florian Sonner
	 */
	private static class TitleBarButtonUI extends BasicButtonUI {
		@Override
		public void paint(Graphics g, JComponent component) {
			JButton button = (JButton) component;

			// TODO implement drawing...

			super.paint(g, component);
		}
	}

	public class MyButtonHider extends MouseAdapter {

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

	public boolean isEuclidianDockPanel3D() {
		return false;
	}

}
