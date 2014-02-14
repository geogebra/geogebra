package geogebra.web.gui.menubar;

import geogebra.common.gui.Layout;
import geogebra.common.io.layout.Perspective;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * Web implementation of FileMenu
 */
public class PerspectivesMenuW extends MenuBar {
	
	/** Application */
	AppW app;
	private Layout layout;
	
	/**
	 * @param app application
	 */
	public PerspectivesMenuW(AppW app) {
	    super(true);
	    this.app = app;
	    this.layout = app.getGuiManager().getLayout();
	    addStyleName("GeoGebraMenuBar");
	    initActions();
		update();
	}

	private void update() {
	    // TODO Auto-generated method stub
	    
    }

	private void initActions() {

		
	    Perspective[] defaultPerspectives = geogebra.common.gui.Layout.defaultPerspectives;

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			final int index = i;
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), 
				app.getMenu("Perspective."+ defaultPerspectives[i].getId()), true),true,new Command() {
			
			public void execute() {
				layout.applyPerspective(geogebra.common.gui.Layout.defaultPerspectives[index]);
				app.updateViewSizes();
			}
		});			
		}
			

		// this is enabled always
	    
		

	}
	
	

}
