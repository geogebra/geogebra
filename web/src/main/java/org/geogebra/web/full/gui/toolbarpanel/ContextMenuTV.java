package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

/**
 * Context menu which is opened with the table of values header 3dot button
 * 
 * @author csilla
 *
 */
public class ContextMenuTV {
	/**
	 * popup for the context menu
	 */
	protected GPopupMenuW wrappedPopup;
	/**
	 * localization
	 */
	protected Localization loc;
	private GeoElement geo;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param geo
	 *            label of geo
	 */
	public ContextMenuTV(AppW app, GeoElement geo) {
		this.loc = app.getLocalization();
		this.geo = geo;
		buildGui(app);
	}

	/**
	 * @return geo element
	 */
	public GeoElement getGeo() {
		return geo;
	}

	private void buildGui(AppW app) {
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matMenu");
		if (geo != null) {
			addDelete();
		}
	}

	private void addDelete() {
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.delete_black()
								.getSafeUri().asString(),
						loc.getMenu("RemoveColumn"), true),
				true, new Command() {

					@Override
					public void execute() {
						getGeo().notifyRemove();
					}
				});
		wrappedPopup.addItem(mi);
	}

	/**
	 * Show the context menu at the p given point.
	 * 
	 * @param p
	 *            point to show the menu at.
	 */
	public void show(GPoint p) {
		wrappedPopup.show(p);
		focusDeferred();
	}

	/**
	 * Show the context menu at the (x, y) screen coordinates.
	 * 
	 * @param x
	 *            y coordinate.
	 * @param y
	 *            y coordinate.
	 */
	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
		focusDeferred();
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
