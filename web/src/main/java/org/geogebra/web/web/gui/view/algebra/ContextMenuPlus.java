package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class ContextMenuPlus {
	protected GPopupMenuW wrappedPopup;
	protected Localization loc;
	
	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	ContextMenuPlus(AppW app) {
		this.loc = app.getLocalization();
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("mioMenu");

		buildGUI();
	}

	private void buildGUI() {
		wrappedPopup.clearItems();
		addExpressionItem();
		addTextItem();
		addImageItem();
		addHelpItem();
	}
	
	private void addExpressionItem() {
		MenuItem mi = new MenuItem(loc.getMenu("Expression"),
				new Command() {
					
					@Override
					public void execute() {
						// TODO Auto-generated method stub
						
					}
				});

		mi.addStyleName("no_image");
		wrappedPopup.addItem(mi);
	}

	private void addTextItem() {
		MenuItem mi = new MenuItem(loc.getMenu("Text"),
				new Command() {
					
					@Override
					public void execute() {
						// TODO Auto-generated method stub
						
					}
				});

		mi.addStyleName("no_image");
		wrappedPopup.addItem(mi);
	}
	
	private void addImageItem() {
		MenuItem mi = new MenuItem(loc.getMenu("Image"),
				new Command() {
					
					@Override
					public void execute() {
						// TODO Auto-generated method stub
						
					}
				});

		mi.addStyleName("no_image");
		wrappedPopup.addItem(mi);
	}

	private void addHelpItem() {
		String img = GuiResources.INSTANCE.icon_help().getSafeUri()
		.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img, loc.getMenu("Help"), true),
				true, new Command() {
					
					@Override
					public void execute() {
						// TODO Auto-generated method stub
						
					}
				});

		mi.addStyleName("image");
		wrappedPopup.addItem(mi);
	}
	public void show(GPoint p) {
		wrappedPopup.show(p);
	}

	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
	}

}
