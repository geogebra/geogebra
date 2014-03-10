package geogebra.web.gui.view.algebra;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.images.StyleBarResources;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.html5.AttachedToDOM;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.javax.swing.GPopupMenuW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class AlgebraContextMenuW implements AttachedToDOM{

	private AppW app;
	GPopupMenuW wrappedPopup;

	public AlgebraContextMenuW(AppW application){
		app = application;
		wrappedPopup = new GPopupMenuW(app);
		initItems();
	}

	/**
	 * Initialize the menu items.
	 */
	private void initItems() {
		
	    MenuItem title = new MenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getPlain("AlgebraWindow")),
	    		true, new Command() {
					
					public void execute() {
						wrappedPopup.setVisible(false);
					}
				});
	    title.addStyleName("menuTitle");
	    wrappedPopup.addItem(title);
	    
	    wrappedPopup.addSeparator();
		
	    // TODO Auto-generated method stub
		ScheduledCommand showAuxiliaryAction = new ScheduledCommand() {
			public void execute() {
				app.setShowAuxiliaryObjects(!app.showAuxiliaryObjects());
            }
		};     
		
		GCheckBoxMenuItem cbShowAuxiliary = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(
				StyleBarResources.INSTANCE.auxiliary().getSafeUri().asString(), app.getPlain("AuxiliaryObject")), showAuxiliaryAction);
		
		cbShowAuxiliary.setSelected(app.showAuxiliaryObjects());
		
		wrappedPopup.addItem(cbShowAuxiliary);	    
    }

	public void show(AlgebraView view, int x, int y) {
	    wrappedPopup.show(new GPoint(x,y));
    }

	public void removeFromDOM() {
	    wrappedPopup.removeFromDOM();
    }
	
	
}
