package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.draw.HasFormat;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.contextmenu.FontSubMenu;
import org.geogebra.web.full.gui.dialog.HyperlinkDialog;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

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
	private final List<HasFormat> inlines;

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

	private boolean hasInlineTable() {
		for (GeoElement geo : geos) {
			if (geo instanceof GeoInlineTable) {
				return true;
			}
		}

		return false;
	}

	private void fillInlines() {
		for (GeoElement geo : geos) {
			inlines.add((HasFormat) app.getActiveEuclidianView().getDrawableFor(geo));
		}
	}

	/**
	 * Add all text items that's available for the geo including
	 * its group if any.
	 */
	void addItems() {
		if (inlines.isEmpty()) {
			return;
		}

		if (hasInlineTable()) {
			addFontSubmenu();
			menu.addSeparator();
		} else {
			addToolbar();
			addFontSubmenu();
			addHyperlinkItems();
			menu.addSeparator();
		}
	}

	private void addToolbar() {
		InlineTextToolbar toolbar = factory.newInlineTextToolbar(inlines, app);
		menu.addItem(toolbar.getItem(), false);
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
		if (inlines.size() != 1) {
			return;
		}

		if (StringUtil.emptyOrZero(((DrawInlineText) inlines.get(0)).getHyperLinkURL())) {
			addHyperlinkItem("Link");
		} else {
			addHyperlinkItem("editLink");
			addRemoveHyperlinkItem();
		}
	}

	private void addHyperlinkItem(String labelTransKey) {
		addItem(labelTransKey, this::openHyperlinkDialog);
	}

	private void openHyperlinkDialog() {
		HyperlinkDialog hyperlinkDialog = new HyperlinkDialog((AppW) app,
				getTextController());
		hyperlinkDialog.center();
	}

	private InlineTextController getTextController() {
		return ((DrawInlineText) inlines.get(0)).getTextController();
	}

	private void addRemoveHyperlinkItem() {
		Command addRemoveHyperlinkCommand = new Command() {
			@Override
			public void execute() {
				getTextController().setHyperlinkUrl(null);
			}
		};

		addItem("removeLink", addRemoveHyperlinkCommand);
	}

	/**
	 *
	 * @return true if no text items for the geo(s)
	 */
	public boolean isEmpty() {
		return inlines.isEmpty();
	}
}
