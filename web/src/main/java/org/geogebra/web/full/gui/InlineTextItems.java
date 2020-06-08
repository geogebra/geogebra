package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GeoElement;
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
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;

/**
 * Adds Inline Text related context menu items
 * Like text toolbar, link and font items
 *
 * @author laszlo
 */
public class InlineTextItems {
	private final Localization loc;
	private App app;
	private final ArrayList<GeoElement> geos;
	private GPopupMenuW menu;
	private ContextMenuFactory factory;
	private List<DrawInlineText> inlines;

	/**
	 * @param app the application
	 * @param geos the elements what items are for
	 *@param menu to add the items to.
	 */
	public InlineTextItems(App app, ArrayList<GeoElement> geos, GPopupMenuW menu,
						   ContextMenuFactory factory) {
		this.app = app;
		this.loc = app.getLocalization();
		this.geos = geos;
		this.factory = factory;
		this.menu = menu;
		inlines = new ArrayList<>();
		if (allGeosAreText()) {
			fillInlines();
		}
	}

	private boolean allGeosAreText() {
		for (GeoElement geo: geos) {
			if (!(geo instanceof GeoInlineText)) {
				return false;
			}
		}
		return true;
	}

	private void fillInlines() {
		for (GeoElement geo: geos) {
			inlines.add((DrawInlineText) app.getActiveEuclidianView()
					.getDrawableFor(geo));
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

		addToolbar();
		addFontSubmenu();
		addHyperlinkItems();
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

		if (StringUtil.emptyOrZero(inlines.get(0).getHyperLinkURL())) {
			addHyperlinkItem("Link");
		} else {
			addHyperlinkItem("editLink");
			addRemoveHyperlinkItem();
		}
	}

	private void addHyperlinkItem(String labelTransKey) {
		Command addHyperlinkCommand = new Command() {
			@Override
			public void execute() {
				openHyperlinkDialog();
			}
		};

		addItem(labelTransKey, addHyperlinkCommand);
	}

	private void  openHyperlinkDialog() {
		DialogData data = new DialogData(null, "Cancel", "OK");
		HyperlinkDialog hyperlinkDialog = new HyperlinkDialog((AppW) app, data,
				getTextController());
		hyperlinkDialog.center();
	}

	private InlineTextController getTextController() {
		return inlines.get(0).getTextController();
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
