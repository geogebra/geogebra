package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.ClearInputItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DeleteItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;

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
	private MenuItemCollection<GeoElement> actions;
	private GeoElement geo;
	private ClearInputItem clearInputAction;

	/**
	 * Creates new context menu
	 *
	 * @param item
	 *            application
	 * @param collection
	 *            collection of items
	 */
	ContextMenuAVItemMore(RadioTreeItem item, MenuItemCollection<GeoElement> collection) {
		mApp = item.getApplication();
		loc = mApp.getLocalization();
		wrappedPopup = new GPopupMenuW(mApp);

		this.actions = collection;
		setGeo(item.geo);
		if (item.isInputTreeItem()) {
			clearInputAction = new ClearInputItem(item);
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
			addAction(new DeleteItem());
			return;
		}

		wrappedPopup.clearItems();
		for (MenuItem<GeoElement> action : actions) {
			if (action.getAction().isAvailable(geo)) {
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
		wrappedPopup.show(x, y);
		wrappedPopup.getPopupMenu().focusDeferred();
	}

	private void addAction(final MenuItem<GeoElement> menuItem) {
		AriaMenuItem mi = new AriaMenuItem(menuItem.getTitle(loc),
				false, () -> select(menuItem));
		TestHarness.setAttr(mi, "menu" + menuItem.getTitle());
		mi.addStyleName("no-image");
		wrappedPopup.addItem(mi);
	}

	/**
	 * @param menuItem
	 *            action to be executed
	 */
	protected void select(final MenuItem<GeoElement> menuItem) {
		menuItem.getAction().execute(geo, mApp);
	}

	@Override
	public void setLabels() {
		buildGUI();
	}

	/**
	 * Adds menu for clearing input.
	 */
	void addClearInputItem() {
		wrappedPopup.clearItems();
		addAction(clearInputAction);
	}
}
