package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.view.algebra.contextmenu.DeleteAction;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
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
	/** parent item */
	private RadioTreeItem item;
	private MenuActionCollection<GeoElement> actions;
	private GeoElement inputGeo;

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
		this.item = item;
		wrappedPopup = new GPopupMenuW(mApp);
		if (mApp.isUnbundled()) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
		} else {
			wrappedPopup.getPopupPanel().addStyleName("mioMenu");
		}
		this.actions = collection;
		buildGUI();
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
		wrappedPopup.clearItems();
		if (item.isInputTreeItem()) {
			buildForInputItem();
		} else {
			buildForGeo(item.geo);
		}
	}

	private void buildForInputItem() {
		GeoElementND geo = item.getLatexController().evaluateToGeo();
		if (geo != null) {
			inputGeo = (GeoElement) geo;
			buildForGeo(inputGeo);
		} else {
			addAction(new DeleteAction());
		}
	}

	private void buildForGeo(GeoElement geo) {
		for (MenuAction<GeoElement> action : actions) {
			if (action.isAvailable(geo)) {
				addAction(action);
			}
		}
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
		SVGResource img = menuAction.getImage();
		String html = MainMenu.getMenuBarHtml(img, menuAction.getTitle(loc));
		AriaMenuItem mi = new AriaMenuItem(html, true, new Command() {

			@Override
			public void execute() {
				select(menuAction);
			}

		});
		wrappedPopup.addItem(mi);
	}

	/**
	 * @param menuAction
	 *            action to be executed
	 */
	protected void select(final MenuAction<GeoElement> menuAction) {
		if (item.isInputTreeItem()) {
			createdGeoAndSelect(menuAction);
		} else {
			menuAction.execute(item.geo, mApp);
		}
	}

	private void createdGeoAndSelect(final MenuAction<GeoElement> menuAction) {
		item.getLatexController().createGeoFromInput(new AsyncOperation<GeoElementND[]>() {
			
			@Override
			public void callback(GeoElementND[] obj) {
				if (obj == null) {
					item.clearInput();
					return;
				}
				menuAction.execute((GeoElement)obj[0], mApp);
			}
		});
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
}
