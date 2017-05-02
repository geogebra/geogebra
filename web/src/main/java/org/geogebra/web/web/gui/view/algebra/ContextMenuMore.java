package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.Localization;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.web.MathFieldW;

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
		addCloseItem();
	}
	
	public void show(GPoint p) {
		wrappedPopup.show(p);
	}

	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
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
	@Override
	public void setLabels() {
		buildGUI();
	}
	
}

