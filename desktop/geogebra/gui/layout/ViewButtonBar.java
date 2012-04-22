package geogebra.gui.layout;

import geogebra.main.Application;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class ViewButtonBar extends JToolBar implements ActionListener{

	private Application app;
	private Layout layout;

	private ArrayList<ViewButton> viewButtons;

	public ViewButtonBar(Application app) {

		this.app = app;
		this.layout = app.getGuiManager().getLayout();
		this.setOrientation(JToolBar.HORIZONTAL);
		buildToolBar();
	
	}

	
	private void buildToolBar() {
		
		this.setFloatable(false);
		
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

				btn = new ViewButton();
				btn.setToolTipText(panel.getPlainTitle());
				btn.setAlignmentX(Component.CENTER_ALIGNMENT);

				if (panel.getIcon() != null) {
					btn.setIcon(panel.getIcon());
				} else {
					btn.setIcon(app.getEmptyIcon());
				}
				
				btn.addActionListener(this);
				btn.setViewID(viewID);

				viewButtons.add(btn);
				this.add(btn);
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
			
		}
	}
	
	
	
	public void updateViewButtons() {

		buildToolBar();
	}

	
	
	public void updateViewButtonVisibility() {

		for (ViewButton btn : viewButtons) {
			DockPanel panel = layout.getDockManager().getPanel(btn.getViewID());
			btn.setVisible(panel.isHidden()
					|| (!panel.isHidden() && btn.getViewID() == Application.VIEW_PROPERTIES));
			// btn.setSelected(app.getGuiManager().showView(btn.getViewID()));
		}
	}
	
	
	
	
	
	private class ViewButton extends JButton {

		private static final long serialVersionUID = 1L;

		private int viewID;

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(24, 24);
		}

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

	

}
