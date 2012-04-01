package geogebra.gui.layout;


import geogebra.common.kernel.algos.AlgoTurtle;
import geogebra.common.kernel.geos.GeoTurtle;
import geogebra.common.main.AbstractApplication;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.gui.dialog.TurtleDriverPanel;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

	private static final int btnSize = 36;
	private static final int iconSize = btnSize - 10;

	private Application app;
	private Layout layout;

	private JToolBar viewToolBar, menuToolBar, inputToolBar;
	private JPanel mainPanel, minimumPanel;

	private ArrayList<ViewButton> viewButtons;
	private JButton btnToggleMenu, btnToggleInputBar, btnMinimize, btnKeyboard,
			btnTurtle;

	private boolean isMinimized = true;

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
	private void updateLayout() {
		this.removeAll();
		if (isMinimized)
			this.add(getMinimizedPanel(), BorderLayout.CENTER);
		else
			this.add(mainPanel, BorderLayout.CENTER);

		this.revalidate();
		this.repaint();
	}

	private void initGUI() {

		initToolBars();

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(menuToolBar, BorderLayout.NORTH);
		mainPanel.add(inputToolBar, BorderLayout.SOUTH);
		mainPanel.add(viewToolBar, BorderLayout.CENTER);
		Border outsideBorder = BorderFactory.createMatteBorder(1, 0, 0, 1,
				SystemColor.controlShadow);
		Border insideBorder = BorderFactory.createMatteBorder(0, 0, 0, 1,
				SystemColor.controlLtHighlight);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(outsideBorder,
				insideBorder));

	}

	private void initButtons() {

		btnToggleMenu = new JButton();
		btnToggleMenu.setIcon(GeoGebraIcon.ensureIconSize(app
				.getImageIcon("triangle-up.png"), new Dimension(iconSize,
				iconSize)));
		btnToggleMenu.setIcon(app.getImageIcon("triangle-up.png"));
		btnToggleMenu.addActionListener(this);
		// btnToggleMenu.setBorderPainted(false);
		btnToggleMenu.setPreferredSize(new Dimension(btnSize, 20));

		btnMinimize = new JButton();
		final Dimension iconDim = new Dimension(iconSize, iconSize);
		btnMinimize.setIcon(GeoGebraIcon.createPointStyleIcon(
				EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST, 3, iconDim,
				Color.DARK_GRAY, null));
		// btnMinimize.setRolloverEnabled(true);
		// btnMinimize.setRolloverIcon(GeoGebraIcon.createPointStyleIcon(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST,
		// 3, iconSize, Color.BLACK, null));
		btnMinimize.addActionListener(this);
		btnMinimize.setBorderPainted(false);
		btnMinimize.setPreferredSize(new Dimension(btnSize, btnSize));

		btnToggleInputBar = new JButton();
		btnToggleInputBar.setIcon(app.getImageIcon("triangle-up.png"));
		btnToggleInputBar.addActionListener(this);
		// btnToggleInputBar.setBorderPainted(false);
		btnToggleInputBar.setPreferredSize(new Dimension(btnSize, 20));

		btnKeyboard = new JButton();
		btnKeyboard.setIcon(GeoGebraIcon.ensureIconSize(app
				.getImageIcon("spreadsheet_grid.png"), new Dimension(iconSize,
				iconSize)));

		btnKeyboard.addActionListener(this);
		btnKeyboard.setPreferredSize(new Dimension(btnSize, btnSize));

		btnTurtle = new JButton("T");
		// btnTurtle.setIcon(app.getImageIcon("triangle-up.png"));
		btnTurtle.addActionListener(this);
		btnTurtle.setPreferredSize(new Dimension(btnSize, 20));

		// view toggle buttons
		getViewButtonList();
		updateViewButtons();

	}

	private void initToolBars() {

		// create toolbars
		viewToolBar = new JToolBar();
		viewToolBar.setFloatable(false);
		viewToolBar.setOrientation(SwingConstants.VERTICAL);

		menuToolBar = new JToolBar();
		menuToolBar.setFloatable(false);
		menuToolBar.setOrientation(SwingConstants.VERTICAL);
		Border outsideBorder = menuToolBar.getBorder();
		Border insideBorder = BorderFactory.createEmptyBorder(10, 0, 10, 0);
		menuToolBar.setBorder(BorderFactory.createCompoundBorder(outsideBorder,
				insideBorder));

		inputToolBar = new JToolBar();
		inputToolBar.setFloatable(false);
		inputToolBar.setOrientation(SwingConstants.VERTICAL);

		// register mouse listeners
		inputToolBar.addMouseListener(new MyMouseListener());
		menuToolBar.addMouseListener(new MyMouseListener());
		viewToolBar.addMouseListener(new MyMouseListener());

		// add buttons
		initButtons();
		menuToolBar.add(btnToggleMenu);
		menuToolBar.add(btnToggleInputBar);
		menuToolBar.add(btnTurtle);
		// inputToolBar.add(btnMinimize);

	}

	private void getViewButtonList() {

		AbstractAction action;

		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());

		// construct array with view buttons
		// TODO initializing the viewButtons make no sense immediately before
		// the list is cleared...?
		if (viewButtons == null)
			viewButtons = new ArrayList<ViewButton>();
		viewButtons.clear();

		{
			ViewButton btn;

			for (DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if (panel.getMenuOrder() < 0) {
					continue;
				}

				final int viewID = panel.getViewId();

				if (!app.getGuiManager().showView(viewID)
						&& !(viewID == AbstractApplication.VIEW_PROPERTIES))
					continue;

				btn = new ViewButton();
				btn.setToolTipText(panel.getPlainTitle());
				// btn.setText("" + panel.getViewId());
				setButtonIcon(btn, panel);

				btn.setPreferredSize(new Dimension(btnSize, btnSize));
				btn.setMaximumSize(btn.getPreferredSize());
				// btn.setBorderPainted(false);

				action = new AbstractAction(app.getPlain(panel.getViewTitle())) {

					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent arg0) {
						app.getGuiManager().setShowView(
								!app.getGuiManager().showView(viewID), viewID);
						updateViewButtons();
					}
				};

				btn.addActionListener(action);
				btn.setViewID(viewID);

				viewButtons.add(btn);
			}
		}
	}

	private void setButtonIcon(JButton btn, DockPanel panel) {

		ImageIcon icon;

		if (panel.getViewId() == AbstractApplication.VIEW_EUCLIDIAN)
			icon = app.getImageIcon("euclidian.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_EUCLIDIAN2)
			icon = app.getImageIcon("euclidian.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_CAS)
			icon = app.getImageIcon("xy_table.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_SPREADSHEET)
			icon = app.getImageIcon("spreadsheet.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_ALGEBRA)
			icon = app.getImageIcon("font.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_PROPERTIES)
			icon = app.getImageIcon("document-properties.png");
		else
			icon = app.getImageIcon("tool.png");

		icon = GeoGebraIcon.ensureIconSize(icon, new Dimension(iconSize,
				iconSize));
		btn.setIcon(icon);

		// icon = GeoGebraIcon.joinIcons(GeoGebraIcon.createColorSwatchIcon(1f,
		// new Dimension(6,6), Color.darkGray, Color.green), icon);
		// btn.setSelectedIcon(icon);

	}

	public void updateViewButtons() {

		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());

		viewToolBar.removeAll();

		for (ViewButton btn : viewButtons) {

			viewToolBar.add(btn);

			// btn.setVisible(!app.getGuiManager().showView(btn.getViewID()));
			btn.setSelected(app.getGuiManager().showView(btn.getViewID()));

		}

		viewToolBar.add(btnKeyboard);
	}

	class ViewButton extends JButton {

		private static final long serialVersionUID = 1L;

		private int viewID;

		public int getViewID() {
			return viewID;
		}

		public void setViewID(int viewID) {
			this.viewID = viewID;
		}

	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnToggleMenu) {
			app.setShowToolBar(!app.showToolBar());
		}

		if (source == btnToggleInputBar) {
			app.setShowAlgebraInput(!app.showAlgebraInput(), true);
		}
		if (source == btnMinimize) {
			isMinimized = true;
			updateLayout();
		}
		if (source == btnKeyboard) {
			if (Application.isVirtualKeyboardActive()
					&& !app.getGuiManager().showVirtualKeyboard()) {

				// if keyboard is active but hidden, just show it
				app.getGuiManager().toggleKeyboard(true);
				// update();

			} else {

				Application.setVirtualKeyboardActive(!Application
						.isVirtualKeyboardActive());
				app.getGuiManager().toggleKeyboard(
						Application.isVirtualKeyboardActive());
				// update();
			}
		}
		if (source == btnTurtle) {
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK)
				createTurtle(1);
			else
				createTurtle(2);
		}

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
	
	
	
	public class MyDialog extends JDialog{
		public MyDialog(TurtleDriverPanel turtlePanel){
			super(app.getFrame(), "Turtle Driver", false);
			getContentPane().add(turtlePanel, "Center");
			setSize(300, 300);
			pack();
		};
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
		geo.turn(45);
		geo.forward(2);
		geo.turn(20);
		geo.update();
		
		demo = 3;
		if (demo == 1) {
			geo.setPenColor(geogebra.awt.Color.green);
			randomWalk(geo);
			geo.setPenColor(geogebra.awt.Color.blue);
			randomWalk(geo);
			geo.setPenColor(geogebra.awt.Color.yellow);
			randomWalk(geo);

		}

		if (demo == 2) {
			geo.setPenColor(geogebra.awt.Color.red);
			drawDragonCurves(geo);
			geo.update();
		}
		*/

	}
	
	public static BufferedImage imageToBufferedImage(Image im) {
	     BufferedImage bi = new BufferedImage
	        (im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
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

}
