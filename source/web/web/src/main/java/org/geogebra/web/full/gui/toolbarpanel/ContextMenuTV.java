package org.geogebra.web.full.gui.toolbarpanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.contextmenu.TableValuesContextMenuItem;
import org.geogebra.common.gui.view.table.RegressionSpecification;
import org.geogebra.common.gui.view.table.TableUtil;
import org.geogebra.common.gui.view.table.TableValuesPoints;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.dialog.StatsBuilder;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AttributedString;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.Command;

/**
 * Context menu which is opened with the table of values header 3dot button
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
		GeoEvaluatable column = view.getEvaluatable(getColumnIdx());
		List<TableValuesContextMenuItem> items = GlobalScope.contextMenuFactory
				.makeTableValuesContextMenu(column, columnIdx, view.getTableValuesModel(),
				app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC,
				GlobalScope.examController.isExamActive());
		for (TableValuesContextMenuItem item: items) {
			if (item.getItem() == TableValuesContextMenuItem.Item.Separator) {
				wrappedPopup.addVerticalSeparator();
			} else {
				addCommand(() -> executeItem(item),
						item.getLocalizedTitle(app.getLocalization()),
						getTestTitle(item.getItem()));
			}
		}
	}

	private String getTestTitle(TableValuesContextMenuItem.Item item) {
		switch (item) {
		case Edit:
			return "edit";
		case ClearColumn:
			return "clear";
		case RemoveColumn:
			return "delete";
		case ShowPoints:
		case HidePoints:
			return "showhide";
		case ImportData:
			return "importData";
		case Regression:
			return "regression";
		case Statistics1:
		case Statistics2:
			return "stats";
		case Separator:
			break;
		}
		return "";
	}

	private void executeItem(TableValuesContextMenuItem item) {
		switch (item.getItem()) {
		case Edit:
			edit();
			break;
		case ClearColumn:
			view.clearValues();
			break;
		case RemoveColumn:
			removeColumn();
			break;
		case HidePoints:
		case ShowPoints:
			showPoints();
			break;
		case ImportData:
			importData();
			break;
		case Regression:
			showRegression();
			break;
		case Statistics1:
			showStats1Var();
			break;
		case Statistics2:
			showStats2Var();
			break;
		case Separator:
			break;
		}
	}

	private void showStats2Var() {
		DialogData twoVarStat = new DialogData("2VariableStatistics",
				getColumnTitleHTML("x " + getHeaderHTMLName()), "Close", null);
		showStats(view::getStatistics2Var, twoVarStat, "StatsDialog.NoDataMsg2VarStats");
	}

	private void showStats1Var() {
		DialogData oneVarStat = new DialogData("1VariableStatistics",
				getColumnTitleHTML(getHeaderHTMLName()), "Close", null);
		showStats(view::getStatistics1Var, oneVarStat, "StatsDialog.NoDataMsg1VarStats");
	}

	private String getColumnTitleHTML(String argument) {
		return app.getLocalization().getPlainDefault("ColumnA",
				"Column %0", argument);
	}

	private void edit() {
		if (getColumnIdx() == 0) {
			DialogManager dialogManager = getApp().getDialogManager();
			if (dialogManager != null) {
				dialogManager.openTableViewDialog(null);
			}
			return;
		}
		GuiManagerInterfaceW guiManager = getApp().getGuiManager();
		if (guiManager != null) {
			guiManager.startEditing(geo);
		}
	}

	private String getHeaderHTMLName() {
		return TableUtil.getHeaderHtml(view.getTableValuesModel(), getColumnIdx());
	}

	private void showRegression() {
		DialogData data = new DialogData("Regression",
				getColumnTitleHTML(getHeaderHTMLName()), "Close", "Plot");
		GeoList[] cleanLists = new StatsBuilder(view.getEvaluatable(0),
				view.getEvaluatable(columnIdx)).getCleanLists2Var();
		final List<RegressionSpecification> availableRegressions =
				RegressionSpecification.getForListSize(cleanLists[0].size());
		if (availableRegressions.isEmpty()) {
			showErrorDialog(data, "StatsDialog.NoDataMsgRegression");
			return;
		}
		app.getAsyncManager().scheduleCallback(() -> {
			List<StatisticGroup> regression = view.getRegression(getColumnIdx(),
					availableRegressions.get(0));
			StatsDialogTV dialog = new StatsDialogTV(app, view, getColumnIdx(), data);
			dialog.addRegressionChooserHasError(availableRegressions, regression);
		});
	}

	private void showErrorDialog(DialogData dialogData, String msgKey) {
		DialogData errorDialogData = new DialogData(dialogData.getTitleTransKey(),
				dialogData.getSubTitleHTML(), "Close", null);
		ComponentDialog dialog = new ComponentDialog(app, errorDialogData, true, true);
		dialog.addStyleName("statistics error");
		InfoErrorData errorData = new InfoErrorData(app.getLocalization()
				.getMenu("StatsDialog.NoData"), app.getLocalization()
				.getMenu(msgKey), null, MaterialDesignResources.INSTANCE.bar_chart_black());
		ComponentInfoErrorPanel infoPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				errorData, null);
		dialog.addDialogContent(infoPanel);
		dialog.show();
	}

	private void showStats(Function<Integer, List<StatisticGroup>> statFunction,
			DialogData data, String noDataMsg) {
		app.getAsyncManager().scheduleCallback(() -> {
			List<StatisticGroup> rowData = statFunction.apply(getColumnIdx());
			if (!rowData.isEmpty()) {
				StatsDialogTV dialog = new StatsDialogTV(app, view, getColumnIdx(), data);
				dialog.setRowsAndShow(rowData);
			} else {
				showErrorDialog(data, noDataMsg);
			}
		});
	}

	private void showPoints() {
		final TableValuesPoints tvPoints = getApp().getGuiManager()
				.getTableValuesPoints();
		final int column = getColumnIdx();
		dispatchShowPointsTV(column, !tvPoints.arePointsVisible(column));
		tvPoints.setPointsVisible(column,
				!tvPoints.arePointsVisible(column));
	}

	private void dispatchShowPointsTV(int column, boolean show) {
		Map<String, Object> showPointsJson = new HashMap<>();
		showPointsJson.put("column", column);
		showPointsJson.put("show",  show);
		app.dispatchEvent(new Event(EventType.SHOW_POINTS_TV).setJsonArgument(showPointsJson));
	}

	private void addCommand(Command command, AttributedString localizedName, String testTitle) {
		AriaMenuItem item = new AriaMenuItem(localizedName,
				null, command);
		addItem(item, testTitle);
	}

	private void addItem(AriaMenuItem mi, String testTitle) {
		mi.addStyleName("no-image");
		TestHarness.setAttr(mi, "menu_" + testTitle);
		wrappedPopup.addItem(mi);
	}

	private void removeColumn() {
		GeoEvaluatable column = view.getEvaluatable(getColumnIdx());
		view.hideColumn(column);
		if (!column.isGeoList()) {
			app.dispatchEvent(new Event(EventType.REMOVE_TV, (GeoElement) column));
		}
	}

	private void importData() {
		((AppWFull) app).getCsvHandler().execute();
	}

	/**
	 * Show the context menu at the (x, y) screen coordinates.
	 * 
	 * @param x
	 *            y coordinate.
	 * @param y
	 *            y coordinate.
	 */
	public void show(Element source, int x, int y) {
		wrappedPopup.showAndFocus(source, x, y);
	}

	public void hide() {
		wrappedPopup.hideMenu();
	}
}
