package geogebra.web.gui.app;

import geogebra.common.io.layout.Perspective;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;
import geogebra.web.gui.inputbar.AlgebraInput;
import geogebra.web.gui.toolbar.ToolBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GGWToolBar extends Composite {

	private static GGWToolBarUiBinder uiBinder = GWT
	        .create(GGWToolBarUiBinder.class);

	interface GGWToolBarUiBinder extends UiBinder<Widget, GGWToolBar> {
	}

	private VerticalPanel toolbarPanel = new VerticalPanel(); //just dummy!
	private VerticalPanel toolbars;
	private AbstractApplication app;
	@UiField ToolBar toolBar;
	

	public GGWToolBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * Initialisation of the GGWToolbar.
	 * 
	 * @param app
	 */
	public void init(AbstractApplication app){
		this.app = app;
		toolbars = new VerticalPanel();
		toolBar = new ToolBar();
		toolBar.setApp((geogebra.web.main.Application) app);
		addToolbar(toolBar);
		buildGui();
	}
	
	public void buildGui() {
	    // TODO Auto-generated method stub

		toolbarPanel = new VerticalPanel();
	    updateToolbarPanel();
	    
	    
	    //setActiveToolbar(activeToolbar);
    }
	
	/**
	 * Update toolbars.
	 */
	public void updateToolbarPanel() {
		AbstractApplication.debug("Implementation needed - just finishing");
	
		toolbarPanel.clear();
		
		for(Widget toolbar : toolbars) {
			if(toolbar != null) {
				((ToolBar)toolbar).buildGui();
				//TODO
				//toolbarPanel.add(toolbar, Integer.toString(getViewId(toolbar)));
				toolbarPanel.add(toolbar);
			}
		}
		
		//TODO
		//toolbarPanel.show(Integer.toString(activeToolbar));
		toolbarPanel.setVisible(true);
		
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
