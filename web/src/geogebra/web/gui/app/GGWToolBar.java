package geogebra.web.gui.app;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.toolbar.ToolBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GGWToolBar extends Composite {

	private static GGWToolBarUiBinder uiBinder = GWT
	        .create(GGWToolBarUiBinder.class);

	interface GGWToolBarUiBinder extends UiBinder<Widget, GGWToolBar> {
	}

	private VerticalPanel toolbarPanel;
	private VerticalPanel toolbars;

	public GGWToolBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void buildGui() {
	    // TODO Auto-generated method stub
	    
    }
	
	/**
	 * Update toolbars.
	 */
	public void updateToolbarPanel() {
		toolbarPanel.clear();

		/*AGfor(ToolBar toolbar : toolbars) {
			if(toolbar != null) {
				toolbar.buildGui();
				toolbarPanel.add(toolbar, Integer.toString(getViewId(toolbar)));
			}
		}

		toolbarPanel.show(Integer.toString(activeToolbar));*/
		AbstractApplication.debug("Implementation needed");
	}

	/**
	 * Adds a toolbar to this container. Use updateToolbarPanel() to update the GUI after 
	 * all toolbar changes were made. 
	 * 
	 * @param toolbar
	 */
	public void addToolbar(ToolBar toolbar) {
		toolbars.add(toolbar);
	}

	/**
	 * Removes a toolbar from this container. Use {@link #updateToolbarPanel()} to update the GUI
	 * after all toolbar changes were made. If the removed toolbar was the active toolbar as well
	 * the active toolbar is changed to the general (but again, {@link #updateToolbarPanel()}
	 * has to be called for a visible effect).
	 * 
	 * @param toolbar
	 */
	public void removeToolbar(ToolBar toolbar) {
		toolbars.remove(toolbar);

		/*AGif(getViewId(toolbar) == activeToolbar) {
			activeToolbar = -1;
		}*/
	}

}
