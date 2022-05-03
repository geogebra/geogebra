package org.geogebra.web.full.gui.toolbarpanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.geogebra.common.gui.view.table.TableUtil;
import org.geogebra.common.gui.view.table.TableValuesPoints;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;

import com.google.gwt.user.client.Command;

/**
 * Context menu which is opened with the table of values header 3dot button
 * 
 * @author csilla
 *
 */
public class ContextMenuTV {
	private final TableValuesView view;
	/**
	 * popup for the context menu
	 */
	protected GPopupMenuW wrappedPopup;
	/**
	 * application
	 */
	protected AppW app;
	private final int columnIdx;
	private final GeoElement geo;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param geo
	 *            label of geo
	 * @param column
	 *            index of column
	 */
	public ContextMenuTV(AppW app, TableValuesView view,
			GeoElement geo, int column) {
		this.app = app;
		this.view = view;
		this.columnIdx = column;
		this.geo = geo;
		buildGui();
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
		wrappedPopup.getPopupPanel().addStyleName("tvContextMenu");
		if (getColumnIdx() > 0) {
			GeoEvaluatable column = view.getEvaluatable(getColumnIdx());
			addShowHidePoints();
			if (column instanceof GeoList) {
				buildYColumnMenu(((GeoList) column).size());
			} else {
				buildFunctionColumnMenu();
			}
		} else {
			buildXColumnMenu();
		}
	}

	private void buildXColumnMenu() {
		addEdit(() -> {
			DialogManager dialogManager = getApp().getDialogManager();
			if (dialogManager != null) {
				dialogManager.openTableViewDialog(null);
			}
		});
		addCommand(view::clearValues, "ClearColumn", "clear");
	}

	private void buildYColumnMenu(int rows) {
		addDelete();
		wrappedPopup.addVerticalSeparator();

		String headerHTMLName = TableUtil.getHeaderHtml(view.getTableValuesModel(),
				getColumnIdx());
		DialogData oneVarStat = new DialogData("1VariableStatistics",
				getColumnTitleHTML(headerHTMLName), "Close", null);
		addStats(getStatisticsTitleHTML(headerHTMLName), view::getStatistics1Var, oneVarStat);

		DialogData twoVarStat = new DialogData("2VariableStatistics",
				getColumnTitleHTML("x " + headerHTMLName), "Close", null);
		addStats(getStatisticsTitleHTML("x " + headerHTMLName),
				view::getStatistics2Var, twoVarStat);

		DialogData regressionData = new DialogData("Regression",
				getColumnTitleHTML(headerHTMLName), "Close", "Plot");
		addCommand(() -> showRegression(regressionData, rows), "Regression",
				"regression");
	}

	private String getStatisticsTitleHTML(String argument) {
		return app.getLocalization().getPlainDefault("AStatistics",
				"%0 Statistics", argument);
	}

	private String getColumnTitleHTML(String argument) {
		return app.getLocalization().getPlainDefault("ColumnA",
				"Column %0", argument);
	}

	private void buildFunctionColumnMenu() {
		addEdit(() -> {
			GuiManagerInterfaceW guiManager = getApp().getGuiManager();
			if (guiManager != null) {
				guiManager.startEditing(geo);
			}
		});
		addDelete();
	}

	private void addStats(String title, Function<Integer, List<StatisticGroup>> statFunction,
			DialogData data) {
		addCommandLocalized(() -> showStats(statFunction, data), title, "stats");
	}

	private void showRegression(DialogData data, int rows) {
		StatsDialogTV dialog = new StatsDialogTV(app, view, getColumnIdx(), data);
		boolean hasError = dialog.addRegressionChooserHasError(rows);
		if (!hasError) {
			dialog.show();
		} else {
			showErrorDialog(data);
		}
	}

	private void showErrorDialog(DialogData dialogData) {
		DialogData errorDialogData = new DialogData(dialogData.getTitleTransKey(),
				dialogData.getSubTitleHTML(), "Close", null);
		ComponentDialog dialog = new ComponentDialog(app, errorDialogData, true, true);
		dialog.addStyleName("statistics error");
		InfoErrorData errorData = new InfoErrorData(app.getLocalization()
				.getMenu("StatsDialog.NoData"), app.getLocalization()
				.getMenu("StatsDialog.NoDataMsg"), null);
		ComponentInfoErrorPanel infoPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				errorData, MaterialDesignResources.INSTANCE.bar_chart_black(), null);
		dialog.addDialogContent(infoPanel);
		dialog.show();
	}

	private void showStats(Function<Integer, List<StatisticGroup>> statFunction,
			DialogData data) {
		StatsDialogTV dialog = new StatsDialogTV(app, view, getColumnIdx(), data);
		dialog.updateContent(statFunction);
	}

	private void addShowHidePoints() {
		final TableValuesPoints tvPoints = getApp().getGuiManager()
				.getTableValuesPoints();
		final int column = getColumnIdx();
		String transKey = tvPoints.arePointsVisible(column) ? "HidePoints"
				: "ShowPoints";
		Command pointCommand = () -> {
			dispatchShowPointsTV(column, !tvPoints.arePointsVisible(column));
			tvPoints.setPointsVisible(column,
				!tvPoints.arePointsVisible(column));
		};
		addCommand(pointCommand, transKey, "showhide");
	}

	private void dispatchShowPointsTV(int column, boolean show) {
		Map<String, Object> showPointsJson = new HashMap<>();
		showPointsJson.put("column", column);
		showPointsJson.put("show",  show);
		app.dispatchEvent(new Event(EventType.SHOW_POINTS_TV).setJsonArgument(showPointsJson));
	}

	private void addCommand(Command command, String transKey, String testTitle) {
		addCommandLocalized(command, app.getLocalization().getMenu(transKey), testTitle);
	}

	private void addCommandLocalized(Command command, String title, String testTitle) {
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml((SVGResource) null, title),
				true, command);
		mi.addStyleName("no-image");
		TestHarness.setAttr(mi, "menu_" + testTitle);
		wrappedPopup.addItem(mi);
	}

	private void addDelete() {
		Command deleteCommand = () -> {
			GeoEvaluatable column = view.getEvaluatable(getColumnIdx());
			view.hideColumn(column);
			if (!column.isGeoList()) {
				app.dispatchEvent(new Event(EventType.REMOVE_TV, (GeoElement) column));
			}
		};
		addCommand(deleteCommand, "RemoveColumn", "delete");
	}

	private void addEdit(Command cmd) {
		addCommand(cmd, "Edit", "edit");
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
		wrappedPopup.show(x, y);
		wrappedPopup.getPopupMenu().focusDeferred();
	}

	public void hide() {
		wrappedPopup.hideMenu();
	}
}
