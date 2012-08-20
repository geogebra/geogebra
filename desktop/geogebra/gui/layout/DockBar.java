package geogebra.gui.layout;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.gui.view.properties.PropertiesView.OptionType;
import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.gui.util.HelpAction;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * Toolbar to hold launching buttons for minimized views.
 * 
 * @author G. Sturr
 * 
 */
public class DockBar extends JPanel implements SetLabels, ActionListener {

	private static final long serialVersionUID = 1L;

	AppD app;
	private LayoutD layout;

	private JPopupMenu popup;

	private JPanel fullPanel;

	protected JPanel minimumPanel;
	private ViewButtonBar viewButtonBar;

	protected boolean enablePopup = false;

	private AbstractAction showKeyboardAction;

	/**
	 * flag to determine if dockbar is in a minimized state
	 */
	protected boolean isMinimized = true;

	private DockButton btnFileOpen, btnFileSave, btnPrint, btnKeyboard,
			btnRefresh, btnLayout;

	private DockButton btnProperties;

	/**
	 * Constructs a DockBar
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

	private void initGUI() {

		setLayout(new BorderLayout());

		viewButtonBar = new ViewButtonBar(app);
		viewButtonBar.setOrientation(JToolBar.VERTICAL);

		JLabel lblArrow = new JLabel(app.getImageIcon("dockbar-triangle-right.png"));
		// wrap viewButtonBar to be vertically centered
		JPanel gluePanel = new JPanel();
		gluePanel.setLayout(new BoxLayout(gluePanel, BoxLayout.Y_AXIS));
		gluePanel.add(Box.createVerticalGlue());
		//gluePanel.add(viewButtonBar);
		
		gluePanel.add(new PerspectivePanel(app));
		// gluePanel.add(Box.createVerticalStrut(30));
		getGridButtonPanel();
		//gluePanel.add(getGridButtonPanel());
		gluePanel.add(Box.createVerticalGlue());
		gluePanel.setBackground(SystemColor.control);

		fullPanel = new JPanel(new BorderLayout());
		fullPanel.add(Box.createVerticalStrut(20), BorderLayout.NORTH);
		fullPanel.add(gluePanel, BorderLayout.CENTER);
		//fullPanel.add(lblArrow, BorderLayout.EAST);
		fullPanel.setBackground(SystemColor.control);
		fullPanel.setOpaque(true);

		getMinimumPanel();

		
		setLabels();
		//updateLayout();
		registerListeners();

		isMinimized = false;
		toggleMinimumFullPanel();
	}

	private void registerListeners() {

		if (enablePopup) {
			RollOverPopupListener ml = new RollOverPopupListener();
			viewButtonBar.addMouseListener(ml);
			popup.addMouseListener(ml);

			addListenerToAllComponents(this, ml);

		} else {
			ToggleListener l = new ToggleListener();
			minimumPanel.addMouseListener(l);
			fullPanel.addMouseListener(l);
		}
	}

	private JToolBar getGridButtonPanel() {

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
		btnHelp.setToolTipText(app.getMenuTooltip("Help"));

		// TODO: better help action ?
		btnHelp.addActionListener(new HelpAction(app, app
				.getImageIcon("help.png"), app.getMenu("Help"),
				App.WIKI_MANUAL));

		//
		// ====== Button Panel ===================================

		JToolBar buttonPanel = new JToolBar();
		buttonPanel.setFloatable(false);
		buttonPanel.setOrientation(JToolBar.VERTICAL);
		buttonPanel.setOpaque(true);
		buttonPanel.setBackground(SystemColor.control);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder());

		// buttonPanel.add(btnKeyboard);
		buttonPanel.add(Box.createVerticalStrut(30));
		buttonPanel.add(btnProperties);

		return buttonPanel;
	}

	Border normalBorder = BorderFactory.createMatteBorder(1, 1, 0, 1,
			SystemColor.controlShadow);
	JLabel lblIcon;

	/**
	 * Returns minimumPanel, a slim vertical bar that acts a button to open the
	 * dockbar. When clicked it restores the dockBar to full size.
	 * 
	 */
	private JPanel getMinimumPanel() {

		if (minimumPanel == null) {
			minimumPanel = new JPanel(new BorderLayout(0, 0));

			lblIcon = new JLabel(app.getImageIcon("dockbar-triangle-left.png"));
			lblIcon.setPreferredSize(new Dimension(10, 0));
			minimumPanel.add(lblIcon, BorderLayout.CENTER);

			minimumPanel.setBackground(null);
			minimumPanel.setBorder(normalBorder);
		}

		return minimumPanel;
	}

	/**
	 * Updates the layout. If minimized, minimumPanel is shown Otherwise, either
	 * the popup or the full panel is shown
	 */
	protected void updateLayout() {

		if (true || enablePopup) {
			popup.removeAll();
			popup.add(fullPanel);
			fullPanel.setBorder(BorderFactory.createEmptyBorder());
		} else {
			Border highlightBorder = BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(1, 1, 0, 0,
							SystemColor.controlShadow), BorderFactory
							.createMatteBorder(0, 1, 0, 0,
									SystemColor.controlLtHighlight));
			fullPanel.setBorder(BorderFactory.createCompoundBorder(
					highlightBorder,
					BorderFactory.createEmptyBorder(0, 0, 0, 1)));
		}
	}

	/**
	 * Toggles the dockbar layout between a minimized panel that acts as a
	 * button and the full panel with all buttons visible
	 */
	protected void toggleMinimumFullPanel() {

		isMinimized = !isMinimized;
		removeAll();

		if (true || isMinimized) {
			add(minimumPanel, BorderLayout.CENTER);
			lblIcon.setIcon(app.getImageIcon("dockbar-triangle-left.png"));
		} else {
			add(fullPanel, BorderLayout.CENTER);
			//add(minimumPanel, BorderLayout.EAST);
			lblIcon.setIcon(app.getImageIcon("dockbar-triangle-right.png"));
		}
		this.revalidate();
		this.repaint();
	}

	public void showPopup() {
	//	if (!popup.isVisible()) {
			popup = new PerspectivePanel(app);
		//	popup.setPopupSize(popup.getPreferredSize().width,
			//		getMinimumPanel().getHeight() - 4);
						
			popup.show(this, -popup.getPreferredSize().width, (app.getFrame().getHeight() - popup.getPreferredSize().height)/2);
	//	}app.
	}

	protected void hidePopup() {
		if (popup.isVisible()) {
			popup.setVisible(false);
		}
	}

	public void openDockBar() {
		isMinimized = false;
		//updateLayout();
	}

	public void update() {
		updateViewButtons();
		btnKeyboard.setSelected(AppD.isVirtualKeyboardActive());
		btnProperties.removeActionListener(this);
		btnProperties.setSelected(app.getGuiManager().showView(
				App.VIEW_PROPERTIES));
		btnProperties.addActionListener(this);
	}

	public void updateViewButtons() {
		viewButtonBar.updateViewButtons();
	}

	/**
	 * Mouse listener to handle showing the popup.
	 */
	private class RollOverPopupListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!(e.getSource() instanceof AbstractButton)) {
				hidePopup();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			showPopup();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// Application.printStacktrace("exit"
			// +e.getSource().getClass().getName());

			Component source = (Component) e.getSource();
			Point p = e.getPoint();
			SwingUtilities.convertPointToScreen(p, source);
			Rectangle r = new Rectangle(popup.getLocationOnScreen());
			r.width = popup.getWidth();
			r.height = popup.getHeight();

			if (!r.contains(p) && e.getPoint().x < app.getFrame().getWidth()) {
				hidePopup();
				// System.out.println(mainPanel.getBounds().contains(e.getPoint()));
			}

			// lblIcon.setIcon(app.getImageIcon("dockbar-triangle.png"));
			// minimumPanel.setBackground(null);
			// minimumPanel.setBorder(normalBorder);
		}
	}

	public static void addListenerToAllComponents(JComponent c, MouseAdapter l) {

		c.addMouseListener(l);

		for (Component cc : c.getComponents())
			if (cc instanceof JComponent) {
				addListenerToAllComponents((JComponent) cc, l);
			}
	}

	/**
	 * Mouse listener to handle toggling between minimum and full panels.
	 */
	public class ToggleListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 0) {
				showPopup();
				if (!enablePopup) {
					//toggleMinimumFullPanel();
					//minimumPanel.setBackground(null);
				}
				
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {

			if (enablePopup) {
				showPopup();
			}
			if (e.getSource() == minimumPanel) {
				if (isMinimized) {
					lblIcon.setIcon(app
							.getImageIcon("dockbar-triangle-left-rollover.png"));
				} else {
					lblIcon.setIcon(app
							.getImageIcon("dockbar-triangle-right-rollover.png"));
				}
				minimumPanel.setBackground(Color.LIGHT_GRAY);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (e.getSource() == minimumPanel) {
				if (isMinimized) {
					lblIcon.setIcon(app
							.getImageIcon("dockbar-triangle-left.png"));
				} else {
					lblIcon.setIcon(app
							.getImageIcon("dockbar-triangle-right.png"));
				}
				minimumPanel.setBackground(null);
			}
		}
	}

	public void setLabels() {

		btnProperties.setToolTipText(app.getMenu("Layout"));
		btnKeyboard.setToolTipText(app.getPlain("Keyboard"));

		btnPrint.setToolTipText(app.getMenu("Print"));
		btnFileSave.setToolTipText(app.getMenu("Save"));
		btnFileOpen.setToolTipText(app.getMenu("Load"));

		updateViewButtons();
		
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
						&& !((GuiManagerD) app.getGuiManager()).showVirtualKeyboard()) {

					// if keyboard is active but hidden, just show it
					((GuiManagerD) app.getGuiManager()).toggleKeyboard(true);
					update();

				} else {

					AppD.setVirtualKeyboardActive(!AppD
							.isVirtualKeyboardActive());
					((GuiManagerD) app.getGuiManager()).toggleKeyboard(
							AppD.isVirtualKeyboardActive());
					update();
				}

			}
		};

	}

	// ============================================
	// Full screen button
	// (experimental code)
	// ===========================================
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

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnProperties) {
			((PropertiesView) app.getGuiManager().getPropertiesView())
					.setOptionPanel(OptionType.LAYOUT);
			int viewId = App.VIEW_PROPERTIES;
			app.getGuiManager().setShowView(
					!app.getGuiManager().showView(viewId), viewId, false);
		}
	}

}
