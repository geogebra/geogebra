package geogebra.web.gui.menubar;

import geogebra.common.gui.Layout;
import geogebra.common.io.layout.Perspective;
import geogebra.html5.main.AppW;
import geogebra.web.gui.ImageFactory;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.images.PerspectiveResources;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

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
	    ArrayList<ResourcePrototype> icons = new ArrayList<ResourcePrototype>();
	    PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
	    icons.add(pr.menu_icon_algebra());
	    icons.add(pr.menu_icon_geometry());
	    icons.add(pr.menu_icon_spreadsheet());
	    icons.add(pr.menu_icon_cas());
	    icons.add(pr.menu_icon_graphics3D());
	    icons.add(pr.menu_icon_probability());
		for (int i = 0; i < defaultPerspectives.length; ++i) {
			if(defaultPerspectives[i] == null){
				continue;
			}
			final int index = i;
			addItem(MainMenu.getMenuBarHtml(GGWToolBar.safeURI(icons.get(i)), 
					app.getMenu(defaultPerspectives[i].getId()), true),true,new MenuCommand(app) {
						
				@Override
				public void doExecute() {
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
