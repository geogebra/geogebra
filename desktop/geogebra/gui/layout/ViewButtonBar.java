package geogebra.gui.layout;

import geogebra.gui.MySmallJButton;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * @author G. Sturr
 *
 */
public class ViewButtonBar extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private Application app;
	private Layout layout;

	private ArrayList<ViewButton> viewButtons;

	private int orientation = SwingConstants.HORIZONTAL;
	
	
	/**
	 * @param app
	 */
	public ViewButtonBar(Application app) {

		this.app = app;
		this.layout = app.getGuiManager().getLayout();
		
		buildToolBar();
	
	}

	
	void setOrientation(int orientation) {
		
		this.orientation = orientation;
		buildToolBar();
	}


	private void buildToolBar() {
		
		//this.setFloatable(false);
		
		if(orientation == JToolBar.HORIZONTAL){
			setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		}else{
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		}
		
		
		this.removeAll();
		
		this.add(Box.createVerticalStrut(24));
		
		if (viewButtons == null)
			viewButtons = new ArrayList<ViewButton>();
		viewButtons.clear();
		
		ViewButton btn;

		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());

		// iterate through the dock panels
		for (DockPanel panel : dockPanels) {
			
			// skip panels with negative order 
			if (panel.getMenuOrder() < 0) {
				continue;
			}

			final int viewID = panel.getViewId();

			Icon ic = null;
			if (panel.getIcon() != null) {
				ic = panel.getIcon();
			} else {
				ic = app.getEmptyIcon();
			}
			

			
				btn = new ViewButton(ic,0);
				btn.setToolTipText(panel.getPlainTitle());
				btn.setAlignmentX(Component.CENTER_ALIGNMENT);
				btn.setFocusable(false);

								btn.addActionListener(this);
				btn.setViewID(viewID);
				//btn.setSelected(true);
				viewButtons.add(btn);
				this.add(btn);
				//this.add(Box.createHorizontalStrut(2));
		}

		updateViewButtonVisibility();
	}

	
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() instanceof ViewButton){
			
			int viewID = ((ViewButton)e.getSource()).getViewID();
			DockPanel panel = layout.getDockManager().getPanel(viewID);
			if(!panel.isAlone()){
			app.getGuiManager().setShowView(
					!app.getGuiManager().showView(viewID), viewID, false);
			
			}
		//	if(app.getControlDown())
			//	toggleLAF();
		}
	}
	
//	static boolean forceCrossPlatform = false;

//	public  void toggleLAF() {
//		forceCrossPlatform = !forceCrossPlatform;
//		GeoGebraFrame.setLAF(forceCrossPlatform);
//		SwingUtilities.updateComponentTreeUI(app.getFrame());
//	}

	
	public void updateViewButtons() {

		buildToolBar();
	}

	
	
	public void updateViewButtonVisibility() {

		for (ViewButton btn : viewButtons) {
			DockPanel panel = layout.getDockManager().getPanel(btn.getViewID());
			btn.setVisible(panel.isHidden()
					|| (!panel.isHidden() && btn.getViewID() == Application.VIEW_PROPERTIES));
			
			//btn.setVisible(panel.isHidden()
				//	||app.getGuiManager().showView(btn.getViewID()) || btn.getViewID() == Application.VIEW_PROPERTIES);
			//btn.setSelected(!app.getGuiManager().showView(btn.getViewID()));
		}
	}
	
	
	
	
	static class ViewButton extends MySmallJButton {

		public ViewButton(Icon icon, int addPixel) {
			super(icon, addPixel);
			// TODO Auto-generated constructor stub
		}


		private static final long serialVersionUID = 1L;

		private int viewID;
		private static final Color selectedColor = new Color(150, 150, 150, 128);
		private static final Color unselectedColor = new Color(100, 100, 100, 50);
		
/*		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(24, 24);
		}
*/
		@Override
		public void setBorderPainted(boolean setBorderPainted) {
			super.setBorderPainted(false);
		}

		@Override
		public void setBorder(Border border) {
			super.setBorder(BorderFactory.createEmptyBorder());
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
		
	/*	
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g2);

			if (isSelected()) {
				g2.setColor(selectedColor);
				g2.drawRect(0, 0, 22, 22);
				//g2.drawRect(1, 1, 20, 20);
			}else{
				g2.setColor(unselectedColor);
				g2.fillRect(0, 0, 23, 23);
			}
		}
		
*/
	}

	

}
