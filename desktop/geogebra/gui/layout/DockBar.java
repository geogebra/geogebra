package geogebra.gui.layout;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.Application;
import geogebra.util.ImageManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * Toolbar to hold launching buttons for minimized views. 
 * 
 * @author G. Sturr
 *
 */
public class DockBar extends JPanel implements ActionListener {

	private static final int btnSize = 36;
	private static final int iconSize = btnSize - 10;

	private Application app;
	private Layout layout;

	private JToolBar viewToolBar, menuToolBar, inputToolBar;
	private JPanel mainPanel, minimumPanel;

	private AbstractAction[] showViews;
	private JButton[] viewButtons;
	private JButton btnToggleMenu, btnToggleInputBar, btnMinimize;

	private boolean isMinimized = true;



	/*******************************************************
	 * Constructs a DockBar
	 * @param app
	 */
	public DockBar(Application app){

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
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()>0){
				isMinimized = true;
				updateLayout();
			}
		}
	};



	/**
	 * Updates the layout to either show the dockbar and its components 
	 * or show the minimized panel. 
	 */
	private void updateLayout(){
		this.removeAll();
		if(isMinimized)
			this.add(getMinimizedPanel(), BorderLayout.CENTER);
		else
			this.add(mainPanel, BorderLayout.CENTER);

		this.revalidate();
		this.repaint();
	}



	private void initGUI(){

		initToolBars();

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(menuToolBar, BorderLayout.NORTH);
		mainPanel.add(inputToolBar, BorderLayout.SOUTH);
		mainPanel.add(viewToolBar, BorderLayout.CENTER);					
		Border outsideBorder = BorderFactory.createMatteBorder(1, 0, 0, 1, SystemColor.controlShadow);
		Border insideBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, SystemColor.controlLtHighlight);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));

	}



	private void initButtons(){

		btnToggleMenu = new JButton();
		btnToggleMenu.setIcon(GeoGebraIcon.ensureIconSize(app.getImageIcon("triangle-up.png"), new Dimension(iconSize,iconSize)));
		btnToggleMenu.setIcon(app.getImageIcon("triangle-up.png"));
		btnToggleMenu.addActionListener(this);
		//btnToggleMenu.setBorderPainted(false);
		btnToggleMenu.setPreferredSize(new Dimension(btnSize, 20));


		btnMinimize = new JButton();
		final Dimension iconDim = new Dimension(iconSize,iconSize);
		btnMinimize.setIcon(GeoGebraIcon.createPointStyleIcon(EuclidianView.POINT_STYLE_TRIANGLE_WEST, 3, iconDim, Color.DARK_GRAY, null));
		//btnMinimize.setRolloverEnabled(true);
		//btnMinimize.setRolloverIcon(GeoGebraIcon.createPointStyleIcon(EuclidianView.POINT_STYLE_TRIANGLE_WEST, 3, iconSize, Color.BLACK, null));
		btnMinimize.addActionListener(this);
		btnMinimize.setBorderPainted(false);
		btnMinimize.setPreferredSize(new Dimension(btnSize, btnSize));


		btnToggleInputBar = new JButton();
		btnToggleInputBar.setIcon(app.getImageIcon("triangle-up.png"));
		btnToggleInputBar.addActionListener(this);
		//	btnToggleInputBar.setBorderPainted(false);
		btnToggleInputBar.setPreferredSize(new Dimension(btnSize, 20));

		
		// view toggle buttons
		initViewActions();
		initViewButtons();
		updateViews();

	}



	private void initToolBars(){

		// create toolbars
		viewToolBar = new JToolBar();
		viewToolBar.setFloatable(false);
		viewToolBar.setOrientation(JToolBar.VERTICAL);
		
		menuToolBar = new JToolBar();
		menuToolBar.setFloatable(false);
		menuToolBar.setOrientation(JToolBar.VERTICAL);	
		Border outsideBorder = menuToolBar.getBorder();
		Border insideBorder = BorderFactory.createEmptyBorder(10, 0, 10, 0);
		menuToolBar.setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));
		
		inputToolBar = new JToolBar();
		inputToolBar.setFloatable(false);
		inputToolBar.setOrientation(JToolBar.VERTICAL);	
		
		
		
		// register mouse listeners
		inputToolBar.addMouseListener(new MyMouseListener());	
		menuToolBar.addMouseListener(new MyMouseListener());
		viewToolBar.addMouseListener(new MyMouseListener());
		
		
		// add buttons
		initButtons();
		menuToolBar.add(btnToggleMenu);
		menuToolBar.add(btnToggleInputBar);
		//inputToolBar.add(btnMinimize);
			
	}



	private void initViewActions() {
		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());
		int viewsInMenu = 0;

		// count visible views first..
		for(DockPanel panel : dockPanels) {
			// skip panels with negative order by design
			if(panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}

		// construct array with menu items
		showViews = new AbstractAction[viewsInMenu];
		{
			int i = 0;
			AbstractAction action;

			for(DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if(panel.getMenuOrder() < 0) {
					continue;
				}

				final int viewId = panel.getViewId();

				action = new AbstractAction(app.getPlain(panel.getViewTitle())) {
					public void actionPerformed(ActionEvent arg0) {
						app.getGuiManager().setShowView(!app.getGuiManager().showView(viewId), viewId);
						updateViews();
					}
				};

				showViews[i] = action;
				++i;
			}
		}
	}

	private void initViewButtons() {
		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());
		int viewsInMenu = 0;

		// count visible views first..
		for(DockPanel panel : dockPanels) {
			// skip panels with negative order by design
			if(panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}

		// construct array with view buttons
		viewButtons = new JButton[viewsInMenu];
		{
			int i = 0;
			JButton btn;

			for(DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if(panel.getMenuOrder() < 0) {
					continue;
				}

				btn = new JButton();
				btn.setToolTipText(panel.getPlainTitle());
				//btn.setText("" + panel.getViewId());
				setButtonIcon(btn, panel);

				btn.setPreferredSize(new Dimension(btnSize,btnSize));
				btn.setMaximumSize(btn.getPreferredSize());
				//	btn.setBorderPainted(false);

				viewButtons[i] = btn;

				btn.addActionListener(showViews[i]);
				++i;
			}
		}
	}


	private void setButtonIcon(JButton btn, DockPanel panel){

		ImageIcon icon;

		if(panel.getViewId() == Application.VIEW_EUCLIDIAN)
			icon = app.getImageIcon("euclidian.png");
		else if(panel.getViewId() == Application.VIEW_EUCLIDIAN2)
			icon = app.getImageIcon("euclidian.png");
		else if(panel.getViewId() == Application.VIEW_CAS)
			icon = app.getImageIcon("xy_table.png");
		else if(panel.getViewId() == Application.VIEW_SPREADSHEET)
			icon = app.getImageIcon("spreadsheet.png");
		else if(panel.getViewId() == Application.VIEW_ALGEBRA)
			icon = app.getImageIcon("font.png");
		else if(panel.getViewId() == Application.VIEW_PROPERTIES)
			icon = app.getImageIcon("document-properties.png");
		else
			icon = app.getImageIcon("tool.png");

		icon = ImageManager.getScaledIcon(icon, iconSize,iconSize);
		btn.setIcon(icon);

		//icon = GeoGebraIcon.joinIcons(GeoGebraIcon.createColorSwatchIcon(1f, new Dimension(6,6), Color.darkGray, Color.green), icon);
		//btn.setSelectedIcon(icon);

	}



	public void updateViews() {		

		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());
		int viewsInMenu = 0;

		// count visible views first..
		for(DockPanel panel : dockPanels) {
			// skip panels with negative order by design
			if(panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}

		// update views
		{
			viewToolBar.removeAll();
			int i = 0;
			for(DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if(panel.getMenuOrder() < 0) {
					continue;
				}

				viewToolBar.add(viewButtons[i]);
				//viewToolBar.add(new JToolBar.Separator());

				if(!app.getGuiManager().showView(panel.getViewId())){
					//	this.add(Box.createRigidArea(new Dimension(0,4)));
					//	this.add(viewButtons[i]);

				}

				//viewButtons[i].setVisible(!app.getGuiManager().showView(panel.getViewId()));
				viewButtons[i].setSelected(app.getGuiManager().showView(panel.getViewId()));


				if(panel.getViewId() == Application.VIEW_CONSTRUCTION_PROTOCOL)
					//||panel.getViewId() == Application.VIEW_PROPERTIES)
					viewButtons[i].setVisible(false);
				//	else
				//		viewToolBar.add(Box.createRigidArea(new Dimension(0,4)));


				++i;
			}
		}
	}


	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == btnToggleMenu){
			app.setShowToolBar(!app.showToolBar());
		}

		if(source == btnToggleInputBar){
			app.setShowAlgebraInput(!app.showAlgebraInput(), true);
		}
		if(source == btnMinimize){
			isMinimized = true;
			updateLayout();
		}

	}




	/**
	 * Returns minimumPanel, a slim vertical bar displayed when the dockBar is
	 * minimized. When clicked it restores the dockBar to full size.
	 * 
	 */
	private JPanel getMinimizedPanel() {
		final Border border = BorderFactory.createEtchedBorder();
		final Border hoverBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);

		if (minimumPanel == null) {
			minimumPanel = new JPanel();
			minimumPanel.setMinimumSize(new Dimension(6,0));
			minimumPanel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()>0){
						isMinimized = false;
						updateLayout();
					}
				}
				public void mouseEntered(MouseEvent e) {
					minimumPanel.setBackground(Color.LIGHT_GRAY);
					//restorePanel.setBorder(hoverBorder);
				}
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

}
