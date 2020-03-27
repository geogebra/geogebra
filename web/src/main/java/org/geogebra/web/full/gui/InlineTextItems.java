package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.groups.Group;
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
public class InlineTextItems {
	private final Localization loc;
	private App app;
	private GeoElement geo;
	private GPopupMenuW menu;
	private List<DrawInlineText> inlines;

	/**
	 * @param app the application
	 * @param geo the element what items are for
	 *            (and its group if any)
	 *
	 * @param menu to add the items to.
	 */
	public InlineTextItems(App app, GeoElement geo, GPopupMenuW menu) {
		this.app = app;
		this.loc = app.getLocalization();
		this.geo = geo;
		inlines = geo.hasGroup() ? getGroupAsDrawInlineTexts()
				: getSingleList();
		this.menu = menu;
	}

	private List<DrawInlineText> getSingleList() {
		DrawInlineText drawInlineText = getDrawableInlineText(geo);
		return drawInlineText != null
				? Collections.singletonList(drawInlineText)
				: Collections.<DrawInlineText>emptyList();
	}

	private DrawInlineText firstDrawInlineText() {
		return inlines.get(0);
	}

	private List<DrawInlineText> getGroupAsDrawInlineTexts() {
		List<DrawInlineText> inlines = new ArrayList<>();
		Group group = geo.getParentGroup();
		for (GeoElement geo: group.getGroupedGeos()) {
			DrawInlineText drawInlineText = getDrawableInlineText(geo);
			if (drawInlineText == null) {
				return Collections.emptyList();
			}
			inlines.add(drawInlineText);
		}
		return inlines;
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
		InlineTextToolbar toolbar = new InlineTextToolbar(inlines, app);
		menu.addItem(toolbar, false);
	}

	private DrawInlineText getDrawableInlineText(GeoElement geo) {
		return geo instanceof GeoInlineText ? (DrawInlineText) app.getActiveEuclidianView()
				.getDrawableFor(geo) : null;
	}

	private void addFontSubmenu() {
		AriaMenuItem item = new AriaMenuItem(loc.getMenu("ContextMenu.Font"),
				false,
				new FontSubMenu((AppW) app, inlines));
		item.addStyleName("no-image");
		menu.addItem(item);
	}

	private void addItem(String text, Command command) {
		AriaMenuItem menuItem = new AriaMenuItem(loc.getMenu(text), false,
				command);
		menuItem.getElement().getStyle()
				.setPaddingLeft(16, Style.Unit.PX);
		menu.addItem(menuItem);
	}

	private void addHyperlinkItems() {
		if (inlines.size() != 1) {
			return;
		}

		if (StringUtil.emptyOrZero(firstDrawInlineText().getHyperLinkURL())) {
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
		HyperlinkDialog hyperlinkDialog = new HyperlinkDialog((AppW) app,
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
}
