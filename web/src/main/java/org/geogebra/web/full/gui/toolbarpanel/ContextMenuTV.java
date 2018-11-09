package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.arithmetic.Evaluatable;
import org.geogebra.common.kernel.geos.GeoElement;
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
	 * application
	 */
	protected AppW app;
	private int columntIdx;
	private GeoElement geo;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param geo
	 *            label of geo
	 * @param column
	 *            index of column
	 */
	public ContextMenuTV(AppW app, GeoElement geo, int column) {
		this.app = app;
		this.columntIdx = column;
		this.geo = geo;
		buildGui();
	}

	/**
	 * @return geo element
	 */
	public GeoElement getGeo() {
		return geo;
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @return index of column
	 */
	public int getColumnIdx() {
		return columntIdx;
	}

	private void buildGui() {
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matMenu");
		addEdit();
		if (getColumnIdx() >= 0) {
			addDelete();
		}
	}

	private void addDelete() {
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.delete_black()
								.getSafeUri().asString(),
						app.getLocalization().getMenu("RemoveColumn"), true),
				true, new Command() {

					@Override
					public void execute() {
						if (getApp().getGuiManager() != null && getApp()
								.getGuiManager().getTableValuesView() != null) {
							Evaluatable column = ((TableValuesView) getApp()
									.getGuiManager().getTableValuesView())
											.getEvaluatable(getColumnIdx());
							((TableValuesView) getApp().getGuiManager()
									.getTableValuesView()).hideColumn(column);
						}
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addEdit() {
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.edit_black()
								.getSafeUri().asString(),
						app.getLocalization().getMenu("Edit"), true),
				true, new Command() {

					@Override
					public void execute() {
						// column index = -1 -> edit x-column
						if (getColumnIdx() < 0) {
							getApp().getDialogManager()
									.openTableViewDialog(getGeo());
						}
						// column index >= -1 -> edit function
						else {
							// TODO edit function
						}
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
