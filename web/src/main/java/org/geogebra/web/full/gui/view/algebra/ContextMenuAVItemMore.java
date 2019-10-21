package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.view.algebra.contextmenu.ClearInputAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.DeleteAction;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

/**
 * The ... menu for AV items
 *
 */
public class ContextMenuAVItemMore implements SetLabels {

	/** visible component */
	protected GPopupMenuW wrappedPopup;
	/** localization */
	private Localization loc;
	private AppWFull mApp;
	private MenuActionCollection<GeoElement> actions;
	private GeoElement geo;
	private ClearInputAction clearInputAction;

	/**
	 * Creates new context menu
	 *
	 * @param item
	 *            application
	 * @param collection
	 *            collection of items
	 */
	ContextMenuAVItemMore(RadioTreeItem item, MenuActionCollection<GeoElement> collection) {
		mApp = item.getApplication();
		loc = mApp.getLocalization();
		wrappedPopup = new GPopupMenuW(mApp);
		if (mApp.isUnbundled()) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
		} else {
			wrappedPopup.getPopupPanel().addStyleName("mioMenu");
		}
		this.actions = collection;
		setGeo(item.geo);
		if (item.isInputTreeItem()) {
			clearInputAction = new ClearInputAction(item);
		}
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getApp() {
		return mApp;
	}

	/**
	 * Rebuild the UI
	 */
	public void buildGUI() {
		if (geo == null) {
			addAction(new DeleteAction());
			return;
		}

		wrappedPopup.clearItems();
		for (MenuAction<GeoElement> action : actions) {
			if (action.isAvailable(geo)) {
				addAction(action);
			}
		}
	}

	/**
	 * Sets geo for menu building the menu items
	 * @param geo for
	 */
	public void setGeo(GeoElement geo) {
		this.geo = geo;
		buildGUI();
	}

	/**
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
		focusDeferred();
	}

	private void addAction(final MenuAction<GeoElement> menuAction) {
		SVGResource img = mApp.isUnbundled() ? null : menuAction.getImage();
		String html = MainMenu.getMenuBarHtml(img, menuAction.getTitle(loc));
		AriaMenuItem mi = new AriaMenuItem(html, true, new Command() {

			@Override
			public void execute() {
				select(menuAction);
			}

		});
		TestHarness.setAttr(mi, "menu" + menuAction.getTitle());
		mi.addStyleName("no-image");
		wrappedPopup.addItem(mi);
	}

	/**
	 * @param menuAction
	 *            action to be executed
	 */
	protected void select(final MenuAction<GeoElement> menuAction) {
		menuAction.execute(geo, mApp);
	}

	@Override
	public void setLabels() {
		buildGUI();
	}

	private void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				wrappedPopup.getPopupMenu().getElement().focus();
			}
		});
	}

	/**
	 * Adds menu for clearing input.
	 */
	void addClearInputItem() {
		wrappedPopup.clearItems();
		addAction(clearInputAction);
	}
}
