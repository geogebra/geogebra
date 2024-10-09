package org.geogebra.web.full.gui;

import static org.geogebra.common.kernel.statistics.AlgoTableToChart.ChartType.BarChart;
import static org.geogebra.common.kernel.statistics.AlgoTableToChart.ChartType.LineGraph;
import static org.geogebra.common.kernel.statistics.AlgoTableToChart.ChartType.PieChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.statistics.AlgoTableToChart;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.contextmenu.FontSubMenu;
import org.geogebra.web.full.gui.dialog.HyperlinkDialog;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.EmbedManagerW;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbAPIW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.Command;

/**
 * Adds Inline Text related context menu items
 * Like text toolbar, link and font items
 *
 * @author laszlo
 */
public class InlineFormattingItems {

	private final App app;
	private final Localization loc;
	private final GPopupMenuW menu;
	private final ContextMenuFactory factory;

	private final ArrayList<GeoElement> geos;
	private final List<HasTextFormat> inlines;

	/**
	 * @param app the application
	 * @param geos the elements what items are for
	 *@param menu to add the items to.
	 */
	public InlineFormattingItems(App app, ArrayList<GeoElement> geos, GPopupMenuW menu,
						   ContextMenuFactory factory) {
		this.app = app;
		this.loc = app.getLocalization();
		this.geos = geos;
		this.factory = factory;
		this.menu = menu;
		this.inlines = new ArrayList<>();

		if (allGeosHaveFormats()) {
			fillInlines();
		}
	}

	private boolean allGeosHaveFormats() {
		for (GeoElement geo : geos) {
			if (!(geo instanceof HasTextFormatter)) {
				return false;
			}
		}
		return true;
	}

	private void fillInlines() {
		for (GeoElement geo : geos) {
			if (geo instanceof HasTextFormatter) {
				inlines.add(((HasTextFormatter) geo).getFormatter());
			}
		}
	}

	/**
	 * Add all text items that's available for the geo including
	 * its group if any.
	 */
	void addFormatItems() {
		if (inlines.isEmpty()) {
			return;
		}

		addToolbar();
		addFontSubmenu();
		addHyperlinkItems();
		addTextWrappingItem();
		addTextRotationItem();
		addHeadingItem();
		menu.addSeparator();
	}

	private void addTextWrappingItem() {
		if (!inlines.stream().allMatch(f -> f instanceof InlineTableController)) {
			return;
		}

		AriaMenuBar wrappingSubmenu = new AriaMenuBar();

		String firstWrapping = ((InlineTableController) inlines.get(0)).getWrapping();

		String wrapping;
		if (inlines.stream().allMatch(f ->
				Objects.equals(firstWrapping, ((InlineTableController) f).getWrapping()))) {
			wrapping = firstWrapping;
		} else {
			wrapping = null;
		}

		for (String setting : new String[] {"wrap", "clip"}) {
			Scheduler.ScheduledCommand command = () -> {
				for (HasTextFormat formatter : inlines) {
					((InlineTableController) formatter).setWrapping(setting);
				}
			};

			AriaMenuItem item = factory.newAriaMenuItem(null,
					loc.getMenu("ContextMenu." + setting), command);

			if (setting.equals(wrapping)) {
				item.addStyleName("highlighted");
			}

			wrappingSubmenu.addItem(item);
		}

		AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu.textWrapping"),
				null, wrappingSubmenu);
		item.addStyleName("no-image");
		menu.addItem(item);
	}

	private void addTextRotationItem() {
		if (!inlines.stream().allMatch(f -> f instanceof InlineTableController)) {
			return;
		}

		AriaMenuBar rotationSubmenu = new AriaMenuBar();

		String firstRotation = ((InlineTableController) inlines.get(0)).getRotation();

		String rotation;
		if (inlines.stream().allMatch(f ->
				Objects.equals(firstRotation, ((InlineTableController) f).getRotation()))) {
			rotation = firstRotation;
		} else {
			rotation = null;
		}

		for (String setting : new String[] {"None", "Up", "Down"}) {
			Scheduler.ScheduledCommand command = () -> {
				for (HasTextFormat formatter : inlines) {
					((InlineTableController) formatter).setRotation(setting.toLowerCase(Locale.US));
				}
			};

			AriaMenuItem item = factory.newAriaMenuItem(null, loc.getMenu("ContextMenu.rotate"
							+ setting), command);

			if (setting.toLowerCase(Locale.US).equals(rotation)) {
				item.addStyleName("highlighted");
			}

			rotationSubmenu.addItem(item);
		}

		AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu.textRotation"),
				null, rotationSubmenu);
		item.addStyleName("no-image");
		menu.addItem(item);
	}

	private void addSubMenuItem(AriaMenuBar submenu, SVGResource icon,
			String transKey, Scheduler.ScheduledCommand cmd) {
		AriaMenuItem submenuItem = factory.newAriaMenuItem(
				icon, loc.getMenu(transKey), cmd);
		submenu.addItem(submenuItem);
	}

	void addChartItem() {
		if (inlines.size() != 1 || !(inlines.get(0) instanceof InlineTableController)) {
			return;
		}

		GeoInlineTable table = (GeoInlineTable) geos.get(0);

		Consumer<AlgoTableToChart.ChartType> chartCreator = (chartType) -> {
			int column = ((InlineTableController) inlines.get(0)).getSelectedColumn();

			String command = "TableToChart(" + table.getLabelSimple()
					+ ", \"" + chartType.toString()
					+ "\", " + column + ", " + app.getEmbedManager().nextID() + ")";

			((GgbAPIW) app.getGgbApi()).asyncEvalCommandGetLabels(command, (label) -> {
				GeoEmbed embed = (GeoEmbed) app.getKernel().lookupLabel(label.asT());

				((EmbedManagerW) app.getEmbedManager()).doIfCalcEmbed(embed, calcEmbedElement -> {
					calcEmbedElement.initChart(app.isMebis(), chartType);
				});

				app.getUndoManager().storeUndoInfo();
			}, Log::error);
		};

		AriaMenuBar chartSubmenu = new AriaMenuBar();
		addSubMenuItem(chartSubmenu, MaterialDesignResources.INSTANCE.table_line_chart(),
				"ContextMenu.LineChart", () -> chartCreator.accept(LineGraph));

		addSubMenuItem(chartSubmenu, MaterialDesignResources.INSTANCE.table_bar_chart(),
				"ContextMenu.BarChart", () -> chartCreator.accept(BarChart));

		addSubMenuItem(chartSubmenu, MaterialDesignResources.INSTANCE.table_pie_chart(),
				"ContextMenu.PieChart", () -> chartCreator.accept(PieChart));

		AriaMenuItem chartItem = factory.newAriaMenuItem(loc.getMenu("ContextMenu.CreateChart"),
				null, chartSubmenu);
		chartItem.addStyleName("no-image");

		menu.addItem(chartItem);
		if (!isEditModeTable() || isSingleTableCellSelection()) {
			menu.addSeparator();
		}
	}

	private void addHeadingItem() {
		if (!inlines.stream().allMatch(f -> f instanceof InlineTableController)) {
			return;
		}

		GColor color = app.isMebis() ? GColor.MOW_TABLE_HEADING_COLOR : GColor.TABLE_HEADING_COLOR;

		AriaMenuBar headingSubmenu = new AriaMenuBar();

		addSubMenuItem(headingSubmenu, MaterialDesignResources.INSTANCE.table_heading_row(),
				"ContextMenu.Row", () -> {
					for (HasTextFormat formatter : inlines) {
						((InlineTableController) formatter).setHeading(color, true);
					}
				});

		addSubMenuItem(headingSubmenu, MaterialDesignResources.INSTANCE.table_heading_column(),
				"ContextMenu.Column", () -> {
					for (HasTextFormat formatter : inlines) {
						((InlineTableController) formatter).setHeading(color, false);
					}
				});

		AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu.Heading"),
				null, headingSubmenu);
		item.addStyleName("no-image");
		menu.addItem(item);
	}

	void addTableItemsIfNeeded() {
		if (isSingleTableCellSelection()) {
			addTableItems();
		}
	}

	void addTableItems() {
		final GeoInlineTable table = (GeoInlineTable) geos.get(0);
		final InlineTableController controller = (InlineTableController) table.getFormatter();

		addItem("ContextMenu.insertRowAbove", controller::insertRowAbove);
		addItem("ContextMenu.insertRowBelow", controller::insertRowBelow);
		addItem("ContextMenu.insertColumnLeft", controller::insertColumnLeft);
		addItem("ContextMenu.insertColumnRight", controller::insertColumnRight);

		menu.addSeparator();

		addItem("ContextMenu.deleteRow", controller::removeRow);
		addItem("ContextMenu.deleteColumn", controller::removeColumn);
	}

	private void addToolbar() {
		if (inlines.stream().allMatch(this::textOrEditModeTable)) {
			AriaMenuItem toolbar = factory.newInlineTextToolbar(inlines, app);
			menu.addItem(toolbar, false);
		}
	}

	private void addFontSubmenu() {
		AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu.Font"),
				null,
				new FontSubMenu((AppW) app, inlines));
		item.addStyleName("no-image");
		menu.addItem(item);
	}

	private void addItem(String text, Command command) {
		AriaMenuItem menuItem = factory.newAriaMenuItem(null, loc.getMenu(text),
				command);
		menuItem.getElement().getStyle()
				.setPaddingLeft(16, Unit.PX);
		menu.addItem(menuItem);
	}

	protected void addHyperlinkItems() {
		if (inlines.size() == 1 && textOrEditModeTable(inlines.get(0))) {
			if (StringUtil.emptyOrZero(inlines.get(0).getHyperLinkURL())) {
				addHyperlinkItem("Link");
			} else {
				addHyperlinkItem("editLink");
				addRemoveHyperlinkItem();
			}
		}
	}

	private boolean textOrEditModeTable(HasTextFormat hasTextFormat) {
		return hasTextFormat instanceof InlineTextController
				|| isEditModeTable(hasTextFormat);
	}

	public boolean isEditModeTable() {
		return !inlines.isEmpty() && isEditModeTable(inlines.get(0));
	}

	private boolean isEditModeTable(HasTextFormat hasTextFormat) {
		return hasTextFormat instanceof InlineTableController
				&& ((InlineTableController) hasTextFormat).isInEditMode()
				&& ((InlineTableController) hasTextFormat).hasSelection();
	}

	boolean isSingleTableCellSelection() {
		return !inlines.isEmpty()
				&& inlines.get(0) instanceof InlineTableController
				&& ((InlineTableController) inlines.get(0)).isSingleCellSelection();
	}

	private void addHyperlinkItem(String labelTransKey) {
		addItem(labelTransKey, this::openHyperlinkDialog);
	}

	private void openHyperlinkDialog() {
		DialogData data = new DialogData(null);
		HyperlinkDialog hyperlinkDialog = new HyperlinkDialog((AppW) app, data, inlines.get(0));
		hyperlinkDialog.center();
	}

	private void addRemoveHyperlinkItem() {
		addItem("removeLink", () -> inlines.get(0).setHyperlinkUrl(null));
	}

	/**
	 *
	 * @return true if no text items for the geo(s)
	 */
	public boolean isEmpty() {
		return inlines.isEmpty();
	}
}
