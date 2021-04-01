package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.contextmenu.FontSubMenu;
import org.geogebra.web.full.gui.dialog.HyperlinkDialog;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;

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
			if (!(geo instanceof GeoInlineText) && !(geo instanceof GeoInlineTable)) {
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
		if (!isEditModeTable() || isSingleTableCellSelection()) {
			menu.addSeparator();
		}
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

			AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu." + setting),
					false, command);

			if (setting.equals(wrapping)) {
				item.addStyleName("highlighted");
			}

			wrappingSubmenu.addItem(item);
		}

		AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu.textWrapping"),
				false, wrappingSubmenu);
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

			AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu.rotate"
							+ setting), false, command);

			if (setting.toLowerCase(Locale.US).equals(rotation)) {
				item.addStyleName("highlighted");
			}

			rotationSubmenu.addItem(item);
		}

		AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu.textRotation"),
				false, rotationSubmenu);
		item.addStyleName("no-image");
		menu.addItem(item);
	}

	private void addHeadingItem() {
		if (!inlines.stream().allMatch(f -> f instanceof InlineTableController)) {
			return;
		}

		GColor color = app.isMebis() ? GColor.MOW_TABLE_HEADING_COLOR : GColor.TABLE_HEADING_COLOR;

		AriaMenuBar headingSubmenu = new AriaMenuBar();

		SVGResource row = MaterialDesignResources.INSTANCE.table_heading_row();
		AriaMenuItem rowItem = factory.newAriaMenuItem(
				MainMenu.getMenuBarHtml(row, loc.getMenu("ContextMenu.Row")), true, () -> {
			for (HasTextFormat formatter : inlines) {
				((InlineTableController) formatter).setHeading(color, true);
			}
		});
		headingSubmenu.addItem(rowItem);

		SVGResource column = MaterialDesignResources.INSTANCE.table_heading_column();
		AriaMenuItem columnItem = factory.newAriaMenuItem(
				MainMenu.getMenuBarHtml(column, loc.getMenu("ContextMenu.Column")), true, () -> {
					for (HasTextFormat formatter : inlines) {
						((InlineTableController) formatter).setHeading(color, false);
					}
				});
		headingSubmenu.addItem(columnItem);

		AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu.Heading"),
				false, headingSubmenu);
		item.addStyleName("no-image");
		menu.addItem(item);
	}

	void addTableItemsIfNeeded() {
		if (isEditModeTable() && isSingleTableCellSelection()) {
			addTableItems();
		}
	}

	void addTableItems() {
		final GeoInlineTable table = (GeoInlineTable) geos.get(0);
		final InlineTableController controller = table.getFormatter();

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
			InlineTextToolbar toolbar = factory.newInlineTextToolbar(inlines, app);
			menu.addItem(toolbar.getItem(), false);
		}
	}

	private void addFontSubmenu() {
		AriaMenuItem item = factory.newAriaMenuItem(loc.getMenu("ContextMenu.Font"),
				false,
				new FontSubMenu((AppW) app, inlines));
		item.addStyleName("no-image");
		menu.addItem(item);
	}

	private void addItem(String text, Command command) {
		AriaMenuItem menuItem = factory.newAriaMenuItem(loc.getMenu(text), false,
				command);
		menuItem.getElement().getStyle()
				.setPaddingLeft(16, Style.Unit.PX);
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
				&& ((InlineTableController) inlines.get(0)).isInEditMode()
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
