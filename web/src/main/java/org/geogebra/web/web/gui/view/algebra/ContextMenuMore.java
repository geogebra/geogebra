package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class ContextMenuMore implements SetLabels {
	protected GPopupMenuW wrappedPopup;
	protected Localization loc;
	private AppW app;
	private RadioTreeItem item;
	/**
	 * Creates new context menu
	 * 
	 * @param item
	 *            application
	 */
	ContextMenuMore(RadioTreeItem item) {
		app = item.getApplication();
		loc = app.getLocalization();
		this.item = item;
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("mioMenu");
		buildGUI();
		}

	private void buildGUI() {
		wrappedPopup.clearItems();
		addDuplicateItem();
		addCloseItem();
		wrappedPopup.addSeparator();
		addPropertiesItem();
	}
	
	public void show(GPoint p) {
		wrappedPopup.show(p);
	}

	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
	}

	private void addDuplicateItem() {
		String img = AppResources.INSTANCE.duplicate20().getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getMenu("Duplicate"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						String dup = item.geo.getDefinitionForEditorNoLabel();
						item.getAV().getInputTreeItem().setText(dup);
					}
				});

		wrappedPopup.addItem(mi);
	}
		
	private void addCloseItem() {
		String img = AppResources.INSTANCE.delete_small().getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getPlain("Delete"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						item.geo.remove();
					}
				});

		wrappedPopup.addItem(mi);
	}

	private void addPropertiesItem() {
		String img = AppResources.INSTANCE.properties20().getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getMenu("Properties"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						ArrayList<GeoElement> list = new ArrayList<GeoElement>();
						list.add(item.geo);
						app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS,
								list);
					}
				});

		wrappedPopup.addItem(mi);
	}

	@Override
	public void setLabels() {
		buildGUI();
	}
	
}

