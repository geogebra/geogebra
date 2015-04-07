package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.html5.AttachedToDOM;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

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
		
	    MenuItem title = new MenuItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getPlain("AlgebraWindow")),
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
		
		GCheckBoxMenuItem cbShowAuxiliary = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
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
