package geogebra.gui.layout;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.main.App;
import geogebra.common.main.OptionType;
import geogebra.gui.GuiManagerD;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.gui.util.HelpAction;
import geogebra.main.AppD;
import geogebra.main.DockBarInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 * Toolbar to hold launching buttons for minimized views.
 * 
 * @author G. Sturr
 * 
 */
public class DockBar extends JPanel implements SetLabels, ActionListener,
		DockBarInterface {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private LayoutD layout;

	private PerspectivePanel popup;
	private JPanel buttonPanel, slimSidebarPanel;
	private ViewButtonBar viewButtonBar;
	private JLabel lblIcon, lblIconRight;

	private boolean isEastOrientation = true;
	private boolean showButtonBar = false;

	private DockButton btnFileOpen, btnFileSave, btnPrint, btnKeyboard,
			btnPerspectives, btnProperties;

	private AbstractAction showKeyboardAction;

	/***************************************************
	 * Constructor
	 * 
	 * @param app
	 */
	public DockBar(AppD app) {

		this.app = app;
		this.layout = (LayoutD) app.getGuiManager().getLayout();
		setBorder(BorderFactory.createEmptyBorder());
		initActions();
		initGUI();

	}

	// ==============================
	// GUI
	// ==============================

	private void initGUI() {

		JLabel lblArrow = new JLabel(
				app.getImageIcon("dockbar-triangle-right.png"));

		buildButtonPanel();
		buildSlimSidebarPanel();
		slimSidebarPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0,
				SystemColor.controlShadow));
		buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1,
				SystemColor.controlShadow));

		registerListeners();
		setLayout(new BorderLayout());
		updateLayout();
	}

	private void registerListeners() {
		SidebarMouseListener l = new SidebarMouseListener();
		slimSidebarPanel.addMouseListener(l);
	}

	private void buildButtonPanel() {

		viewButtonBar = new ViewButtonBar(app);
		viewButtonBar.setOrientation(JToolBar.VERTICAL);

		JPanel northButtonPanel = new JPanel();
		northButtonPanel.setLayout(new BoxLayout(northButtonPanel,
				BoxLayout.Y_AXIS));
		northButtonPanel.add(Box.createVerticalStrut(50));
		northButtonPanel.add(buildExtraButtonToolBar());
		northButtonPanel.add(Box.createVerticalStrut(50));
		northButtonPanel.setBackground(SystemColor.control);

		buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(northButtonPanel, BorderLayout.NORTH);
		buttonPanel.add(viewButtonBar, BorderLayout.CENTER);
		buttonPanel.setBackground(SystemColor.control);
		buttonPanel.setOpaque(true);

	}

	/**
	 * 
	 * @return a new perspective panel
	 */
	PerspectivePanel newPerspectivePanel() {
		return new PerspectivePanel(app, this);
	}

	private JToolBar buildExtraButtonToolBar() {

		// perspectives button
		btnPerspectives = new DockButton(app,
				app.getImageIcon("options-layout24.png"));
		btnPerspectives.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				popup = newPerspectivePanel();
				popup.show(btnPerspectives, -popup.getPreferredSize().width, 0);
				btnPerspectives.setSelected(false);
			}
		});

		// properties button
		btnProperties = new DockButton(app,
				app.getImageIcon("view-properties22.png"));
		btnProperties.addActionListener(this);

		// keyboard button
		btnKeyboard = new DockButton(app, app.getImageIcon("keyboard.png"));
		btnKeyboard.addActionListener(showKeyboardAction);

		// file open button
		btnFileOpen = new DockButton(app,
				app.getImageIcon("document-open22.png"));
		btnFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				app.getGuiManager().openFile();
			}
		});

		// save button
		btnFileSave = new DockButton(app,
				app.getImageIcon("document-save22.png"));
		btnFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				app.getGuiManager().save();
			}
		});

		// print button
		btnPrint = new DockButton(app, app.getImageIcon("document-print22.png"));
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GeoGebraMenuBar.showPrintPreview(app);
			}
		});

		// help button
		DockButton btnHelp = new DockButton(app, app.getImageIcon("help22.png"));
		btnHelp.setFocusPainted(false);
		btnHelp.setBorderPainted(false);
		btnHelp.setContentAreaFilled(false);
		btnHelp.setToolTipText(app.getLocalization().getMenuTooltip("Help"));

		// TODO: better help action ?
		btnHelp.addActionListener(new HelpAction(app, app
				.getImageIcon("help.png"), app.getMenu("Help"), App.WIKI_MANUAL));

		JToolBar extraButtonPanel = new JToolBar();
		extraButtonPanel.setFloatable(false);
		extraButtonPanel.setOrientation(JToolBar.VERTICAL);
		extraButtonPanel.setOpaque(true);
		extraButtonPanel.setBackground(SystemColor.control);
		extraButtonPanel.setBorder(BorderFactory.createEmptyBorder());

		// buttonPanel.add(btnKeyboard);
		extraButtonPanel.add(Box.createVerticalStrut(30));
		// buttonPanel.add(btnPrint);
		// buttonPanel.add(btnFileSave);
		// buttonPanel.add(btnFileOpen);
		// buttonPanel.add(btnProperties);

		extraButtonPanel.add(btnPerspectives);

		return extraButtonPanel;
	}

	/**
	 * Creates sidebarButtonPanel, a slim vertical bar that acts a button to
	 * open the perspective popup.
	 * 
	 */
	private JPanel buildSlimSidebarPanel() {

		if (slimSidebarPanel == null) {
			slimSidebarPanel = new JPanel(new BorderLayout(0, 0));

			lblIcon = new JLabel(app.getImageIcon("dockbar-triangle-left.png"));
			lblIcon.setPreferredSize(new Dimension(10, 0));

			slimSidebarPanel.add(lblIcon, BorderLayout.CENTER);

			slimSidebarPanel.setBackground(null);

		}

		return slimSidebarPanel;
	}

	/**
	 * set sidebar triangle orientation
	 * 
	 * @param popupIsVisible
	 *            right if true, left if false
	 */
	public void setSidebarTriangle(boolean popupIsVisible) {

		if (popupIsVisible ^ !isEastOrientation()) {
			lblIcon.setIcon(app.getImageIcon("dockbar-triangle-right.png"));
		} else {
			lblIcon.setIcon(app.getImageIcon("dockbar-triangle-left.png"));
		}

		slimSidebarPanel.repaint();

	}

	// ==============================
	// Event Handlers
	// ==============================

	/**
	 * Updates the layout to display either (1) a minimized panel that acts as a
	 * button (slimSidebarPanel) or (2) a panel with buttons to hide/show views
	 * (buttonPanel)
	 */
	protected void updateLayout() {

		removeAll();

		if (showButtonBar) {
			add(buttonPanel, BorderLayout.CENTER);
		} else {
			add(slimSidebarPanel, BorderLayout.CENTER);
		}
		revalidate();
	}

	public void showPopup() {

		popup = newPerspectivePanel();
		int horizontal;
		if (isEastOrientation())
			horizontal = -popup.getPreferredSize().width;
		else
			horizontal = slimSidebarPanel.getPreferredSize().width;

		int y = (slimSidebarPanel.getHeight() - popup.getPreferredSize().height) / 2;

		popup.show(this, horizontal, y);

		if (!popup.isVisible()) // setVisible() may have not worked if sidebar
								// has mouse
			popup.superSetVisible(true);
	}

	public void hidePopup() {
		if (popup != null && popup.isVisible()) {
			popup.superSetVisible(false);
		}
	}

	void togglePopup() {
		if (popup == null || !popup.isVisible())
			showPopup();
		else
			popup.superSetVisible(false);

	}

	public void update() {
		updateViewButtons();
		// btnKeyboard.setSelected(AppD.isVirtualKeyboardActive());
		// btnProperties.removeActionListener(this);
		// btnProperties.setSelected(app.getGuiManager().showView(
		// App.VIEW_PROPERTIES));
		// btnProperties.addActionListener(this);
	}

	public void updateViewButtons() {
		viewButtonBar.updateViewButtons();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnProperties) {
			((PropertiesView) app.getGuiManager().getPropertiesView())
					.setOptionPanel(OptionType.LAYOUT);
			int viewId = App.VIEW_PROPERTIES;
			app.getGuiManager().setShowView(
					!app.getGuiManager().showView(viewId), viewId, false);
		}
	}

	public void setLabels() {

		// btnProperties.setToolTipText(app.getMenu("Layout"));
		// btnKeyboard.setToolTipText(app.getPlain("Keyboard"));

		// btnPrint.setToolTipText(app.getMenu("Print"));
		// btnFileSave.setToolTipText(app.getMenu("Save"));
		// btnFileOpen.setToolTipText(app.getMenu("Load"));

		updateViewButtons();

	}

	private boolean sideBarHasMouse = false;

	void setSideBarHasMouse(boolean flag) {
		sideBarHasMouse = flag;
	}

	/**
	 * 
	 * @return true if side bar has mouse
	 */
	public boolean sideBarHasMouse() {
		return sideBarHasMouse;
	}

	// ==============================
	// Listeners
	// ==============================

	/**
	 * Mouse listener to handle mouse events for the sidebar.
	 */
	public class SidebarMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 0) {
				togglePopup();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (e.getSource() == slimSidebarPanel) {
				slimSidebarPanel.setBackground(Color.LIGHT_GRAY);
				setSideBarHasMouse(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (e.getSource() == slimSidebarPanel) {
				slimSidebarPanel.setBackground(null);
				setSideBarHasMouse(false);
			}
		}

	}

	// ==============================
	// Getters/Setters
	// ==============================

	public boolean isShowButtonBar() {
		return showButtonBar;
	}

	public void setShowButtonBar(boolean showButtonBar) {
		this.showButtonBar = showButtonBar;
		updateLayout();
	}

	public boolean isEastOrientation() {
		return isEastOrientation;
	}

	public void setEastOrientation(boolean isEastOrientation) {
		this.isEastOrientation = isEastOrientation;
		setSidebarTriangle(popup != null && popup.isVisible());
	}

	/***********************************************
	 * Popup class
	 */
	class MyPopup extends JPopupMenu {

		private static final long serialVersionUID = 1L;

		public MyPopup() {
			super();
			setOpaque(true);
			setBackground(SystemColor.control);
			setFocusable(false);
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 8));

		}

		@Override
		public void setVisible(boolean isVisible) {

			super.setVisible(isVisible);
		}

	}

	/**
	 * Initialize the actions.
	 */
	private void initActions() {
		showKeyboardAction = new AbstractAction(app.getPlain("Keyboard")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				if (AppD.isVirtualKeyboardActive()
						&& !((GuiManagerD) app.getGuiManager())
								.showVirtualKeyboard()) {

					// if keyboard is active but hidden, just show it
					((GuiManagerD) app.getGuiManager()).toggleKeyboard(true);
					update();

				} else {

					AppD.setVirtualKeyboardActive(!AppD
							.isVirtualKeyboardActive());
					((GuiManagerD) app.getGuiManager()).toggleKeyboard(AppD
							.isVirtualKeyboardActive());
					update();
				}

			}
		};

	}

	// ==============================================================
	// Full screen button
	// (experimental code)
	// ==============================================================
	private boolean fullScreen = false;

	private void toggleFullScreen() {

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		// GraphicsDevice[] gs = ge.getScreenDevices();
		// Determine if full-screen mode is supported directly
		if (gs.isFullScreenSupported()) {
			App.info("full screen mode supported");
		} else {
			App.info("full screen mode not supported");
		}

		fullScreen = !fullScreen;
		JFrame f = app.getFrame();
		try {
			if (fullScreen) { // Enter full-screen mode

				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Dimension dim = toolkit.getScreenSize();
				f.setResizable(true);

				f.removeNotify();
				f.setUndecorated(true);
				f.addNotify();

				gs.setFullScreenWindow(f);

				f.setLocation(0, 0);
				f.setSize(dim);
				f.validate();

			} else { // Return to normal windowed mode

				gs.setFullScreenWindow(null);

				f.removeNotify();
				f.setUndecorated(false);
				f.addNotify();
				f.validate();

			}
		}

		catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}

		finally {
			// Exit full-screen mode
			// AbstractApplication.info("finally");
			// gs.setFullScreenWindow(null);
		}

	}

}
