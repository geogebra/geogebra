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
public class DockBar extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;

	private Application app;
	private Layout layout;

	private ConfigurationPanel configPanel;

	private JToolBar viewToolBar, menuToolBar, inputToolBar;
	private JPanel mainPanel, minimumPanel;

	private ArrayList<ViewButton> viewButtons;
	private JButton btnSettings, btnGeoGebra, btnView, btnOptions;

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
	
	public void update(){
		
	//	btnSettings.setSelected(!app.isMainPanelShowing());
		
	//	btnView.setVisible(app.isMainPanelShowing());
	//	btnOptions.setVisible(app.isMainPanelShowing());
		
	}

	private void initGUI() {

		initToolBars();

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(Box.createVerticalStrut(15));
		mainPanel.add(btnSettings);
		mainPanel.add(Box.createVerticalStrut(35));
		mainPanel.add(btnView);
		mainPanel.add(btnOptions);
		
		
		//mainPanel.add(menuToolBar, BorderLayout.NORTH);
		//mainPanel.add(inputToolBar, BorderLayout.SOUTH);
		//mainPanel.add(viewToolBar, BorderLayout.CENTER);
		
		
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
		

		btnSettings = new JButton();
		btnSettings.setIcon(app.getImageIcon("tool_32.png"));
		//btnSettings.setSelectedIcon(app.getImageIcon("go-previous24.png"));
		btnSettings.addActionListener(this);
		btnSettings.setFocusPainted(false);
		btnSettings.setBorderPainted(false);
		btnSettings.setContentAreaFilled(false);
		

		btnView = new JButton();
		btnView.setIcon(app.getImageIcon("view_btn.png"));
		btnView.addActionListener(this);
		btnView.setFocusPainted(false);
	//	btnView.setBorderPainted(false);
	//	btnView.setContentAreaFilled(false);

		btnOptions = new JButton();
		btnOptions.setIcon(app.getImageIcon("options_btn.png"));
		btnOptions.addActionListener(this);
		btnOptions.setFocusPainted(false);
	//	btnOptions.setBorderPainted(false);
	//	btnOptions.setContentAreaFilled(false);
		
		
		//btnOptions.addMouseListener(this);
		//btnOptions.setComponentPopupMenu(optionsPopupMenu);
		

		btnTurtle = new JButton("T");
		btnTurtle.addActionListener(this);
		
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
		Border insideBorder = BorderFactory.createEmptyBorder(2, 0, 2, 0);
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
		//menuToolBar.add(btnGeoGebra);
		menuToolBar.add(btnSettings);
		
		
		viewToolBar.add(btnView);
		viewToolBar.add(btnOptions);


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

				// btn.setPreferredSize(new Dimension(btnSize, btnSize));
				// btn.setMaximumSize(btn.getPreferredSize());
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

				//viewButtons.add(btn);
			}
		}
	}

	private void setButtonIcon(AbstractButton btn, DockPanel panel) {

		ImageIcon icon;

		if (panel.getViewId() == AbstractApplication.VIEW_EUCLIDIAN)
			icon = app.getImageIcon("euclidian.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_EUCLIDIAN2)
			icon = app.getImageIcon("euclidian.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_CAS)
			icon = app.getImageIcon("panel_cas.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_SPREADSHEET)
			icon = app.getImageIcon("panel_spreadsheet.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_ALGEBRA)
			icon = app.getImageIcon("panel_algebra.png");
		else if (panel.getViewId() == AbstractApplication.VIEW_PROPERTIES)
			icon = app.getImageIcon("document-properties.png");
		else
			icon = app.getImageIcon("tool.png");

		// icon = GeoGebraIcon.ensureIconSize(icon, new Dimension(iconSize,
		// iconSize));
		btn.setIcon(icon);

		// icon = GeoGebraIcon.joinIcons(GeoGebraIcon.createColorSwatchIcon(1f,
		// new Dimension(6,6), Color.darkGray, Color.green), icon);
		// btn.setSelectedIcon(icon);

	}

	public void updateViewButtons() {

		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());

	//	viewToolBar.removeAll();

		for (ViewButton btn : viewButtons) {

			//viewToolBar.add(btn);

			btn.setVisible(!app.getGuiManager().showView(btn.getViewID()));
			btn.setSelected(app.getGuiManager().showView(btn.getViewID()));

		}

		// viewToolBar.add(btnKeyboard);
	}

	class ViewButton extends JButton {

		private static final long serialVersionUID = 1L;

		private int viewID;

		@Override
		public void setBorderPainted(boolean setBorderPainted) {
			super.setBorderPainted(false);
		}

		@Override
		public void setContentAreaFilled(boolean setContentAreaFilled) {
			super.setContentAreaFilled(false);
		}

		public int getViewID() {
			return viewID;
		}

		public void setViewID(int viewID) {
			this.viewID = viewID;
		}

	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnSettings) {

			if (configPanel == null) {
				configPanel = new ConfigurationPanel(app, this);
				app.setBackPanel(configPanel);
			}
			//app.setBackPanel(configPanel);
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
			//viewPopupMenu = new ViewPopupMenu(app, app.getGuiManager()
				//	.getLayout());
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

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
