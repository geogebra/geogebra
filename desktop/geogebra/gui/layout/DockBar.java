package geogebra.gui.layout;

import geogebra.common.kernel.algos.AlgoTurtle;
import geogebra.common.kernel.geos.GeoTurtle;
import geogebra.common.main.AbstractApplication;
import geogebra.gui.dialog.TurtleDriverPanel;
import geogebra.gui.dialog.options.OptionsDialog;
import geogebra.gui.menubar.OptionsMenu;
import geogebra.gui.menubar.ViewMenu;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * Toolbar to hold launching buttons for minimized views.
 * 
 * @author G. Sturr
 * 
 */
public class DockBar extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Application app;
	private Layout layout;

	private ConfigurationPanel configPanel;

	private JPanel mainPanel, minimumPanel;

	private ViewButtonBar viewButtonBar;
	
	private JButton btnConfigure, btnGeoGebra, btnView, btnOptions;

	private JButton btnTurtle;

	/**
	 * flag to determine if the dockbar is in a minimized state
	 */
	protected boolean isMinimized = true;

	/*******************************************************
	 * Constructs a DockBar
	 * 
	 * @param app
	 */
	public DockBar(Application app) {

		this.app = app;
		this.layout = app.getGuiManager().getLayout();

		initGUI();

		setBorder(BorderFactory.createEmptyBorder());
		setLayout(new BorderLayout());
		updateLayout();

		addMouseListener(new MyMouseListener());
	}

	/**
	 * Mouse listener to handle minimizing the dock.
	 */
	class MyMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 0) {
				isMinimized = true;
				updateLayout();
			}
		}
	}

	/**
	 * Updates the layout to either show the dockbar and its components or show
	 * the minimized panel.
	 */
	protected void updateLayout() {
		this.removeAll();
		if (isMinimized)
			this.add(getMinimizedPanel(), BorderLayout.CENTER);
		else
			this.add(mainPanel, BorderLayout.CENTER);

		this.revalidate();
		this.repaint();
	}

	public void update() {
		//app.updateToolBar();

		// btnSettings.setSelected(!app.isMainPanelShowing());

		// btnView.setVisible(app.isMainPanelShowing());
		// btnOptions.setVisible(app.isMainPanelShowing());

	}

	private void initGUI() {

		initButtons();

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(Box.createVerticalStrut(15));
	//	mainPanel.add(btnConfigure);
		//mainPanel.add(btnOptions);
		mainPanel.add(Box.createVerticalStrut(35));
		//mainPanel.add(btnView);

		// view toggle buttons
		
		viewButtonBar = new ViewButtonBar(app);
		viewButtonBar.setOrientation(JToolBar.VERTICAL);
		viewButtonBar.setPreferredSize(new Dimension(32,32));
		mainPanel.add(viewButtonBar);

		Border outsideBorder = BorderFactory.createMatteBorder(1, 0, 0, 1,
				SystemColor.controlShadow);
		Border insideBorder = BorderFactory.createMatteBorder(0, 0, 0, 1,
				SystemColor.controlLtHighlight);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(outsideBorder,
				insideBorder));

	}

	private void initButtons() {

		btnGeoGebra = new JButton();
		btnGeoGebra.setIcon(app.getImageIcon("geogebra32.png"));
		btnGeoGebra.addActionListener(this);
		btnGeoGebra.setFocusPainted(false);

		btnConfigure = new JButton();
		btnConfigure.setIcon(app.getImageIcon("configure-32.png"));
		// btnSettings.setSelectedIcon(app.getImageIcon("go-previous24.png"));
		btnConfigure.addActionListener(this);
		btnConfigure.setFocusPainted(false);
		btnConfigure.setBorderPainted(false);
		btnConfigure.setContentAreaFilled(false);
		btnConfigure.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnView = new JButton();
		btnView.setIcon(app.getImageIcon("view_btn.png"));
		btnView.addActionListener(this);
		btnView.setFocusPainted(false);
		// btnView.setBorderPainted(false);
		// btnView.setContentAreaFilled(false);

		btnOptions = new JButton();
		btnOptions.setIcon(app.getImageIcon("options_btn.png"));
		btnOptions.addActionListener(this);
		btnOptions.setFocusPainted(false);
		// btnOptions.setBorderPainted(false);
		// btnOptions.setContentAreaFilled(false);

		// btnOptions.addMouseListener(this);
		// btnOptions.setComponentPopupMenu(optionsPopupMenu);

		btnTurtle = new JButton("T");
		btnTurtle.addActionListener(this);

	}

	

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnConfigure) {

			if (configPanel == null) {
				configPanel = new ConfigurationPanel(app, this);
				app.setBackPanel(configPanel);
			}
			// app.setBackPanel(configPanel);
			app.showBackPanel();

		}

		if (source == btnOptions) {
			showOptionsPopup(btnOptions);
		}

		if (source == btnView) {
			showViewPopup(btnView);
		}

		if (source == btnGeoGebra) {
			app.showMainPanel();
		}

		if (source == btnTurtle) {
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK)
				createTurtle(1);
			else
				createTurtle(2);
		}

		update();

	}

	public void openDockBar() {
		isMinimized = false;
		updateLayout();
	}

	/**
	 * Returns minimumPanel, a slim vertical bar displayed when the dockBar is
	 * minimized. When clicked it restores the dockBar to full size.
	 * 
	 */
	private JPanel getMinimizedPanel() {
		final Border border = BorderFactory.createEtchedBorder();
		final Border hoverBorder = BorderFactory
				.createBevelBorder(BevelBorder.RAISED);

		if (minimumPanel == null) {
			minimumPanel = new JPanel();
			minimumPanel.setMinimumSize(new Dimension(6, 0));
			minimumPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 0) {
						isMinimized = false;
						updateLayout();
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					minimumPanel.setBackground(Color.LIGHT_GRAY);
					// restorePanel.setBorder(hoverBorder);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					minimumPanel.setBackground(null);
					minimumPanel.setBorder(border);
				}
			});
		}
		minimumPanel.setBackground(null);
		minimumPanel.setBorder(border);
		return minimumPanel;
	}

	//
	//
	// =============================================================
	//
	// Turtle demo code
	//
	// =============================================================

	public class MyDialog extends JDialog {
		public MyDialog(TurtleDriverPanel turtlePanel) {
			super(app.getFrame(), "Turtle Driver", false);
			getContentPane().add(turtlePanel, "Center");
			setSize(300, 300);
			pack();
		}
	}

	private void createTurtle(int demo) {

		AlgoTurtle algo = new AlgoTurtle(app.getKernel().getConstruction(),
				null);
		GeoTurtle geo = algo.getTurtle();
		geo.setEuclidianVisible(true);

		TurtleDriverPanel turtlePanel = new TurtleDriverPanel(geo);

		MyDialog dlg = new MyDialog(turtlePanel);
		dlg.setVisible(true);

		/*
		 * geo.turn(45); geo.forward(2); geo.turn(20); geo.update();
		 * 
		 * demo = 3; if (demo == 1) { geo.setPenColor(geogebra.awt.Color.green);
		 * randomWalk(geo); geo.setPenColor(geogebra.awt.Color.blue);
		 * randomWalk(geo); geo.setPenColor(geogebra.awt.Color.yellow);
		 * randomWalk(geo);
		 * 
		 * }
		 * 
		 * if (demo == 2) { geo.setPenColor(geogebra.awt.Color.red);
		 * drawDragonCurves(geo); geo.update(); }
		 */

	}

	public static BufferedImage imageToBufferedImage(Image im) {
		BufferedImage bi = new BufferedImage(im.getWidth(null),
				im.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(im, 0, 0, null);
		bg.dispose();
		return bi;
	}

	private void randomWalk(GeoTurtle t) {
		t.setPenDown(false);
		t.setPosition(0, 0);
		t.setPenDown(true);
		for (int i = 0; i < 200; i++) {
			t.forward(Math.random());
			t.turn(360 * Math.random());
		}
		t.update();
	}

	private void dragon(GeoTurtle t, int n, double angle, double d) {
		if (n < 1) {
			t.forward(d);
			return;
		}
		dragon(t, n - 1, 90, d);
		t.turn(angle);
		dragon(t, n - 1, -90, d);
	}

	private void drawDragonCurves(GeoTurtle turtle) {
		// turtle.clear();
		int n = 10;
		double d = 0.5;
		dragon(turtle, n, 90, d);
		dragon(turtle, n, 90, d);
		dragon(turtle, n, 90, d);
		dragon(turtle, n, 90, d);
	}

	OptionsDialog optionsDialog;
	/**
	 * Object which provides an option dialog if requested. Used because
	 * different option dialogs are needed for GeoGebra 4 and 5.
	 */
	private OptionsDialog.Factory optionsDialogFactory;

	private JPanel optionsPanel;

	public void setOptionsPanel(int tabIndex) {

		if (optionsPanel == null) {
			initOptionsPanel();
		}

		optionsDialog.updateGUI();
		if (tabIndex > -1)
			optionsDialog.showTab(tabIndex);
		app.setBackPanel(optionsPanel);
		optionsDialog.getTabbedPane().repaint();
		optionsDialog.pack();
	}

	public void initOptionsPanel() {

		if (optionsPanel == null) {
			optionsPanel = new JPanel(new BorderLayout());

			JLabel title = new JLabel(app.getMenu("Settings"));
			title.setHorizontalAlignment(SwingConstants.CENTER);
			title.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0,
							SystemColor.controlDkShadow), BorderFactory
							.createEmptyBorder(10, 0, 5, 0)));
			optionsPanel.add(title, BorderLayout.NORTH);
		}

		if (optionsDialogFactory == null) {
			optionsDialogFactory = new OptionsDialog.Factory();
		}

		if (optionsDialog == null)
			optionsDialog = optionsDialogFactory.create(((Application) app));

		int n = optionsDialog.getTabbedPane().getTabCount();
		for (int i = 0; i < n; i++) {
			// optionsDialog.getTabbedPane().setIconAt(i, null);
		}

		// optionsDialog.getTabbedPane().setTabPlacement(JTabbedPane.TOP);

		optionsPanel.add(optionsDialog.getContentPane(), BorderLayout.CENTER);

		optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 40, 10));

		// optionsDialog.setVisible(tabIndex != -2);
	}

	PerspectivePanel perspectivePanel;

	public void setPerspectivePanel() {
		if (perspectivePanel == null) {
			perspectivePanel = new PerspectivePanel(app, this);
		}

		app.setBackPanel(perspectivePanel);

	}

	JPopupMenu viewPopupMenu;

	private void showViewPopup(JComponent comp) {
		if (viewPopupMenu == null) {
			ViewMenu viewMenu = new ViewMenu(app, layout);
			// viewPopupMenu = new ViewPopupMenu(app, app.getGuiManager()
			// .getLayout());
			viewPopupMenu = viewMenu.getPopupMenu();
		}
		viewPopupMenu.show(comp, comp.getWidth(), 0);
	}

	JPopupMenu optionsPopupMenu;

	private void showOptionsPopup(JComponent comp) {

		if (optionsPopupMenu == null) {
			OptionsMenu optionsMenu = new OptionsMenu(app);
			optionsPopupMenu = optionsMenu.getPopupMenu();
		}

		optionsPopupMenu.show(comp, comp.getWidth(), 0);
	}

	private class ViewPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		JMenuItem menuItem;

		public ViewPopupMenu(Application app, Layout layout) {
			this.setOpaque(true);
			setFont(app.getPlainFont());

			ViewMenu viewMenu = new ViewMenu(app, layout);
			add(viewMenu.getComponentPopupMenu());
			add(new OptionsMenu(app));
		}
	}

	public void updateViewButtons() {
		viewButtonBar.updateViewButtons();
		
	}

}
