package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.table.TableValuesPoints;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.DialogManager;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;

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
	private int columnIdx;
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
		this.columnIdx = column;
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
		return columnIdx;
	}

	private void buildGui() {
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matMenu");
		if (getColumnIdx() >= 0) {
			// column index >= 0 -> edit function
			addShowHide();
			addEdit(new Command() {

				@Override
				public void execute() {
					GuiManagerInterfaceW guiManager = getApp().getGuiManager();
					if (guiManager != null) {
						guiManager.startEditing(getGeo());
					}
				}
			});
			addDelete();
		} else {
			// column index = -1 -> edit x-column
			addEdit(new Command() {

				@Override
				public void execute() {
					DialogManager dialogManager = getApp().getDialogManager();
					if (dialogManager != null) {
						dialogManager.openTableViewDialog(null);
					}
				}
			});
		}
	}

	private void addShowHide() {
		final TableValuesPoints tvPoints = getApp().getGuiManager()
				.getTableValuesPoints();
		final int column = getColumnIdx() + 1;
		String transKey = tvPoints.arePointsVisible(column) ? "HidePoints"
				: "ShowPoints";
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml((SVGResource) null,
						app.getLocalization().getMenu(transKey)),
				true, new Command() {

					@Override
					public void execute() {
						tvPoints.setPointsVisible(column,
								!tvPoints.arePointsVisible(column));
					}
				});
		addItem(mi, "showhide");
	}

	private void addItem(AriaMenuItem mi, String title) {
		mi.addStyleName("no-image");
		TestHarness.setAttr(mi, "menu_" + title);
		wrappedPopup.addItem(mi);
	}

	private void addDelete() {
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(
						(SVGResource) null,
						app.getLocalization().getMenu("RemoveColumn")),
				true, new Command() {

					@Override
					public void execute() {
						GuiManagerInterfaceW guiManager = getApp().getGuiManager();
						if (guiManager != null && guiManager.getTableValuesView() != null) {
							TableValuesView tableValuesView = (TableValuesView) guiManager
									.getTableValuesView();
							GeoEvaluatable column = tableValuesView
									.getEvaluatable(getColumnIdx());
							tableValuesView.hideColumn(column);
						}
					}
				});
		addItem(mi, "delete");
	}

	private void addEdit(Command cmd) {
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(
						(SVGResource) null,
						app.getLocalization().getMenu("Edit")),
				true, cmd);
		addItem(mi, "edit");
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
