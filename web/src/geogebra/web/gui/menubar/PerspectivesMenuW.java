package geogebra.web.gui.menubar;

import geogebra.common.gui.Layout;
import geogebra.common.io.layout.Perspective;
import geogebra.html5.gui.util.AppResources;
import geogebra.html5.main.AppW;

import com.google.gwt.user.client.Command;

/**
 * Web implementation of FileMenu
 */
public class PerspectivesMenuW extends GMenuBar {
	
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
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), 
				app.getMenu(defaultPerspectives[i].getId()), true),true,new Command() {
			
			public void execute() {
				app.persistWidthAndHeight();
				boolean changed = layout.applyPerspective(geogebra.common.gui.Layout.defaultPerspectives[index]);
				app.updateViewSizes();
				app.getGuiManager().updateMenubar();
				if(changed){
					app.storeUndoInfo();
				}
			}
		});			
		}
			

		// this is enabled always
	    
		

	}
	
	

}
