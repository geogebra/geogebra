package geogebra.gui.layout;

import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JToolBar;

/**
 * @author G. Sturr
 * 
 */
public class ViewButtonBar extends JToolBar {

	private static final long serialVersionUID = 1L;
	private Application app;
	private LayoutD layout;

	private ArrayList<ViewButton> viewButtons;

	/**
	 * @param app
	 */
	public ViewButtonBar(Application app) {

		this.app = app;
		this.layout = app.getGuiManager().getLayout();
		this.setOpaque(false);
		this.setBorder(BorderFactory.createEmptyBorder());
		buildToolBar();

	}

	private void buildToolBar() {

		setFloatable(false);
		createViewButtons();
		updateViewButtonVisibility();
	}

	private void createViewButtons() {
		if (viewButtons == null)
			viewButtons = new ArrayList<ViewButton>();
		viewButtons.clear();

		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());

		// get the PropertiesView dock panel first
		for (DockPanel panel : dockPanels) {
			if (panel.getViewId() == Application.VIEW_PROPERTIES) {
				viewButtons.add(new ViewButton(app, panel));
			}
		}
					
		// iterate through the dock panels
		for (DockPanel panel : dockPanels) {

			// skip panels with negative order and the PropertiesView panel
			if (panel.getMenuOrder() < 0
					|| panel.getViewId() == Application.VIEW_PROPERTIES) {
				continue;
			}

			viewButtons.add(new ViewButton(app, panel));
		}
	}

	public void updateViewButtons() {

		buildToolBar();
	}

	public void updateViewButtonVisibility() {

		removeAll();

		for (ViewButton btn : viewButtons) {
			DockPanel panel = btn.getPanel();

				btn.setSelected(app.getGuiManager().showView(btn.getViewId()));

				if (panel.getViewId() != AbstractApplication.VIEW_PROPERTIES 
						&& panel.getViewId() != AbstractApplication.VIEW_ASSIGNMENT ) 
					add(btn);
		}
		
		// spacer
		//add(Box.createVerticalStrut(20));
		
		// add properties view button
		//add(viewButtons.get(0));
		
		viewButtons.get(0).setSelected(app.getGuiManager().showView(viewButtons.get(0).getViewId()));
	}

}
