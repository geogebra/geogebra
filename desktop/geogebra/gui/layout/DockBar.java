package geogebra.gui.layout;

import geogebra.common.main.AbstractApplication;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
	private JButton btnToggleMenu, btnToggleInputBar, btnMinimize, btnKeyboard;

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
		@Override
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


		btnKeyboard = new JButton();
		btnKeyboard.setIcon(GeoGebraIcon.ensureIconSize(app.getImageIcon("spreadsheet_grid.png"), new Dimension(iconSize,iconSize)));
		
		btnKeyboard.addActionListener(this);
		btnKeyboard.setPreferredSize(new Dimension(btnSize, btnSize));

		
		// view toggle buttons
		getViewButtonList();
		updateViewButtons();

	}



	private void initToolBars(){

		// create toolbars
		viewToolBar = new JToolBar();
		viewToolBar.setFloatable(false);
		viewToolBar.setOrientation(SwingConstants.VERTICAL);

		menuToolBar = new JToolBar();
		menuToolBar.setFloatable(false);
		menuToolBar.setOrientation(SwingConstants.VERTICAL);	
		Border outsideBorder = menuToolBar.getBorder();
		Border insideBorder = BorderFactory.createEmptyBorder(10, 0, 10, 0);
		menuToolBar.setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));

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
		//inputToolBar.add(btnMinimize);

	}




	private void getViewButtonList() {

		AbstractAction action;

		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());

		// construct array with view buttons
		// TODO initializing the viewButtons make no sense immediately before the list is cleared...?
		if(viewButtons == null)
			viewButtons = new ArrayList<ViewButton>();
		viewButtons.clear();

		{
			ViewButton btn;

			for(DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if(panel.getMenuOrder() < 0) {
					continue;
				}

				final int viewID = panel.getViewId();

				if(!app.getGuiManager().showView(viewID)
						&& !(viewID == AbstractApplication.VIEW_PROPERTIES))
					continue;



				btn = new ViewButton();
				btn.setToolTipText(panel.getPlainTitle());
				//btn.setText("" + panel.getViewId());
				setButtonIcon(btn, panel);

				btn.setPreferredSize(new Dimension(btnSize,btnSize));
				btn.setMaximumSize(btn.getPreferredSize());
				//	btn.setBorderPainted(false);


				action = new AbstractAction(app.getPlain(panel.getViewTitle())) {
	
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent arg0) {
						app.getGuiManager().setShowView(!app.getGuiManager().showView(viewID), viewID);
						updateViewButtons();
					}
				};

				btn.addActionListener(action);			
				btn.setViewID(viewID);

				viewButtons.add(btn);
			}
		}
	}


	private void setButtonIcon(JButton btn, DockPanel panel){

		ImageIcon icon;

		if(panel.getViewId() == AbstractApplication.VIEW_EUCLIDIAN)
			icon = app.getImageIcon("euclidian.png");
		else if(panel.getViewId() == AbstractApplication.VIEW_EUCLIDIAN2)
			icon = app.getImageIcon("euclidian.png");
		else if(panel.getViewId() == AbstractApplication.VIEW_CAS)
			icon = app.getImageIcon("xy_table.png");
		else if(panel.getViewId() == AbstractApplication.VIEW_SPREADSHEET)
			icon = app.getImageIcon("spreadsheet.png");
		else if(panel.getViewId() == AbstractApplication.VIEW_ALGEBRA)
			icon = app.getImageIcon("font.png");
		else if(panel.getViewId() == AbstractApplication.VIEW_PROPERTIES)
			icon = app.getImageIcon("document-properties.png");
		else
			icon = app.getImageIcon("tool.png");

		icon = GeoGebraIcon.ensureIconSize(icon, new Dimension(iconSize,iconSize));
		btn.setIcon(icon);

		//icon = GeoGebraIcon.joinIcons(GeoGebraIcon.createColorSwatchIcon(1f, new Dimension(6,6), Color.darkGray, Color.green), icon);
		//btn.setSelectedIcon(icon);

	}



	public void updateViewButtons() {		

		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());

		viewToolBar.removeAll();

		for(ViewButton btn: viewButtons) {

			viewToolBar.add(btn);

			//btn.setVisible(!app.getGuiManager().showView(btn.getViewID()));
			btn.setSelected(app.getGuiManager().showView(btn.getViewID()));

		}
		
		viewToolBar.add(btnKeyboard);
	}

	class ViewButton extends JButton{

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
		if(source == btnKeyboard){
			if (Application.isVirtualKeyboardActive() && !app.getGuiManager().showVirtualKeyboard()) {

				// if keyboard is active but hidden, just show it
				app.getGuiManager().toggleKeyboard(true);
				//update();

			} else {

				Application.setVirtualKeyboardActive(!Application.isVirtualKeyboardActive());				
				app.getGuiManager().toggleKeyboard(Application.isVirtualKeyboardActive());
				//update();
			}
		}

	}

	
	
	

	public void openDockBar(){
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
		final Border hoverBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);

		if (minimumPanel == null) {
			minimumPanel = new JPanel();
			minimumPanel.setMinimumSize(new Dimension(6,0));
			minimumPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()>0){
						isMinimized = false;
						updateLayout();
					}
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					minimumPanel.setBackground(Color.LIGHT_GRAY);
					//restorePanel.setBorder(hoverBorder);
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

}
